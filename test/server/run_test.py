"""Sniper Arena end-to-end test on a real Forge 1.20.1 server.

Three bot players join the world, start a match from the lobby pad and fight a
full match through RCON-driven kills, while every game rule is checked on the
server. The "Guest" bot reads its socket at a limited rate like a player who
joins the host through an internet tunnel; for it we measure what each death
costs on the network with the vanilla death (respawn) and with the mod's
virtual death.

Usage: python3 run_test.py <server_dir> <view_distance>
"""
import json
import math
import os
import re
import subprocess
import sys
import threading
import time

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from mcbot import Bot, Disconnected, ARMOR_STAND_TYPE, CB_CHUNK, CB_RESPAWN, NAMES  # noqa: E402
from rcon import Rcon  # noqa: E402

SERVER_DIR = sys.argv[1]
VIEW = int(sys.argv[2]) if len(sys.argv) > 2 else 12
GUEST_RATE = 250_000  # bytes/s = 2 Mbit/s
LOG = os.path.join(SERVER_DIR, 'logs', 'latest.log')
ARENA = (148, -64, -89, 217, -20, 6)
FORGE_ARGS = 'libraries/net/minecraftforge/forge/1.20.1-47.2.0/unix_args.txt'
DATAPACK = os.path.join(SERVER_DIR, 'world', 'datapacks', 'sniper_arena', 'data', 'sniper_arena', 'functions')

results = []
report = []


def log(msg):
    print(time.strftime('%H:%M:%S ') + msg, flush=True)


def check(ok, what, detail=''):
    results.append((bool(ok), what, detail))
    log(('PASS ' if ok else 'FAIL ') + what + ((' -- ' + str(detail)) if detail != '' else ''))
    return bool(ok)


# ---------------------------------------------------------------------------- server
class Server:
    def __init__(self):
        self.proc = None

    def start(self):
        out = open(os.path.join(SERVER_DIR, 'console.log'), 'w')
        args = ['java', '-Xmx3G', '-Dfml.queryResult=confirm', '@user_jvm_args.txt', '@' + FORGE_ARGS, 'nogui']
        self.proc = subprocess.Popen(args, cwd=SERVER_DIR, stdin=subprocess.PIPE, stdout=out, stderr=subprocess.STDOUT)

    def wait_for(self, pattern, timeout):
        end = time.time() + timeout
        path = os.path.join(SERVER_DIR, 'console.log')
        while time.time() < end:
            if self.proc.poll() is not None:
                raise RuntimeError('server exited with code %s' % self.proc.returncode)
            try:
                with open(path, encoding='utf-8', errors='replace') as f:
                    if re.search(pattern, f.read()):
                        return
            except FileNotFoundError:
                pass
            time.sleep(1)
        raise TimeoutError('server did not print %r' % pattern)

    def stop(self):
        if self.proc and self.proc.poll() is None:
            try:
                self.proc.stdin.write(b'stop\n')
                self.proc.stdin.flush()
                self.proc.wait(timeout=90)
            except Exception:  # noqa: BLE001
                self.proc.kill()


# ---------------------------------------------------------------------------- queries
rc = None
probe_rc = None


def score(holder, objective):
    out = rc.cmd('scoreboard players get %s %s' % (holder, objective))
    m = re.search(r' has (-?\d+) \[', out)
    return int(m.group(1)) if m else None


def edata(target, path):
    out = rc.cmd('data get entity %s %s' % (target, path))
    m = re.search(r'has the following entity data: (.*)', out, re.S)
    return m.group(1).strip() if m else None


def gamemode(name):
    v = edata(name, 'playerGameType')
    return int(v) if v is not None else None


def health(name):
    v = edata(name, 'Health')
    return float(v.rstrip('f')) if v else None


def tags(name):
    v = edata(name, 'Tags')
    return json.loads(v) if v else []


def pos(name):
    v = edata(name, 'Pos')
    return [float(x.strip().rstrip('d')) for x in v.strip('[]').split(',')] if v else None


def weapon(name, slot=0):
    v = edata(name, 'Inventory[{Slot:%db}].tag' % slot)
    if not v:
        return None
    m = re.search(r'(?:GunId|MeleeWeaponId): "([^"]+)"', v)
    return m.group(1) if m else v


