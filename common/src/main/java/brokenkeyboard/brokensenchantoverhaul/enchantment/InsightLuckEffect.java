package brokenkeyboard.brokensenchantoverhaul.enchantment;

import brokenkeyboard.brokensenchantoverhaul.ModRegistry;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

public record InsightLuckEffect() implements ConditionalAttributeEffect {

    public static final MapCodec<InsightLuckEffect> CODEC = MapCodec.unit(new InsightLuckEffect());

    @Override
    public ResourceLocation id() {
        return ModRegistry.location("enchantment.insight_luck");
    }

    @Override
    public Holder<Attribute> attribute() {
        return Attributes.LUCK;
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
    public double getModifierValue(int enchantmentLevel, ItemStack item, LivingEntity entity) {
        double enchantmentValue = 0;
        for (ItemStack stack : entity.getArmorSlots()) {
            if (stack.getItem() instanceof ArmorItem armor) {
                enchantmentValue += armor.getMaterial().value().enchantmentValue();
            }
        }
        return 1 + Math.floor(enchantmentValue / 24);
    }
}
