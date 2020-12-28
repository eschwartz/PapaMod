package com.example.examplemod;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.network.IPacket;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;

public class Projectile extends ProjectileItemEntity {

    public Projectile(World world, LivingEntity livingEntity) {
        super(CustomSwordMod.projectileEntityType, livingEntity, world);
    }

    public Projectile(EntityType<? extends Projectile> entityType, World world) {
        super(entityType, world);
    }



    @Override
    protected Item getDefaultItem() {
        return CustomSwordMod.sword;
    }

    // If you forget to override this method, the default vanilla method will be called.
    // This sends a vanilla spawn packet, which is then silently discarded when it reaches the client.
    //  Your entity will be present on the server and can cause effects, but the client will not have a copy of the entity
    //    and hence it will not render.
    @Nonnull
    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
