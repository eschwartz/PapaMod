package com.example.examplemod;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.horse.LlamaEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.tags.ItemTags;
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
import java.util.function.Predicate;

public class Blaster extends ShootableItem {

    private static final Logger LOGGER = LogManager.getLogger();

    public Blaster() {
        super(new Item.Properties().maxStackSize(1).group(ItemGroup.COMBAT));
    }

    /**
     * Called to trigger the item's "innate" right click behavior.
     * To handle when this item is used on a Block, see
     * {@link #onItemUse}.
     */
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity playerIn, Hand handIn) {
        // Check if we have ammo
        ItemStack blaster = playerIn.getHeldItem(handIn);
        ItemStack ammo = playerIn.findAmmo(blaster);

        // Fail if no ammo
        if (ammo.isEmpty()) {
           playerIn.sendStatusMessage(
                   new StringTextComponent("Out of ammo!"),
                   true
           );
           return ActionResult.resultFail(blaster);
        }

        // Shoot!
        if (!world.isRemote && !isAnimating) {
            isAnimating = true;
            animationFrame = 0;

            // Can shoot up to 5 ammo
            int projectileCount = Math.min(5, ammo.getCount());

            for (int i = 0; i < projectileCount; i++) {
                // Create BlasterShot (ammo items)
                ItemStack itemStackToThrow = new ItemStack(PapaMod.blasterShot);
                BlasterShotEntity blasterShotEntity = new BlasterShotEntity(world, playerIn);
                blasterShotEntity.setItem(itemStackToThrow);

                // set the motion of the new entity
                // Copied from MinecraftByExample repo (EmojiItem)
                blasterShotEntity.func_234612_a_(
                        playerIn,
                        playerIn.rotationPitch + (random.nextFloat() * 20) - 10,
                        playerIn.rotationYaw + (random.nextFloat() * 20) - 10,
                        0.0F, 1.5F, 1.0F
                ); //.shoot

                // Add Blaster Shot to world
                world.addEntity(blasterShotEntity);
            }

            // Remove ammo from inventory
            ammo.shrink(projectileCount);
            if (ammo.isEmpty()) {
                playerIn.inventory.deleteStack(ammo);
            }
        }


        return super.onItemRightClick(world, playerIn, handIn);
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

    private int animationFrame = 0;
    private boolean isAnimating = false;
    private long lastAnimationTick = 0;
    private final int ANIMATION_INTERVAL = 1;
    private final int MAX_ANIMATION_FRAMES = 9;

    /**
     * Animation Timer
     *
     * Return which frame of animation to show, depending on the state of the blaster
     */
    public float getAnimationFrame(ItemStack stack, @Nullable ClientWorld worldIn, @Nullable LivingEntity entityIn) {
        if (!isAnimating) {
            animationFrame = 0;
            return animationFrame;
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
        if (animationFrame >  MAX_ANIMATION_FRAMES - 1) {
            isAnimating = false;
            animationFrame = 0;
        }

        return animationFrame;
    }

    /**
     * Get the predicate to match ammunition when searching the player's inventory, not their main/offhand
     */
    @Override
    public Predicate<ItemStack> getInventoryAmmoPredicate() {
        return (stack) -> {
            return stack.getItem() == PapaMod.blasterShot;
        };
    }

    @Override
    public int func_230305_d_() {
        // ¯\_(ツ)_/¯
        // This is the value used by BowItem, something to do with target distance? or cooldown?
        return 15;
    }

    // Animation of player?!
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }
}
