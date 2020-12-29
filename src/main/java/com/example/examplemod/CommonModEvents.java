package com.example.examplemod;

import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommonModEvents {

    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void registerBlocks(final RegistryEvent.Register<Item> itemRegisterEvent) {
        PapaMod.blaster.setRegistryName("blaster");
        itemRegisterEvent.getRegistry().register(PapaMod.blaster);

        PapaMod.blasterShot.setRegistryName("blastershot");
        itemRegisterEvent.getRegistry().register(PapaMod.blasterShot);
    }

    @SubscribeEvent
    public static void onEntityTypeRegistration(RegistryEvent.Register<EntityType<?>> entityTypeRegisterEvent) {
        PapaMod.projectileEntityType.setRegistryName("papamod:projectile");
        entityTypeRegisterEvent.getRegistry().register(PapaMod.projectileEntityType);
    }
}