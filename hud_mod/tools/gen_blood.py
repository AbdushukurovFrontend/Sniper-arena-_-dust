"""Generates the blood textures of the HUD mod (procedural, no external images).

python3 hud_mod/tools/gen_blood.py  ->  src/main/resources/assets/sniper_arena_hud/textures/gui/
"""
import os

import numpy as np
from PIL import Image, ImageFilter

OUT = os.path.join(os.path.dirname(__file__), '..', 'src', 'main', 'resources', 'assets', 'sniper_arena_hud',
                   'textures', 'gui')
SS = 2  # supersampling for smooth edges


def value_noise(rng, n, cells):
    grid = rng.random((cells + 1, cells + 1)).astype(np.float32)
    img = Image.fromarray((grid * 255).astype(np.uint8)).resize((n, n), Image.BICUBIC)
    return np.asarray(img, dtype=np.float32) / 255.0


def fbm(rng, n, octaves=((4, 0.5), (8, 0.3), (16, 0.2))):
    out = np.zeros((n, n), np.float32)
    for cells, w in octaves:
        out += w * value_noise(rng, n, cells)
    return out


def blur(a, radius):
    img = Image.fromarray(np.clip(a * 255, 0, 255).astype(np.uint8))
    return np.asarray(img.filter(ImageFilter.GaussianBlur(radius)), np.float32) / 255.0


def smoothstep(e0, e1, x):
    t = np.clip((x - e0) / (e1 - e0), 0, 1)
    return t * t * (3 - 2 * t)


class Field:
    """Metaball field: sum of r^2/d^2 terms, the shape is where the field exceeds 1."""

    def __init__(self, n):
        self.n = n
        self.yy, self.xx = np.mgrid[0:n, 0:n].astype(np.float32)
        self.f = np.zeros((n, n), np.float32)

    def ball(self, x, y, r, sx=1.0, sy=1.0, angle=0.0):
        reach = r * max(sx, sy) * 5 + 2
        x0, x1 = int(max(0, x - reach)), int(min(self.n, x + reach + 1))
        y0, y1 = int(max(0, y - reach)), int(min(self.n, y + reach + 1))
        if x0 >= x1 or y0 >= y1:
            return
        dx, dy = self.xx[y0:y1, x0:x1] - x, self.yy[y0:y1, x0:x1] - y
        if angle:
            ca, sa = np.cos(angle), np.sin(angle)
            dx, dy = dx * ca + dy * sa, -dx * sa + dy * ca
        d2 = (dx / sx) ** 2 + (dy / sy) ** 2 + 1e-3
        self.f[y0:y1, x0:x1] += r * r / d2

    def drip(self, rng, x, y, length, w):
        steps = int(length / (w * 0.35)) + 2
        phase = rng.uniform(0, 6.3)
        for i in range(steps):
            t = i / (steps - 1)
            self.ball(x + np.sin(t * 5 + phase) * w * 0.6, y + t * length, w * (1 - 0.45 * t))
        self.ball(x + np.sin(5 + phase) * w * 0.6, y + length + w * 0.4, w * 1.05)

    def shape(self, rng, wobble=0.35):
        noise = (fbm(rng, self.n, ((8, 0.5), (20, 0.3), (48, 0.2))) - 0.5) * wobble
        return smoothstep(0.92, 1.08, self.f * (1 + noise))


def colorize(mask, rng, n, core=(62, 0, 1), mid=(112, 2, 4), edge=(158, 10, 10), max_alpha=0.95):
    thick = np.clip(blur(mask, n / 45) * mask, 0, 1)
    tone = np.clip(thick * 1.35 + (fbm(rng, n) - 0.5) * 0.3, 0, 1)
    rgb = np.empty((n, n, 3), np.float32)
    for c in range(3):
        a = edge[c] + (mid[c] - edge[c]) * np.clip(tone * 2, 0, 1)
        rgb[..., c] = a + (core[c] - a) * np.clip(tone * 2 - 1, 0, 1)
    # darker dried rim right at the border
    rim = np.clip(mask - blur(mask, n / 160) * 1.15, 0, 1) * mask
    rgb *= (1 - 0.35 * rim)[..., None]
    # faint wet shine
    shine = np.clip((fbm(rng, n, ((12, 0.6), (28, 0.4))) - 0.8) * 6, 0, 1) * np.clip(thick * 2 - 0.4, 0, 1)
    rgb[..., 0] += shine * 35
    rgb[..., 1] += shine * 8
    rgb[..., 2] += shine * 8
    alpha = mask * np.clip(0.72 + 0.35 * thick + (fbm(rng, n) - 0.5) * 0.12, 0, 1) * max_alpha
    return np.dstack([np.clip(rgb, 0, 255), np.clip(alpha * 255, 0, 255)]).astype(np.uint8)


