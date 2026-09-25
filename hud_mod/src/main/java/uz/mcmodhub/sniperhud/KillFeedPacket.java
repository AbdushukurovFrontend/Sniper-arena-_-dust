package uz.mcmodhub.sniperhud;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

/** Server -> klient: "qotil [qurol] o'lgan" kill feed yozuvi. */
public record KillFeedPacket(String killer, String victim, ItemStack weapon) {

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(killer);
        buf.writeUtf(victim);
        buf.writeItem(weapon);
    }

    public static KillFeedPacket decode(FriendlyByteBuf buf) {
        return new KillFeedPacket(buf.readUtf(), buf.readUtf(), buf.readItem());
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> uz.mcmodhub.sniperhud.client.KillFeed.add(killer, victim, weapon));
        ctx.get().setPacketHandled(true);
    }
}
