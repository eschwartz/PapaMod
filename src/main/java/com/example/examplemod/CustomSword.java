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
                new StringTextComponent("Happy Llamukkah"),
                true
        );

        BlockPos playerPos = player.getPosition();
        Direction facing = player.getHorizontalFacing();

        int offset = 3;
        int nextX = playerPos.getX();
        int nextY = playerPos.getY() + 3;
        int nextZ = playerPos.getZ();
        switch (facing) {
            case EAST:
                LOGGER.info("east");
                nextX += offset;
                break;
            case WEST:
                LOGGER.info("west");
                nextX -= offset;
                break;
            case NORTH:
                LOGGER.info("north");
                nextZ -= offset;
                break;
            case SOUTH:
                LOGGER.info("south");
                nextZ += offset;
                break;
            default:
                LOGGER.info("Unknown facing: {}", facing);
        }

        LOGGER.info("Player pos: {}, {}, {}", playerPos.getX(), playerPos.getY(), playerPos.getZ());
        LOGGER.info("Llama pos: {}, {}, {}", nextX, nextY, nextZ);

        Entity llama = new LlamaEntity(EntityType.LLAMA, world);
        llama.setPosition(
                nextX,
                nextY,
                nextZ
        );

        world.addEntity(llama);

        return super.onItemRightClick(world, player, hand);
    }
}
