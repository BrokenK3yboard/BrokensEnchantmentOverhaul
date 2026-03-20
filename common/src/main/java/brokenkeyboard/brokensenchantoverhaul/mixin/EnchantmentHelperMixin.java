package brokenkeyboard.brokensenchantoverhaul.mixin;

import brokenkeyboard.brokensenchantoverhaul.Config;
import brokenkeyboard.brokensenchantoverhaul.ModRegistry;
import brokenkeyboard.brokensenchantoverhaul.enchantment.BreachAPEffect;
import com.google.common.collect.Lists;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.stream.Stream;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    // Limit to one enchantment
    @ModifyReturnValue(method = "selectEnchantment", at = @At(value = "RETURN", ordinal = 1))
    private static List<EnchantmentInstance> modifyEnchantments(List<EnchantmentInstance> original) {
        if (!Config.OVERHAUL_ENCHANTMENTS.get() || original.isEmpty()) return original;
        List<EnchantmentInstance> list = Lists.newArrayList();
        list.add(original.getFirst());
        return list;
    }

    // Remove blacklisted enchantments from villager trades (EnchantmentTags.ON_TRADED_EQUIPMENT), enchantWithLevels, etc.
    @WrapOperation(method = "selectEnchantment", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getAvailableEnchantmentResults(ILnet/minecraft/world/item/ItemStack;Ljava/util/stream/Stream;)Ljava/util/List;"))
    private static List<EnchantmentInstance> limitEnchantments(int level, ItemStack stack, Stream<Holder<Enchantment>> possibleEnchantments, Operation<List<EnchantmentInstance>> original) {
        Stream<Holder<Enchantment>> filteredList = possibleEnchantments.filter(ench -> !ench.is(ModRegistry.REMOVED_ENCHANTMENTS));
        return original.call(level, stack, filteredList);
    }

    @WrapOperation(method = "runIterationOnItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/enchantment/EnchantmentHelper$EnchantmentVisitor;)V",
    at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper$EnchantmentVisitor;accept(Lnet/minecraft/core/Holder;I)V"))
    private static void returnLevel(EnchantmentHelper.EnchantmentVisitor visitor, Holder<Enchantment> enchantmentHolder, int enchantLevel, Operation<Void> original) {
        original.call(visitor, enchantmentHolder, enchantmentHolder.unwrapKey().isPresent() && Config.OVERHAUL_ENCHANTMENTS.get()
                ? ModRegistry.MAX_LEVELS.getOrDefault(enchantmentHolder.unwrapKey().get(), 1) : enchantLevel);
    }

    @WrapOperation(method = "runIterationOnItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/enchantment/EnchantmentHelper$EnchantmentInSlotVisitor;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper$EnchantmentInSlotVisitor;accept(Lnet/minecraft/core/Holder;ILnet/minecraft/world/item/enchantment/EnchantedItemInUse;)V"))
    private static void returnLevel(EnchantmentHelper.EnchantmentInSlotVisitor visitor, Holder<Enchantment> enchantmentHolder, int enchantLevel, EnchantedItemInUse item, Operation<Void> original) {
        original.call(visitor, enchantmentHolder, enchantmentHolder.unwrapKey().isPresent() && Config.OVERHAUL_ENCHANTMENTS.get()
                ? ModRegistry.MAX_LEVELS.getOrDefault(enchantmentHolder.unwrapKey().get(), 1) : enchantLevel, item);
    }

    @ModifyReturnValue(method = "getTridentReturnToOwnerAcceleration", at = @At(value = "RETURN"))
    private static int modifyLevel(int original) {
        return Config.TRIDENT_BUILTIN_LOYALTY.get() ? 3 : original;
    }

    @Inject(method = "modifyArmorEffectiveness", at = @At("RETURN"), cancellable = true)
    private static void modifyArmor(ServerLevel level, ItemStack stack, Entity entity, DamageSource source, float amount, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(BreachAPEffect.modifyArmorPiercing(stack, cir.getReturnValue()));
    }
}