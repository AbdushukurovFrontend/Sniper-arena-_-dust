package uz.mcmodhub.sniperhud.arena;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

/** Server -> klient: ruletka oynasini ochish. Nomzodlar ro'yxati hamma klientda bir xil tartibda. */
public record RoulettePacket(List<Candidate> candidates, int winnerIndex, int windowTicks) {
    public record Candidate(String name, int color, ItemStack icon) {
        void encode(FriendlyByteBuf buf) {
            buf.writeUtf(name);
            buf.writeInt(color);
            buf.writeItem(icon);
        }

        static Candidate decode(FriendlyByteBuf buf) {
            return new Candidate(buf.readUtf(), buf.readInt(), buf.readItem());
        }
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(candidates.size());
        for (Candidate c : candidates) {
            c.encode(buf);
        }
        buf.writeVarInt(winnerIndex);
        buf.writeVarInt(windowTicks);
    }

    public static RoulettePacket decode(FriendlyByteBuf buf) {
        int count = buf.readVarInt();
        List<Candidate> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            list.add(Candidate.decode(buf));
        }
        int winner = buf.readVarInt();
        int window = buf.readVarInt();
        return new RoulettePacket(list, winner, window);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> uz.mcmodhub.sniperhud.client.RouletteScreen.open(this));
        ctx.get().setPacketHandled(true);
    }
}
