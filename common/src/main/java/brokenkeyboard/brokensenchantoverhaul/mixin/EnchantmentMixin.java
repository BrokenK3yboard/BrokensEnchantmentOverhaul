package brokenkeyboard.brokensenchantoverhaul.mixin;

import brokenkeyboard.brokensenchantoverhaul.Config;
import brokenkeyboard.brokensenchantoverhaul.ModRegistry;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

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
}
