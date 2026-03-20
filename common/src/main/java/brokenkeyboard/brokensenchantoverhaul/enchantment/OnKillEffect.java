package brokenkeyboard.brokensenchantoverhaul.enchantment;

import brokenkeyboard.brokensenchantoverhaul.ModRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.function.Function;

public interface OnKillEffect {

    Codec<OnKillEffect> CODEC = ModRegistry.ON_KILL_REGISTRY.byNameCodec().dispatch(OnKillEffect::codec, Function.identity());

    void apply(int enchantmentLevel, LivingEntity victim, DamageSource source);

    MapCodec<? extends OnKillEffect> codec();

    static void applyOnKillEffect(LivingEntity entity, DamageSource source) {
        ItemStack weapon = source.getWeaponItem();
        if (weapon == null) return;
        EnchantmentHelper.runIterationOnItem(weapon, (holder, enchantmentLevel) ->
                holder.value().getEffects(ModRegistry.ON_KILL).forEach(effect ->
                        effect.effect().apply(enchantmentLevel, entity, source)));
    }
}
