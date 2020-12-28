package com.example.examplemod;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.network.IPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;

public class Projectile extends ProjectileItemEntity {

    private static final byte VANILLA_IMPACT_STATUS_ID = 3;

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

    /*   see https://wiki.vg/Entity_statuses
         make a cloud of particles at the impact point
     */
    @Override
    public void handleStatusUpdate(byte statusID) {
        if (statusID == VANILLA_IMPACT_STATUS_ID) {
            IParticleData particleData = ParticleTypes.EXPLOSION;

            for(int i = 0; i < 8; ++i) {
                this.world.addParticle(particleData, this.getPosX(), this.getPosY(), this.getPosZ(), 0.0D, 0.0D, 0.0D);
            }
        }
    }

    /**
     * Called when this Entity hits a block or entity.
     */
    @Override
    protected void onImpact(RayTraceResult result) {
        boolean didHitBlock = result.getType() == RayTraceResult.Type.BLOCK;
        boolean isServer = !this.world.isRemote;
        if (didHitBlock && isServer) {
            BlockRayTraceResult blockResult = (BlockRayTraceResult) result;
            BlockPos hitPos = blockResult.getPos();

            LOGGER.info("Hit: {}", hitPos);

            // calls handleStatusUpdate which tells the client to render particles
            this.world.setEntityState(this, VANILLA_IMPACT_STATUS_ID);
            this.remove();
            this.world.createExplosion(
                    null,
                    hitPos.getX(), hitPos.getY(), hitPos.getZ(),
                    2.0F,
                    Explosion.Mode.BREAK
            );
        }
    }
}
