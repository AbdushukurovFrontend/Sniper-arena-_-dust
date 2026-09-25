package uz.mcmodhub.sniperhud.client;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.ToIntFunction;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;

/** Sniper Arena ekrani: vanilla pastki qismni yashiradi va CS2 uslubidagi elementlarni chizadi. */
public final class ClientHud {
    /** Arenada (lobby + o'yin) doim yashiriladi: yurak, ovqat, tajriba, hotbar... */
    private static final Set<ResourceLocation> HIDE_IN_ARENA = Set.of(
            VanillaGuiOverlay.HOTBAR.id(),
            VanillaGuiOverlay.PLAYER_HEALTH.id(),
            VanillaGuiOverlay.FOOD_LEVEL.id(),
            VanillaGuiOverlay.EXPERIENCE_BAR.id(),
            VanillaGuiOverlay.ARMOR_LEVEL.id(),
            VanillaGuiOverlay.AIR_LEVEL.id(),
            VanillaGuiOverlay.MOUNT_HEALTH.id(),
            VanillaGuiOverlay.JUMP_BAR.id(),
            VanillaGuiOverlay.ITEM_NAME.id(),
            VanillaGuiOverlay.POTION_ICONS.id());
    /** O'yin paytida: tepadagi bossbar va o'ngdagi sidebar o'rniga bizning panel chiqadi. */
    private static final Set<ResourceLocation> HIDE_IN_MATCH = Set.of(
            VanillaGuiOverlay.BOSS_EVENT_PROGRESS.id(),
            VanillaGuiOverlay.SCOREBOARD.id());

    private ClientHud() {
    }

    public static void init(IEventBus modBus) {
        modBus.addListener(ClientHud::registerOverlays);
        MinecraftForge.EVENT_BUS.addListener(ClientHud::onRenderOverlay);
    }

