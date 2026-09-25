package uz.mcmodhub.sniperhud;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

/** Server -> otgan o'yinchi: nishon qancha jon yo'qotdi (boshi ustida raqam bo'lib chiqadi). */
public record DamagePacket(int victimId, float amount, byte flags) {
    public static final byte HEAD = 1;
    public static final byte KILL = 2;

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(victimId);
        buf.writeFloat(amount);
        buf.writeByte(flags);
    }

    public static DamagePacket decode(FriendlyByteBuf buf) {
        return new DamagePacket(buf.readVarInt(), buf.readFloat(), buf.readByte());
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> uz.mcmodhub.sniperhud.client.DamageNumbers.add(victimId, amount, flags));
        ctx.get().setPacketHandled(true);
    }
}
