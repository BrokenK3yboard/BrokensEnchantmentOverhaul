package brokenkeyboard.brokensenchantoverhaul.enchantment;

import brokenkeyboard.brokensenchantoverhaul.ModRegistry;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import org.apache.commons.lang3.mutable.MutableFloat;

import java.util.Optional;

import static brokenkeyboard.brokensenchantoverhaul.ModRegistry.LOOT_PICKUP_BONUS;
import static brokenkeyboard.brokensenchantoverhaul.ModRegistry.SCAVENGER_STACKS;

public record ScavengerToughnessEffect(LevelBasedValue multiplier) implements ConditionalAttributeEffect {

    public static final MapCodec<ScavengerToughnessEffect> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            LevelBasedValue.CODEC.fieldOf("multiplier").forGetter(ScavengerToughnessEffect::multiplier)
    ).apply(instance, ScavengerToughnessEffect::new));

    @Override
    public ResourceLocation id() {
        return ModRegistry.location("enchantment.scavenger");
    }

    @Override
    public Holder<Attribute> attribute() {
        return Attributes.ARMOR_TOUGHNESS;
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
    public double getModifierValue(int enchantLevel, ItemStack stack, LivingEntity entity) {
        Optional<Integer> barrier = Optional.ofNullable(stack.get(ModRegistry.SCAVENGER_STACKS));
        return barrier.map(value -> Math.ceil((double) value / 10) * multiplier.calculate(enchantLevel)).orElse(0D);
    }

    public static void postHurt(DamageSource source, float damage, LivingEntity entity) {
        if (source.is(DamageTypeTags.BYPASSES_ARMOR)) return;
        for (ItemStack stack : entity.getArmorSlots()) {
            Optional<Integer> resist = Optional.ofNullable(stack.get(ModRegistry.SCAVENGER_STACKS));
            if (resist.orElse(0) > 0) {
                stack.update(ModRegistry.SCAVENGER_STACKS, 0, newValue -> (int) Math.max(0, resist.get() - (damage / 4)));
            }
        }
    }

    public static void postLootPickup(Player player, int stackCount) {
        EnchantmentHelper.getRandomItemWith(LOOT_PICKUP_BONUS, player, stack -> Optional.ofNullable(stack.get(ModRegistry.SCAVENGER_STACKS)).orElse(0) < 40)
            .ifPresent(item -> {
                ItemStack stack = item.itemStack();
                int stacks = Optional.ofNullable(stack.get(ModRegistry.SCAVENGER_STACKS)).orElse(0);
                MutableFloat toAdd = new MutableFloat(0);
                EnchantmentHelper.runIterationOnItem(stack, (enchantment, level) ->
                    enchantment.value().getEffects(LOOT_PICKUP_BONUS).forEach(effect ->
                            toAdd.add(effect.effect().process(level, player.getRandom(), stackCount))));
                stack.update(SCAVENGER_STACKS, 0, integer -> (int) Math.min(40, stacks + toAdd.floatValue()));
            });
    }
}
