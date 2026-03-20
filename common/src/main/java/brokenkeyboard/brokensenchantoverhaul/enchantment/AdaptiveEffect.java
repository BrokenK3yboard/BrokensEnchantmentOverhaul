package brokenkeyboard.brokensenchantoverhaul.enchantment;

import brokenkeyboard.brokensenchantoverhaul.ModRegistry;
import brokenkeyboard.brokensenchantoverhaul.component.DamageTypeResist;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import org.apache.commons.lang3.mutable.MutableFloat;

import java.util.Optional;

public record AdaptiveEffect(LevelBasedValue reduction, LevelBasedValue maxStacks) implements ConditionalProtectionEffect {

    private static final DamageTypeResist EMPTY = new DamageTypeResist("NONE", 0);
    public static final MapCodec<AdaptiveEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LevelBasedValue.CODEC.fieldOf("reduction").forGetter(AdaptiveEffect::reduction),
            LevelBasedValue.CODEC.fieldOf("maxStacks").forGetter(AdaptiveEffect::maxStacks)
    ).apply(instance, AdaptiveEffect::new));

    @Override
    public float apply(ServerLevel level, int enchantLevel, EnchantedItemInUse item, LivingEntity user, float damageAmount, DamageSource source, float damageProtection) {
        MutableFloat mutableFloat = new MutableFloat(damageProtection);
        ItemStack stack = item.itemStack();
        Optional.ofNullable(item.itemStack().get(ModRegistry.DAMAGETYPE_RESIST)).ifPresentOrElse(resist -> {
            if (resist.hasResistance(source)) {
                int stacks = resist.stacks();
                mutableFloat.add(reduction.calculate(enchantLevel) * stacks);
                int max = (int) maxStacks.calculate(enchantLevel);
                stack.update(ModRegistry.DAMAGETYPE_RESIST, EMPTY, value -> new DamageTypeResist(resist.damageType(), Math.clamp(stacks + 1, 0, max)));
            } else {
                stack.update(ModRegistry.DAMAGETYPE_RESIST, EMPTY, value -> new DamageTypeResist(DamageTypeResist.getResistanceType(source), 1));
            }
        }, () -> stack.update(ModRegistry.DAMAGETYPE_RESIST, EMPTY, resist -> new DamageTypeResist(DamageTypeResist.getResistanceType(source), 1)));
        return mutableFloat.floatValue();
    }

    @Override
    public MapCodec<? extends ConditionalProtectionEffect> codec() {
        return CODEC;
    }

    public static void equipmentChanged(ItemStack stack) {
        Optional<DamageTypeResist> adaptive = Optional.ofNullable(stack.get(ModRegistry.DAMAGETYPE_RESIST));
        adaptive.ifPresent(barrier1 -> stack.remove(ModRegistry.DAMAGETYPE_RESIST));
    }
}