def test(selector):
    return 'Test passed' in rc.cmd('execute if entity %s' % selector)


def camera_of(victim):
    out = rc.cmd('execute as @e[type=minecraft:armor_stand,tag=sa.cam] if score @s sa.pid = %s sa.pid '
                 'run data get entity @s Pos' % victim)
    m = re.search(r'entity data: \[([^\]]*)\]', out)
    if not m:
        return None
    p = [float(x.strip().rstrip('d')) for x in m.group(1).split(',')]
    out = rc.cmd('execute as @e[type=minecraft:armor_stand,tag=sa.cam] if score @s sa.pid = %s sa.pid '
                 'run data get entity @s Rotation' % victim)
    m = re.search(r'entity data: \[([^\]]*)\]', out)
    r = [float(x.strip().rstrip('f')) for x in m.group(1).split(',')] if m else None
    return p, r


def in_arena(p):
    return p is not None and ARENA[0] <= p[0] <= ARENA[3] + 1 and ARENA[2] <= p[2] <= ARENA[5] + 1


def facing_error(cam_pos, cam_rot, target):
    dx, dy, dz = (target[i] - cam_pos[i] for i in range(3))
    yaw = math.degrees(math.atan2(dz, dx)) - 90.0
    pitch = -math.degrees(math.atan2(dy, math.hypot(dx, dz)))
    dyaw = (cam_rot[0] - yaw + 180.0) % 360.0 - 180.0
    return max(abs(dyaw), abs(cam_rot[1] - pitch))


def wait_until(fn, timeout, step=0.1):
    end = time.time() + timeout
    while time.time() < end:
        v = fn()
        if v:
            return v
        time.sleep(step)
    return fn()


def levels():
    out = {}
    for n in range(1, 19):
        with open(os.path.join(DATAPACK, 'guns', 'level_%d.mcfunction' % n), encoding='utf-8') as f:
            m = re.search(r'(?:GunId|MeleeWeaponId):"([^"]+)"', f.read())
            out[n] = m.group(1)
    return out


# ---------------------------------------------------------------------------- game helpers
bots = {}
pids = {}
LEVEL = {}
expected = {}


def kill(victim, killer):
    return rc.cmd('damage %s 1000 minecraft:player_attack by %s' % (victim, killer))


def wait_ready(name, what):
    """Wait until the player is back from the death screen, then until spawn protection ends."""
    ok = wait_until(lambda: gamemode(name) == 2 and 'sa.deathcam' not in tags(name), 8)
    check(ok, '%s: %s respawned in adventure mode after the death screen' % (what, name))
    p = pos(name)
    check(in_arena(p), '%s: %s respawned inside the arena' % (what, name), p)
    check(health(name) == 100.0, '%s: %s has full 100 HP after respawn' % (what, name), health(name))
    want = LEVEL[min(expected[name], 17) + 1]
    check(weapon(name) == want, '%s: %s holds the weapon for %d kills' % (what, name, expected[name]),
          '%s (want %s)' % (weapon(name), want))
    check(weapon(name, 1) == 'lrtactical:karambit', '%s: %s still has the karambit' % (what, name), weapon(name, 1))
    time.sleep(3.3)  # spawn protection (resistance 4 for 3 s)


def check_victim(victim, killer, what):
    ok = wait_until(lambda: 'sa.deathcam' in tags(victim), 3)
    check(ok, '%s: %s is on the death screen' % (what, victim))
    check(gamemode(victim) == 3, '%s: %s is a spectator during the death screen' % (what, victim), gamemode(victim))
    if killer:
        check(score(victim, 'sa.killer') == pids[killer], '%s: death screen knows the killer (%s)' % (what, killer),
              score(victim, 'sa.killer'))
        cam = wait_until(lambda: camera_of(victim), 2)
        if check(cam is not None, '%s: killcam camera exists for %s' % (what, victim)):
            kp = pos(killer)
            eyes = [kp[0], kp[1] + 1.62, kp[2]]
            err = facing_error(cam[0], cam[1], eyes)
            dist = math.dist(cam[0], eyes)
            check(err < 2.0, '%s: killcam looks at %s (angle error %.2f deg, distance %.1f)' % (what, killer, err, dist))


