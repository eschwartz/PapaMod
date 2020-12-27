package com.example.examplemod;

import net.minecraft.item.Item;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModEvents {

    private static final Logger LOGGER = LogManager.getLogger();

    public static CustomSword sword;

    @SubscribeEvent
    public static void registerBlocks(final RegistryEvent.Register<Item> itemRegisterEvent) {
        sword = CustomSwordMod.sword;
        sword.setRegistryName("swordmod");
        itemRegisterEvent.getRegistry().register(sword);
    }

}
