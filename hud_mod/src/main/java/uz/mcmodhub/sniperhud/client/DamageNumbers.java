package uz.mcmodhub.sniperhud.client;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import org.joml.Matrix4f;
import uz.mcmodhub.sniperhud.DamagePacket;

/**
 * Roblox'dagidek zarar raqamlari: o'q tekkan o'yinchining boshi ustida qancha jon ketgani chiqadi, tepaga ko'tarilib
 * so'nadi. Boshga tekkan o'q — sariq, o'ldirgan zarba — qizil va kattaroq. Avtomatdan ketma-ket tekkan o'qlar bitta
 * raqamga qo'shilib boradi. Raqamni faqat otgan o'yinchi ko'radi (server shunga yuboradi).
 */
public final class DamageNumbers {
    private static final int LIFE = 26;
    /** Shu vaqt (tick) ichida yana tegsa, raqam yangisi bilan qo'shiladi. */
    private static final int MERGE = 10;
    private static final int MAX = 24;
    private static final List<Entry> ENTRIES = new ArrayList<>();
    private static final RandomSource RANDOM = RandomSource.create();

    private DamageNumbers() {
    }

    private static final class Entry {
        final int entityId;
        Vec3 pos;
        float amount;
        boolean head;
        boolean kill;
        int age;

        Entry(int entityId, Vec3 pos) {
            this.entityId = entityId;
            this.pos = pos;
        }
    }

    public static void add(int entityId, float amount, byte flags) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }
        Entity entity = mc.level.getEntity(entityId);
        if (entity == null) {
            return;
        }
        boolean head = (flags & DamagePacket.HEAD) != 0;
        boolean kill = (flags & DamagePacket.KILL) != 0;
        Vec3 pos = new Vec3(entity.getX() + (RANDOM.nextFloat() - 0.5F) * 0.5F,
                entity.getY() + entity.getBbHeight() + 0.3F,
                entity.getZ() + (RANDOM.nextFloat() - 0.5F) * 0.5F);
        for (Entry entry : ENTRIES) {
            if (entry.entityId == entityId && entry.age < MERGE && !entry.kill) {
                entry.amount += amount;
                entry.head |= head;
                entry.kill = kill;
                entry.age = 0;
                entry.pos = pos;
                return;
            }
        }
        Entry entry = new Entry(entityId, pos);
        entry.amount = amount;
        entry.head = head;
        entry.kill = kill;
        ENTRIES.add(entry);
        while (ENTRIES.size() > MAX) {
            ENTRIES.remove(0);
        }
    }

    static void onClientTick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (event.phase != TickEvent.Phase.END || mc.isPaused()) {
            return;
        }
        if (mc.level == null) {
            ENTRIES.clear();
            return;
        }
        Iterator<Entry> it = ENTRIES.iterator();
        while (it.hasNext()) {
            if (++it.next().age >= LIFE) {
                it.remove();
            }
        }
    }

    static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES || ENTRIES.isEmpty()) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui) {
            return;
        }
        Camera camera = event.getCamera();
        Vec3 cam = camera.getPosition();
        PoseStack pose = event.getPoseStack();
        MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();
        Font font = mc.font;
        float partialTick = event.getPartialTick();
        for (Entry entry : ENTRIES) {
            float age = entry.age + partialTick;
            float t = age / LIFE;
            int alpha = (int) (255 * (t < 0.6F ? 1.0F : Math.max(0.0F, 1.0F - (t - 0.6F) / 0.4F)));
            if (alpha < 8) {
                continue;
            }
            double dx = entry.pos.x - cam.x;
            double dy = entry.pos.y + t * 0.8 - cam.y;
            double dz = entry.pos.z - cam.z;
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
            // uzoqdagi (snayper) nishonda ham o'qiladigan kattalikda qolsin; tekkanda biroz "sakraydi"
            float pop = age < 4.0F ? 1.5F - 0.5F * age / 4.0F : 1.0F;
            float size = 0.03F * (float) Math.max(1.0, dist / 7.0) * pop * (entry.kill ? 1.35F : entry.head ? 1.15F : 1.0F);
            String text = String.valueOf(Math.max(1, Math.round(entry.amount)));
            int rgb = entry.kill ? 0xFF3B3B : entry.head ? 0xFFC83D : 0xFFFFFF;

            pose.pushPose();
            pose.translate(dx, dy, dz);
            pose.mulPose(camera.rotation());
            pose.scale(-size, -size, size);
            Matrix4f matrix = pose.last().pose();
            float x = -font.width(text) / 2.0F;
            int outline = alpha << 24;
            font.drawInBatch(text, x - 1, 0, outline, false, matrix, buffers, Font.DisplayMode.SEE_THROUGH, 0, LightTexture.FULL_BRIGHT);
            font.drawInBatch(text, x + 1, 0, outline, false, matrix, buffers, Font.DisplayMode.SEE_THROUGH, 0, LightTexture.FULL_BRIGHT);
            font.drawInBatch(text, x, -1, outline, false, matrix, buffers, Font.DisplayMode.SEE_THROUGH, 0, LightTexture.FULL_BRIGHT);
            font.drawInBatch(text, x, 1, outline, false, matrix, buffers, Font.DisplayMode.SEE_THROUGH, 0, LightTexture.FULL_BRIGHT);
            font.drawInBatch(text, x, 0, (alpha << 24) | rgb, false, matrix, buffers, Font.DisplayMode.SEE_THROUGH, 0,
                    LightTexture.FULL_BRIGHT);
            pose.popPose();
        }
        buffers.endBatch();
    }
}
