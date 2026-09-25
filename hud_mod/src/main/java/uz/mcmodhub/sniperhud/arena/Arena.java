package uz.mcmodhub.sniperhud.arena;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.phys.AABB;

/** Bitta arena: nomi, ruletkadagi rasmi, spawn nuqtalari va (ixtiyoriy) chegarasi. */
public final class Arena {
    /** Chegara berilmagan bo'lsa spawnlar atrofida shuncha blok. */
    private static final double MARGIN = 24;

    public record Spawn(double x, double y, double z, float yaw, float pitch) {
    }

    public final String id;
    public String name;
    public String icon = "minecraft:spyglass";
    public int color = 0xC0392B;
    public boolean enabled = true;
    public final List<Spawn> spawns = new ArrayList<>();
    public BlockPos pos1;
    public BlockPos pos2;

    public Arena(String id, String name) {
        this.id = id;
        this.name = name;
    }

    /** Arena chegarasi: pos1/pos2 berilgan bo'lsa shular, aks holda spawnlar atrofida. */
    public AABB bounds() {
        if (pos1 != null && pos2 != null) {
            return new AABB(Math.min(pos1.getX(), pos2.getX()), Math.min(pos1.getY(), pos2.getY()),
                    Math.min(pos1.getZ(), pos2.getZ()), Math.max(pos1.getX(), pos2.getX()) + 1,
                    Math.max(pos1.getY(), pos2.getY()) + 1, Math.max(pos1.getZ(), pos2.getZ()) + 1);
        }
        if (spawns.isEmpty()) {
            return null;
        }
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE, minZ = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE, maxZ = -Double.MAX_VALUE;
        for (Spawn s : spawns) {
            minX = Math.min(minX, s.x());
            minY = Math.min(minY, s.y());
            minZ = Math.min(minZ, s.z());
            maxX = Math.max(maxX, s.x());
            maxY = Math.max(maxY, s.y());
            maxZ = Math.max(maxZ, s.z());
        }
        return new AABB(minX - MARGIN, minY - 8, minZ - MARGIN, maxX + MARGIN, maxY + MARGIN, maxZ + MARGIN);
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Id", id);
        tag.putString("Name", name);
        tag.putString("Icon", icon);
        tag.putInt("Color", color);
        tag.putBoolean("Enabled", enabled);
        ListTag list = new ListTag();
        for (Spawn s : spawns) {
            CompoundTag st = new CompoundTag();
            st.putDouble("X", s.x());
            st.putDouble("Y", s.y());
            st.putDouble("Z", s.z());
            st.putFloat("Yaw", s.yaw());
            st.putFloat("Pitch", s.pitch());
            list.add(st);
        }
        tag.put("Spawns", list);
        if (pos1 != null) {
            tag.put("Pos1", NbtUtils.writeBlockPos(pos1));
        }
        if (pos2 != null) {
            tag.put("Pos2", NbtUtils.writeBlockPos(pos2));
        }
        return tag;
    }

    public static Arena load(CompoundTag tag) {
        Arena arena = new Arena(tag.getString("Id"), tag.getString("Name"));
        if (tag.contains("Icon")) {
            arena.icon = tag.getString("Icon");
        }
        if (tag.contains("Color")) {
            arena.color = tag.getInt("Color");
        }
        arena.enabled = !tag.contains("Enabled") || tag.getBoolean("Enabled");
        ListTag list = tag.getList("Spawns", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag st = list.getCompound(i);
            arena.spawns.add(new Spawn(st.getDouble("X"), st.getDouble("Y"), st.getDouble("Z"),
                    st.getFloat("Yaw"), st.getFloat("Pitch")));
        }
        if (tag.contains("Pos1")) {
            arena.pos1 = NbtUtils.readBlockPos(tag.getCompound("Pos1"));
        }
        if (tag.contains("Pos2")) {
            arena.pos2 = NbtUtils.readBlockPos(tag.getCompound("Pos2"));
        }
        return arena;
    }
}
