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

        // Throw the megaman?
        if (!world.isRemote) {
            ItemStack itemStackToThrow = new ItemStack(CustomSwordMod.sword);
            Projectile projectileEntity = new Projectile(world, player);
            projectileEntity.setItem(itemStackToThrow);

            // set the motion of the new entity
            // Copied from MinecraftByExample repo (EmojiItem)
            projectileEntity.func_234612_a_(
                    player,
                    player.rotationPitch,
                    player.rotationYaw,
                    0.0F, 1.5F, 1.0F
            ); //.shoot

            world.addEntity(projectileEntity);
        }


        return super.onItemRightClick(world, player, hand);
    }
}
