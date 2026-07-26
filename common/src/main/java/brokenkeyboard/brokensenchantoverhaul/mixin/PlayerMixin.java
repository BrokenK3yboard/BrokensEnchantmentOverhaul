package brokenkeyboard.brokensenchantoverhaul.mixin;

import brokenkeyboard.brokensenchantoverhaul.ModRegistry;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import javax.management.Attribute;
import java.util.List;

@Mixin(Player.class)
public class PlayerMixin {

    @WrapOperation(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private boolean modifyTargetDamage(Entity target, DamageSource source, float damage, Operation<Boolean> original,
                                       @Share("totalDamage") LocalFloatRef totalDamage, @Local(ordinal = 3) boolean canSweepAttack) {
        if (canSweepAttack) {
            Player player = (Player) (Object) this;
            List<LivingEntity> entities = player.level().getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(1.0F, 0.25F, 1.0F));
            long entityCount = entities.stream().filter(target1 -> !target1.is(player) && !target1.isAlliedTo(player) &&
                    (!(target1 instanceof ArmorStand armorStand) || !armorStand.isMarker()) && player.distanceToSqr(target1) < 9.0D).count();
            float bonusDamage = (float) (player.getAttributeValue(ModRegistry.SWEEPING_DAMAGE_BONUS) * Math.max(0, entityCount - 1));
            totalDamage.set(bonusDamage);
            return original.call(target, source, (damage + bonusDamage + 1));
        }
        return original.call(target, source, damage);
    }

    @WrapOperation(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getAttributeValue(Lnet/minecraft/core/Holder;)D", ordinal = 1))
    private double modifySweepingDamage(Player player, Holder<Attribute> attributeHolder, Operation<Double> original,
                                        @Share("totalDamage") LocalFloatRef totalDamage, @Share("sweepingDamageBonus") LocalFloatRef sweepingDamageBonus) {
        double sweepingDamageRatio = original.call(player, attributeHolder);

        if (sweepingDamageRatio >= 0) {
            sweepingDamageRatio = 1;
        }

        sweepingDamageBonus.set((float) (totalDamage.get() * sweepingDamageRatio));
        return sweepingDamageRatio;
    }

    @WrapOperation(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private boolean modifySweepingDamage(LivingEntity entity, DamageSource source, float amount, Operation<Boolean> original, @Share("sweepingDamageBonus") LocalFloatRef sweepingDamageBonus) {
        return original.call(entity, source, amount + sweepingDamageBonus.get());
    }
}
