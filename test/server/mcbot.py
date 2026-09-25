"""Minimal Minecraft 1.20.1 (protocol 763) client for server tests.

Offline-mode login, keep-alive, teleport confirmation and auto-respawn like the
vanilla client with doImmediateRespawn. Every received packet is logged with its
time and size on the wire, so tests can measure what a real player receives.
A read rate limit simulates a slow internet link (e.g. a relay/tunnel).
"""
import hashlib
import json
import re
import socket
import struct
import threading
import time
import zlib

PROTOCOL = 763  # 1.20.1

# clientbound (play)
CB_SPAWN_ENTITY = 0x01
CB_SPAWN_PLAYER = 0x03
CB_DISCONNECT = 0x1A
CB_UNLOAD_CHUNK = 0x1E
CB_GAME_EVENT = 0x1F
CB_KEEP_ALIVE = 0x23
CB_CHUNK = 0x24
CB_LOGIN = 0x28
CB_PING = 0x32
CB_COMBAT_DEATH = 0x38
CB_SYNC_POSITION = 0x3C
CB_REMOVE_ENTITIES = 0x3E
CB_RESPAWN = 0x41
CB_SET_CAMERA = 0x4C
CB_SET_HEALTH = 0x57
CB_SYSTEM_CHAT = 0x64
# serverbound (play)
SB_CONFIRM_TELEPORT = 0x00
SB_CLIENT_COMMAND = 0x07
SB_CLIENT_INFO = 0x08
SB_PLUGIN = 0x0D
SB_KEEP_ALIVE = 0x12
SB_POS_ROT = 0x15
SB_PONG = 0x20

ARMOR_STAND_TYPE = 2  # minecraft:armor_stand in the 1.20.1 entity type registry

NAMES = {
    0x01: 'spawn_entity', 0x03: 'spawn_player', 0x12: 'container_content', 0x14: 'container_slot',
    0x1A: 'disconnect', 0x1E: 'unload_chunk', 0x1F: 'game_event', 0x23: 'keep_alive', 0x24: 'chunk',
    0x26: 'particle', 0x27: 'light', 0x28: 'login', 0x2B: 'entity_pos', 0x2C: 'entity_pos_rot',
    0x2D: 'entity_rot', 0x38: 'combat_death', 0x3A: 'player_info', 0x3C: 'sync_position',
    0x3E: 'remove_entities', 0x41: 'respawn', 0x42: 'head_rot', 0x4C: 'set_camera', 0x52: 'entity_data',
    0x55: 'equipment', 0x57: 'set_health', 0x64: 'system_chat', 0x68: 'teleport_entity',
    0x6A: 'attributes', 0x6C: 'entity_effect', 0x46: 'actionbar', 0x5F: 'title', 0x5D: 'subtitle',
}


def varint(value):
    value &= 0xFFFFFFFF
    out = bytearray()
    while True:
        b = value & 0x7F
        value >>= 7
        if value:
            out.append(b | 0x80)
        else:
            out.append(b)
            return bytes(out)


def read_varint(buf, pos=0):
    result = 0
    shift = 0
    while True:
        b = buf[pos]
        pos += 1
        result |= (b & 0x7F) << shift
        if not b & 0x80:
            break
        shift += 7
        if shift > 35:
            raise ValueError('VarInt too big')
    if result & (1 << 31):
        result -= 1 << 32
    return result, pos


def mc_string(text):
    data = text.encode('utf-8')
    return varint(len(data)) + data


def read_string(buf, pos):
    n, pos = read_varint(buf, pos)
    return buf[pos:pos + n].decode('utf-8', 'replace'), pos + n


def offline_uuid(name):
    h = bytearray(hashlib.md5(('OfflinePlayer:' + name).encode()).digest())
    h[6] = (h[6] & 0x0F) | 0x30
    h[8] = (h[8] & 0x3F) | 0x80
    return bytes(h)


class Disconnected(Exception):
    pass


