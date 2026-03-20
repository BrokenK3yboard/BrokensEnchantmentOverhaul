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
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.LevelBasedValue;

public record DexterityReachEffect(LevelBasedValue amount) implements ConditionalAttributeEffect {

    public static final MapCodec<DexterityReachEffect> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            LevelBasedValue.CODEC.fieldOf("amount").forGetter(DexterityReachEffect::amount)
    ).apply(instance, DexterityReachEffect::new));

    @Override
    public ResourceLocation id() {
        return ModRegistry.location("enchantment.dexterity");
    }

    @Override
    public Holder<Attribute> attribute() {
        return Attributes.BLOCK_INTERACTION_RANGE;
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
        return entity.getMainHandItem().getItem() instanceof DiggerItem ? amount.calculate(enchantmentLevel) : 0;
    }
}
