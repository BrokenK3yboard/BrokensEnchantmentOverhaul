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

public record AdaptiveBREffect(ResourceLocation id, AttributeModifier.Operation operation) implements ConditionalAttributeEffect {

    public static final MapCodec<AdaptiveBREffect> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(AdaptiveBREffect::id),
            AttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(AdaptiveBREffect::operation)
    ).apply(instance, AdaptiveBREffect::new));

    @Override
    public ResourceLocation id() {
        return id;
    }

    @Override
    public Holder<Attribute> attribute() {
        return Attributes.EXPLOSION_KNOCKBACK_RESISTANCE;
    }

    @Override
    public MapCodec<? extends ConditionalAttributeEffect> codec() {
        return CODEC;
    }

    @Override
    public double getModifierValue(int enchantmentLevel, ItemStack item, LivingEntity entity) {
        Optional<DamageTypeResist> resist = Optional.ofNullable(item.get(ModRegistry.DAMAGETYPE_RESIST));
        return resist.isPresent() && resist.get().damageType().equals("EXPLOSIVE") ? resist.get().stacks() * 0.1F : 0F;
    }
}
