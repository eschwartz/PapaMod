package com.example.examplemod;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class CommonProxy {

    public static CustomSword sword;

    @SubscribeEvent
    public void registerBlocks(final RegistryEvent.Register<Item> itemRegisterEvent) {
        sword = new CustomSword();
        sword.setRegistryName("swordmod");
        itemRegisterEvent.getRegistry().register(sword);
    }

}
