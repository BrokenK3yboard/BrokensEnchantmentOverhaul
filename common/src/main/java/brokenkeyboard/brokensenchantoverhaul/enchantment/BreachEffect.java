package brokenkeyboard.brokensenchantoverhaul.enchantment;

import brokenkeyboard.brokensenchantoverhaul.ModRegistry;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;

public record BreachEffect(LevelBasedValue charges) implements EnchantmentEntityEffect {

    public static final MapCodec<BreachEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LevelBasedValue.CODEC.fieldOf("charges").forGetter(BreachEffect::charges)
    ).apply(instance, BreachEffect::new));

    @Override
    public void apply(ServerLevel serverLevel, int enchantLevel, EnchantedItemInUse item, Entity entity, Vec3 vec3) {
        if (entity instanceof LivingEntity living && living.fallDistance >= 8) {
            int uses = (int) charges.calculate(enchantLevel);
            item.itemStack().set(ModRegistry.BREACH_USES, uses);
        }
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
