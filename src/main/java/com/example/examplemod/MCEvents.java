package com.example.examplemod;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MCEvents {

    private static final Logger LOGGER = LogManager.getLogger();

   /* @SubscribeEvent
    public static void onTick(final TickEvent.PlayerTickEvent evt) {
        BlockPos pos = evt.player.getPosition();
        Direction face = evt.player.getHorizontalFacing();

        LOGGER.info("pos: {}, face: {}", pos, face);
    }*/

}
