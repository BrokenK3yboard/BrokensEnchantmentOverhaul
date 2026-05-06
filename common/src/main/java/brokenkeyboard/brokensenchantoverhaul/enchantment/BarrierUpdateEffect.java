package brokenkeyboard.brokensenchantoverhaul.enchantment;

import brokenkeyboard.brokensenchantoverhaul.ModRegistry;
import brokenkeyboard.brokensenchantoverhaul.component.Barrier;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public record BarrierUpdateEffect(LevelBasedValue refreshDelay, LevelBasedValue refreshAmount, LevelBasedValue maxBarriers) implements EnchantmentEntityEffect {

    public static final MapCodec<BarrierUpdateEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LevelBasedValue.CODEC.fieldOf("refreshDelay").forGetter(BarrierUpdateEffect::refreshDelay),
            LevelBasedValue.CODEC.fieldOf("refreshAmount").forGetter(BarrierUpdateEffect::refreshAmount),
            LevelBasedValue.CODEC.fieldOf("maxBarriers").forGetter(BarrierUpdateEffect::maxBarriers)
    ).apply(instance, BarrierUpdateEffect::new));

    @Override
    public void apply(ServerLevel level, int enchantLevel, EnchantedItemInUse item, Entity entity, Vec3 pos) {
        ItemStack stack = item.itemStack();
        int tickCount = entity.tickCount;
        float maxUses = maxBarriers.calculate(enchantLevel);
        Optional<Barrier> barrier = Optional.ofNullable(stack.get(ModRegistry.BARRIER_INSTANCE));

        if (barrier.isPresent() && barrier.get().charges() < maxUses) {
            int elapsed = tickCount - barrier.get().timestamp();
            int stacks = barrier.get().charges();

            if (elapsed > 0 && elapsed % refreshDelay.calculate(enchantLevel) == 0) {
                stack.update(ModRegistry.BARRIER_INSTANCE, new Barrier(tickCount, 0), component -> new Barrier(tickCount, (int) Math.clamp(stacks + refreshAmount.calculate(enchantLevel), 0, maxUses)));
            } else if (elapsed < 0) {
                stack.update(ModRegistry.BARRIER_INSTANCE, new Barrier(tickCount, 0), component -> new Barrier(tickCount, stacks));
            }
        } else if (barrier.isEmpty()) {
            stack.set(ModRegistry.BARRIER_INSTANCE, new Barrier(tickCount, 0));
        }
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
