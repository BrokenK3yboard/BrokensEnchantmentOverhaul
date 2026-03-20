package brokenkeyboard.brokensenchantoverhaul.enchantment;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

public record StabilizeKnockbackEffect(ResourceLocation id, Holder<Attribute> attribute, AttributeModifier.Operation operation) implements ConditionalAttributeEffect {

    public static final MapCodec<StabilizeKnockbackEffect> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(StabilizeKnockbackEffect::id),
            Attribute.CODEC.fieldOf("attribute").forGetter(StabilizeKnockbackEffect::attribute),
            AttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(StabilizeKnockbackEffect::operation)
    ).apply(instance, StabilizeKnockbackEffect::new));

    @Override
    public ResourceLocation id() {
        return id;
    }

    @Override
    public Holder<Attribute> attribute() {
        return attribute;
    }

    @Override
    public AttributeModifier.Operation operation() {
        return operation;
    }

    @Override
    public MapCodec<? extends ConditionalAttributeEffect> codec() {
        return CODEC;
    }

    @Override
    public double getModifierValue(int enchantmentLevel, ItemStack item, LivingEntity entity) {
        return Math.clamp(entity.getArmorValue() * 0.015, 0, 0.3);
    }
}
