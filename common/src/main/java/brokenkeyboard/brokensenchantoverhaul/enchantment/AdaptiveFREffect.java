package brokenkeyboard.brokensenchantoverhaul.enchantment;

import brokenkeyboard.brokensenchantoverhaul.ModRegistry;
import brokenkeyboard.brokensenchantoverhaul.component.DamageTypeResist;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public record AdaptiveFREffect(ResourceLocation id, AttributeModifier.Operation operation) implements ConditionalAttributeEffect {

    public static final MapCodec<AdaptiveFREffect> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(AdaptiveFREffect::id),
            AttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(AdaptiveFREffect::operation)
    ).apply(instance, AdaptiveFREffect::new));

    @Override
    public ResourceLocation id() {
        return id;
    }

    @Override
    public Holder<Attribute> attribute() {
        return Attributes.BURNING_TIME;
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
    public double getModifierValue(int enchantLevel, ItemStack item, LivingEntity entity) {
        Optional<DamageTypeResist> resist = Optional.ofNullable(item.get(ModRegistry.DAMAGETYPE_RESIST));
        return resist.isPresent() && resist.get().damageType().equals("FIRE") ? resist.get().stacks() * -0.1F : 0F;
    }
}
