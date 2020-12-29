package com.example.examplemod;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
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

import javax.annotation.Nullable;
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

        //player.swingArm(hand);

        // Throw the Item
        if (!world.isRemote) {
            // Start animation
            isAnimating = true;
            animationFrame = 1;

            int PROJECTILE_COUNT = 5;
            for (int i = 0; i < PROJECTILE_COUNT; i++) {
                ItemStack itemStackToThrow = new ItemStack(PapaMod.blasterShot);
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

    private int animationFrame = 1;
    private boolean isAnimating = false;
    private long lastAnimationTick = 0;
    private int ANIMATION_INTERVAL = 3;
    private int MAX_ANIMATION_FRAMES = 3;

    /**
     * Animation Timer
     *
     * Return which frame of animation to show, depending on the state of the blaster
     */
    public float getAnimationFrame(ItemStack stack, @Nullable ClientWorld worldIn, @Nullable LivingEntity entityIn) {
        if (!isAnimating) {
            animationFrame = 1;
            return 0;
        }

        // Verify world && entity exist
        if (worldIn == null || entityIn == null) {
            return animationFrame;
        }

        // If it's been longer than INTERVAL since the last frame,
        // increment the frames
        long gameTime = worldIn.getGameTime();
        if (worldIn.getGameTime() - lastAnimationTick >= ANIMATION_INTERVAL) {
            lastAnimationTick = gameTime;
            animationFrame += 1;
        }

        // Reset to first frame, if we're at the end
        if (animationFrame >  MAX_ANIMATION_FRAMES) {
            isAnimating = false;
            animationFrame = 1;
        }

        return animationFrame;
    }
}
