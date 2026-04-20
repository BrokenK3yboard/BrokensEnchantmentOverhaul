package brokenkeyboard.brokensenchantoverhaul.enchantment;

import brokenkeyboard.brokensenchantoverhaul.MiningHandler;
import brokenkeyboard.brokensenchantoverhaul.ModRegistry;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.ibm.icu.impl.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public interface ConditionalAttributeEffect {

    ResourceLocation ENCHANTED_EFFICIENCY_ID = ModRegistry.location("tool.mining_efficiency");

    Codec<ConditionalAttributeEffect> CODEC = ModRegistry.ATTRIBUTE_REGISTRY.byNameCodec().dispatch(ConditionalAttributeEffect::codec, Function.identity());

    ResourceLocation id();

    Holder<Attribute> attribute();

    AttributeModifier.Operation operation();

    MapCodec<? extends ConditionalAttributeEffect> codec();

    default void applyModifiers(EnchantedItemInUse item, LivingEntity entity, int enchantmentLevel) {
        if (item.inSlot() == null) return;
        ResourceLocation slotID = ConditionalAttributeEffect.idForSlot(id(), item.inSlot());
        boolean flag = entity.getAttributes().hasModifier(attribute(), slotID);
        double value = getModifierValue(enchantmentLevel, item.itemStack(), entity);

        if (value > 0 && (!flag || entity.getAttributes().getModifierValue(attribute(), slotID) != value)) {
            entity.getAttributes().addTransientAttributeModifiers(ConditionalAttributeEffect.attributeMap(attribute(), slotID, value, operation()));
        } else if (flag && value == 0) {
            entity.getAttributes().removeAttributeModifiers(ConditionalAttributeEffect.attributeMap(attribute(), slotID, entity.getAttributes().getModifierValue(attribute(), slotID), operation()));
        }
    }

    default void removeModifiers(EnchantedItemInUse item, LivingEntity entity) {
        if (item.inSlot() == null) return;
        ResourceLocation slotID = ConditionalAttributeEffect.idForSlot(id(), item.inSlot());
        boolean flag = entity.getAttributes().hasModifier(attribute(), slotID);
        if (flag) {
            double value = entity.getAttributes().getModifierValue(attribute(), slotID);
            entity.getAttributes().removeAttributeModifiers(ConditionalAttributeEffect.attributeMap(attribute(), slotID, value, operation()));
        }
    }

    default AttributeModifier getModifier(Level level, int enchantmentLevel, ItemStack item, LivingEntity entity, double value, StringRepresentable slot) {
        return new AttributeModifier(ConditionalAttributeEffect.idForSlot(id(), slot), value, operation());
    }

    double getModifierValue(int enchantmentLevel, ItemStack item, LivingEntity entity);

    static ResourceLocation idForSlot(ResourceLocation id, StringRepresentable slot) {
        return id.withSuffix("/" + slot.getSerializedName());
    }

    static HashMultimap<Holder<Attribute>, AttributeModifier> attributeMap(Holder<Attribute> attribute, ResourceLocation id, double amount, AttributeModifier.Operation operation) {
        HashMultimap<Holder<Attribute>, AttributeModifier> hashmultimap = HashMultimap.create();
        hashmultimap.put(attribute, new AttributeModifier(id, amount, operation));
        return hashmultimap;
    }

    @SuppressWarnings("deprecation")
    static Multimap<Holder<Attribute>, AttributeModifier> collectAttributes(Level level, ItemStack stack, Player player, EquipmentSlotGroup slotGroup, ItemAttributeModifiers itemAttributes) {
        ItemAttributeModifiers attributes = !itemAttributes.modifiers().isEmpty() ? itemAttributes : stack.getItem().getDefaultAttributeModifiers();
        Map<String, Pair<Holder<Attribute>, AttributeModifier>> totalAttributes = new HashMap<>();
        Multimap<Holder<Attribute>, AttributeModifier> result = HashMultimap.create();

        attributes.modifiers().forEach(entry -> {
            if (entry.slot().equals(slotGroup)) {
                addModifier(totalAttributes, entry.attribute(), entry.modifier());
            }
        });

        EnchantmentHelper.runIterationOnItem(stack, (enchantment, enchantmentLevel) ->
                enchantment.value().getEffects(EnchantmentEffectComponents.ATTRIBUTES).forEach((effect) -> {
                    if (enchantment.value().definition().slots().contains(slotGroup)) {
                        addModifier(totalAttributes, effect.attribute(), effect.getModifier(enchantmentLevel, slotGroup));
                    }
                }));

        EnchantmentHelper.runIterationOnItem(stack, (enchantment, enchantmentLevel) ->
                enchantment.value().getEffects(ModRegistry.CONDITIONAL_ATTRIBUTE).forEach((effect) -> {
                    ConditionalAttributeEffect effect1 = effect.effect();
                    double value = effect1.getModifierValue(enchantmentLevel, stack, player);
                    if (enchantment.value().definition().slots().contains(slotGroup) && value != 0) {
                        addModifier(totalAttributes, effect1.attribute(), effect1.getModifier(level, enchantmentLevel, stack, player, value, slotGroup));
                    }
                }));

        if (stack.is(ModRegistry.TOOL_EFFICIENCY_BONUS) && slotGroup.equals(EquipmentSlotGroup.MAINHAND)) {
            addModifier(totalAttributes, Attributes.MINING_EFFICIENCY, new AttributeModifier(ENCHANTED_EFFICIENCY_ID, MiningHandler.modifyMiningEfficiency(stack), AttributeModifier.Operation.ADD_VALUE));
        }

        if (!totalAttributes.isEmpty()) {
            totalAttributes.forEach((string, pair) -> result.put(pair.first, pair.second));
        }
        return result;
    }

    static void addModifier(Map<String, Pair<Holder<Attribute>, AttributeModifier>> attributeMap, Holder<Attribute> attribute, AttributeModifier modifier) {
        String key = attribute.value().getDescriptionId() + "_" + modifier.operation();
        if (!attributeMap.containsKey(key)) {
            attributeMap.put(key, Pair.of(attribute, modifier));
        } else {
            Pair<Holder<Attribute>, AttributeModifier> found = attributeMap.get(key);
            double totalAmount = switch(modifier.operation()) {
                case AttributeModifier.Operation.ADD_VALUE, AttributeModifier.Operation.ADD_MULTIPLIED_BASE -> found.second.amount() + modifier.amount();
                case AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL -> found.second.amount() * (1 + modifier.amount());
            };
            attributeMap.put(key, Pair.of(found.first, new AttributeModifier(found.second.id(), totalAmount, found.second.operation())));
        }
    }

    static void removeAttribute(ServerLevel serverLevel, ItemStack stack, LivingEntity entity, EquipmentSlot slot) {
        EnchantmentHelper.runIterationOnItem(stack, (enchantment, enchantmentLevel) ->
                enchantment.value().getEffects(ModRegistry.CONDITIONAL_ATTRIBUTE).forEach((effect) -> {
                    if (!effect.matches(Enchantment.entityContext(serverLevel, enchantmentLevel, entity, entity.position()))) {
                        effect.effect().removeModifiers(new EnchantedItemInUse(stack, slot, entity), entity);
                    }
                }));
    }

    static void updateAttribute(ServerLevel serverLevel, LivingEntity entity) {
        EnchantmentHelper.runIterationOnEquipment(entity, (enchantment, enchantmentLevel, itemInUse) ->
                enchantment.value().getEffects(ModRegistry.CONDITIONAL_ATTRIBUTE).forEach((effect) -> {
                    if (effect.matches(Enchantment.entityContext(serverLevel, enchantmentLevel, entity, entity.position()))) {
                        effect.effect().applyModifiers(itemInUse, entity, enchantmentLevel);
                    }
                }));
    }
}