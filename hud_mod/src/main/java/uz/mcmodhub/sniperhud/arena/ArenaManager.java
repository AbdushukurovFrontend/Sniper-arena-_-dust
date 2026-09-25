package uz.mcmodhub.sniperhud.arena;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import uz.mcmodhub.sniperhud.Net;

/**
 * Ko'p arenali FFA: arenalar ro'yxatini boshqaradi, doiradagi sanoq tugagach (yoki har g'alabadan keyin)
 * ulardan birini tasodifiy tanlaydi, ruletka oynasini ochadi, tanlangan arenani yuklab (forceload) qolganlarini
 * yuklamaydi, o'yinchilarni yangi spawn nuqtalariga joylaydi va arena chegarasini nazorat qiladi.
 *
 * Datapack bilan til: scoreboard sa.var/sa.cfg orqali. #mod = 1 (mod borligi belgisi, har tick yangilanadi),
 * #state 4 = arena tanlanmoqda, #arena_ready 1 = arena tanlandi va spawn nuqtalari tayyor, #choose_ticks (sa.cfg)
 * = tanlash oynasi davomiyligi (tick). Arena spawnlari uchun mod minecraft:marker (tag sa.spawn) summon qiladi,
 * xuddi datapackning eski qo'lda qo'yilgan spawnlari kabi — shuning uchun game/spawn_random va boshqalar
 * o'zgarishsiz ishlayveradi.
 */
public final class ArenaManager {
    /** Eski (bitta arenali) xaritaning asl chegarasi — migratsiyadan keyin mod o'zi boshqaradi. */
    private static final int LEGACY_MIN_CX = 146 >> 4;
    private static final int LEGACY_MAX_CX = 218 >> 4;
    private static final int LEGACY_MIN_CZ = -90 >> 4;
    private static final int LEGACY_MAX_CZ = 8 >> 4;

    private int lastSeenState = -1;
    private int handledMatchId = -1;
    private boolean rouletteOpen;
    private int roulEndTick;
    private Arena roulWinner;
    private final Map<String, Set<Long>> pendingChunks = new HashMap<>();
    private final Set<Long> activeChunks = new HashSet<>();
    private final List<UUID> spawnedMarkers = new ArrayList<>();
    private int migrateWaitTicks;
    /** Shuncha tick kutamiz (create_markers odatda ~20 tickda ishlaydi): topilmasa, migratsiya tugadi deb hisoblanadi. */
    private static final int MIGRATE_GIVE_UP_TICKS = 200;

    public ArenaManager() {
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        MinecraftServer server = net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return;
        }
        ServerLevel level = server.overworld();
        Scoreboard sb = level.getScoreboard();
        setScore(sb, "sa.var", "#mod", 1);

        ArenaData data = ArenaData.get(server);
        migrateIfNeeded(level, data, sb);

        int state = getScore(sb, "sa.var", "#state", 0);
        int matchId = getScore(sb, "sa.var", "#match_id", 0);

        if (state == 4 && !rouletteOpen && handledMatchId != matchId) {
            handledMatchId = matchId;
            beginRoulette(server, level, data, sb, matchId);
        } else if (rouletteOpen) {
            if (state != 4) {
                cancelRoulette(level);
            } else if (server.getTickCount() >= roulEndTick) {
                finishRoulette(level, data, sb, matchId);
            }
        }

        if (lastSeenState != 0 && state == 0) {
            release(level, activeChunks);
            activeChunks.clear();
            for (UUID id : spawnedMarkers) {
                Entity e = level.getEntity(id);
                if (e != null) {
                    e.discard();
                }
            }
            spawnedMarkers.clear();
            data.active = null;
            data.setDirty();
        }
        lastSeenState = state;

