package uz.mcmodhub.sniperhud.arena;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

/** Arenalar ro'yxati — dunyo bilan birga saqlanadi (data/sniper_arena_arenas.dat). */
public final class ArenaData extends SavedData {
    private static final String NAME = "sniper_arena_arenas";

    public final List<Arena> arenas = new ArrayList<>();
    /** Hozir o'yin uchun yuklangan arena (bo'lmasa null). */
    public String active;
    /** Oxirgi tanlangan arena — ketma-ket ikki marta tushmaydi. */
    public String last;
    /** Eski (datapack) spawn markerlari 1-arena sifatida ko'chirilganmi. */
    public boolean migrated;
    /** Biz majburan yuklagan chunklar (faqat shularni qo'yib yuboramiz). */
    public final List<Long> forced = new ArrayList<>();

    public static ArenaData get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(ArenaData::load, ArenaData::new, NAME);
    }

    public Arena find(String id) {
        if (id == null) {
            return null;
        }
        for (Arena arena : arenas) {
            if (arena.id.equalsIgnoreCase(id)) {
                return arena;
            }
        }
        return null;
    }

    /** O'ynash mumkin bo'lgan arenalar: yoqilgan va kamida bitta spawn nuqtasi bor. */
    public List<Arena> playable() {
        List<Arena> out = new ArrayList<>();
        for (Arena arena : arenas) {
            if (arena.enabled && !arena.spawns.isEmpty()) {
                out.add(arena);
            }
        }
        return out;
    }

    public static ArenaData load(CompoundTag tag) {
        ArenaData data = new ArenaData();
        ListTag list = tag.getList("Arenas", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            data.arenas.add(Arena.load(list.getCompound(i)));
        }
        data.active = tag.contains("Active") ? tag.getString("Active") : null;
        data.last = tag.contains("Last") ? tag.getString("Last") : null;
        data.migrated = tag.getBoolean("Migrated");
        ListTag forced = tag.getList("Forced", Tag.TAG_LONG);
        for (int i = 0; i < forced.size(); i++) {
            data.forced.add(((LongTag) forced.get(i)).getAsLong());
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();
        for (Arena arena : arenas) {
            list.add(arena.save());
        }
        tag.put("Arenas", list);
        if (active != null) {
            tag.putString("Active", active);
        }
        if (last != null) {
            tag.putString("Last", last);
        }
        tag.putBoolean("Migrated", migrated);
        ListTag forcedList = new ListTag();
        for (long chunk : forced) {
            forcedList.add(LongTag.valueOf(chunk));
        }
        tag.put("Forced", forcedList);
        return tag;
    }
}
