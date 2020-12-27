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

public class CustomSword extends SwordItem {


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

        Entity llama = new LlamaEntity(EntityType.LLAMA, world);
        llama.setPosition(
                playerPos.getX() + facing.getXOffset() * 4,
                playerPos.getY() + facing.getYOffset() * 4,
                playerPos.getZ() + 1
        );

        world.addEntity(llama);

        return super.onItemRightClick(world, player, hand);
    }


    @Override
    public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
        player.sendStatusMessage(
                new StringTextComponent("S$WING S$WING"),
                true
        );
        return false;
    }
}
