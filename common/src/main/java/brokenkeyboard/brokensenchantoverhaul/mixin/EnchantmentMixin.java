package brokenkeyboard.brokensenchantoverhaul.mixin;

import brokenkeyboard.brokensenchantoverhaul.Config;
import brokenkeyboard.brokensenchantoverhaul.ModRegistry;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.RemoveBinomial;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Enchantment.class)
public class EnchantmentMixin {

    @ModifyReturnValue(method = "getMaxLevel", at = @At("RETURN"))
    private int setMaxLevel(int original) {
        if (!ModRegistry.updateMaxLevels && Config.OVERHAUL_ENCHANTMENTS.get()) {
            return 1;
        }
        return original;
    }

    @ModifyReturnValue(method = "getMinCost", at = @At("RETURN"))
    private int setMinCost(int original) {
        return Config.OVERHAUL_ENCHANTMENTS.get() ? 10 : original;
    }

    @ModifyReturnValue(method = "getMaxCost", at = @At("RETURN"))
    private int setMaxCost(int original) {
        return Config.OVERHAUL_ENCHANTMENTS.get() ? 50 : original;
    }

    @Inject(method = "modifyDurabilityChange", at = @At(value = "TAIL"))
    private void modifyDurability(ServerLevel level, int enchantmentLevel, ItemStack tool, MutableFloat durabilityChange, CallbackInfo ci) {
        boolean hasUnbreakingBonus = tool.isEnchanted() && Config.OVERHAUL_ENCHANTMENTS.get() && tool.is(ModRegistry.WEAPON_DURABILITY_BONUS);
        if (hasUnbreakingBonus) {
            float enchantPower = tool.getItem().getEnchantmentValue();
            float chance = 0.0008F * enchantPower * enchantPower + 0.002F * enchantPower + 0.2F;
            durabilityChange.setValue(new RemoveBinomial(LevelBasedValue.constant(chance)).process(1, level.getRandom(), durabilityChange.getValue()));
        }
    }
}
