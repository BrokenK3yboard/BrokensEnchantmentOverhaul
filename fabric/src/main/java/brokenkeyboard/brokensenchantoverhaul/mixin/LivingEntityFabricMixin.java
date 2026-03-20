package brokenkeyboard.brokensenchantoverhaul.mixin;

import brokenkeyboard.brokensenchantoverhaul.ModRegistry;
import brokenkeyboard.brokensenchantoverhaul.enchantment.BarrierEffect;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(LivingEntity.class)
public class LivingEntityFabricMixin {

    @Shadow
    protected int useItemRemaining;

    @Inject(method = "getDamageAfterMagicAbsorb", at = @At(value = "RETURN"), cancellable = true)
    private void preHurt(DamageSource damageSource, float damageAmount, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(BarrierEffect.modifyDamage((LivingEntity) (Object) this, damageSource, damageAmount));
    }

    @ModifyReturnValue(method = "createLivingAttributes", at = @At("RETURN"))
    private static AttributeSupplier.Builder addAttributes(AttributeSupplier.Builder original) {
        original.add(ModRegistry.HEALING_EFFICIENCY)
                .add(ModRegistry.POSITIVE_EFFECT_DURATION)
                .add(ModRegistry.NEGATIVE_EFFECT_DURATION)
                .add(ModRegistry.MONSTER_AWARENESS_RANGE);
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
}