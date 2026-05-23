package brokenkeyboard.brokensenchantoverhaul.mixin;

import brokenkeyboard.brokensenchantoverhaul.ModEnchantmentHelper;
import brokenkeyboard.brokensenchantoverhaul.ModRegistry;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public class PlayerFabricMixin {

    @Definition(id = "SwordItem", type = SwordItem.class)
    @Expression("? instanceof SwordItem")
    @ModifyExpressionValue(method = "attack", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean modifyCheck(boolean original, @Local(ordinal = 1) ItemStack weapon) {
        return EnchantmentHelper.has(weapon, ModRegistry.SWEEPING_DAMAGE_BONUS);
    }

    @ModifyReturnValue(method = "createAttributes", at = @At("RETURN"))
    private static AttributeSupplier.Builder addAttributes(AttributeSupplier.Builder original) {
        original.add(ModRegistry.LOOTING_LEVEL);
        return original;
    }

    @WrapOperation(method = "getDestroySpeed", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getAttributeValue(Lnet/minecraft/core/Holder;)D"))
    private double modifyDestroySpeed(Player player, Holder<Attribute> holder, Operation<Double> original) {
        return original.call(player, holder) + ModEnchantmentHelper.modifyMiningEfficiency(player.getMainHandItem());
    }
}
