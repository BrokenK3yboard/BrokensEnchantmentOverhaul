package brokenkeyboard.brokensenchantoverhaul.mixin;

import brokenkeyboard.brokensenchantoverhaul.ModRegistry;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Mixin(ThrownPotion.class)
public class ThrownPotionMixin {

    @Unique
    private static final Predicate<LivingEntity> ENTITY_HAS_DEPTH_STRIDER = entity ->
            EnchantmentHelper.getRandomItemWith(ModRegistry.DEPTH_STRIDER_SPLASH_WATER_BONUS, entity, stack -> true).isPresent();

    @Inject(method = "applyWater", at = @At("TAIL"))
    private void applyDepthStriderSplashEffect(CallbackInfo ci, @Local AABB aabb) {
        for (LivingEntity livingentity : ((ThrownPotion) (Object) this).level().getEntitiesOfClass(LivingEntity.class, aabb, ENTITY_HAS_DEPTH_STRIDER)) {
            livingentity.addEffect(new MobEffectInstance(ModRegistry.DEPTH_STRIDER, 900));
        }
    }
}
