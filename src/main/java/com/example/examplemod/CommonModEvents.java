package com.example.examplemod;

import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommonModEvents {

    private static final Logger LOGGER = LogManager.getLogger();

    public static Blaster sword;

    @SubscribeEvent
    public static void registerBlocks(final RegistryEvent.Register<Item> itemRegisterEvent) {
        sword = PapaMod.blaster;
        sword.setRegistryName("blaster");
        itemRegisterEvent.getRegistry().register(sword);
    }

    @SubscribeEvent
    public static void onEntityTypeRegistration(RegistryEvent.Register<EntityType<?>> entityTypeRegisterEvent) {
        LOGGER.warn("--------------");
        LOGGER.warn("--------------");
        LOGGER.warn("ent type reg");
        LOGGER.warn("--------------");
        LOGGER.warn("--------------");
        PapaMod.projectileEntityType.setRegistryName("papamod:projectile");
        entityTypeRegisterEvent.getRegistry().register(PapaMod.projectileEntityType);
    }
}