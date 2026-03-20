package brokenkeyboard.brokensenchantoverhaul.mixin;

import brokenkeyboard.brokensenchantoverhaul.Config;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EnchantmentInstance.class)
public class EnchantmentInstanceMixin {

    @ModifyVariable(method = "<init>", at = @At(value = "HEAD"), argsOnly = true)
    private static int setLevel(int value) {
        return Config.OVERHAUL_ENCHANTMENTS.get() ? 1 : value;
    }
}