class Bot:
    def __init__(self, name, host='127.0.0.1', port=25565, view_distance=12):
        self.name = name
        self.host = host
        self.port = port
        self.view_distance = view_distance
        self.rate = 0            # bytes/s, 0 = unlimited
        self._tokens = 0.0
        self._last = time.monotonic()
        self.lock = threading.Lock()
        self.send_lock = threading.Lock()
        self.gen = 0
        self.reset_state()

    def reset_state(self):
        self.sock = None
        self.threshold = -1
        self.eid = None
        self.in_play = False
        self.connected = False
        self.disconnect_reason = None
        self.pos = None
        self.bytes_log = []       # (t, n) socket reads
        self.packets = []         # (t, id, wire_size)
        self.events = []          # (t, kind, data)
        self.entities = {}        # id -> type (-1 = player)
        self.probes = {}          # probe id -> receive time
        self.error = None
        self._buf = bytearray()
        self._pos = 0

    # ------------------------------------------------------------------ connection
    def connect(self, timeout=60):
        self.gen += 1
        self.reset_state()
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        # small, fixed receive window: a throttled reader backs up into the server like a slow link
        sock.setsockopt(socket.SOL_SOCKET, socket.SO_RCVBUF, 128 * 1024)
        sock.settimeout(60)
        sock.connect((self.host, self.port))
        self.sock = sock
        self.connected = True
        self._send_raw(0x00, varint(PROTOCOL) + mc_string(self.host) + struct.pack('>H', self.port) + varint(2))
        self._send_raw(0x00, mc_string(self.name) + b'\x01' + offline_uuid(self.name))
        self.thread = threading.Thread(target=self._run, name='bot-' + self.name, daemon=True)
        self.thread.start()
        end = time.time() + timeout
        while time.time() < end:
            if self.in_play and self.pos is not None:
                return
            if not self.connected:
                raise Disconnected('%s: %s' % (self.name, self.disconnect_reason or self.error))
            time.sleep(0.05)
        raise TimeoutError('%s did not join in time' % self.name)

    def close(self):
        self.connected = False
        try:
            self.sock.shutdown(socket.SHUT_RDWR)
        except OSError:
            pass
        try:
            self.sock.close()
        except OSError:
            pass

    # ------------------------------------------------------------------ io
    def _recv_some(self):
        rate = self.rate
        size = 65536
        if rate:
            while True:
                now = time.monotonic()
                self._tokens = min(rate * 0.05, self._tokens + (now - self._last) * rate)
                self._last = now
                if self._tokens >= 1024:
                    break
                time.sleep((1024 - self._tokens) / rate)
            size = int(min(size, self._tokens))
        data = self.sock.recv(size)
        if not data:
            raise Disconnected('connection closed')
        if rate:
            self._tokens -= len(data)
        with self.lock:
            self.bytes_log.append((time.time(), len(data)))
        return data

    def _need(self, n):
        while len(self._buf) - self._pos < n:
            if self._pos > (1 << 20):
                del self._buf[:self._pos]
                self._pos = 0
            self._buf += self._recv_some()

    def _read(self, n):
        self._need(n)
        out = bytes(self._buf[self._pos:self._pos + n])
        self._pos += n
        return out

    def _read_frame(self):
        length = 0
        shift = 0
        header = 0
        while True:
            b = self._read(1)[0]
            header += 1
            length |= (b & 0x7F) << shift
            if not b & 0x80:
                break
            shift += 7
        body = self._read(length)
        wire = header + length
        if self.threshold >= 0:
            data_len, p = read_varint(body, 0)
            if data_len == 0:
                data = body[p:]
            else:
                data = zlib.decompress(body[p:])
        else:
            data = body
        pid, p = read_varint(data, 0)
        return pid, data[p:], wire

    def _send_raw(self, pid, payload=b''):
        data = varint(pid) + payload
        if self.threshold >= 0:
            if len(data) >= self.threshold:
                body = varint(len(data)) + zlib.compress(data)
            else:
                body = varint(0) + data
        else:
            body = data
        with self.send_lock:
            self.sock.sendall(varint(len(body)) + body)

    def send(self, pid, payload=b''):
        if self.connected:
            try:
                self._send_raw(pid, payload)
            except OSError:
                pass

    # ------------------------------------------------------------------ protocol
    def _run(self):
        gen = self.gen
        try:
            self._login()
            self._play()
        except Exception as exc:  # noqa: BLE001 - record any failure for the test report
            if self.connected and self.gen == gen:
                self.error = repr(exc)
        finally:
            if self.gen == gen:
                self.connected = False
                self.in_play = False

    def _login(self):
        while True:
            pid, data, _ = self._read_frame()
            if pid == 0x00:
                self.disconnect_reason = data.decode('utf-8', 'replace')
                raise Disconnected(self.disconnect_reason)
            if pid == 0x01:
                raise Disconnected('server is in online mode')
            if pid == 0x03:
                self.threshold, _ = read_varint(data, 0)
            elif pid == 0x04:
                msg_id, _ = read_varint(data, 0)
                self._send_raw(0x02, varint(msg_id) + b'\x00')
            elif pid == 0x02:
                return

    def _event(self, kind, data=None):
        with self.lock:
            self.events.append((time.time(), kind, data))

    def _play(self):
        while True:
            pid, data, wire = self._read_frame()
            now = time.time()
            with self.lock:
                self.packets.append((now, pid, wire))
            if pid == CB_KEEP_ALIVE:
                self.send(SB_KEEP_ALIVE, data[:8])
            elif pid == CB_PING:
                self.send(SB_PONG, data[:4])
            elif pid == CB_LOGIN:
                self.eid = struct.unpack('>i', data[:4])[0]
                self.in_play = True
                self._event('login', self.eid)
                self.send(SB_CLIENT_INFO, mc_string('en_us') + struct.pack('>b', self.view_distance) + varint(0)
                          + b'\x01' + b'\x7f' + varint(1) + b'\x00' + b'\x01')
                self.send(SB_PLUGIN, mc_string('minecraft:brand') + mc_string('vanilla'))
            elif pid == CB_SYNC_POSITION:
                x, y, z, yaw, pitch, flags = struct.unpack('>dddffb', data[:33])
                tp_id, _ = read_varint(data, 33)
                old = self.pos or (0.0, 0.0, 0.0, 0.0, 0.0)
                vals = [x, y, z, yaw, pitch]
                for i in range(5):
                    if flags & (1 << i):
                        vals[i] += old[i]
                self.pos = tuple(vals)
                self.send(SB_CONFIRM_TELEPORT, varint(tp_id))
                self.send(SB_POS_ROT, struct.pack('>dddff', *self.pos) + b'\x00')
                self._event('teleport', self.pos)
            elif pid == CB_COMBAT_DEATH:
                player_id, _ = read_varint(data, 0)
                if player_id == self.eid:
                    self._event('death')
                    self.send(SB_CLIENT_COMMAND, varint(0))  # doImmediateRespawn: respawn at once
            elif pid == CB_RESPAWN:
                self._event('respawn')
            elif pid == CB_SET_CAMERA:
                cam, _ = read_varint(data, 0)
                with self.lock:
                    known = self.entities.get(cam)
                self._event('camera', (cam, known))
            elif pid == CB_SPAWN_ENTITY:
                ent, p = read_varint(data, 0)
                etype, _ = read_varint(data, p + 16)
                with self.lock:
                    self.entities[ent] = etype
            elif pid == CB_SPAWN_PLAYER:
                ent, _ = read_varint(data, 0)
                with self.lock:
                    self.entities[ent] = -1
            elif pid == CB_REMOVE_ENTITIES:
                count, p = read_varint(data, 0)
                with self.lock:
                    for _ in range(count):
                        ent, p = read_varint(data, p)
                        self.entities.pop(ent, None)
            elif pid == CB_GAME_EVENT:
                event, value = struct.unpack('>Bf', data[:5])
                if event == 3:
                    self._event('gamemode', int(value))
            elif pid == CB_SET_HEALTH:
                self._event('health', struct.unpack('>f', data[:4])[0])
            elif pid == CB_SYSTEM_CHAT:
                text, _ = read_string(data, 0)
                m = re.search(r'#probe:(\d+)', text)
                if m:
                    with self.lock:
                        self.probes[int(m.group(1))] = now
                else:
                    self._event('chat', text)
            elif pid == CB_DISCONNECT:
                text, _ = read_string(data, 0)
                self.disconnect_reason = text
                self._event('disconnect', text)
                raise Disconnected(text)

    # ------------------------------------------------------------------ stats
    def bytes_between(self, t0, t1):
        with self.lock:
            return sum(n for t, n in self.bytes_log if t0 <= t < t1)

    def packets_between(self, t0, t1):
        with self.lock:
            return [(t, pid, w) for t, pid, w in self.packets if t0 <= t < t1]

    def events_between(self, t0, t1, kind=None):
        with self.lock:
            return [(t, k, d) for t, k, d in self.events if t0 <= t < t1 and (kind is None or k == kind)]