    private static void registerOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("arena_hud", ClientHud::render);
    }

    private static String teamName() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return null;
        }
        Team team = mc.player.getTeam();
        return team == null ? null : team.getName();
    }

    private static boolean inArena() {
        String team = teamName();
        return team != null && team.startsWith("sa.");
    }

    private static boolean inMatch() {
        return "sa.game".equals(teamName());
    }

    private static void onRenderOverlay(RenderGuiOverlayEvent.Pre event) {
        if (!inArena()) {
            return;
        }
        ResourceLocation id = event.getOverlay().id();
        if (HIDE_IN_ARENA.contains(id) || (inMatch() && HIDE_IN_MATCH.contains(id))) {
            event.setCanceled(true);
        }
    }

    private static void render(ForgeGui gui, GuiGraphics g, float partialTick, int width, int height) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || mc.options.hideGui || !inArena()) {
            return;
        }
        renderWeapons(mc, g, width, height);
        renderHealth(mc, g, height);
        if (inMatch()) {
            renderTopBar(mc, g, width);
        }
        renderKillFeed(mc, g, width);
    }

    // ---------------------------------------------------------------- o'ng past: qurollar ro'yxati
    private static void renderWeapons(Minecraft mc, GuiGraphics g, int width, int height) {
        Inventory inv = mc.player.getInventory();
        List<Integer> slots = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            if (!inv.getItem(i).isEmpty()) {
                slots.add(i);
            }
        }
        if (slots.isEmpty()) {
            return;
        }
        Font font = mc.font;
        int boxW = 120;
        int rowH = 30;
        int x = width - boxW - 6;
        // TaCZ o'q hisoblagichi (eng pastda) ustida turadi
        int y = height - 54 - slots.size() * rowH;
        for (int slot : slots) {
            ItemStack stack = inv.getItem(slot);
            boolean selected = inv.selected == slot;
            g.fill(x, y, x + boxW, y + rowH - 3, selected ? 0x99000000 : 0x55000000);
            if (selected) {
                g.fill(x + boxW - 2, y, x + boxW, y + rowH - 3, 0xFFFF4444);
            }
            g.drawString(font, String.valueOf(slot + 1), x + 4, y + 4, selected ? 0xFFFFFFFF : 0xFF777777, false);

            g.pose().pushPose();
            g.pose().translate(x + boxW - 30, y + 1, 0);
            g.pose().scale(1.5f, 1.5f, 1f);
            g.renderItem(stack, 0, 0);
            g.pose().popPose();

            if (selected) {
                Component name = stack.getHoverName();
                int maxW = boxW - 42;
                int nameW = font.width(name);
                float scale = nameW > maxW ? (float) maxW / nameW : 1f;
                g.pose().pushPose();
                g.pose().translate(x + 14, y + 16, 0);
                g.pose().scale(scale, scale, 1f);
                g.drawString(font, name, 0, 0, 0xFFFFFFFF, true);
                g.pose().popPose();
            }
            y += rowH;
        }
    }

    // ---------------------------------------------------------------- chap past: jon (CS2 kabi)
    private static void renderHealth(Minecraft mc, GuiGraphics g, int height) {
        if (mc.player.isSpectator()) {
            return;
        }
        float hp = mc.player.getHealth();
        float max = Math.max(1f, mc.player.getMaxHealth());
        boolean low = hp <= max * 0.3f;
        int color = low ? 0xFFFF5555 : 0xFFFFFFFF;
        int x = 8;
        int y = height - 24;
        g.fill(x - 3, y - 5, x + 96, y + 15, 0x66000000);
        g.drawString(mc.font, "✚", x + 1, y + 1, color, false);
        g.pose().pushPose();
        g.pose().translate(x + 13, y - 1, 0);
        g.pose().scale(1.5f, 1.5f, 1f);
        g.drawString(mc.font, String.valueOf(Mth.ceil(hp)), 0, 0, color, true);
        g.pose().popPose();
        int barX = x + 42;
        int barW = 48;
        g.fill(barX, y + 3, barX + barW, y + 7, 0xFF333333);
        g.fill(barX, y + 3, barX + (int) (barW * Mth.clamp(hp / max, 0f, 1f)), y + 7, color);
    }

    // ---------------------------------------------------------------- tepa o'rta: vaqt + o'yinchilar
    private static void renderTopBar(Minecraft mc, GuiGraphics g, int width) {
        ClientPacketListener connection = mc.getConnection();
        if (connection == null) {
            return;
        }
        Scoreboard scoreboard = mc.level.getScoreboard();
        Objective kills = scoreboard.getObjective("sa.kills");
        ToIntFunction<String> score = name -> kills != null && scoreboard.hasPlayerScore(name, kills)
                ? scoreboard.getOrCreatePlayerScore(name, kills).getScore() : 0;

        List<PlayerInfo> players = new ArrayList<>();
        for (PlayerInfo info : connection.getOnlinePlayers()) {
            PlayerTeam team = info.getTeam();
            if (team != null && "sa.game".equals(team.getName())) {
                players.add(info);
            }
        }
        players.sort(Comparator.comparingInt((PlayerInfo p) -> score.applyAsInt(p.getProfile().getName())).reversed());

        int time = score.applyAsInt("#time");
        int target = score.applyAsInt("#target");
        String timeText = time > 0 ? String.format("%d:%02d", time / 60, time % 60) : "--:--";

        int cx = width / 2;
        int top = 4;
        int face = 18;
        int gap = 4;
        int centerW = 48;
        g.fill(cx - centerW / 2, top, cx + centerW / 2, top + 28, 0xAA000000);
        g.drawCenteredString(mc.font, timeText, cx, top + 5, 0xFFFFFFFF);
        if (target > 0) {
            g.drawCenteredString(mc.font, target + " kill", cx, top + 16, 0xFFFFAA00);
        }

        String me = mc.player.getGameProfile().getName();
        int left = 0;
        int right = 0;
        for (int i = 0; i < players.size(); i++) {
            PlayerInfo info = players.get(i);
            String name = info.getProfile().getName();
            boolean onLeft = i % 2 == 0;
            int x = onLeft
                    ? cx - centerW / 2 - gap - (++left) * (face + gap) + gap
                    : cx + centerW / 2 + gap + (right++) * (face + gap);
            int border = name.equals(me) ? 0xFFFFD700 : (i == 0 ? 0xFFFF4444 : 0xFF444444);
            g.fill(x - 1, top - 1, x + face + 1, top + face + 1, border);
            PlayerFaceRenderer.draw(g, info.getSkinLocation(), x, top, face);
            g.drawCenteredString(mc.font, String.valueOf(score.applyAsInt(name)), x + face / 2, top + face + 3, 0xFFFFFFFF);
        }
    }

    // ---------------------------------------------------------------- o'ng tepa: kill feed
    private static void renderKillFeed(Minecraft mc, GuiGraphics g, int width) {
        long now = Util.getMillis();
        KillFeed.ENTRIES.removeIf(e -> now - e.time() > KillFeed.LIFE_MS);
        if (KillFeed.ENTRIES.isEmpty()) {
            return;
        }
        Font font = mc.font;
        String me = mc.player.getGameProfile().getName();
        int y = 4;
        for (KillFeed.Entry e : KillFeed.ENTRIES) {
            int killerW = font.width(e.killer());
            int victimW = font.width(e.victim());
            int w = 4 + killerW + 4 + 16 + 4 + victimW + 4;
            int x = width - w - 4;
            g.fill(x, y, x + w, y + 18, 0x99000000);
            boolean mine = e.killer().equals(me);
            if (mine || e.victim().equals(me)) {
                int c = mine ? 0xFFFF3333 : 0xFF888888;
                g.fill(x, y, x + w, y + 1, c);
                g.fill(x, y + 17, x + w, y + 18, c);
                g.fill(x, y, x + 1, y + 18, c);
                g.fill(x + w - 1, y, x + w, y + 18, c);
            }
            g.drawString(font, e.killer(), x + 4, y + 5, 0xFF9FD4FF, false);
            g.renderItem(e.weapon(), x + 4 + killerW + 4, y + 1);
            g.drawString(font, e.victim(), x + 4 + killerW + 4 + 16 + 4, y + 5, 0xFFFFFFFF, false);
            y += 20;
        }
    }
}
