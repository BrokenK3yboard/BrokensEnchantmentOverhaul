package brokenkeyboard.brokensenchantoverhaul.mixin;

import brokenkeyboard.brokensenchantoverhaul.effect.EnchantmentMobEffect;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.common.EffectCure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Set;

@Mixin(MobEffectInstance.class)
public class MobEffectInstanceNFMixin {

    @WrapOperation(method = "<init>(Lnet/minecraft/core/Holder;IIZZZLnet/minecraft/world/effect/MobEffectInstance;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffect;fillEffectCures(Ljava/util/Set;Lnet/minecraft/world/effect/MobEffectInstance;)V"))
    private void modifyCures(MobEffect effect, Set<EffectCure> cures, MobEffectInstance mobEffectInstance, Operation<Void> original) {
        if (!(effect instanceof EnchantmentMobEffect)) {
            original.call(effect, cures, mobEffectInstance);
        }
    }
}
