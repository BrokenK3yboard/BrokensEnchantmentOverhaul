package brokenkeyboard.brokensenchantoverhaul.enchantment;

import brokenkeyboard.brokensenchantoverhaul.ModRegistry;
import brokenkeyboard.brokensenchantoverhaul.platform.Services;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.apache.commons.lang3.mutable.MutableBoolean;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.BiPredicate;

public record GlintModifier() {

    public static final MapCodec<GlintModifier> CODEC = MapCodec.unit(new GlintModifier());

    private static final Map<ResourceKey<Enchantment>, BiPredicate<LivingEntity, ItemStack>> GLINT_OVERRIDES = Map.of(
            Enchantments.BREACH, (entity, stack) -> entity.hasEffect(ModRegistry.BREACH_EFFECT),
            ModRegistry.POWER_SHOT, (entity, stack) -> entity.getUseItem().equals(stack) && stack.getUseDuration(entity) - entity.getUseItemRemainingTicks() >= 60,
            ModRegistry.PROSPECTING, (entity, stack) -> Services.PLATFORM.getDetectedBlocksNearby(entity),
            ModRegistry.SPELUNKER, (entity, stack) -> Services.PLATFORM.getDetectedBlocksNearby(entity));

    public static boolean overrideGlint(ItemStack stack, @Nullable LivingEntity entity) {
        if (entity == null) return false;
        MutableBoolean bool = new MutableBoolean(false);

        EnchantmentHelper.runIterationOnItem(stack, (enchantHolder, enchantLevel) -> {
            if (bool.isTrue()) return;
            if (enchantHolder.value().effects().has(ModRegistry.GLINT_OVERRIDE) && GLINT_OVERRIDES.getOrDefault(enchantHolder.unwrap().left().orElse(null),
                    (livingEntity, itemStack) -> false).test(entity, stack)) {
                bool.setTrue();
            }
        });
        return bool.getValue();
    }
}
