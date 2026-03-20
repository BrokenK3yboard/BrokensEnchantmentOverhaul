package brokenkeyboard.brokensenchantoverhaul.enchantment;

import brokenkeyboard.brokensenchantoverhaul.ModRegistry;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.LevelBasedValue;

import java.util.Optional;

public record BreachAttackSpeedEffect(LevelBasedValue amount) implements ConditionalAttributeEffect {

    public static final MapCodec<BreachAttackSpeedEffect> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            LevelBasedValue.CODEC.fieldOf("amount").forGetter(BreachAttackSpeedEffect::amount)
    ).apply(instance, BreachAttackSpeedEffect::new));

    @Override
    public ResourceLocation id() {
        return ModRegistry.location("enchantment.breach_attack_speed");
    }

    @Override
    public Holder<Attribute> attribute() {
        return Attributes.ATTACK_SPEED;
    }

    @Override
    public AttributeModifier.Operation operation() {
        return AttributeModifier.Operation.ADD_VALUE;
    }

    @Override
    public MapCodec<? extends ConditionalAttributeEffect> codec() {
        return CODEC;
    }

    @Override
    public double getModifierValue(int enchantmentLevel, ItemStack stack, LivingEntity entity) {
        return Optional.ofNullable(stack.get(ModRegistry.BREACH_USES)).orElse(0) > 0 ? amount.calculate(enchantmentLevel) : 0;
    }
}
