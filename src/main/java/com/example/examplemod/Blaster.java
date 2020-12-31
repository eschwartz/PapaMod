package com.example.examplemod;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
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

    enum AnimationFrame {
        DEFAULT,
        PULLBACK_1,
        PULLBACK_2,
        PULLBACK_3,
    }

    private AnimationFrame currentAnimationFrame = AnimationFrame.DEFAULT;

    /**
     * Animation Timer
     *
     * Return which frame of animation to show, depending on the state of the blaster
     */
    public float getAnimationFrame(ItemStack stack, @Nullable ClientWorld worldIn, @Nullable LivingEntity entityIn) {
        return currentAnimationFrame.ordinal();
    }

    protected boolean isInUse = false;

    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        // Check if we have ammo
        ItemStack blaster = playerIn.getHeldItem(handIn);
        ItemStack ammo = playerIn.findAmmo(blaster);

//        if (isInUse) {
//            return ActionResult.resultFail(blaster);
//        }

        // Fail if no ammo
        if (ammo.isEmpty() && !playerIn.isCreative()) {
            playerIn.sendStatusMessage(
                    new StringTextComponent("Out of ammo!"),
                    true
            );
            return ActionResult.resultFail(blaster);
        }

        isInUse = true;

        playerIn.setActiveHand(handIn);
        return ActionResult.resultPass(blaster);
    }

    /**
     * How long it takes to use or consume an item
     */
    @Override
    public int getUseDuration(ItemStack stack) {
        final int ARBITRARY_LONG_TIME = 72000;
        return ARBITRARY_LONG_TIME;
    }

    protected ItemStack getPlayerAmmo(PlayerEntity playerIn) {
        Hand handIn = playerIn.getActiveHand();
        ItemStack blaster = playerIn.getHeldItem(handIn);
        ItemStack ammo = playerIn.findAmmo(blaster);

        return ammo;
    }

    /**
     * Called when the player stops using an Item (stops holding the right mouse button).
     */
    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
        if (!(entityLiving instanceof PlayerEntity)) return;
        PlayerEntity playerIn = (PlayerEntity)entityLiving;

        Hand handIn = playerIn.getActiveHand();

        // Check if we have ammo
        ItemStack blaster = playerIn.getHeldItem(handIn);
        ItemStack ammo = playerIn.findAmmo(blaster);

        // Do nothing if if no ammo charged
        if (ammo.isEmpty() && !playerIn.isCreative()) {
            animateDown(30);
            return;
        }

        // Shoot!
        if (!worldIn.isRemote) {

            for (int i = 0; i < ammoCharged; i++) {
                // Create BlasterShot (ammo items)
                ItemStack itemStackToThrow = new ItemStack(PapaMod.blasterShot);
                BlasterShotEntity blasterShotEntity = new BlasterShotEntity(worldIn, playerIn);
                blasterShotEntity.setItem(itemStackToThrow);

                // set the motion of the new entity
                // Copied from MinecraftByExample repo (EmojiItem)
                blasterShotEntity.func_234612_a_(
                        playerIn,
                        playerIn.rotationPitch + (random.nextFloat() * 30) - 15,
                        playerIn.rotationYaw + (random.nextFloat() * 30) - 15,
                        0.0F, 1.5F, 1.0F
                ); //.shoot

                // Add Blaster Shot to world
                worldIn.addEntity(blasterShotEntity);
            }

            ammoCharged = 0;

            // Animate blaster back to default frame
            animateDown(30);

            // TODO:
            // [x] Reduce ammo while charging
            //      - Or how about consume ammo *while* it's charging
            //        in creative could consume and replace
            // [x] animate while charging (pull back)
            // - particles from the blaster
            //      wouldn't render?
            // - Recipes
            // - Code cleanup
            // sound effects
            // Bundle and share?

        }
    }

    private final ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(5);


    private void animateDown(int msAnimationDelay) {
        if (currentAnimationFrame.ordinal() == 0) {
            return;
        }

        currentAnimationFrame = AnimationFrame.values()[currentAnimationFrame.ordinal() - 1];

        exec.schedule(() -> animateDown(msAnimationDelay), msAnimationDelay, TimeUnit.MILLISECONDS);
    }

    protected final float ticksPerCharge = 5F;
    protected final int maxAmmoPerShot = 5;

    protected int ammoCharged = 0;

    /**
     * Remove ammo from inventory, while charging
     *
     * Called each tick while using an item.
     *
     * @param stack  The Item being used
     * @param entity The Player using the item
     * @param timeLeft  Ticks remaining of item usage duration
     */
    @Override
    public void onUsingTick(ItemStack stack, LivingEntity entity, int timeLeft) {
        if (!(entity instanceof PlayerEntity)) return;
        PlayerEntity playerIn = (PlayerEntity)entity;

        // Only do this on the server
        if (playerIn.world.isRemote) {
            return;
        }

        // Calculate how long we've been holding down right-click button
        int ticksInUse = this.getUseDuration(stack) - timeLeft;

        // If it's been more than `ticksPerCharge` ticks,
        // then we're ready to remove an item from inventory
        boolean isTimeForNewCharge = ticksInUse % ticksPerCharge < 1;

        // Check how many ammo we've charged up
        // (so we don't remove more than our max)
        int potentialChargeCount = (int)Math.floor(ticksInUse / ticksPerCharge);


        if (isTimeForNewCharge && potentialChargeCount < maxAmmoPerShot) {
            LOGGER.info("Ammo charged!");

            // Remove an ammo from inventory
            // (note, if w
            ItemStack ammo = getPlayerAmmo(playerIn);

            if (!ammo.isEmpty()) {
                // Shave the ammo count, for when we actually shoot
                ammoCharged += 1;

                // Reduce inventory
                if (!playerIn.isCreative()) {
                    ammo.shrink(1);
                }
            }
            if (ammo.isEmpty()) {
                playerIn.inventory.deleteStack(ammo);
            }
        }


        // Update animation frame (blaster pulls back)
        if (ticksInUse <= 1) {
            currentAnimationFrame = AnimationFrame.PULLBACK_1;
        }
        else if (ticksInUse <= 2) {
            currentAnimationFrame = AnimationFrame.PULLBACK_2;
        }
        else {
            currentAnimationFrame = AnimationFrame.PULLBACK_3;
        }

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

   /* private int animationFrame = 0;
    private boolean isAnimating = false;
    private long lastAnimationTick = 0;
    private final int ANIMATION_INTERVAL = 1;
    private final int MAX_ANIMATION_FRAMES = 9;

    *//**
     * Animation Timer
     *
     * Return which frame of animation to show, depending on the state of the blaster
     *//*
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
    }*/

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
}
