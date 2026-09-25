"""Tiny RCON client (Source RCON protocol, as used by Minecraft)."""
import socket
import struct
import threading


class Rcon:
    def __init__(self, host='127.0.0.1', port=25575, password='test', timeout=30):
        self.sock = socket.create_connection((host, port), timeout=timeout)
        self.req = 0
        self.lock = threading.Lock()
        self._send(3, password)
        rid, _, _ = self._recv()
        if rid == -1:
            raise RuntimeError('RCON authentication failed')

    def _send(self, typ, body):
        self.req += 1
        payload = struct.pack('<ii', self.req, typ) + body.encode('utf-8') + b'\x00\x00'
        self.sock.sendall(struct.pack('<i', len(payload)) + payload)

    def _recv_exact(self, n):
        out = b''
        while len(out) < n:
            chunk = self.sock.recv(n - len(out))
            if not chunk:
                raise ConnectionError('RCON closed')
            out += chunk
        return out

    def _recv(self):
        (n,) = struct.unpack('<i', self._recv_exact(4))
        data = self._recv_exact(n)
        rid, typ = struct.unpack('<ii', data[:8])
        return rid, typ, data[8:-2].decode('utf-8', 'replace')

    def cmd(self, command):
        with self.lock:
            self._send(2, command)
            _, _, body = self._recv()
            return body

    def close(self):
        try:
            self.sock.close()
        except OSError:
            pass
