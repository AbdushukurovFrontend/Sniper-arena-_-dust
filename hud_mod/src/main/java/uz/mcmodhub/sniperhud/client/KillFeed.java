package uz.mcmodhub.sniperhud.client;

import java.util.ArrayDeque;
import java.util.Deque;
import net.minecraft.Util;
import net.minecraft.world.item.ItemStack;

/** O'ng tepadagi kill feed yozuvlari (eng yangisi tepada, 6 soniya turadi). */
public final class KillFeed {
    public record Entry(String killer, String victim, ItemStack weapon, long time) {
    }

    static final Deque<Entry> ENTRIES = new ArrayDeque<>();
    static final long LIFE_MS = 6000L;
    private static final int MAX = 5;

    private KillFeed() {
    }

    public static void add(String killer, String victim, ItemStack weapon) {
        ENTRIES.addFirst(new Entry(killer, victim, weapon, Util.getMillis()));
        while (ENTRIES.size() > MAX) {
            ENTRIES.removeLast();
        }
    }
}