def splat(seed, size=256):
    rng = np.random.default_rng(seed)
    n = size * SS
    c = n / 2
    fld = Field(n)
    # body: a cluster of lobes
    for _ in range(rng.integers(6, 10)):
        a = rng.uniform(0, 2 * np.pi)
        d = rng.uniform(0, 0.13) * n
        fld.ball(c + np.cos(a) * d, c + np.sin(a) * d, rng.uniform(0.05, 0.09) * n)
    # thrown blood: trails of shrinking drops flying outwards
    for _ in range(rng.integers(5, 9)):
        a = rng.uniform(0, 2 * np.pi)
        d = rng.uniform(0.14, 0.2) * n
        r = rng.uniform(0.035, 0.055) * n
        for _ in range(rng.integers(4, 8)):
            fld.ball(c + np.cos(a) * d, c + np.sin(a) * d, r, sx=2.2, sy=0.75, angle=a)
            d += r * rng.uniform(1.6, 2.6)
            r *= rng.uniform(0.62, 0.8)
            a += rng.uniform(-0.08, 0.08)
    # loose droplets
    for _ in range(rng.integers(18, 30)):
        a = rng.uniform(0, 2 * np.pi)
        d = rng.uniform(0.2, 0.46) * n
        fld.ball(c + np.cos(a) * d, c + np.sin(a) * d, rng.uniform(0.004, 0.012) * n * (1.3 - d / n * 1.6))
    # drips running down
    for _ in range(rng.integers(2, 5)):
        fld.drip(rng, c + rng.uniform(-0.12, 0.12) * n, c + rng.uniform(0.04, 0.1) * n,
                 rng.uniform(0.15, 0.34) * n, rng.uniform(0.016, 0.026) * n)
    mask = fld.shape(rng)
    rgba = colorize(mask, rng, n)
    return Image.fromarray(rgba, 'RGBA').resize((size, size), Image.LANCZOS)


def spray(seed, size=512):
    """Blood thrown across the screen from one point: an impact spot, flying drops with tails, fine mist.

    The spray flies to the right (+x) from the left side; the game rotates it randomly.
    """
    rng = np.random.default_rng(seed)
    n = size * SS
    fld = Field(n)
    ox, oy = 0.1 * n, 0.5 * n
    spread = rng.uniform(0.3, 0.55)

    # impact spot: a few merged drops and short streaks
    for _ in range(rng.integers(4, 8)):
        a = rng.normal(0, spread * 0.8)
        d = rng.uniform(0, 0.06) * n
        fld.ball(ox + np.cos(a) * d, oy + np.sin(a) * d, rng.uniform(0.012, 0.03) * n, sx=1.6, sy=1.0, angle=a)

    def drop(a, d, r):
        x, y = ox + np.cos(a) * d, oy + np.sin(a) * d
        stretch = 1.0 + d / n * rng.uniform(1.5, 3.5)
        fld.ball(x, y, r, sx=stretch, sy=1.0, angle=a)
        # the narrow tail points the way the drop was flying (as real stains do)
        tx, ty, tr = x, y, r * 0.7
        for _ in range(rng.integers(1, 4)):
            tx += np.cos(a) * tr * 2.2 * stretch
            ty += np.sin(a) * tr * 2.2 * stretch
            tr *= 0.62
            fld.ball(tx, ty, tr, sx=1.6, sy=1.0, angle=a)

    # big and medium drops (fewer, flying far)
    for _ in range(rng.integers(14, 24)):
        drop(rng.normal(0, spread), rng.uniform(0.12, 0.85) * n, rng.uniform(0.007, 0.02) * n)
    # small drops
    for _ in range(rng.integers(60, 110)):
        drop(rng.normal(0, spread * 1.1), rng.uniform(0.06, 0.9) * n, rng.uniform(0.003, 0.007) * n)
    # long thin streaks (fast drops)
    for _ in range(rng.integers(3, 7)):
        a = rng.normal(0, spread * 0.8)
        d = rng.uniform(0.05, 0.12) * n
        r = rng.uniform(0.006, 0.01) * n
        for _ in range(rng.integers(8, 16)):
            fld.ball(ox + np.cos(a) * d, oy + np.sin(a) * d, r, sx=2.5, sy=1.0, angle=a)
            d += r * 3.5
            r *= 0.93
    mask = fld.shape(rng, 0.25)
    # fine mist around the spray
    mist = np.zeros((n, n), np.float32)
    yy, xx = fld.yy, fld.xx
    for _ in range(rng.integers(150, 260)):
        a = rng.normal(0, spread * 1.3)
        d = rng.uniform(0.03, 0.75) * n
        x, y = ox + np.cos(a) * d, oy + np.sin(a) * d
        rad = rng.uniform(0.0012, 0.003) * n
        x0, x1 = int(max(0, x - rad - 2)), int(min(n, x + rad + 3))
        y0, y1 = int(max(0, y - rad - 2)), int(min(n, y + rad + 3))
        if x0 < x1 and y0 < y1:
            sub = np.hypot(xx[y0:y1, x0:x1] - x, yy[y0:y1, x0:x1] - y)
            mist[y0:y1, x0:x1] = np.maximum(mist[y0:y1, x0:x1], np.clip(rad + 0.5 - sub, 0, 1) * 0.8)
    mask = np.maximum(mask, mist)
    rgba = colorize(mask, rng, n, core=(70, 0, 2), mid=(120, 2, 4), edge=(165, 10, 10))
    return Image.fromarray(rgba, 'RGBA').resize((size, size), Image.LANCZOS)


