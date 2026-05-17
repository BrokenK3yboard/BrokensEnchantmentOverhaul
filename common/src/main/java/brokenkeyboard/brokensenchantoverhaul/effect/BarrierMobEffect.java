package brokenkeyboard.brokensenchantoverhaul.effect;

import brokenkeyboard.brokensenchantoverhaul.platform.Services;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

public class BarrierMobEffect extends EnchantmentMobEffect {

    public BarrierMobEffect(ResourceKey<Enchantment> enchantment, int color) {
        super(enchantment, color);
    }

    @Override
    public boolean applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        return super.applyEffectTick(entity, amplifier) && Services.PLATFORM.getBarrierAmount(entity) > 0;
    }
}
