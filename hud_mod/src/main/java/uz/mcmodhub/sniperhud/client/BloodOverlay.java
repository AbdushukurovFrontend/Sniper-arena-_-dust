package uz.mcmodhub.sniperhud.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.event.TickEvent;
import uz.mcmodhub.sniperhud.SniperArenaHud;

/**
 * Ekrandagi qon: o'q tekkanda ekranning turli joylariga qon sachraydi (zarar qancha katta bo'lsa, shuncha ko'p),
 * jon kamaygan sari ekran chetlaridan qon bosib keladi, jon juda kam bo'lsa ekranda qon qoladi va yurak
 * urishidek pulslanadi. Faqat Sniper Arena ichida ishlaydi. Teksturalar: hud_mod/tools/gen_blood.py.
 */
final class BloodOverlay {
    private static final ResourceLocation[] SPRAYS = new ResourceLocation[6];
    private static final ResourceLocation[] BLOTCHES = new ResourceLocation[2];
    private static final ResourceLocation VIGNETTE = texture("blood_vignette");
    private static final int SPRAY_TEX = 512;
    private static final int BLOTCH_TEX = 256;
    private static final int VIGNETTE_TEX = 512;
    private static final int MAX_SPLATS = 18;

    static {
        for (int i = 0; i < SPRAYS.length; i++) {
            SPRAYS[i] = texture("blood_spray_" + i);
        }
        for (int i = 0; i < BLOTCHES.length; i++) {
            BLOTCHES[i] = texture("blood_splat_" + i);
        }
    }

    private static final List<Splat> SPLATS = new ArrayList<>();
    private static final RandomSource RANDOM = RandomSource.create();
    private static float lastHealth = -1;
    /** Zarba paytidagi qizil chaqnash (1 dan 0 gacha so'nadi). */
    private static float flash;

    private BloodOverlay() {
    }

    private static ResourceLocation texture(String name) {
        return new ResourceLocation(SniperArenaHud.MODID, "textures/gui/" + name + ".png");
    }

    private static final class Splat {
        final ResourceLocation texture;
        final int textureSize;
        final float x;
        final float y;
        final float size;
        final float rotation;
        final float maxAlpha;
        boolean persistent;
        int life;
        int age;

        Splat(boolean blotch, boolean persistent, float damage) {
            this.texture = blotch ? BLOTCHES[RANDOM.nextInt(BLOTCHES.length)] : SPRAYS[RANDOM.nextInt(SPRAYS.length)];
            this.textureSize = blotch ? BLOTCH_TEX : SPRAY_TEX;
            // ekranning istalgan joyiga, faqat markazdagi nishon (crosshair) ochiq qoladi
            float angle = RANDOM.nextFloat() * Mth.TWO_PI;
            float dist = 0.13F + RANDOM.nextFloat() * 0.33F;
            this.x = 0.5F + Mth.cos(angle) * dist;
            this.y = 0.5F + Mth.sin(angle) * dist * 0.85F;
            float base = blotch ? 0.26F + RANDOM.nextFloat() * 0.14F : 0.36F + RANDOM.nextFloat() * 0.26F;
            this.size = base * (0.85F + Math.min(damage, 0.5F));
            this.rotation = RANDOM.nextFloat() * 360.0F;
            this.persistent = persistent;
            this.maxAlpha = persistent ? 0.85F : Math.min(0.95F, 0.7F + damage);
            this.life = 36 + RANDOM.nextInt(24);
        }
    }

