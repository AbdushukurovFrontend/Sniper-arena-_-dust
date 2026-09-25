package uz.mcmodhub.sniperhud;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.scores.Team;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.ServerLifecycleHooks;

public final class ServerEvents {
    private ServerEvents() {
    }

    /**
     * Arenadagi o'lim: hamma klientga kill feed yuboriladi, o'yinda qatnashayotgan o'yinchi uchun esa
     * haqiqiy o'lim "virtual o'lim"ga almashtiriladi (VirtualDeath). Boshqa modlar soxta o'limga
     * reaksiya qilmasligi uchun eng yuqori ustuvorlikda ishlaydi.
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer victim)) {
            return;
        }
        Team team = victim.getTeam();
        if (team == null || !team.getName().startsWith("sa.")) {
            return;
        }
        DamageSource source = event.getSource();
        LivingEntity credit = victim.getKillCredit();
        ServerPlayer killer = null;
        if (source.getEntity() instanceof ServerPlayer shooter && shooter != victim) {
            killer = shooter;
        } else if (credit instanceof ServerPlayer player && player != victim) {
            killer = player;
        }
        if (killer != null) {
            Net.CHANNEL.send(PacketDistributor.ALL.noArg(), new KillFeedPacket(
                    killer.getGameProfile().getName(),
                    victim.getGameProfile().getName(),
                    killer.getMainHandItem().copy()));
        }
        if (VirtualDeath.applies(victim)) {
            event.setCanceled(true);
            VirtualDeath.die(victim, source, credit);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.player instanceof ServerPlayer player) {
            VirtualDeath.onPlayerTickEnd(player);
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && ServerLifecycleHooks.getCurrentServer() != null) {
            VirtualDeath.onServerTickEnd(ServerLifecycleHooks.getCurrentServer());
        }
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            VirtualDeath.onLogout(player);
        }
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        VirtualDeath.clear();
    }
}
