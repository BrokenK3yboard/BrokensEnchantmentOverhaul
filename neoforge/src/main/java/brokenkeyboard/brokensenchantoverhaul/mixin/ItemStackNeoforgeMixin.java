package brokenkeyboard.brokensenchantoverhaul.mixin;

import brokenkeyboard.brokensenchantoverhaul.ModRegistry;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemStack.class)
public class ItemStackNeoforgeMixin {

    @WrapOperation(method = "hurtAndBreak(ILnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;processDurabilityChange(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;I)I"))
    private int applyBlacksmithDurabilityBonus(ServerLevel level, ItemStack stack, int damage, Operation<Integer> original, @Local(argsOnly = true)LivingEntity entity) {
        int durabilityDamage = entity != null && entity.hasEffect(ModRegistry.BLACKSMITH_EFFECT) ? (int) ModRegistry.BLACKSMITH_DURABILITY_BONUS.process(1, level.getRandom(), damage) : damage;
        return original.call(level, stack, durabilityDamage);
    }
}
