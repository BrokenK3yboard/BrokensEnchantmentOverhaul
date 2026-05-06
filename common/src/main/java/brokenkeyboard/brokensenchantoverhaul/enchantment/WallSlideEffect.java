package brokenkeyboard.brokensenchantoverhaul.enchantment;

import brokenkeyboard.brokensenchantoverhaul.platform.Services;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public record WallSlideEffect(LevelBasedValue slideTime) implements EnchantmentEntityEffect {

    public static final MapCodec<WallSlideEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LevelBasedValue.CODEC.fieldOf("slideTime").forGetter(WallSlideEffect::slideTime)
    ).apply(instance, WallSlideEffect::new));

    @Override
    public void apply(ServerLevel level, int enchantLevel, EnchantedItemInUse item, Entity entity, Vec3 pos) {
        if (!(entity instanceof LivingEntity living) || !shouldSlide(level, living)) return;

        int time = Services.PLATFORM.getWallSlideTicks(living);
        Services.PLATFORM.setWallSlideTicks(living, slideTime.calculate(enchantLevel) > time ? time + 1 : -1);
        Vec3 movement = entity.getDeltaMovement();
        entity.setDeltaMovement(movement.x(), -0.05, movement.z());

        if (entity.fallDistance > 1) {
            entity.fallDistance = 1;
        }
        entity.hurtMarked = true;
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }

    public static boolean shouldSlide(Level level, LivingEntity living) {
        return Services.PLATFORM.getWallSlideTicks(living) >= 0 &&
                !living.isFallFlying() && living.fallDistance >= 1 &&
                living.getDeltaMovement().y() < 0 &&
                level.getBlockCollisions(living, living.getBoundingBox().inflate(0.25, 0, 0.25)).iterator().hasNext();
    }
}