def check_killer(killer, what):
    ok = wait_until(lambda: score(killer, 'sa.kills') == expected[killer], 3)
    check(ok, '%s: %s has %d kills' % (what, killer, expected[killer]), score(killer, 'sa.kills'))
    if expected[killer] < 18:
        want = LEVEL[expected[killer] + 1]
        ok = wait_until(lambda: weapon(killer) == want, 2)
        check(ok, '%s: %s got the next weapon (level %d)' % (what, killer, expected[killer] + 1),
              '%s (want %s)' % (weapon(killer), want))


last_death = {}


def ensure_ready(name, what):
    """Before killing someone again: wait for the death screen to end and spawn protection to wear off."""
    if name in last_death:
        del last_death[name]
        wait_ready(name, what)


def single_kill(victim, killer, what):
    t_kill = time.time()
    kill(victim, killer)
    last_death[victim] = time.time()
    expected[killer] += 1
    check_killer(killer, what)
    check_victim(victim, killer, what)
    bot = bots[victim]
    cams = wait_until(lambda: [e for e in bot.events_between(t_kill, time.time() + 1, 'camera') if e[2][0] != bot.eid], 3)
    if cams:
        _, _, (cam_id, known) = cams[0]
        check(known == ARMOR_STAND_TYPE, '%s: %s client switched to the killcam entity it already knows' % (what, victim),
              'camera id %s, known type %s' % (cam_id, known))
    else:
        check(False, '%s: %s client received the killcam camera' % (what, victim))


def start_match(what):
    for name in bots:
        rc.cmd('tp %s @e[type=minecraft:marker,tag=sa.pad,limit=1]' % name)
    ok = wait_until(lambda: score('#state', 'sa.var') == 2, 40, 0.5)
    check(ok, '%s: countdown on the pad started the match' % what)
    for name in bots:
        expected[name] = 0
        check('sa.ingame' in tags(name), '%s: %s joined the match' % (what, name))
        check(in_arena(pos(name)), '%s: %s was teleported into the arena' % (what, name), pos(name))
        check(gamemode(name) == 2, '%s: %s is in adventure mode' % (what, name))
        check(health(name) == 100.0, '%s: %s has 100 HP' % (what, name), health(name))
        check(weapon(name) == LEVEL[1], '%s: %s has the first weapon' % (what, name), weapon(name))
        check(weapon(name, 1) == 'lrtactical:karambit', '%s: %s has the karambit' % (what, name), weapon(name, 1))
    time.sleep(3.3)


# ---------------------------------------------------------------------------- network measurement
probe_counter = [0]


def measure_death(victim, killer, label, window=8.0):
    bot = bots[victim]
    sent = {}
    stop = threading.Event()

    def prober():
        while not stop.is_set():
            probe_counter[0] += 1
            n = probe_counter[0]
            probe_rc.cmd('tellraw %s {"text":"#probe:%d"}' % (victim, n))
            sent[n] = time.time()
            time.sleep(0.2)

    th = threading.Thread(target=prober, daemon=True)
    th.start()
    time.sleep(1.5)
    t0 = time.time()
    kill(victim, killer)
    time.sleep(window)
    stop.set()
    th.join()
    # wait for the backlog to drain (every probe sent during the window delivered)
    in_window = [n for n, t in sent.items() if t >= t0]
    wait_until(lambda: all(n in bot.probes for n in in_window) or not bot.connected, 60, 0.25)
    t_end = max([bot.probes.get(n, time.time()) for n in in_window] + [t0 + window])
    lat_before = [bot.probes[n] - t for n, t in sent.items() if t < t0 and n in bot.probes]
    lat_after = [bot.probes.get(n, time.time()) - t for n, t in sent.items() if t >= t0]
    pk = bot.packets_between(t0, t_end + 0.5)
    chunks = [p for p in pk if p[1] == CB_CHUNK]
    by_type = {}
    for _, pid, w in pk:
        by_type[pid] = by_type.get(pid, 0) + w
    top = sorted(by_type.items(), key=lambda kv: -kv[1])[:5]
    cams = [e for e in bot.events_between(t0, t_end + 0.5, 'camera') if e[2][0] != bot.eid]
    res = {
        'label': label,
        'bytes': bot.bytes_between(t0, t_end + 0.5),
        'chunks': len(chunks),
        'chunk_bytes': sum(p[2] for p in chunks),
        'respawns': len(bot.events_between(t0, t_end + 0.5, 'respawn')),
        'camera_delay': (cams[0][0] - t0) if cams else None,
        'camera_known': (cams[0][2][1] == ARMOR_STAND_TYPE) if cams else False,
        'lat_before': max(lat_before) if lat_before else None,
        'lat_max': max(lat_after) if lat_after else None,
        'connected': bot.connected,
        'top': ', '.join('%s %.0f KB' % (NAMES.get(pid, hex(pid)), w / 1024) for pid, w in top),
    }
    log('measure %s: %s' % (label, res))
    return res


