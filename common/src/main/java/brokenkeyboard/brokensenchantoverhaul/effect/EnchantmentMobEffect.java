package brokenkeyboard.brokensenchantoverhaul.effect;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.jetbrains.annotations.NotNull;

public class EnchantmentMobEffect extends MobEffect {

    private final ResourceKey<Enchantment> ENCHANTMENT;

    public EnchantmentMobEffect(ResourceKey<Enchantment> enchantment, int color) {
        super(MobEffectCategory.NEUTRAL, color);
        this.ENCHANTMENT = enchantment;
    }

    @Override
    public boolean applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        return EnchantmentHelper.getEnchantmentLevel(entity.level().registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(ENCHANTMENT), entity) > 0;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
