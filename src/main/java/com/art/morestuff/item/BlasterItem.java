package com.art.morestuff.item;

import com.art.morestuff.MorestuffintheEnd;
import com.art.morestuff.entity.BlasterProjectile;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class BlasterItem extends Item {
    private static final int COOLDOWN_TICKS = 20; // 1 second cooldown (20 ticks per second)

    public BlasterItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        
        // Check if player is on cooldown
        if (player.getCooldowns().isOnCooldown(itemstack)) {
            return InteractionResult.FAIL;
        }

        if (!level.isClientSide()) {
            // Calculate direction based on player's look direction
            Vec3 lookDirection = player.getLookAngle();
            Vec3 eyePosition = player.getEyePosition();
            
            // Spawn the projectile
            BlasterProjectile projectile = new BlasterProjectile(
                MorestuffintheEnd.BLASTER_PROJECTILE.get(),
                player,
                level
            );
            
            // Set initial position slightly in front of player's eyes
            Vec3 spawnPos = eyePosition.add(lookDirection.scale(0.5));
            projectile.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
            
            // Set velocity based on look direction
            double speed = 1.5; // Adjust speed as needed
            projectile.shoot(lookDirection.x, lookDirection.y, lookDirection.z, (float)speed, 0.0f);
            
            level.addFreshEntity(projectile);
            
            // Play sound
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.5F);
        }
        
        // Set cooldown
        player.getCooldowns().addCooldown(itemstack, COOLDOWN_TICKS);
        
        return InteractionResult.SUCCESS;
    }
}

