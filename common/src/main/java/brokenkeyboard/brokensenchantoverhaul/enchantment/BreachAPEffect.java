package brokenkeyboard.brokensenchantoverhaul.enchantment;

import brokenkeyboard.brokensenchantoverhaul.ModRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import org.apache.commons.lang3.mutable.MutableFloat;

import java.util.Optional;

public record BreachAPEffect(LevelBasedValue amount) {

    public static final Codec<BreachAPEffect> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(LevelBasedValue.CODEC.fieldOf("amount").forGetter(BreachAPEffect::amount))
            .apply(instance, BreachAPEffect::new));

    public float apply(int enchantLevel, ItemStack stack, float piercing) {
        int uses = Optional.ofNullable(stack.get(ModRegistry.BREACH_USES)).orElse(0);

        if (uses > 0) {
            stack.update(ModRegistry.BREACH_USES, 0, value -> uses - 1);
            return piercing + amount.calculate(enchantLevel);
        }
        return piercing;
    }

    public static float modifyArmorPiercing(ItemStack stack, float piercing) {
        MutableFloat mutablefloat = new MutableFloat(piercing);
        EnchantmentHelper.runIterationOnItem(stack, (enchantment, enchantmentLevel) -> enchantment.value().getEffects(ModRegistry.BREACH_AP_EFFECT)
                .forEach(effect -> mutablefloat.setValue(effect.effect().apply(enchantmentLevel, stack, mutablefloat.getValue()))));
        return mutablefloat.floatValue();
    }
}
