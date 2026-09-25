package uz.mcmodhub.sniperhud;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class Net {
    private static final String VERSION = "1";

    // Mod bo'lmagan o'yinchilar ham ulana oladi (acceptMissingOr)
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(SniperArenaHud.MODID, "main"),
            () -> VERSION,
            NetworkRegistry.acceptMissingOr(VERSION),
            NetworkRegistry.acceptMissingOr(VERSION));

    private Net() {
    }

    public static void register() {
        CHANNEL.messageBuilder(KillFeedPacket.class, 0, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(KillFeedPacket::encode)
                .decoder(KillFeedPacket::decode)
                .consumerMainThread(KillFeedPacket::handle)
                .add();
    }
}
