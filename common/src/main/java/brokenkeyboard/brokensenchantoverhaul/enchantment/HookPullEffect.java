package brokenkeyboard.brokensenchantoverhaul.enchantment;

import brokenkeyboard.brokensenchantoverhaul.ModRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.function.Function;

public interface HookPullEffect {

    Codec<HookPullEffect> CODEC = ModRegistry.HOOK_PULL_REGISTRY.byNameCodec().dispatch(HookPullEffect::codec, Function.identity());

    int apply(ServerLevel serverLevel, int enchantmentLevel, ItemStack stack, FishingHook hook, int value);

    MapCodec<? extends HookPullEffect> codec();

    static int applyHookPullEffect(ServerLevel level, ItemStack stack, FishingHook hook, MutableInt value) {
        EnchantmentHelper.runIterationOnItem(stack, (holder, enchantmentLevel) ->
                holder.value().getEffects(ModRegistry.HOOK_PULL).forEach(effect ->
                        value.setValue(Math.min(effect.effect().apply(level, enchantmentLevel, stack, hook, value.getValue()), value.intValue()))));
        return value.intValue();
    }
}
