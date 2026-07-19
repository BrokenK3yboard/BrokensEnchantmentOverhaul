package brokenkeyboard.brokensenchantoverhaul.mixin;

import brokenkeyboard.brokensenchantoverhaul.ModRegistry;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ArmorItem.class)
public class ArmorItemMixin {

    @Final
    @Shadow
    protected Holder<ArmorMaterial> material;

    @ModifyReturnValue(method = "getEnchantmentValue", at = @At("RETURN"))
    public int changeEnchantmentValue(int original) {
        return ModRegistry.ARMOR_ENCHANTABILITY_OVERRIDE.getOrDefault(material.getRegisteredName().toLowerCase(), original);
    }
}
