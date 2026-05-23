package brokenkeyboard.brokensenchantoverhaul.mixin;

import brokenkeyboard.brokensenchantoverhaul.ModEnchantmentHelper;
import brokenkeyboard.brokensenchantoverhaul.ModRegistry;
import brokenkeyboard.brokensenchantoverhaul.platform.Services;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.List;

@Mixin(Entity.class)
public class EntityMixin {

    @ModifyArgs(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private void modifyBurnDamage(Args args) {
        args.set(1, ((float) args.get(1)) + Services.PLATFORM.getBurnStacks((Entity) (Object) this));
    }

    @Inject(method = "setRemainingFireTicks", at = @At("HEAD"))
    private void clearBurnStacks(CallbackInfo ci, @Local(argsOnly = true) int remainingFireTicks) {
        if (remainingFireTicks <= 0) {
            Services.PLATFORM.setBurnStacks((Entity) (Object) this, 0);
        }
    }

    @Inject(method = "shouldBlockExplode", at = @At(value = "RETURN"), cancellable = true)
    private void modifyBlockExplode(Explosion explosion, BlockGetter level, BlockPos pos, BlockState blockState, float explosionPower, CallbackInfoReturnable<Boolean> cir) {
        Entity explosionSource = (Entity) (Object) this;
        List<LivingEntity> entities = explosionSource.level().getEntitiesOfClass(LivingEntity.class, new AABB(pos).inflate(4), ModEnchantmentHelper.HAS_STABILIZE);
        if (!entities.isEmpty()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "isInWaterOrRain", at = @At("RETURN"), cancellable = true)
    private void checkDepthStrider(CallbackInfoReturnable<Boolean> cir) {
        if ((Entity) (Object) this instanceof LivingEntity entity && EnchantmentHelper.getRandomItemWith(ModRegistry.CHANGE_WATER_EFFECTS, entity, stack -> true).isPresent()) {
            cir.setReturnValue(true);
        }
    }
}
