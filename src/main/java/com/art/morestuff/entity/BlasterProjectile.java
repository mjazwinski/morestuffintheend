package com.art.morestuff.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class BlasterProjectile extends Projectile {
    private static final double MAX_RANGE = 30.0; // 30 blocks
    private static final float DAMAGE = 11.0f; // 11 damage points (5.5 hearts)
    
    private Vec3 startPos;
    private int ticksAlive = 0;

    public BlasterProjectile(EntityType<? extends BlasterProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public BlasterProjectile(EntityType<? extends BlasterProjectile> entityType, LivingEntity owner, Level level) {
        super(entityType, level);
        this.setOwner(owner);
        this.startPos = owner.position();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        // No synced data needed
    }

    @Override
    public void tick() {
        super.tick();
        
        ticksAlive++;
        
        // Check if we've traveled too far
        if (startPos != null) {
            double distanceTraveled = this.position().distanceTo(startPos);
            if (distanceTraveled >= MAX_RANGE) {
                this.discard();
                return;
            }
        }
        
        // Check for collisions
        HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        
        if (hitresult.getType() != HitResult.Type.MISS) {
            this.onHit(hitresult);
        }
        
        // Update position
        Vec3 vec3 = this.getDeltaMovement();
        double d0 = this.getX() + vec3.x;
        double d1 = this.getY() + vec3.y;
        double d2 = this.getZ() + vec3.z;
        
        this.setPos(d0, d1, d2);
        
        // Add particle trail (red/orange laser effect)
        if (this.level().isClientSide()) {
            for (int i = 0; i < 2; i++) {
                this.level().addParticle(
                    ParticleTypes.FLAME,
                    this.getX() + (this.random.nextDouble() - 0.5) * 0.1,
                    this.getY() + (this.random.nextDouble() - 0.5) * 0.1,
                    this.getZ() + (this.random.nextDouble() - 0.5) * 0.1,
                    0.0, 0.0, 0.0
                );
            }
        }
        
        // Apply gravity and air resistance
        this.setDeltaMovement(vec3.scale(0.99));
        
        // Despawn after 5 seconds as safety
        if (ticksAlive > 100) {
            this.discard();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        
        Entity entity = result.getEntity();
        Entity owner = this.getOwner();
        
        // Don't damage the owner
        if (owner != null && entity == owner) {
            return;
        }
        
        // Deal damage
        if (!this.level().isClientSide()) {
            DamageSource damageSource = this.damageSources().mobProjectile(this, owner instanceof LivingEntity ? (LivingEntity)owner : null);
            entity.hurt(damageSource, DAMAGE);
        }
        
        // Spawn hit particles
        if (this.level().isClientSide()) {
            for (int i = 0; i < 8; i++) {
                this.level().addParticle(
                    ParticleTypes.CRIT,
                    this.getX(),
                    this.getY(),
                    this.getZ(),
                    (this.random.nextDouble() - 0.5) * 0.2,
                    (this.random.nextDouble() - 0.5) * 0.2,
                    (this.random.nextDouble() - 0.5) * 0.2
                );
            }
        }
        
        this.discard();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        
        // Spawn impact particles
        if (this.level().isClientSide()) {
            for (int i = 0; i < 8; i++) {
                this.level().addParticle(
                    ParticleTypes.SMOKE,
                    this.getX(),
                    this.getY(),
                    this.getZ(),
                    (this.random.nextDouble() - 0.5) * 0.2,
                    (this.random.nextDouble() - 0.5) * 0.2,
                    (this.random.nextDouble() - 0.5) * 0.2
                );
            }
        }
        
        this.discard();
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        return super.canHitEntity(entity) && !entity.noPhysics;
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        this.startPos = this.position();
    }
}

