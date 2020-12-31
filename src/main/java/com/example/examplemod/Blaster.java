package com.example.examplemod;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class Blaster extends ShootableItem {

    private static final Logger LOGGER = LogManager.getLogger();
    protected Random rand = new Random();
    private final ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(5);

    protected final float ticksPerCharge = 5F;
    protected final int maxAmmoPerShot = 5;
    protected int ammoCharged = 0;

    enum AnimationFrame {
        DEFAULT,
        PULLBACK_1,
        PULLBACK_2,
        PULLBACK_3,
    }

    private AnimationFrame currentAnimationFrame = AnimationFrame.DEFAULT;

    public SoundEvent BlasterLoadSound = new SoundEvent(
            new ResourceLocation("papamod", "blaster_load")
    );
    public SoundEvent BlasterFireSound = new SoundEvent(
            new ResourceLocation("papamod", "blaster_fire")
    );

    public Blaster() {
        super(new Item.Properties().maxStackSize(1).group(ItemGroup.COMBAT));
    }

    /**
     * Called when right-click is first pressed down.
     * <p>
     * Just checks if we have ammo, before allowing to continue.
     *
     * @param worldIn
     * @param playerIn
     * @param handIn
     * @return
     */
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        // Check if we have ammo
        ItemStack blaster = playerIn.getHeldItem(handIn);
        ItemStack ammo = playerIn.findAmmo(blaster);

        // Fail if no ammo
        if (ammo.isEmpty() && !playerIn.isCreative()) {
            playerIn.sendStatusMessage(
                    new StringTextComponent("Out of ammo!"),
                    true
            );
            return ActionResult.resultFail(blaster);
        }

        playerIn.setActiveHand(handIn);
        return ActionResult.resultPass(blaster);
    }


    /**
     * Called each tick, while blaster is charging.
     * <p>
     * Remove ammo from inventory, as they're loaded in
     * the blaster gun.
     *
     * @param stack    The Item being used
     * @param entity   The Player using the item
     * @param timeLeft Ticks remaining of item usage duration
     */
    @Override
    public void onUsingTick(ItemStack stack, LivingEntity entity, int timeLeft) {
        if (!(entity instanceof PlayerEntity)) return;
        PlayerEntity playerIn = (PlayerEntity) entity;

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
        int potentialChargeCount = (int) Math.floor(ticksInUse / ticksPerCharge);


        if (isTimeForNewCharge && potentialChargeCount < maxAmmoPerShot) {
            LOGGER.info("Ammo charged!");

            ItemStack ammo = getPlayerAmmo(playerIn);

            if (!ammo.isEmpty()) {
                // Shave the ammo count, for when we actually shoot
                ammoCharged += 1;

                // Sound effect
                playerIn.world.playSound(
                        null,
                        playerIn.getPosition(),
                        BlasterLoadSound,
                        SoundCategory.PLAYERS,
                        0.6F, 2.0F + rand.nextFloat() * 0.4F
                );


                // Reduce inventory by 1
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
        } else if (ticksInUse <= 2) {
            currentAnimationFrame = AnimationFrame.PULLBACK_2;
        } else {
            currentAnimationFrame = AnimationFrame.PULLBACK_3;
        }

    }

    /**
     * Called when charging is released
     * (right-click is let go)
     *
     * Check how much ammo has been charged, and shoot it.
     */
    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
        if (!(entityLiving instanceof PlayerEntity)) return;
        PlayerEntity playerIn = (PlayerEntity) entityLiving;

        Hand handIn = playerIn.getActiveHand();

        // Check if we have ammo
        ItemStack blaster = playerIn.getHeldItem(handIn);
        ItemStack ammo = playerIn.findAmmo(blaster);

        // Do nothing if if no ammo has been charged
        if (ammoCharged == 0) {
            animateReset(30);
            return;
        }

        // Only run on server....
        if (!worldIn.isRemote) {

            // For each charged amo...
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


                // Play sound effect
                int thisIndex = i;
                exec.schedule(
                        () -> playerIn.world.playSound(
                                null,
                                playerIn.getPosition(),
                                BlasterFireSound,
                                SoundCategory.PLAYERS,
                                0.8F / thisIndex, 2.0F + rand.nextFloat() * 0.4F
                        ),
                        i * 25L, TimeUnit.MILLISECONDS);
            }

            // Reset charged ammo, for next time
            ammoCharged = 0;

            // Animate blaster back to default frame
            animateReset(30);
        }
    }


    /**
     * How long it takes to use or consume an item.
     * <p>
     * This value is arbitrary, but allows us to calculate
     * how many ticks we've been charging
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
     * Return the blaster back to the DEFAULT animation frame
     */
    private void animateReset(int msAnimationDelay) {
        if (currentAnimationFrame.ordinal() == 0) {
            return;
        }

        currentAnimationFrame = AnimationFrame.values()[currentAnimationFrame.ordinal() - 1];

        exec.schedule(() -> animateReset(msAnimationDelay), msAnimationDelay, TimeUnit.MILLISECONDS);
    }


    /**
     * Return which frame of animation to show, depending on the state of the blaster
     */
    public float getAnimationFrame(ItemStack stack, @Nullable ClientWorld worldIn, @Nullable LivingEntity entityIn) {
        // Just use whatever currentAnimationFrame is set to
        // (as integer value)
        return currentAnimationFrame.ordinal();
    }


    /**
     * Get the predicate to match ammunition when searching the player's inventory, not their main/offhand
     */
    @Override
    public Predicate<ItemStack> getInventoryAmmoPredicate() {
        return (stack) -> stack.getItem() == PapaMod.blasterShot;
    }

    /**
     * ¯\_(ツ)_/¯
     * <p>
     * Used for shooting projectiles, not sure what it does exactly.
     *
     * @return int
     */
    @Override
    public int func_230305_d_() {
        // This is the value used by BowItem, something to do with target distance? or cooldown?
        return 15;
    }
}