    static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            SPLATS.clear();
            flash = 0;
            lastHealth = -1;
            return;
        }
        if (mc.isPaused()) {
            return;
        }
        float health = mc.player.getHealth();
        float max = Math.max(1.0F, mc.player.getMaxHealth());
        if (!ClientHud.inArena() || mc.player.isSpectator() || !mc.player.isAlive()) {
            SPLATS.clear();
            flash = 0;
            lastHealth = health;
            return;
        }
        float ratio = health / max;
        if (lastHealth >= 0 && health < lastHealth - 0.05F) {
            onHit((lastHealth - health) / max, ratio);
        } else if (lastHealth >= 0 && health > lastHealth + 0.05F) {
            onHeal((health - lastHealth) / max);
        }
        lastHealth = health;

        Iterator<Splat> it = SPLATS.iterator();
        while (it.hasNext()) {
            Splat splat = it.next();
            splat.age++;
            if (!splat.persistent && splat.age >= splat.life) {
                it.remove();
            }
        }
        // jon juda kam: ekranda doimiy qon (jon tiklansa asta-sekin yo'qoladi)
        int wanted = ratio < 0.12F ? 4 : ratio < 0.22F ? 3 : ratio < 0.32F ? 2 : 0;
        int persistent = 0;
        for (Splat splat : SPLATS) {
            if (splat.persistent) {
                persistent++;
            }
        }
        for (; persistent < wanted; persistent++) {
            SPLATS.add(new Splat(persistent % 2 == 1, true, 0.3F));
        }
        for (Splat splat : SPLATS) {
            if (persistent <= wanted) {
                break;
            }
            if (splat.persistent) {
                splat.persistent = false;
                splat.life = splat.age + 30;
                persistent--;
            }
        }
        flash *= 0.86F;
    }

    private static void onHit(float damage, float ratio) {
        // har zarbada ekranning 2-5 ta turli joyiga sachraydi, katta zarbada qo'shimcha qon dog'i
        int sprays = Mth.clamp(2 + (int) (damage * 10.0F), 2, 5) + (ratio < 0.35F ? 1 : 0);
        for (int i = 0; i < sprays; i++) {
            add(new Splat(false, false, damage), ratio);
        }
        if (damage >= 0.3F) {
            add(new Splat(true, false, damage), ratio);
        }
        while (SPLATS.size() > MAX_SPLATS) {
            SPLATS.remove(firstFading());
        }
        flash = Math.min(1.0F, flash + 0.35F + damage * 1.5F);
    }

    /** Jon to'lgan sari qon tezroq yo'qoladi (to'liq tiklansa yarim soniyada). */
    private static void onHeal(float heal) {
        int faster = (int) (heal * 200.0F);
        for (Splat splat : SPLATS) {
            if (!splat.persistent) {
                splat.life = Math.max(splat.age + 10, splat.life - faster);
            }
        }
    }

    private static void add(Splat splat, float ratio) {
        splat.life += (int) ((1.0F - ratio) * 40.0F); // jon qancha kam bo'lsa, qon shuncha uzoq turadi
        SPLATS.add(splat);
    }

    private static int firstFading() {
        for (int i = 0; i < SPLATS.size(); i++) {
            if (!SPLATS.get(i).persistent) {
                return i;
            }
        }
        return 0;
    }

    static void render(ForgeGui gui, GuiGraphics g, float partialTick, int width, int height) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui || !ClientHud.inArena() || mc.player.isSpectator()) {
            return;
        }
        float ratio = mc.player.getHealth() / Math.max(1.0F, mc.player.getMaxHealth());
        float low = Mth.clamp((0.6F - ratio) / 0.6F, 0.0F, 1.0F);
        if (low <= 0 && flash < 0.01F && SPLATS.isEmpty()) {
            return;
        }
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // 1) chetlardan bosib keladigan qon: jon kamaygan sari quyuqroq va ichkariroq
        float pulse = 1.0F;
        if (ratio < 0.3F) {
            float time = (Util.getMillis() % 100000L) / 1000.0F;
            pulse = 0.8F + 0.2F * Mth.sin(time * Mth.TWO_PI * 1.1F);
        }
        float alpha = Math.max(low * 0.92F * pulse, flash * 0.55F);
        if (alpha > 0.01F) {
            float spread = Math.max(low, flash * 0.6F);
            float scale = 1.0F + (1.0F - spread) * 0.35F;
            int w = (int) (width * scale);
            int h = (int) (height * scale);
            g.setColor(1.0F, 1.0F, 1.0F, alpha);
            g.blit(VIGNETTE, (width - w) / 2, (height - h) / 2, w, h, 0, 0, VIGNETTE_TEX, VIGNETTE_TEX,
                    VIGNETTE_TEX, VIGNETTE_TEX);
        }

        // 2) sachragan qon: zarbada "urilib" paydo bo'ladi, sekin pastga oqadi va so'nadi
        for (Splat splat : SPLATS) {
            float age = splat.age + partialTick;
            float fade = splat.persistent ? 1.0F
                    : 1.0F - Mth.clamp((age - splat.life * 0.45F) / (splat.life * 0.55F), 0.0F, 1.0F);
            // jon qancha ko'p bo'lsa, qon shuncha och
            float a = splat.maxAlpha * fade * Math.min(1.0F, age / 1.5F) * (0.5F + 0.5F * (1.0F - ratio));
            if (a <= 0.01F) {
                continue;
            }
            float pop = 0.88F + 0.12F * Math.min(1.0F, age / 3.0F);
            float slide = Math.min(age, 120.0F) * 0.0005F;
            int size = (int) (splat.size * height * pop);
            PoseStack pose = g.pose();
            pose.pushPose();
            pose.translate(splat.x * width, (splat.y + slide) * height, 0.0F);
            pose.mulPose(Axis.ZP.rotationDegrees(splat.rotation));
            g.setColor(1.0F, 1.0F, 1.0F, a);
            g.blit(splat.texture, -size / 2, -size / 2, size, size, 0, 0, splat.textureSize, splat.textureSize,
                    splat.textureSize, splat.textureSize);
            pose.popPose();
        }
        g.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
    }
}
