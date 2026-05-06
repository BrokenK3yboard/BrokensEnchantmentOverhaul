package brokenkeyboard.brokensenchantoverhaul.enchantment;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.LevelBasedValue;

public record HookBurnEffect(LevelBasedValue duration) implements HookPullEffect {

    public static final MapCodec<HookBurnEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LevelBasedValue.CODEC.fieldOf("burn_duration").forGetter(HookBurnEffect::duration)
    ).apply(instance, HookBurnEffect::new));

    @Override
    public int apply(ServerLevel level, int enchantLevel, ItemStack stack, FishingHook hook, int value) {
        if (hook.getHookedIn() instanceof LivingEntity living) {
            living.setRemainingFireTicks((int) duration.calculate(enchantLevel));
            return 1;
        }
        return value;
    }

    @Override
    public MapCodec<? extends HookPullEffect> codec() {
        return CODEC;
    }
}
