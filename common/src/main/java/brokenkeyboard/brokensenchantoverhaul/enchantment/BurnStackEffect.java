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
import net.minecraft.world.phys.Vec3;

public record BurnStackEffect(LevelBasedValue stackIncrease, LevelBasedValue maxStacks) implements EnchantmentEntityEffect {

    public static final MapCodec<BurnStackEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LevelBasedValue.CODEC.fieldOf("stack_increase").forGetter(BurnStackEffect::stackIncrease),
            LevelBasedValue.CODEC.fieldOf("max_stacks").forGetter(BurnStackEffect::maxStacks)
    ).apply(instance, BurnStackEffect::new));

    @Override
    public void apply(ServerLevel serverLevel, int enchantLevel, EnchantedItemInUse enchantedItemInUse, Entity entity, Vec3 vec3) {
        if (entity instanceof LivingEntity living) {
            int burnStacks = Services.PLATFORM.getBurnStacks(living);
            Services.PLATFORM.setBurnStacks(living, (int) Math.min(burnStacks + stackIncrease.calculate(enchantLevel), maxStacks.calculate(enchantLevel)));
        }
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
