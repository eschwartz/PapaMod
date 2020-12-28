package com.example.examplemod;

import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommonModEvents {

    private static final Logger LOGGER = LogManager.getLogger();

    public static CustomSword sword;

    @SubscribeEvent
    public static void registerBlocks(final RegistryEvent.Register<Item> itemRegisterEvent) {
        sword = CustomSwordMod.sword;
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
        CustomSwordMod.projectileEntityType.setRegistryName("swordmod:projectile");
        entityTypeRegisterEvent.getRegistry().register(CustomSwordMod.projectileEntityType);
    }
}