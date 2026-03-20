package brokenkeyboard.brokensenchantoverhaul.enchantment;

import brokenkeyboard.brokensenchantoverhaul.ModRegistry;
import brokenkeyboard.brokensenchantoverhaul.component.Barrier;
import com.mojang.serialization.MapCodec;
import net.minecraft.Util;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.apache.commons.lang3.mutable.MutableFloat;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public record BarrierEffect() {

    public static final MapCodec<BarrierEffect> CODEC = MapCodec.unit(BarrierEffect::new);

    public static float modifyDamage(LivingEntity entity, DamageSource source, float amount) {
        Map<ItemStack, Barrier> barrierItems = new HashMap<>();
        MutableFloat mutableFloat = new MutableFloat(amount);

        EnchantmentHelper.runIterationOnEquipment(entity, (enchant, enchantLevel, item) ->
                enchant.value().getEffects(ModRegistry.BARRIER_EFFECT).forEach(effect -> {
            if (entity.level() instanceof ServerLevel serverLevel && effect.matches(Enchantment.damageContext(serverLevel, enchantLevel, entity, source))) {
                ItemStack stack = item.itemStack();
                int tickCount = entity.tickCount;
                int charges = Optional.ofNullable(stack.get(ModRegistry.BARRIER_INSTANCE)).map(Barrier::charges).orElse(0);
                Barrier newBarrier = new Barrier(tickCount, charges);
                stack.update(ModRegistry.BARRIER_INSTANCE, new Barrier(tickCount, 0), component -> newBarrier);

                if (charges > 0) {
                    barrierItems.put(stack, newBarrier);
                }
            }
        }));

        Util.getRandomSafe(barrierItems.keySet().stream().toList(), entity.getRandom()).ifPresent(stack -> {
            Barrier barrier = barrierItems.get(stack);
            stack.update(ModRegistry.BARRIER_INSTANCE, new Barrier(barrier.timestamp(), 0), component -> new Barrier(barrier.timestamp(), barrier.charges() - 1));
            mutableFloat.setValue(1);
        });
        return mutableFloat.floatValue();
    }

    public static void equipmentChanged(ItemStack stack) {
        Optional<Barrier> barrier = Optional.ofNullable(stack.get(ModRegistry.BARRIER_INSTANCE));
        barrier.ifPresent(barrier1 -> stack.remove(ModRegistry.BARRIER_INSTANCE));
    }
}
