package brokenkeyboard.brokensenchantoverhaul.mixin;

import brokenkeyboard.brokensenchantoverhaul.CommonHandler;
import brokenkeyboard.brokensenchantoverhaul.EnchantOverhaul;
import brokenkeyboard.brokensenchantoverhaul.ModRegistry;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.function.Consumer;

@SuppressWarnings("UnstableApiUsage")
@Mixin(LivingEntity.class)
public class LivingEntityFabricMixin {

    @Shadow
    protected int useItemRemaining;

    @Inject(method = "getDamageAfterMagicAbsorb", at = @At(value = "RETURN", ordinal = 3), cancellable = true)
    private void preHurt(DamageSource damageSource, float damageAmount, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(CommonHandler.handleBarrierDamage((LivingEntity) (Object) this, damageAmount));
    }

    @ModifyReturnValue(method = "createLivingAttributes", at = @At("RETURN"))
    private static AttributeSupplier.Builder addAttributes(AttributeSupplier.Builder original) {
        original.add(ModRegistry.HEALING_EFFICIENCY)
                .add(ModRegistry.POSITIVE_EFFECT_DURATION)
                .add(ModRegistry.NEGATIVE_EFFECT_DURATION)
                .add(ModRegistry.MONSTER_AWARENESS_RANGE)
                .add(ModRegistry.BARRIER_STRENGTH);
        return original;
    }

    @Inject(method = "updateUsingItem", at = @At(value = "HEAD"))
    private void modifyTime(ItemStack usingItem, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        ItemStack useItem = entity.getUseItem();
        if (useItem.getItem() instanceof BowItem && EnchantmentHelper.getItemEnchantmentLevel(
                entity.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(ModRegistry.BARRAGE), useItem) > 0) {
            --useItemRemaining;
        }
    }

    @ModifyReturnValue(method = "getVisibilityPercent", at = @At(value = "RETURN"))
    private double modifyVisibility(double original) {
        Optional<Double> attribute = Optional.ofNullable(((LivingEntity) (Object) this).getAttributeValue(ModRegistry.MONSTER_AWARENESS_RANGE));
        return attribute.map(aDouble -> original * aDouble).orElse(original);
    }

    @WrapOperation(method = "dropFromLootTable", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/storage/loot/LootTable;getRandomItems(Lnet/minecraft/world/level/storage/loot/LootParams;JLjava/util/function/Consumer;)V"))
    private void markLootDrops(LootTable lootTable, LootParams params, long seed, Consumer<ItemStack> output, Operation<Void> original, @Local(argsOnly = true) DamageSource damageSource) {
        Entity attacker = damageSource.getEntity();
        boolean scavenger = attacker instanceof Player player && EnchantmentHelper.getEnchantmentLevel(player.level().registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(ModRegistry.SCAVENGER), player) > 0;
        original.call(lootTable, params, seed, scavenger ? markLoot((Player) attacker) : output);
    }

    @Unique
    private static Consumer<ItemStack> markLoot(LivingEntity entity) {
        return stack -> {
            ItemEntity itemEntity = entity.spawnAtLocation(stack, 0);
            if (itemEntity != null) {
                itemEntity.setAttached(EnchantOverhaul.SCAVENGER_LOOT, entity.getName().getString());
            }
        };
    }
}