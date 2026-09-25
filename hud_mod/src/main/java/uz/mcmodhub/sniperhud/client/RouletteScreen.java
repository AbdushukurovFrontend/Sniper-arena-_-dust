package uz.mcmodhub.sniperhud.client;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import uz.mcmodhub.sniperhud.arena.RoulettePacket;

/**
 * "Arena tanlanmoqda" ruletka oynasi: nomzod arenalar chapdan o'ngga aylanadi va bittasida to'xtaydi
 * (CS2 case-opening uslubida). Faqat ko'rsatish uchun — tanlovni server allaqachon qilib bo'lgan,
 * bu ekran sichqonchani ochib qo'yadi lekin ESC bilan yopilmaydi (server bir necha soniyadan keyin
 * o'zi arenaga teleport qiladi, shu payt HUD kodi ekranni yopadi).
 */
public final class RouletteScreen extends Screen {
    private static final int TILE_W = 132;
    private static final int TILE_H = 160;
    private static final int SPINS = 3;
    /** Aylanish tugagach necha tick "tanlandi" holatida turadi (server arenaga tp qilguncha). */
    private static final int SETTLE_TICKS = 14;

    private final RoulettePacket data;
    private final long startMs;
    private final long spinMs;
    private long targetOffset;
    private boolean landed;

    private RouletteScreen(RoulettePacket data) {
        super(Component.literal("Arena tanlanmoqda"));
        this.data = data;
        this.startMs = Util.getMillis();
        int settleMs = SETTLE_TICKS * 50;
        this.spinMs = Math.max(1200L, data.windowTicks() * 50L - settleMs);
    }

    /** Server ruletka paketi yuborganda: oynani ochadi (avvalgisi bo'lsa almashtiradi). */
    public static void open(RoulettePacket data) {
        Minecraft mc = Minecraft.getInstance();
        mc.setScreen(new RouletteScreen(data));
    }

    /** Deathcam/spectate va boshqa hollarda ochiq qolib ketmasin: arenadan chiqqanda yopiladi. */
    public static void closeIfOpen() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen instanceof RouletteScreen) {
            mc.setScreen(null);
        }
    }

    @Override
    protected void init() {
        int count = Math.max(1, data.candidates().size());
        long fullDistance = (long) TILE_W * (SPINS * count + data.winnerIndex());
        this.targetOffset = fullDistance;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    /** Server arenaga teleport qilib bo'lguncha vaqt (+bufer) o'tsa, oyna o'zi yopiladi. */
    @Override
    public void tick() {
        long elapsed = Util.getMillis() - startMs;
        if (elapsed >= spinMs + SETTLE_TICKS * 50L + 500L && Minecraft.getInstance().screen == this) {
            Minecraft.getInstance().setScreen(null);
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        renderBackground(g);
        int count = Math.max(1, data.candidates().size());
        long elapsed = Util.getMillis() - startMs;
        float t = Mth.clamp(elapsed / (float) spinMs, 0.0F, 1.0F);
        float eased = 1.0F - (1.0F - t) * (1.0F - t) * (1.0F - t); // ease-out cubic
        landed = t >= 1.0F;
        double offset = targetOffset * eased;

        int centerX = this.width / 2;
        int stripY = this.height / 2 - TILE_H / 2;

        // Sarlavha
        Component title = landed
                ? Component.literal(data.candidates().get(data.winnerIndex()).name())
                : Component.literal("ARENA TANLANMOQDA...");
        int titleColor = landed ? data.candidates().get(data.winnerIndex()).color() | 0xFF000000 : 0xFFFFFFFF;
        g.drawCenteredString(this.font, title, centerX, stripY - 34, titleColor);

        // Strip fonini kesib chizish (tile balandligidan tashqarisi qopqora)
        g.enableScissor(0, stripY - 4, this.width, stripY + TILE_H + 4);
        g.fill(0, stripY - 4, this.width, stripY + TILE_H + 4, 0xCC000000);

        int startSlot = (int) Math.floor((offset - centerX - TILE_W) / TILE_W);
        int endSlot = (int) Math.ceil((offset + centerX + TILE_W) / TILE_W);
        for (int slot = startSlot; slot <= endSlot; slot++) {
            int idx = ((slot % count) + count) % count;
            int x = (int) (centerX + slot * (long) TILE_W - offset) - TILE_W / 2;
            boolean isWinnerSlot = landed && slot == targetOffset / TILE_W;
            drawTile(g, data.candidates().get(idx), x, stripY, isWinnerSlot);
        }
        g.disableScissor();

        // Markaziy ko'rsatkich
        int pointerColor = 0xFFFFD54A;
        g.fill(centerX - 2, stripY - 10, centerX + 2, stripY + TILE_H + 10, pointerColor);
        g.fill(centerX - 8, stripY - 14, centerX + 8, stripY - 10, pointerColor);
        g.fill(centerX - 8, stripY + TILE_H + 10, centerX + 8, stripY + TILE_H + 14, pointerColor);

        if (landed) {
            g.drawCenteredString(this.font, Component.literal("Boshlanmoqda..."), centerX, stripY + TILE_H + 26, 0xFFAAAAAA);
        }
    }

    private void drawTile(GuiGraphics g, RoulettePacket.Candidate candidate, int x, int y, boolean winner) {
        int color = candidate.color() | 0xFF000000;
        g.fill(x, y, x + TILE_W - 4, y + TILE_H, winner ? (color & 0x66FFFFFF) : 0x99202020);
        int borderColor = winner ? 0xFFFFD54A : (color & 0x00FFFFFF) | 0x88000000;
        g.fill(x, y, x + TILE_W - 4, y + 3, borderColor);
        g.fill(x, y + TILE_H - 3, x + TILE_W - 4, y + TILE_H, borderColor);
        g.fill(x, y, x + 3, y + TILE_H, borderColor);
        g.fill(x + TILE_W - 7, y, x + TILE_W - 4, y + TILE_H, borderColor);

        g.pose().pushPose();
        g.pose().translate(x + TILE_W / 2.0F - 18, y + TILE_H / 2.0F - 40, 0.0F);
        g.pose().scale(2.2F, 2.2F, 1.0F);
        g.renderItem(candidate.icon(), 0, 0);
        g.pose().popPose();

        int nameW = this.font.width(candidate.name());
        float scale = Math.min(1.0F, (TILE_W - 16.0F) / Math.max(1, nameW));
        g.pose().pushPose();
        g.pose().translate(x + (TILE_W - 4) / 2.0F, y + TILE_H - 28.0F, 0.0F);
        g.pose().scale(scale, scale, 1.0F);
        g.drawCenteredString(this.font, candidate.name(), 0, 0, 0xFFFFFFFF);
        g.pose().popPose();
    }
}