def run_measurements():
    rows = []
    guest = bots['Guest']
    guest.rate = GUEST_RATE
    for mode, value in (('virtual', 1), ('vanilla', 0)):
        rc.cmd('scoreboard players set #virtual_death sa.cfg %d' % value)
        for i in range(2):
            ensure_ready('Guest', 'network (%s)' % mode)
            r = measure_death('Guest', 'Host1', '%s #%d' % (mode, i + 1))
            r['mode'] = mode
            rows.append(r)
            expected['Host1'] += 1
            last_death['Guest'] = time.time()
            if mode == 'virtual':
                check(guest.connected, 'network: slow guest stayed connected with virtual death',
                      guest.disconnect_reason or guest.error or '')
            if not guest.connected:
                report.append('- %s: the slow guest was DISCONNECTED: %s' % (r['label'], guest.disconnect_reason or guest.error))
                guest.connect()
                guest.rate = GUEST_RATE
                last_death['Guest'] = time.time()
    # unthrottled host: raw size of one death
    for mode, value in (('virtual', 1), ('vanilla', 0)):
        rc.cmd('scoreboard players set #virtual_death sa.cfg %d' % value)
        ensure_ready('Host2', 'network host (%s)' % mode)
        r = measure_death('Host2', 'Host1', 'host %s' % mode, window=5.0)
        r['mode'] = 'host-' + mode
        rows.append(r)
        expected['Host1'] += 1
        last_death['Host2'] = time.time()
    rc.cmd('scoreboard players set #virtual_death sa.cfg 1')
    return rows


def network_report(rows):
    def avg(mode, key):
        vals = [r[key] for r in rows if r['mode'] == mode and r[key] is not None]
        return sum(vals) / len(vals) if vals else None

    def fmt_kb(v):
        return '-' if v is None else ('%.2f MB' % (v / 1048576) if v >= 1048576 else '%.1f KB' % (v / 1024))

    def fmt_s(v):
        return '-' if v is None else '%.2f s' % v

    report.append('### Network cost of one death (view-distance %d)' % VIEW)
    report.append('')
    report.append('Guest reads at %.0f Mbit/s (slow tunnel); Host is unthrottled.' % (GUEST_RATE * 8 / 1e6))
    report.append('')
    report.append('| | vanilla death (respawn) | virtual death (mod) |')
    report.append('|---|---|---|')
    report.append('| data sent to the guest | %s | %s |' % (fmt_kb(avg('vanilla', 'bytes')), fmt_kb(avg('virtual', 'bytes'))))
    report.append('| chunk packets re-sent | %s | %s |' % (avg('vanilla', 'chunks'), avg('virtual', 'chunks')))
    report.append('| guest: killcam shown after | %s | %s |' % (fmt_s(avg('vanilla', 'camera_delay')), fmt_s(avg('virtual', 'camera_delay'))))
    report.append('| guest: worst chat/packet delay | %s | %s |' % (fmt_s(avg('vanilla', 'lat_max')), fmt_s(avg('virtual', 'lat_max'))))
    report.append('| host: data per death | %s | %s |' % (fmt_kb(avg('host-vanilla', 'bytes')), fmt_kb(avg('host-virtual', 'bytes'))))
    report.append('')
    for r in rows:
        report.append('- %s: %s, %d chunks, camera %s, delay before %s / max %s, respawns %d; biggest: %s' % (
            r['label'], fmt_kb(r['bytes']), r['chunks'], fmt_s(r['camera_delay']), fmt_s(r['lat_before']),
            fmt_s(r['lat_max']), r['respawns'], r['top']))
    report.append('')


