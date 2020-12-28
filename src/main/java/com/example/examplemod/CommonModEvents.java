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
        sword.setRegistryName("swordmod");
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

    // register our entity types
/*    @SubscribeEvent
    public static void onEntityTypeRegistration(RegistryEvent.Register<EntityType<?>> entityTypeRegisterEvent) {
        emojiEntityType = EntityType.Builder.<EmojiEntity>create(EmojiEntity::new, EntityClassification.MISC)
                .size(0.25F, 0.25F)
                .build("minecraftbyexample:mbe81a_emoji_type_registry_name");
        emojiEntityType.setRegistryName("minecraftbyexample:mbe81a_emoji_type_registry_name");
        entityTypeRegisterEvent.getRegistry().register(emojiEntityType);

        boomerangEntityType = EntityType.Builder.<BoomerangEntity>create(BoomerangEntity::new, EntityClassification.MISC)
                .size(0.25F, 0.25F)
                .build("minecraftbyexample:mbe81b_boomerang_type_registry_name");
        boomerangEntityType.setRegistryName("minecraftbyexample:mbe81b_boomerang_type_registry_name");
        entityTypeRegisterEvent.getRegistry().register(boomerangEntityType);
    }*/

}
