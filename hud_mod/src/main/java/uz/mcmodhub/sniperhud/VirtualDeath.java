package uz.mcmodhub.sniperhud;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

/**
 * Arenadagi "virtual o'lim".
 *
 * Minecraft 1.20.1 da o'yinchi o'lib qayta tug'ilganda server uning atrofidagi HAMMA chunklarni
 * qaytadan yuboradi va barcha entitylarni qayta jo'natadi. Lider (host) uchun bu bepul, lekin internet
 * orqali kirgan o'yinchiga har o'limda bir necha MB ketadi: ping ko'tariladi, killcam kechikadi,
 * navbat 15 soniyadan oshsa "Timed out" bilan chiqib ketadi.
 *
 * Shuning uchun o'yinda qatnashayotgan o'yinchi (tag sa.ingame) o'lganda haqiqiy o'lim bekor qilinadi:
 * vanilla die() ning scoreboard/advancement/statistika qismlari aynan takrorlanadi (datapack hech narsani
 * sezmaydi), o'yinchi darhol kuzatuvchi (spectator) bo'ladi, joni esa o'sha tickning oxirida tiklanadi.
 * Qayta tug'ilish (respawn) bo'lmagani uchun chunklar qayta yuborilmaydi.
 */
public final class VirtualDeath {
    /** Joni hali tiklanmagan o'yinchilar: UUID -> o'lgan paytdagi server tick. */
    private static final Map<UUID, Integer> PENDING = new HashMap<>();

    private VirtualDeath() {
    }

    /** Datapack yoqib qo'ygan bo'lsa (#virtual_death sa.cfg >= 1) va o'yinchi o'yinda bo'lsa. */
    static boolean applies(ServerPlayer victim) {
        if (!victim.getTags().contains("sa.ingame") || victim.isSpectator()) {
            return false;
        }
        Scoreboard scoreboard = victim.getScoreboard();
        Objective cfg = scoreboard.getObjective("sa.cfg");
        return cfg != null
                && scoreboard.hasPlayerScore("#virtual_death", cfg)
                && scoreboard.getOrCreatePlayerScore("#virtual_death", cfg).getScore() >= 1;
    }

    /** ServerPlayer.die() ning o'yin uchun muhim qismlari, lekin o'yinchi o'lmaydi. */
    static void die(ServerPlayer victim, DamageSource source, LivingEntity credit) {
        MinecraftServer server = victim.getServer();

        if (victim.level().getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES)) {
            broadcastDeathMessage(server, victim, victim.getCombatTracker().getDeathMessage());
        }

        Scoreboard scoreboard = victim.getScoreboard();
        scoreboard.forAllObjectives(ObjectiveCriteria.DEATH_COUNT, victim.getScoreboardName(), Score::increment);
        if (credit != null) {
            victim.awardStat(Stats.ENTITY_KILLED_BY.get(credit.getType()));
            // playerKillCount (sa.kill_raw) va entity_killed_player advancementi (killed_by_player) shu yerda
            credit.awardKillScore(victim, 0, source);
            if (credit != victim) {
                copyScore(scoreboard, credit.getScoreboardName(), "sa.pid", victim.getScoreboardName(), "sa.killer");
            }
        }
        victim.awardStat(Stats.DEATHS);
        victim.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_DEATH));
        victim.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
        victim.clearFire();
        victim.setTicksFrozen(0);
        victim.getCombatTracker().recheckStatus();
        victim.setLastDeathLocation(Optional.of(GlobalPos.of(victim.level().dimension(), victim.blockPosition())));

        // Darhol jangdan chiqadi: kuzatuvchiga zarar yetmaydi va u hujum qila olmaydi.
        // Joni 0 bo'lib turadi (qurol modlari buni kill deb sanaydi) va tick oxirida tiklanadi.
        victim.setGameMode(GameType.SPECTATOR);
        PENDING.put(victim.getUUID(), server.getTickCount());
    }

    /** O'yinchining o'z ticki oxirida (jon klientga yuborilishidan oldin) chaqiriladi. */
    static void onPlayerTickEnd(ServerPlayer player) {
        if (!PENDING.isEmpty() && PENDING.remove(player.getUUID()) != null) {
            restore(player);
        }
    }

    /** Zaxira: biror sabab bilan o'yinchi ticki o'tmagan bo'lsa, keyingi tick oxirida tiklanadi. */
    static void onServerTickEnd(MinecraftServer server) {
        if (PENDING.isEmpty()) {
            return;
        }
        int now = server.getTickCount();
        Iterator<Map.Entry<UUID, Integer>> it = PENDING.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, Integer> entry = it.next();
            ServerPlayer player = server.getPlayerList().getPlayer(entry.getKey());
            if (player == null) {
                it.remove();
            } else if (now - entry.getValue() >= 1) {
                it.remove();
                restore(player);
            }
        }
    }

    /** Chiqib ketayotgan o'yinchi 0 jon bilan saqlanib qolmasin. */
    static void onLogout(ServerPlayer player) {
        if (PENDING.remove(player.getUUID()) != null) {
            restore(player);
        }
    }

    static void clear() {
        PENDING.clear();
    }

    private static void restore(ServerPlayer player) {
        if (player.isDeadOrDying()) {
            player.setHealth(player.getMaxHealth());
        }
        player.deathTime = 0;
        player.setLastHurtByMob(null);
        player.setLastHurtByPlayer(null);
        player.fallDistance = 0.0F;
        player.setRemainingFireTicks(0);
        player.setAirSupply(player.getMaxAirSupply());
    }

    /** Vanilla qoidasi (jamoaning deathMessageVisibility) bo'yicha; o'lgan o'yinchining o'ziga ham. */
    private static void broadcastDeathMessage(MinecraftServer server, ServerPlayer victim, Component message) {
        Team team = victim.getTeam();
        Team.Visibility visibility = team == null ? Team.Visibility.ALWAYS : team.getDeathMessageVisibility();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            boolean send = true;
            if (player != victim) {
                if (visibility == Team.Visibility.HIDE_FOR_OTHER_TEAMS) {
                    send = player.getTeam() == team;
                } else if (visibility == Team.Visibility.HIDE_FOR_OWN_TEAM) {
                    send = player.getTeam() != team;
                } else if (visibility == Team.Visibility.NEVER) {
                    send = false;
                }
            }
            if (send) {
                player.sendSystemMessage(message);
            }
        }
    }

    private static void copyScore(Scoreboard scoreboard, String from, String fromObjective, String to, String toObjective) {
        Objective source = scoreboard.getObjective(fromObjective);
        Objective target = scoreboard.getObjective(toObjective);
        if (source == null || target == null || !scoreboard.hasPlayerScore(from, source)) {
            return;
        }
        int value = scoreboard.getOrCreatePlayerScore(from, source).getScore();
        scoreboard.getOrCreatePlayerScore(to, target).setScore(value);
    }
}
