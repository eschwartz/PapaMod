package com.example.examplemod;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.horse.LlamaEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class CustomSword extends SwordItem {


    public CustomSword() {
        super(ItemTier.DIAMOND, 3, -3.0F, (new Item.Properties()).group(ItemGroup.COMBAT));
    }

    

    /**
     * Called on right-click, when pointed at a block or something
     */
    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        PlayerEntity player = context.getPlayer();
        player.sendStatusMessage(
                new StringTextComponent("Happy Llamukkah"),
                true
        );

        BlockPos playerPos = player.getPosition();
        Direction facing = player.getHorizontalFacing();


        World world = context.getWorld();
        Entity llama = new LlamaEntity(EntityType.LLAMA, world);



        llama.setPosition(
                playerPos.getX() + facing.getXOffset() * 2,
                playerPos.getY() + facing.getYOffset() * 2,
                playerPos.getZ()
        );
        world.addEntity(llama);

        return super.onItemUse(context);
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