# ---------------------------------------------------------------------------- full match
def full_match():
    what = 'match'
    last_death.clear()
    start_match(what)
    victims = ['Guest', 'Host2']

    # 1-4: single kills
    for i in range(4):
        v = victims[i % 2]
        ensure_ready(v, what)
        single_kill(v, 'Host1', '%s kill %d' % (what, expected['Host1'] + 1))

    # 5: two players hit the same victim in the same tick -> exactly one kill
    ensure_ready('Host2', what)
    ensure_ready('Guest', what)
    rc.cmd('function satest:double_hit')
    last_death['Host2'] = time.time()
    expected['Host1'] += 1
    check_killer('Host1', 'double hit')
    check(score('Guest', 'sa.kills') == 0, 'double hit: second attacker got no kill', score('Guest', 'sa.kills'))
    check_victim('Host2', 'Host1', 'double hit')

    # 6: trade kill in the same tick -> both get a kill, both die
    rc.cmd('function satest:trade')
    last_death['Guest'] = last_death['Host1'] = time.time()
    expected['Host1'] += 1
    expected['Guest'] += 1
    # both are dead, so their weapons are checked after the respawn (ensure_ready)
    check_victim('Guest', 'Host1', 'trade')
    check_victim('Host1', 'Guest', 'trade')
    for name in ('Host1', 'Guest'):
        ok = wait_until(lambda: score(name, 'sa.kills') == expected[name], 3)
        check(ok, 'trade: %s has %d kills' % (name, expected[name]), score(name, 'sa.kills'))
    ensure_ready('Host1', 'trade')
    ensure_ready('Guest', 'trade')

    # 7: guest disconnects during the death screen and comes back
    single_kill('Guest', 'Host1', 'rejoin')
    bots['Guest'].close()
    time.sleep(2.0)
    bots['Guest'].connect()
    bots['Guest'].rate = GUEST_RATE
    ok = wait_until(lambda: 'sa.ingame' in tags('Guest') and gamemode('Guest') == 2, 10)
    check(ok, 'rejoin: guest returned to the running match')
    check(camera_of('Guest') is None, 'rejoin: old killcam camera removed')
    last_death['Guest'] = time.time()
    ensure_ready('Guest', 'rejoin')

    # 8..18: until Host1 wins
    i = 0
    while expected['Host1'] < 17:
        v = victims[i % 2]
        ensure_ready(v, what)
        single_kill(v, 'Host1', '%s kill %d' % (what, expected['Host1'] + 1))
        i += 1
    v = victims[i % 2]
    ensure_ready(v, what)
    kill(v, 'Host1')
    expected['Host1'] += 1
    ok = wait_until(lambda: score('#state', 'sa.var') == 3, 3)
    check(ok, 'win: 18th kill ends the match')
    check('sa.winner' in tags('Host1'), 'win: Host1 is the winner')
    check(score('Host1', 'sa.kills') == 18, 'win: Host1 has 18 kills', score('Host1', 'sa.kills'))
    ok = wait_until(lambda: score('#state', 'sa.var') == 0, 15, 0.5)
    check(ok, 'end: everybody returned to the lobby')
    time.sleep(1)
    for name in bots:
        check('sa.ingame' not in tags(name), 'end: %s left the match' % name, tags(name))
        check(test('@a[name=%s,team=sa.lobby]' % name), 'end: %s is on the lobby team' % name)
        check(gamemode(name) == 2, 'end: %s is in adventure mode' % name, gamemode(name))
        check(health(name) == 100.0, 'end: %s has 100 HP' % name, health(name))
        check(not in_arena(pos(name)), 'end: %s is out of the arena' % name, pos(name))
    check(score('Host1', 'sa.wins') == 1, 'end: win recorded', score('Host1', 'sa.wins'))
    check(not test('@e[type=minecraft:armor_stand,tag=sa.cam]'), 'end: no killcam cameras left')


