package brokenkeyboard.brokensenchantoverhaul.mixin;

import brokenkeyboard.brokensenchantoverhaul.CommonHandler;
import brokenkeyboard.brokensenchantoverhaul.EnchantOverhaul;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackFabricMixin {

    @WrapOperation(method = "addAttributeTooltips", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;forEachModifier(Lnet/minecraft/world/entity/EquipmentSlotGroup;Ljava/util/function/BiConsumer;)V"))
    private void modifyTooltip(ItemStack stack, EquipmentSlotGroup slotGroup, BiConsumer<Holder<Attribute>, AttributeModifier> action, Operation<Void> original,
                               @Local(argsOnly = true) Consumer<Component> tooltipAdder, @Local(argsOnly = true) Player player) {
        if (player != null) {
            EnchantOverhaul.modifyTooltip(player.level(), stack, player, slotGroup, tooltipAdder);
        } else {
            original.call(stack, slotGroup, action);
        }
    }

    @WrapOperation(method = "hurtAndBreak(ILnet/minecraft/server/level/ServerLevel;Lnet/minecraft/server/level/ServerPlayer;Ljava/util/function/Consumer;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;processDurabilityChange(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;I)I"))
    private int modifyDurabilityLoss(ServerLevel level, ItemStack stack, int damage, Operation<Integer> original) {
        int originalDamage = original.call(level, stack, damage);
        return CommonHandler.modifyDurabilityLoss(level.random, stack, originalDamage);
    }
}