def vignette(seed, size=512):
    """Blood creeping in from the screen edges (stretched over the whole screen in game)."""
    rng = np.random.default_rng(seed)
    n = size * SS
    yy, xx = np.mgrid[0:n, 0:n].astype(np.float32)
    ex, ey = (xx - n / 2) / (n / 2), (yy - n / 2) / (n / 2)
    d = (np.abs(ex) ** 3.2 + np.abs(ey) ** 3.2) ** (1 / 3.2)
    edge = 0.8 + (fbm(rng, n, ((5, 0.45), (12, 0.35), (30, 0.2))) - 0.5) * 0.3
    body = smoothstep(edge - 0.015, edge + 0.015, d)
    # organic drips from the top
    fld = Field(n)
    for _ in range(7):
        x0 = rng.uniform(0.1, 0.9) * n
        top = (1 - 0.8) * n / 2
        fld.drip(rng, x0, top * rng.uniform(0.4, 1.0), rng.uniform(0.05, 0.16) * n, rng.uniform(0.006, 0.011) * n)
    body = np.maximum(body, fld.shape(rng, 0.2))
    rgba = colorize(body, rng, n, core=(68, 0, 2), mid=(118, 2, 4), edge=(165, 8, 8), max_alpha=0.97).astype(np.float32)
    # soft red glow further in (makes the edge read as "hurt" even when the blood is thin)
    glow = smoothstep(0.45, 0.95, d) * (1 - body) * 0.28
    a = rgba[..., 3] / 255.0
    out_a = a + glow * (1 - a)
    for ci, col in enumerate((120, 0, 0)):
        rgba[..., ci] = (rgba[..., ci] * a + col * glow * (1 - a)) / np.maximum(out_a, 1e-4)
    rgba[..., 3] = out_a * 255
    return Image.fromarray(np.clip(rgba, 0, 255).astype(np.uint8), 'RGBA').resize((size, size), Image.LANCZOS)


def main():
    os.makedirs(OUT, exist_ok=True)
    for i, seed in enumerate((11, 37)):
        splat(seed).save(os.path.join(OUT, 'blood_splat_%d.png' % i), optimize=True)
    for i, seed in enumerate((101, 202, 303, 404, 505, 606)):
        spray(seed).save(os.path.join(OUT, 'blood_spray_%d.png' % i), optimize=True)
    vignette(7).save(os.path.join(OUT, 'blood_vignette.png'), optimize=True)


if __name__ == '__main__':
    main()
