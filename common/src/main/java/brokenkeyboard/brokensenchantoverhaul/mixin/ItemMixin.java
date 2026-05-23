package brokenkeyboard.brokensenchantoverhaul.mixin;

import brokenkeyboard.brokensenchantoverhaul.ModEnchantmentHelper;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Item.class)
public class ItemMixin {

    @WrapOperation(method = "mineBlock", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;hurtAndBreak(ILnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;)V"))
    private void checkTempering(ItemStack stack, int amount, LivingEntity entity, EquipmentSlot slot, Operation<Void> original, @Local(argsOnly = true) BlockState state) {
        original.call(stack, ModEnchantmentHelper.temperedAffects(entity.level().registryAccess(), stack, state) ? 0 : amount, entity, slot);
    }
}
