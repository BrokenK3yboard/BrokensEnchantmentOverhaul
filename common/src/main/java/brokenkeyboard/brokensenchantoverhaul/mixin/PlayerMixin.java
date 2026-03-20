package brokenkeyboard.brokensenchantoverhaul.mixin;

import brokenkeyboard.brokensenchantoverhaul.CommonHandler;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(Player.class)
public class PlayerMixin {

    @WrapOperation(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private boolean modifyTargetDamage(Entity target, DamageSource source, float damage, Operation<Boolean> original,
                                       @Local(ordinal = 0) ItemStack weapon, @Share("bonusDamage") LocalFloatRef bonusDamage) {
        Player player = (Player) (Object) this;
        List<LivingEntity> entities = player.level().getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(1.0F, 0.25F, 1.0F));
        float newDamage = CommonHandler.getSweepingEdgeBonus(entities, player, target, weapon, source, damage);
        bonusDamage.set(newDamage);
        return original.call(target, source, newDamage);
    }

    @WrapOperation(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private boolean modifySweepingDamage(LivingEntity entity, DamageSource source, float amount, Operation<Boolean> original, @Share("bonusDamage") LocalFloatRef bonusDamage) {
        return original.call(entity, source, bonusDamage.get());
    }
}
