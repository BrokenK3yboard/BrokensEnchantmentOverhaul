package brokenkeyboard.brokensenchantoverhaul.enchantment;

import brokenkeyboard.brokensenchantoverhaul.ModRegistry;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.apache.commons.lang3.mutable.MutableBoolean;

import javax.annotation.Nullable;

public record GlintModifier(ItemPredicate itemPredicate) {

    public static final MapCodec<GlintModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ItemPredicate.CODEC.fieldOf("itemPredicate").forGetter(GlintModifier::itemPredicate)
    ).apply(instance, GlintModifier::new));

    public boolean apply(ItemStack stack) {
        return itemPredicate.test(stack);
    }

    public static boolean shouldUseGlint(ItemStack stack, @Nullable LivingEntity entity) {
        if (entity == null) return false;
        MutableBoolean bool = new MutableBoolean(false);

        EnchantmentHelper.runIterationOnItem(stack, (enchantment, enchantmentLevel) ->
                enchantment.value().getEffects(ModRegistry.GLINT_OVERRIDE).forEach(effect -> {
                    boolean useGlint = effect.effect().itemPredicate.test(stack);

                    if (useGlint) {
                        bool.setValue(true);
                    }
                }));
        return stack.getItem() instanceof BowItem
                ? (entity.getUseItem().equals(stack) && stack.getUseDuration(entity) - entity.getUseItemRemainingTicks() >= 60 && bool.getValue())
                : bool.getValue();
    }
}
