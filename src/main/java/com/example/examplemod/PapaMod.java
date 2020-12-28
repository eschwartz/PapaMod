package com.example.examplemod;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(PapaMod.MODID)
public class PapaMod {

    public static final String MODID = "papamod";

    private static final Logger LOGGER = LogManager.getLogger();

    public static Blaster blaster = new Blaster();

    public static EntityType<BlasterShotEntity> projectileEntityType = EntityType.Builder
            .<BlasterShotEntity>create(BlasterShotEntity::new, EntityClassification.MISC)
            .size(0.5F, 0.5F)
            .build("papamod:projectile");

    // get a reference to the event bus for this mod;  Registration events are fired on this bus.
    public static IEventBus MOD_EVENT_BUS;


    public PapaMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        MOD_EVENT_BUS = FMLJavaModLoadingContext.get().getModEventBus();

        MOD_EVENT_BUS.register(CommonModEvents.class);

        DistExecutor.runWhenOn(Dist.CLIENT, () -> PapaMod::registerClientEvents);

        MinecraftForge.EVENT_BUS.addListener(this::onTick);
    }



    private void onTick(final TickEvent.PlayerTickEvent evt) {
        BlockPos pos = evt.player.getPosition();
        Direction face = evt.player.getHorizontalFacing();

        if (evt.player.world.getGameTime() % 20 == 0) {
            //LOGGER.info("pos: {}, face: {}", pos, face);`
        }
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("In PapaMod setup");
    }

    public static void registerClientEvents() {
        MOD_EVENT_BUS.register(ClientModEvents.class);
    }
}
