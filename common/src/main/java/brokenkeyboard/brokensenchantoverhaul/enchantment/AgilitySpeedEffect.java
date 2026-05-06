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

public record AgilitySpeedEffect(LevelBasedValue baseAmount, LevelBasedValue missingArmorScaling) implements ConditionalAttributeEffect {

    public static final MapCodec<AgilitySpeedEffect> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            LevelBasedValue.CODEC.fieldOf("baseAmount").forGetter(AgilitySpeedEffect::baseAmount),
            LevelBasedValue.CODEC.fieldOf("missingArmorScaling").forGetter(AgilitySpeedEffect::missingArmorScaling)
    ).apply(instance, AgilitySpeedEffect::new));

    @Override
    public ResourceLocation id() {
        return ModRegistry.location("enchantment.agility_speed");
    }

    @Override
    public Holder<Attribute> attribute() {
        return Attributes.MOVEMENT_SPEED;
    }

    @Override
    public AttributeModifier.Operation operation() {
        return AttributeModifier.Operation.ADD_MULTIPLIED_BASE;
    }

    @Override
    public MapCodec<? extends ConditionalAttributeEffect> codec() {
        return CODEC;
    }

    @Override
    public double getModifierValue(int enchantLevel, ItemStack item, LivingEntity entity) {
        double missingArmor = Math.max(0, 20 - Optional.of(entity.getAttributeValue(Attributes.ARMOR)).orElse(0D));
        double bonusAmount = missingArmorScaling.calculate(enchantLevel) * missingArmor;
        return baseAmount.calculate(enchantLevel) + bonusAmount;
    }
}
