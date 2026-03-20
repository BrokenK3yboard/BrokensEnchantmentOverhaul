package brokenkeyboard.brokensenchantoverhaul.mixin;

import brokenkeyboard.brokensenchantoverhaul.ModRegistry;
import brokenkeyboard.brokensenchantoverhaul.enchantment.AdaptiveEffect;
import brokenkeyboard.brokensenchantoverhaul.enchantment.BarrierEffect;
import brokenkeyboard.brokensenchantoverhaul.enchantment.ConditionalProtectionEffect;
import brokenkeyboard.brokensenchantoverhaul.platform.Services;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Optional;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow
    @Nullable
    public abstract AttributeInstance getAttribute(Holder<Attribute> attribute);

    @ModifyExpressionValue(method = "getDamageAfterMagicAbsorb", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getDamageProtection(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/damagesource/DamageSource;)F"))
    private float modifyDamageCoef(float original, @Local(ordinal = 0, argsOnly = true) DamageSource source, @Local(ordinal = 0, argsOnly = true) float amount) {
        LivingEntity entity = (LivingEntity) (Object) this;
        return original + ConditionalProtectionEffect.modifyEnchantmentProtection((ServerLevel) entity.level(), entity, amount, source);
    }

    @Inject(method = "onEquipItem", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/Equipable;get(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/Equipable;"))
    private void removeEffect(EquipmentSlot slot, ItemStack oldItem, ItemStack newItem, CallbackInfo ci) {
        BarrierEffect.equipmentChanged(newItem);
        AdaptiveEffect.equipmentChanged(newItem);
    }

    @ModifyArgs(method = "heal", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setHealth(F)V"))
    private void modifyHealing(Args args, @Local(ordinal = 0, argsOnly = true) float healing, @Local(ordinal = 1) float health) {
        Optional<AttributeInstance> healingEfficiency = Optional.ofNullable(getAttribute(ModRegistry.HEALING_EFFICIENCY));
        if (healingEfficiency.isPresent() && healingEfficiency.get().getValue() > 0) {
            args.set(0, health + (float) (healing * healingEfficiency.get().getValue()));
        }
    }

    @Inject(method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/effect/MobEffectInstance;getEffect()Lnet/minecraft/core/Holder;"))
    private void modifyEffects(MobEffectInstance effect, Entity entity, CallbackInfoReturnable<Boolean> cir, @Local(argsOnly = true) LocalRef<MobEffectInstance> ref) {
        LivingEntity living = ((LivingEntity) (Object) this);

        if (effect.getEffect().value().getCategory().equals(MobEffectCategory.BENEFICIAL) && living.getAttributes().hasAttribute(ModRegistry.POSITIVE_EFFECT_DURATION)) {
            int updateDuration = (int) (effect.getDuration() * living.getAttributeValue(ModRegistry.POSITIVE_EFFECT_DURATION));
            ref.set(new MobEffectInstance(effect.getEffect(), updateDuration, effect.getAmplifier(), effect.isAmbient(), effect.isVisible(), effect.showIcon()));
        } else if (effect.getEffect().value().getCategory().equals(MobEffectCategory.HARMFUL) && living.getAttributes().hasAttribute(ModRegistry.NEGATIVE_EFFECT_DURATION)) {
            int updateDuration = (int) (effect.getDuration() * living.getAttributeValue(ModRegistry.NEGATIVE_EFFECT_DURATION));
            ref.set(new MobEffectInstance(effect.getEffect(), updateDuration, effect.getAmplifier(), effect.isAmbient(), effect.isVisible(), effect.showIcon()));
        }
    }

    @Inject(method = "checkFallDamage", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;onChangedBlock(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;)V"))
    private void resetSlidingDistance(double y, boolean onGround, BlockState state, BlockPos pos, CallbackInfo ci) {
        Services.PLATFORM.setWallSlideTicks((LivingEntity) (Object) this, 0);
    }
}