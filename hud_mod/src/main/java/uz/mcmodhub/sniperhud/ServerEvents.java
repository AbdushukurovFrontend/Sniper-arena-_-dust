package uz.mcmodhub.sniperhud;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.Team;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;

public final class ServerEvents {
    private ServerEvents() {
    }

    /** Arenada bir o'yinchi boshqasini o'ldirganda hamma klientga kill feed yuboradi. */
    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer victim)) {
            return;
        }
        Entity source = event.getSource().getEntity();
        if (!(source instanceof ServerPlayer killer) || killer == victim) {
            return;
        }
        Team team = victim.getTeam();
        if (team == null || !team.getName().startsWith("sa.")) {
            return;
        }
        Net.CHANNEL.send(PacketDistributor.ALL.noArg(), new KillFeedPacket(
                killer.getGameProfile().getName(),
                victim.getGameProfile().getName(),
                killer.getMainHandItem().copy()));
    }
}
