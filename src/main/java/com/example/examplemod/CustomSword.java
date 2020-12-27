package com.example.examplemod;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.horse.LlamaEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CustomSword extends SwordItem {

    private static final Logger LOGGER = LogManager.getLogger();

    public CustomSword() {
        super(ItemTier.DIAMOND, 3, -3.0F, (new Item.Properties()).group(ItemGroup.COMBAT));
    }

    /**
     * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see
     * {@link #onItemUse}.
     *
     * @param world
     * @param player
     * @param hand
     */
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        player.sendStatusMessage(
                new StringTextComponent("Happy Llamakkah"),
                true
        );

        BlockPos playerPos = player.getPosition();
        Direction facing = player.getHorizontalFacing();

        // Determine llama position, based on the player position and direction
        int offset = 3;
        int nextX = playerPos.getX();
        int nextY = playerPos.getY() + 8;
        int nextZ = playerPos.getZ();
        switch (facing) {
            case EAST:
                nextX += offset;
                break;
            case WEST:
                nextX -= offset;
                break;
            case NORTH:
                nextZ -= offset;
                break;
            case SOUTH:
                nextZ += offset;
                break;
            default:
                LOGGER.info("Unknown facing: {}", facing);
        }


        // Spwan the llama
        LlamaEntity llama = new LlamaEntity(EntityType.LLAMA, world);
        llama.setPosition(
                nextX,
                nextY,
                nextZ
        );
        world.addEntity(llama);

        // Spit, after a couple seconds
        ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
        exec.schedule(() -> {
            LOGGER.info("ATTACK!!!");
            llama.attackEntityWithRangedAttack(player, 0);
        }, 2, TimeUnit.SECONDS);

        // Throw the megaman?
        CustomSword sword = CustomSwordMod.sword;
        if (!world.isRemote) {

        }


        return super.onItemRightClick(world, player, hand);
    }
}