        enforceBounds(server, level, data, state);
    }

    // ---------------------------------------------------------------- legacy migration
    private void migrateIfNeeded(ServerLevel level, ArenaData data, Scoreboard sb) {
        if (data.migrated) {
            return;
        }
        List<Arena.Spawn> spawns = new ArrayList<>();
        List<Entity> found = new ArrayList<>();
        for (Entity e : level.getAllEntities()) {
            if (e.getType() == EntityType.MARKER && e.getTags().contains("sa.spawn")) {
                found.add(e);
                spawns.add(new Arena.Spawn(e.getX(), e.getY(), e.getZ(), e.getYRot(), e.getXRot()));
            }
        }
        if (spawns.isEmpty()) {
            // datapack.setup/create_markers hali ishlamagan bo'lishi mumkin (server ochilgandan ~1 soniya
            // keyin ishlaydi) — bir necha marta qaytadan urinib ko'ramiz, chala migratsiya qilib qo'ymaslik uchun
            if (++migrateWaitTicks < MIGRATE_GIVE_UP_TICKS) {
                return;
            }
        } else {
            Arena legacy = new Arena("arena1", "Asosiy arena");
            legacy.icon = "minecraft:iron_sword";
            legacy.spawns.addAll(spawns);
            data.arenas.add(0, legacy);
            for (Entity e : found) {
                e.discard();
            }
            // Eski qattiq kodlangan forceload endi kerak emas — mod arenalarni o'zi navbat bilan yuklaydi
            for (int cx = LEGACY_MIN_CX; cx <= LEGACY_MAX_CX; cx++) {
                for (int cz = LEGACY_MIN_CZ; cz <= LEGACY_MAX_CZ; cz++) {
                    level.setChunkForced(cx, cz, false);
                }
            }
        }
        data.migrated = true;
        data.setDirty();
        setScore(sb, "sa.var", "#arenas_migrated", 1);
    }

    // ---------------------------------------------------------------- roulette
    private void beginRoulette(MinecraftServer server, ServerLevel level, ArenaData data, Scoreboard sb, int matchId) {
        release(level, activeChunks);
        activeChunks.clear();

        List<Arena> full = new ArrayList<>(data.playable());
        full.sort(Comparator.comparing(a -> a.id));
        if (full.isEmpty()) {
            warnNoArenas(server);
            return; // datapack: game/choose_fallback #choose_ticks dan keyin ishga tushadi
        }

        List<Arena> pool = full;
        if (full.size() > 1 && data.last != null) {
            List<Arena> filtered = full.stream().filter(a -> !a.id.equals(data.last)).collect(Collectors.toList());
            if (!filtered.isEmpty()) {
                pool = filtered;
            }
        }
        Arena winner = pool.get(level.getRandom().nextInt(pool.size()));

        pendingChunks.clear();
        for (Arena arena : full) {
            Set<Long> keys = chunkKeys(arena);
            pendingChunks.put(arena.id, keys);
            forceAdd(level, keys);
        }

        int windowTicks = getScore(sb, "sa.cfg", "#choose_ticks", 100);
        roulEndTick = server.getTickCount() + windowTicks;
        roulWinner = winner;
        rouletteOpen = true;

        List<RoulettePacket.Candidate> candidates = new ArrayList<>();
        int winnerIndex = 0;
        for (int i = 0; i < full.size(); i++) {
            Arena arena = full.get(i);
            if (arena == winner) {
                winnerIndex = i;
            }
            candidates.add(new RoulettePacket.Candidate(arena.name, arena.color, iconStack(arena.icon)));
        }
        RoulettePacket packet = new RoulettePacket(candidates, winnerIndex, windowTicks);
        for (ServerPlayer player : level.players()) {
            if (player.getTags().contains("sa.ingame")) {
                Net.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
            }
        }
    }

    private void cancelRoulette(ServerLevel level) {
        rouletteOpen = false;
        for (Set<Long> keys : pendingChunks.values()) {
            release(level, keys);
        }
        pendingChunks.clear();
        roulWinner = null;
    }

    private void finishRoulette(ServerLevel level, ArenaData data, Scoreboard sb, int matchId) {
        rouletteOpen = false;
        Arena winner = roulWinner;
        if (winner == null) {
            return;
        }
        for (Map.Entry<String, Set<Long>> entry : pendingChunks.entrySet()) {
            if (!entry.getKey().equals(winner.id)) {
                release(level, entry.getValue());
            }
        }
        activeChunks.clear();
        activeChunks.addAll(pendingChunks.getOrDefault(winner.id, chunkKeys(winner)));
        pendingChunks.clear();

        for (UUID id : spawnedMarkers) {
            Entity e = level.getEntity(id);
            if (e != null) {
                e.discard();
            }
        }
        spawnedMarkers.clear();

        Objective matchObj = sb.getObjective("sa.match");
        for (Arena.Spawn s : winner.spawns) {
            Entity marker = EntityType.MARKER.create(level);
            if (marker == null) {
                continue;
            }
            marker.moveTo(s.x(), s.y(), s.z(), s.yaw(), s.pitch());
            marker.addTag("sa.spawn");
            level.addFreshEntity(marker);
            if (matchObj != null) {
                sb.getOrCreatePlayerScore(marker.getStringUUID(), matchObj).setScore(matchId);
            }
            spawnedMarkers.add(marker.getUUID());
        }

        data.active = winner.id;
        data.last = winner.id;
        data.setDirty();
        setScore(sb, "sa.var", "#arena_ready", 1);
    }

    private void warnNoArenas(MinecraftServer server) {
        Component msg = Component.literal("[Sniper Arena] Hech qanday arena sozlanmagan! "
                + "/sa arena create <id> <nomi> va /sa arena addspawn <id> bilan qo'shing. Hozircha eski usulga o'tildi.");
        for (ServerPlayer p : server.getPlayerList().getPlayers()) {
            if (server.getPlayerList().isOp(p.getGameProfile()) || p.getTags().contains("sa.builder")) {
                p.sendSystemMessage(msg);
            }
        }
    }

    // ---------------------------------------------------------------- boundary enforcement
    private void enforceBounds(MinecraftServer server, ServerLevel level, ArenaData data, int state) {
        if (data.active == null) {
            return;
        }
        Arena arena = data.find(data.active);
        AABB box = arena == null ? null : arena.bounds();
        if (box == null) {
            return;
        }
        for (ServerPlayer player : level.players()) {
            boolean ingame = player.getTags().contains("sa.ingame");
            boolean builder = player.getTags().contains("sa.builder");
            boolean deathcam = player.getTags().contains("sa.deathcam");
            boolean inside = box.contains(player.getX(), player.getY(), player.getZ());
            if (state == 2 && ingame && !deathcam && !inside) {
                dispatch(server, player, "function sniper_arena:game/out_of_bounds");
            } else if (!ingame && !builder && inside) {
                dispatch(server, player, "function sniper_arena:lobby/send");
            }
        }
    }

    private void dispatch(MinecraftServer server, ServerPlayer player, String command) {
        CommandSourceStack source = player.createCommandSourceStack().withPermission(4).withSuppressedOutput();
        server.getCommands().performPrefixedCommand(source, command);
    }

    // ---------------------------------------------------------------- chunk helpers
    private Set<Long> chunkKeys(Arena arena) {
        AABB box = arena.bounds();
        if (box == null) {
            return Set.of();
        }
        int minCx = ((int) Math.floor(box.minX)) >> 4;
        int maxCx = ((int) Math.floor(box.maxX - 1)) >> 4;
        int minCz = ((int) Math.floor(box.minZ)) >> 4;
        int maxCz = ((int) Math.floor(box.maxZ - 1)) >> 4;
        Set<Long> out = new HashSet<>();
        for (int cx = minCx; cx <= maxCx; cx++) {
            for (int cz = minCz; cz <= maxCz; cz++) {
                out.add(ChunkPos.asLong(cx, cz));
            }
        }
        return out;
    }

    private void forceAdd(ServerLevel level, Set<Long> keys) {
        for (long key : keys) {
            level.setChunkForced(ChunkPos.getX(key), ChunkPos.getZ(key), true);
        }
    }

    private void release(ServerLevel level, Set<Long> keys) {
        for (long key : keys) {
            level.setChunkForced(ChunkPos.getX(key), ChunkPos.getZ(key), false);
        }
    }

    // ---------------------------------------------------------------- misc
    private ItemStack iconStack(String id) {
        try {
            ResourceLocation rl = new ResourceLocation(id);
            Item item = ForgeRegistries.ITEMS.getValue(rl);
            return item != null && item != Items.AIR ? new ItemStack(item) : new ItemStack(Items.SPYGLASS);
        } catch (RuntimeException ex) {
            return new ItemStack(Items.SPYGLASS);
        }
    }

    static int getScore(Scoreboard sb, String objName, String holder, int def) {
        Objective o = sb.getObjective(objName);
        if (o == null || !sb.hasPlayerScore(holder, o)) {
            return def;
        }
        return sb.getOrCreatePlayerScore(holder, o).getScore();
    }

    static void setScore(Scoreboard sb, String objName, String holder, int value) {
        Objective o = sb.getObjective(objName);
        if (o != null) {
            sb.getOrCreatePlayerScore(holder, o).setScore(value);
        }
    }

    /** /sa roulette bilan admin o'ziga ko'rsatib ko'rish uchun (o'yin holatiga ta'sir qilmaydi). */
    public static void preview(ServerPlayer player) {
        MinecraftServer server = player.getServer();
        if (server == null) {
            return;
        }
        ArenaData data = ArenaData.get(server);
        List<Arena> full = new ArrayList<>(data.arenas);
        full.sort(Comparator.comparing(a -> a.id));
        if (full.isEmpty()) {
            player.sendSystemMessage(Component.literal("[Sniper Arena] Hali arena yo'q — /sa arena create <id> <nomi>"));
            return;
        }
        int windowTicks = getScore(server.overworld().getScoreboard(), "sa.cfg", "#choose_ticks", 100);
        int winnerIndex = server.overworld().getRandom().nextInt(full.size());
        List<RoulettePacket.Candidate> candidates = new ArrayList<>();
        for (Arena arena : full) {
            candidates.add(new RoulettePacket.Candidate(arena.name, arena.color, iconStackStatic(arena.icon)));
        }
        Net.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new RoulettePacket(candidates, winnerIndex, windowTicks));
    }

    private static ItemStack iconStackStatic(String id) {
        try {
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
            return item != null && item != Items.AIR ? new ItemStack(item) : new ItemStack(Items.SPYGLASS);
        } catch (RuntimeException ex) {
            return new ItemStack(Items.SPYGLASS);
        }
    }

    /** Arenaning o'rtacha spawn nuqtasi (yo'q bo'lsa null) — /sa arena tp uchun. */
    public static BlockPos center(Arena arena) {
        if (arena.spawns.isEmpty()) {
            return null;
        }
        double x = 0, y = 0, z = 0;
        for (Arena.Spawn s : arena.spawns) {
            x += s.x();
            y += s.y();
            z += s.z();
        }
        int n = arena.spawns.size();
        return BlockPos.containing(x / n, y / n, z / n);
    }
}