# ---------------------------------------------------------------------------- log scan
def scan_log():
    try:
        with open(os.path.join(SERVER_DIR, 'console.log'), encoding='utf-8', errors='replace') as f:
            lines = f.read().splitlines()
    except FileNotFoundError:
        return
    pats = {
        'function load errors': r'Failed to load function|Unknown function|Couldn\'t load|Whilst parsing command',
        'exceptions': r'Exception|\bERROR\]',
        'server lag (can\'t keep up)': r"Can't keep up",
        'movement kicks/warnings': r'moved too quickly|moved wrongly|disconnect\.flying',
        'lost connections': r'lost connection',
    }
    report.append('### Server log')
    report.append('')
    for label, pat in pats.items():
        hits = [ln for ln in lines if re.search(pat, ln)]
        report.append('- %s: %d' % (label, len(hits)))
        for ln in hits[:8]:
            report.append('  - `%s`' % ln[:300].replace('`', "'"))
    fn_errors = [ln for ln in lines if re.search(pats['function load errors'], ln)]
    check(not fn_errors, 'server: all datapack functions loaded', len(fn_errors))
    report.append('')


# ---------------------------------------------------------------------------- main
def main():
    global rc, probe_rc
    server = Server()
    server.start()
    rows = []
    try:
        server.wait_for(r'Done \(', 600)
        log('server started')
        rc = Rcon()
        probe_rc = Rcon()
        LEVEL.update(levels())
        check('file/sniper_arena' in rc.cmd('datapack list enabled'), 'server: Sniper Arena datapack enabled')
        check(wait_until(lambda: score('#markers', 'sa.var') == 1, 30, 1), 'server: lobby/pad/spawn markers exist')
        check(score('#virtual_death', 'sa.cfg') == 1, 'server: virtual death enabled by config')

        for name in ('Host1', 'Host2', 'Guest'):
            bot = Bot(name, view_distance=VIEW)
            bots[name] = bot
            t = time.time()
            bot.connect()
            log('%s joined in %.1f s' % (name, time.time() - t))
        time.sleep(5)
        for name in bots:
            pids[name] = score(name, 'sa.pid')
            check(pids[name] is not None, 'join: %s got a player id' % name, pids[name])
            check(test('@a[name=%s,team=sa.lobby,tag=sa.init]' % name), 'join: %s is in the lobby' % name)
        report.append('Initial join (all chunks): Guest received %.1f MB' % (bots['Guest'].bytes_between(0, time.time()) / 1048576))
        report.append('')

        start_match('network')
        last_death.clear()
        rows = run_measurements()
        network_report(rows)
        rc.cmd('execute as Host1 run function sniper_arena:admin/force_stop')
        check(wait_until(lambda: score('#state', 'sa.var') == 0, 5), 'force_stop: match aborted')
        time.sleep(4)

        full_match()
        for name, bot in bots.items():
            check(bot.connected, 'bots: %s is still connected at the end' % name, bot.disconnect_reason or bot.error or '')
    except Exception as exc:  # noqa: BLE001
        check(False, 'test run crashed', repr(exc))
        if rows and not any(line.startswith('### Network') for line in report):
            network_report(rows)
    finally:
        for bot in bots.values():
            bot.close()
        server.stop()
        scan_log()
        failed = [r for r in results if not r[0]]
        summary = ['## Sniper Arena server test: %d checks, %d failed' % (len(results), len(failed)), '']
        summary += report
        summary.append('### Checks')
        summary.append('')
        for ok, what, detail in results:
            summary.append('- %s %s%s' % ('PASS' if ok else '**FAIL**', what, (' -- %s' % detail) if detail != '' else ''))
        text = '\n'.join(summary)
        print(text, flush=True)
        path = os.environ.get('GITHUB_STEP_SUMMARY')
        if path:
            with open(path, 'a', encoding='utf-8') as f:
                f.write(text + '\n')
        sys.exit(1 if failed else 0)


if __name__ == '__main__':
    main()
