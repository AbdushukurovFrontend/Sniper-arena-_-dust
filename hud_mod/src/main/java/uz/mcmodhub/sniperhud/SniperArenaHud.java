package uz.mcmodhub.sniperhud;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Sniper Arena xaritasi uchun CS2 uslubidagi ekran (HUD).
 * Faqat o'yinchi "sa." bilan boshlanadigan jamoada bo'lganda ishlaydi (datapack shu jamoalarni beradi).
 */
@Mod(SniperArenaHud.MODID)
public class SniperArenaHud {
    public static final String MODID = "sniper_arena_hud";

    public SniperArenaHud() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(ServerEvents.class);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> uz.mcmodhub.sniperhud.client.ClientHud.init(modBus));
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(Net::register);
    }
}
