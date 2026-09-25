package uz.mcmodhub.sniperhud;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import uz.mcmodhub.sniperhud.arena.RoulettePacket;

public final class Net {
    // 2: ruletka oynasi (1-versiyadagi zarar raqamlari o'rniga). Eski versiyali mod bilan kirib bo'lmaydi —
    // hamma bir xil versiyani qo'yishi kerak. Mod bo'lmagan o'yinchilar ham ulana oladi (acceptMissingOr)
    private static final String VERSION = "2";
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
        CHANNEL.messageBuilder(RoulettePacket.class, 1, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(RoulettePacket::encode)
                .decoder(RoulettePacket::decode)
                .consumerMainThread(RoulettePacket::handle)
                .add();
    }
}
