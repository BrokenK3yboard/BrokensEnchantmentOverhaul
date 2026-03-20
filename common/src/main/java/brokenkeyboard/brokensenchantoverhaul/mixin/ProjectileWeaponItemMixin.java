package brokenkeyboard.brokensenchantoverhaul.mixin;

import brokenkeyboard.brokensenchantoverhaul.ModRegistry;
import brokenkeyboard.brokensenchantoverhaul.platform.Services;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ProjectileWeaponItem.class)
public abstract class ProjectileWeaponItemMixin {

    @Inject(method = "shoot", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/ProjectileWeaponItem;shootProjectile(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/projectile/Projectile;IFFFLnet/minecraft/world/entity/LivingEntity;)V"))
    private void AbstractArrow(ServerLevel level, LivingEntity shooter, InteractionHand hand, ItemStack weapon, List<ItemStack> projectiles, float velocity, float inaccuracy, boolean isCrit, LivingEntity target, CallbackInfo ci,
                               @Local(ordinal = 0) Projectile projectile) {
        if (projectile instanceof AbstractArrow arrow) {
            Holder<Enchantment> enchantment = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(ModRegistry.POWER_SHOT);
            if (EnchantmentHelper.getItemEnchantmentLevel(enchantment, weapon) > 0) {
                Services.PLATFORM.setPowerShotTicks(arrow, weapon.getUseDuration(shooter) - shooter.getUseItemRemainingTicks());
            }
        }
    }
}
