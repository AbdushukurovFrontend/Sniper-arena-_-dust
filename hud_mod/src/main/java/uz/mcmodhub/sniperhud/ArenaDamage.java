package uz.mcmodhub.sniperhud;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

/**
 * AWP qoidasi (CS2 kabi): o'q oyoqdan yuqoriga (tana, qo'l, bosh) tegsa bitta o'qda o'ldiradi,
 * oyoqqa tegsa zarar x0.75 va to'liq jondan bitta o'q bilan o'ldirmaydi.
 */
final class ArenaDamage {
    /** Tana balandligining shu ulushidan pastda — oyoq (o'yinchi modelida oyoq 32 pikseldan 12 tasi). */
    private static final double LEG_LINE = 0.375;
    private static final float LEG_MULTIPLIER = 0.75F;
    /** Oyoqqa tekkan bitta AWP o'qining eng katta zarari (100 jondan). */
    private static final float LEG_MAX = 85.0F;
    private static final float LETHAL = 1000.0F;
    /** TaCZ bitta o'qni ikki qismga bo'lib urishi mumkin: oyoq zarari o'q (tick) bo'yicha jamlanadi. */
    private static final Map<UUID, long[]> LEG_HITS = new HashMap<>();

    private ArenaDamage() {
    }

    static void onHurt(ServerPlayer victim, LivingHurtEvent event) {
        Team team = victim.getTeam();
        if (team == null || !"sa.game".equals(team.getName())) {
            return;
        }
        DamageSource source = event.getSource();
        if (!(source.getEntity() instanceof LivingEntity shooter) || shooter == victim || !isAwp(shooter.getMainHandItem())) {
            return;
        }
        Entity direct = source.getDirectEntity();
        boolean projectile = direct != null && direct != shooter && !(direct instanceof LivingEntity);
        boolean taczDamage = source.typeHolder().unwrapKey()
                .map(key -> "tacz".equals(key.location().getNamespace())).orElse(false);
        if (!projectile && !taczDamage) {
            return; // o'q emas (masalan qurol bilan urish)
        }
        double height = projectile ? hitHeight(direct.position(), direct.getDeltaMovement(), victim)
                : hitHeight(shooter.getEyePosition(), shooter.getLookAngle(), victim);
        if (height >= LEG_LINE) {
            event.setAmount(Math.max(event.getAmount(), LETHAL));
            return;
        }
        long tick = victim.level().getGameTime();
        long[] hit = LEG_HITS.get(victim.getUUID());
        if (hit == null || hit[0] != tick) {
            hit = new long[] {tick, 0};
            LEG_HITS.put(victim.getUUID(), hit);
        }
        float done = Float.intBitsToFloat((int) hit[1]);
        float amount = Math.min(event.getAmount() * LEG_MULTIPLIER, Math.max(0.0F, LEG_MAX - done));
        hit[1] = Float.floatToIntBits(done + amount);
        event.setAmount(amount);
    }

    static void clear() {
        LEG_HITS.clear();
    }

    private static boolean isAwp(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.getString("GunId").contains("awp");
    }

    /**
     * O'q chizig'ining nishon markazidan (vertikal o'qidan) o'tgan joyidagi balandligi,
     * nishon bo'yiga nisbatan: 0 = oyoq tagi, 1 = bosh tepasi.
     */
    private static double hitHeight(Vec3 from, Vec3 direction, LivingEntity victim) {
        double horizontal = direction.x * direction.x + direction.z * direction.z;
        double y = from.y;
        if (horizontal > 1.0E-6) {
            double t = ((victim.getX() - from.x) * direction.x + (victim.getZ() - from.z) * direction.z) / horizontal;
            y = from.y + direction.y * t;
        }
        return (y - victim.getY()) / Math.max(0.1, victim.getBbHeight());
    }
}
