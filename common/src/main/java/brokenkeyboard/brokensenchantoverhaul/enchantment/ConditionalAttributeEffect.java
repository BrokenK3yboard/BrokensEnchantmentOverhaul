package brokenkeyboard.brokensenchantoverhaul.enchantment;

import brokenkeyboard.brokensenchantoverhaul.ModRegistry;
import com.google.common.collect.HashMultimap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.function.Function;

public interface ConditionalAttributeEffect {

    Codec<ConditionalAttributeEffect> CODEC = ModRegistry.ATTRIBUTE_REGISTRY.byNameCodec().dispatch(ConditionalAttributeEffect::codec, Function.identity());

    ResourceLocation id();

    Holder<Attribute> attribute();

    AttributeModifier.Operation operation();

    double getModifierValue(int enchantLevel, ItemStack item, LivingEntity entity);

    MapCodec<? extends ConditionalAttributeEffect> codec();

    private void applyModifiers(EnchantedItemInUse item, LivingEntity entity, int enchantLevel) {
        if (item.inSlot() == null) return;
        ResourceLocation slotID = ConditionalAttributeEffect.idForSlot(id(), item.inSlot());
        boolean hasModifier = entity.getAttributes().hasModifier(attribute(), slotID);
        double value = getModifierValue(enchantLevel, item.itemStack(), entity);

        if (value > 0 && (!hasModifier || entity.getAttributes().getModifierValue(attribute(), slotID) != value)) {
            entity.getAttributes().addTransientAttributeModifiers(ConditionalAttributeEffect.attributeMap(attribute(), slotID, value, operation()));
        } else if (hasModifier && value == 0) {
            entity.getAttributes().removeAttributeModifiers(ConditionalAttributeEffect.attributeMap(attribute(), slotID, entity.getAttributes().getModifierValue(attribute(), slotID), operation()));
        }
    }

    private void removeModifiers(EnchantedItemInUse item, LivingEntity entity) {
        if (item.inSlot() == null) return;
        ResourceLocation slotID = ConditionalAttributeEffect.idForSlot(id(), item.inSlot());

        if (entity.getAttributes().hasModifier(attribute(), slotID)) {
            double value = entity.getAttributes().getModifierValue(attribute(), slotID);
            entity.getAttributes().removeAttributeModifiers(ConditionalAttributeEffect.attributeMap(attribute(), slotID, value, operation()));
        }
    }

    static ResourceLocation idForSlot(ResourceLocation id, StringRepresentable slot) {
        return id.withSuffix("/" + slot.getSerializedName());
    }

    static HashMultimap<Holder<Attribute>, AttributeModifier> attributeMap(Holder<Attribute> attribute, ResourceLocation id, double amount, AttributeModifier.Operation operation) {
        HashMultimap<Holder<Attribute>, AttributeModifier> hashmultimap = HashMultimap.create();
        hashmultimap.put(attribute, new AttributeModifier(id, amount, operation));
        return hashmultimap;
    }

    static void removeAttribute(ItemStack stack, LivingEntity entity, EquipmentSlot slot) {
        EnchantmentHelper.runIterationOnItem(stack, (enchantHolder, enchantLevel) ->
                enchantHolder.value().getEffects(ModRegistry.CONDITIONAL_ATTRIBUTE).forEach((effect) ->
                        effect.effect().removeModifiers(new EnchantedItemInUse(stack, slot, entity), entity)));
    }

    static void updateAttribute(ServerLevel level, LivingEntity entity) {
        EnchantmentHelper.runIterationOnEquipment(entity, (enchantHolder, enchantLevel, item) ->
                enchantHolder.value().getEffects(ModRegistry.CONDITIONAL_ATTRIBUTE).forEach((effect) -> {
                    if (effect.matches(Enchantment.entityContext(level, enchantLevel, entity, entity.position()))) {
                        effect.effect().applyModifiers(item, entity, enchantLevel);
                    } else {
                        effect.effect().removeModifiers(item, entity);
                    }
                }));
    }
}