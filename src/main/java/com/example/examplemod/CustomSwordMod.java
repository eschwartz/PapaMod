package com.example.examplemod;

import cpw.mods.modlauncher.EnumerationHelper;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(CustomSwordMod.MODID)
public class CustomSwordMod {

    public static final String MODID = "swordmod";

    private static final Logger LOGGER = LogManager.getLogger();

    // get a reference to the event bus for this mod;  Registration events are fired on this bus.
    public static IEventBus MOD_EVENT_BUS;


    public CustomSwordMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        MOD_EVENT_BUS = FMLJavaModLoadingContext.get().getModEventBus();

        MOD_EVENT_BUS.register(CommonProxy.class);
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("In CustomSwordMod setup");
    }
}
