package brokenkeyboard.brokensenchantoverhaul.enchantment;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.LevelBasedValue;

public record FixedAttributeEffect(ResourceLocation id, Holder<Attribute> attribute, LevelBasedValue amount, AttributeModifier.Operation operation) implements ConditionalAttributeEffect {

    public static final MapCodec<FixedAttributeEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(FixedAttributeEffect::id),
            Attribute.CODEC.fieldOf("attribute").forGetter(FixedAttributeEffect::attribute),
            LevelBasedValue.CODEC.fieldOf("amount").forGetter(FixedAttributeEffect::amount),
            AttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(FixedAttributeEffect::operation))
            .apply(instance, FixedAttributeEffect::new));

    @Override
    public ResourceLocation id() {
        return this.id;
    }

    @Override
    public Holder<Attribute> attribute() {
        return this.attribute;
    }

    @Override
    public AttributeModifier.Operation operation() {
        return this.operation;
    }

    @Override
    public MapCodec<? extends ConditionalAttributeEffect> codec() {
        return CODEC;
    }

    @Override
    public double getModifierValue(int enchantmentLevel, ItemStack item, LivingEntity entity) {
        return amount.calculate(enchantmentLevel);
    }
}
