package uz.mcmodhub.sniperhud.arena;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.ForgeRegistries;

/** /sa arena ... — arenalarni o'yin ichida sozlash (schem bilan qurilgach). Doim op (permission 2) talab qiladi. */
public final class ArenaCommand {
    private static final SimpleCommandExceptionType NOT_FOUND =
            new SimpleCommandExceptionType(Component.literal("Bunday arena yo'q. /sa arena list"));
    private static final SimpleCommandExceptionType ALREADY_EXISTS =
            new SimpleCommandExceptionType(Component.literal("Shu ID bilan arena allaqachon bor."));

    private ArenaCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("sa").requires(src -> src.hasPermission(2))
                .then(Commands.literal("roulette").executes(ctx -> {
                    ArenaManager.preview(ctx.getSource().getPlayerOrException());
                    return 1;
                }))
                .then(Commands.literal("arena")
                        .then(Commands.literal("list").executes(ArenaCommand::list))
                        .then(Commands.literal("create")
                                .then(Commands.argument("id", StringArgumentType.word())
                                        .then(Commands.argument("name", StringArgumentType.greedyString())
                                                .executes(ArenaCommand::create))))
                        .then(Commands.literal("remove").then(arenaArg().executes(ArenaCommand::remove)))
                        .then(Commands.literal("addspawn").then(arenaArg().executes(ArenaCommand::addSpawn)))
                        .then(Commands.literal("removespawn").then(arenaArg().executes(ArenaCommand::removeSpawn)))
                        .then(Commands.literal("pos1").then(arenaArg().executes(ctx -> setPos(ctx, true))))
                        .then(Commands.literal("pos2").then(arenaArg().executes(ctx -> setPos(ctx, false))))
                        .then(Commands.literal("icon").then(arenaArg()
                                .then(Commands.argument("item", ResourceLocationArgument.id())
                                        .executes(ArenaCommand::icon))))
                        .then(Commands.literal("enable").then(arenaArg().executes(ctx -> setEnabled(ctx, true))))
                        .then(Commands.literal("disable").then(arenaArg().executes(ctx -> setEnabled(ctx, false))))
                        .then(Commands.literal("show").then(arenaArg().executes(ArenaCommand::show)))
                        .then(Commands.literal("tp").then(arenaArg().executes(ArenaCommand::teleport)))
                        .then(Commands.literal("info").then(arenaArg().executes(ArenaCommand::info)))));
    }

    private static com.mojang.brigadier.builder.RequiredArgumentBuilder<CommandSourceStack, String> arenaArg() {
        return Commands.argument("id", StringArgumentType.word());
    }

    private static Arena get(CommandSourceStack src, String id) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        Arena arena = ArenaData.get(src.getServer()).find(id);
        if (arena == null) {
            throw NOT_FOUND.create();
        }
        return arena;
    }

    private static void ok(CommandSourceStack src, String text) {
        src.sendSuccess(() -> Component.literal("[Sniper Arena] " + text), false);
    }

    private static int list(com.mojang.brigadier.context.CommandContext<CommandSourceStack> ctx) {
        ArenaData data = ArenaData.get(ctx.getSource().getServer());
        if (data.arenas.isEmpty()) {
            ok(ctx.getSource(), "Hali arena yo'q. /sa arena create <id> <nomi>");
            return 0;
        }
        List<Arena> sorted = new ArrayList<>(data.arenas);
        sorted.sort(Comparator.comparing(a -> a.id));
        for (Arena arena : sorted) {
            String state = arena.enabled ? "yoqilgan" : "o'CHIRILGAN";
            String active = arena.id.equals(data.active) ? "  ⟵ hozir shu" : "";
            ok(ctx.getSource(), String.format("%s (\"%s\") — %s, %d spawn%s",
                    arena.id, arena.name, state, arena.spawns.size(), active));
        }
        return sorted.size();
    }

    private static int create(com.mojang.brigadier.context.CommandContext<CommandSourceStack> ctx)
            throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        String id = StringArgumentType.getString(ctx, "id");
        String name = StringArgumentType.getString(ctx, "name");
        ArenaData data = ArenaData.get(ctx.getSource().getServer());
        if (data.find(id) != null) {
            throw ALREADY_EXISTS.create();
        }
        data.arenas.add(new Arena(id, name));
        data.setDirty();
        ok(ctx.getSource(), "Arena qo'shildi: " + id + " (\"" + name + "\"). Endi turib: /sa arena addspawn " + id);
        return 1;
    }

    private static int remove(com.mojang.brigadier.context.CommandContext<CommandSourceStack> ctx)
            throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        String id = StringArgumentType.getString(ctx, "id");
        Arena arena = get(ctx.getSource(), id);
        ArenaData data = ArenaData.get(ctx.getSource().getServer());
        data.arenas.remove(arena);
        data.setDirty();
        ok(ctx.getSource(), "Arena o'chirildi: " + id);
        return 1;
    }

    private static int addSpawn(com.mojang.brigadier.context.CommandContext<CommandSourceStack> ctx)
            throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        Arena arena = get(ctx.getSource(), StringArgumentType.getString(ctx, "id"));
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        arena.spawns.add(new Arena.Spawn(player.getX(), player.getY(), player.getZ(), player.getYRot(), 0.0F));
        ArenaData.get(ctx.getSource().getServer()).setDirty();
        ok(ctx.getSource(), arena.id + ": spawn qo'shildi. Jami: " + arena.spawns.size());
        return arena.spawns.size();
    }

    private static int removeSpawn(com.mojang.brigadier.context.CommandContext<CommandSourceStack> ctx)
            throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        Arena arena = get(ctx.getSource(), StringArgumentType.getString(ctx, "id"));
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        Arena.Spawn nearest = null;
        double best = 9.0; // 3 blok radius
        for (Arena.Spawn s : arena.spawns) {
            double d = Math.pow(s.x() - player.getX(), 2) + Math.pow(s.y() - player.getY(), 2)
                    + Math.pow(s.z() - player.getZ(), 2);
            if (d < best) {
                best = d;
                nearest = s;
            }
        }
        if (nearest == null) {
            ok(ctx.getSource(), "3 blok ichida " + arena.id + " arenasining spawni yo'q.");
            return 0;
        }
        arena.spawns.remove(nearest);
        ArenaData.get(ctx.getSource().getServer()).setDirty();
        ok(ctx.getSource(), arena.id + ": spawn o'chirildi. Qolgan: " + arena.spawns.size());
        return arena.spawns.size();
    }

    private static int setPos(com.mojang.brigadier.context.CommandContext<CommandSourceStack> ctx, boolean first)
            throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        Arena arena = get(ctx.getSource(), StringArgumentType.getString(ctx, "id"));
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        BlockPos pos = player.blockPosition();
        if (first) {
            arena.pos1 = pos;
        } else {
            arena.pos2 = pos;
        }
        ArenaData.get(ctx.getSource().getServer()).setDirty();
        ok(ctx.getSource(), arena.id + ": pos" + (first ? "1" : "2") + " = " + pos.toShortString()
                + (arena.pos1 != null && arena.pos2 != null ? " (chegara to'liq)" : " (ikkinchisini ham qo'ying)"));
        return 1;
    }

    private static int icon(com.mojang.brigadier.context.CommandContext<CommandSourceStack> ctx)
            throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        Arena arena = get(ctx.getSource(), StringArgumentType.getString(ctx, "id"));
        ResourceLocation id = ResourceLocationArgument.getId(ctx, "item");
        Item item = ForgeRegistries.ITEMS.getValue(id);
        if (item == null) {
            ok(ctx.getSource(), "Bunday item topilmadi: " + id);
            return 0;
        }
        arena.icon = id.toString();
        ArenaData.get(ctx.getSource().getServer()).setDirty();
        ok(ctx.getSource(), arena.id + ": rasm = " + id);
        return 1;
    }

    private static int setEnabled(com.mojang.brigadier.context.CommandContext<CommandSourceStack> ctx, boolean enabled)
            throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        Arena arena = get(ctx.getSource(), StringArgumentType.getString(ctx, "id"));
        arena.enabled = enabled;
        ArenaData.get(ctx.getSource().getServer()).setDirty();
        ok(ctx.getSource(), arena.id + (enabled ? ": yoqildi." : ": o'chirildi (ruletkada chiqmaydi)."));
        return 1;
    }

    private static int show(com.mojang.brigadier.context.CommandContext<CommandSourceStack> ctx)
            throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        Arena arena = get(ctx.getSource(), StringArgumentType.getString(ctx, "id"));
        ServerLevel level = ctx.getSource().getLevel();
        for (Arena.Spawn s : arena.spawns) {
            level.sendParticles(net.minecraft.core.particles.ParticleTypes.END_ROD,
                    s.x(), s.y() + 0.1, s.z(), 12, 0.05, 1.0, 0.05, 0.0);
        }
        AABB box = arena.bounds();
        if (box != null) {
            ok(ctx.getSource(), arena.id + ": " + arena.spawns.size() + " spawn, chegara " + (int) box.getXsize()
                    + "x" + (int) box.getYsize() + "x" + (int) box.getZsize() + " blok (markazi ko'rsatildi).");
        }
        return 1;
    }

    private static int teleport(com.mojang.brigadier.context.CommandContext<CommandSourceStack> ctx)
            throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        Arena arena = get(ctx.getSource(), StringArgumentType.getString(ctx, "id"));
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        BlockPos pos = ArenaManager.center(arena);
        if (pos == null) {
            ok(ctx.getSource(), arena.id + ": hali spawn yo'q.");
            return 0;
        }
        player.teleportTo(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
        return 1;
    }

    private static int info(com.mojang.brigadier.context.CommandContext<CommandSourceStack> ctx)
            throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        Arena arena = get(ctx.getSource(), StringArgumentType.getString(ctx, "id"));
        AABB box = arena.bounds();
        ok(ctx.getSource(), arena.id + " — \"" + arena.name + "\", rasm " + arena.icon + ", "
                + (arena.enabled ? "yoqilgan" : "o'chirilgan") + ", " + arena.spawns.size() + " spawn"
                + (box == null ? "" : String.format(", chegara %.0fx%.0fx%.0f", box.getXsize(), box.getYsize(), box.getZsize()))
                + (arena.pos1 != null && arena.pos2 != null ? " (pos1/pos2 qo'lda)" : " (spawnlardan hisoblangan)"));
        return 1;
    }
}
