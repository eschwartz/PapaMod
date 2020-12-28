package com.example.examplemod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientModEvents {
    private static final Logger LOGGER = LogManager.getLogger();


    @SubscribeEvent
    public static void onClientSetupEvent(FMLClientSetupEvent event) {
        // Register the custom renderer for each entity
        RenderingRegistry.registerEntityRenderingHandler(
                PapaMod.projectileEntityType,
                erm -> new SpriteRenderer<>(erm, Minecraft.getInstance().getItemRenderer())
        );
    }
}
