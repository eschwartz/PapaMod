package com.example.examplemod;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.horse.LlamaEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Blaster extends SwordItem {

    private static final Logger LOGGER = LogManager.getLogger();

    public Blaster() {
        super(ItemTier.DIAMOND, 3, -3.0F, (new Item.Properties()).group(ItemGroup.COMBAT));
    }

    /**
     * Called to trigger the item's "innate" right click behavior.
     * To handle when this item is used on a Block, see
     * {@link #onItemUse}.
     */
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        //spawnLlama(world, player);

        // Throw the Item
        if (!world.isRemote) {
            int PROJECTILE_COUNT = 10;
            for (int i = 0; i < PROJECTILE_COUNT; i++) {
                ItemStack itemStackToThrow = new ItemStack(PapaMod.blaster);
                BlasterShotEntity blasterShotEntity = new BlasterShotEntity(world, player);
                blasterShotEntity.setItem(itemStackToThrow);

                // set the motion of the new entity
                // Copied from MinecraftByExample repo (EmojiItem)
                blasterShotEntity.func_234612_a_(
                        player,
                        player.rotationPitch + (random.nextFloat() * 20) - 10,
                        player.rotationYaw + (random.nextFloat() * 20) - 10,
                        0.0F, 1.5F, 1.0F
                ); //.shoot

                world.addEntity(blasterShotEntity);
            }
        }


        return super.onItemRightClick(world, player, hand);
    }

    private void spawnLlama(World world, PlayerEntity player) {
        player.sendStatusMessage(
                new StringTextComponent("Happy Llamakkah"),
                true
        );

        BlockPos playerPos = player.getPosition();
        Direction facing = player.getHorizontalFacing();

        // Spwan the llama in front of the player
        LlamaEntity llama = new LlamaEntity(EntityType.LLAMA, world);
        BlockPos llamaPos = playerPos.offset(facing, 3);
        llama.setPosition(llamaPos.getX(), llamaPos.getY(), llamaPos.getZ());
        world.addEntity(llama);

        // Spit, after a couple seconds
        ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
        exec.schedule(() -> {
            player.sendStatusMessage(
                    new StringTextComponent("ATTACK!!!"),
                    true
            );
            LOGGER.info("ATTACK!!!");
            llama.attackEntityWithRangedAttack(player, 0);
        }, 2, TimeUnit.SECONDS);
    }
}
