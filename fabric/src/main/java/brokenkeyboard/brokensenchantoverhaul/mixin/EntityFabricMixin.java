package brokenkeyboard.brokensenchantoverhaul.mixin;

import brokenkeyboard.brokensenchantoverhaul.EnchantOverhaul;
import brokenkeyboard.brokensenchantoverhaul.ModRegistry;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("UnstableApiUsage")
@Mixin(Entity.class)
public class EntityFabricMixin {

    @Inject(method = "spawnAtLocation(Lnet/minecraft/world/item/ItemStack;F)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/item/ItemEntity;setDefaultPickUpDelay()V"))
    private void markScavengerLoot(ItemStack stack, float offsetY, CallbackInfoReturnable<ItemEntity> cir, @Local ItemEntity itemEntity) {
        if ((Entity) (Object) this instanceof LivingEntity livingEntity && livingEntity.isDeadOrDying()) {
            if (livingEntity.getKillCredit() instanceof Player player &&
                    EnchantmentHelper.getEnchantmentLevel(player.level().registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(ModRegistry.SCAVENGER), player) > 0) {
                itemEntity.setAttached(EnchantOverhaul.SCAVENGER_LOOT, player.getName().getString());
            }
        }
    }
}
