package com.example.examplemod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

public class ClientModEvents {
    private static final Logger LOGGER = LogManager.getLogger();


    @SubscribeEvent
    public static void onClientSetupEvent(FMLClientSetupEvent event) {
        // Register the custom renderer for each entity
        RenderingRegistry.registerEntityRenderingHandler(
                PapaMod.projectileEntityType,
                erm -> new SpriteRenderer<>(erm, Minecraft.getInstance().getItemRenderer())
        );


        // we need to attach the PropertyOverride to the Item, but there are two things to be careful of:
        // 1) We should do this on a client installation only, not on a DedicatedServer installation.  Hence we need to use
        //    FMLClientSetupEvent.
        // 2) FMLClientSetupEvent is multithreaded but ItemModelsProperties is not multithread-safe.  So we need to use the enqueueWork method,
        //    which lets us register a function for synchronous execution in the main thread after the parallel processing is completed
        event.enqueueWork(ClientModEvents::registerPropertyOverride);
    }

    // We use a PropertyOverride for this item to change the appearance depending on the state of the property.
    //  See ItemNBTanimationTimer for more information.
    // ItemNBTanimationTimer() is used as a lambda function to calculate the current chargefraction during rendering
    public static void registerPropertyOverride() {
        ItemModelsProperties.registerProperty(
                PapaMod.blaster,
                new ResourceLocation("frame"),
                (stack, worldIn, entityIn) -> PapaMod.blaster.getAnimationFrame(stack, worldIn, entityIn)
        );
    }
}
