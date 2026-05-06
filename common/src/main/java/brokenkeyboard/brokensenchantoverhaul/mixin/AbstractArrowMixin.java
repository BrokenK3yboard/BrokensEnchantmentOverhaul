package brokenkeyboard.brokensenchantoverhaul.mixin;

import brokenkeyboard.brokensenchantoverhaul.CommonHandler;
import brokenkeyboard.brokensenchantoverhaul.ModRegistry;
import brokenkeyboard.brokensenchantoverhaul.platform.Services;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractArrow.class)
public class AbstractArrowMixin {

    @Shadow @Nullable private ItemStack firedFromWeapon;

    @WrapOperation(method = "onHitEntity", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/damagesource/DamageSources;arrow(Lnet/minecraft/world/entity/projectile/AbstractArrow;Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/world/damagesource/DamageSource;"))
    private DamageSource modifyDamageSource(DamageSources instance, AbstractArrow arrow, Entity shooter, Operation<DamageSource> original) {
        return (firedFromWeapon != null && arrow.level() instanceof ServerLevel level &&
                EnchantmentHelper.processProjectileCount(level, firedFromWeapon, shooter, 1) > 1)
                ? level.damageSources().source(ModRegistry.ARROW_MULTISHOT, arrow, shooter) : original.call(instance, arrow, shooter);
    }

    @WrapOperation(method = "onHitEntity", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;modifyDamage(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;F)F"))
    private float modifyDamage(ServerLevel level, ItemStack tool, Entity entity, DamageSource damageSource, float damage, Operation<Float> original) {
        int powerShotTicks = Services.PLATFORM.getPowerShotTicks((AbstractArrow) (Object) this);
        return original.call(level, tool, entity, damageSource, damage) + CommonHandler.getPowerShotDamage(firedFromWeapon, entity, powerShotTicks);
    }

    @WrapOperation(method = "doKnockback", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;modifyKnockback(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;F)F"))
    private float modifyKnockback(ServerLevel level, ItemStack tool, Entity entity, DamageSource damageSource, float knockback, Operation<Float> original) {
        int powerShotTicks = Services.PLATFORM.getPowerShotTicks((AbstractArrow) (Object) this);
        return original.call(level, tool, entity, damageSource, knockback) + CommonHandler.getPowerShotKnockback(firedFromWeapon, entity, powerShotTicks);
    }
}