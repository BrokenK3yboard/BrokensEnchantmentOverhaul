package brokenkeyboard.brokensenchantoverhaul.mixin;

import brokenkeyboard.brokensenchantoverhaul.ModRegistry;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TieredItem.class)
public class TieredItemMixin {

    @Final
    @Shadow
    private Tier tier;

    @ModifyReturnValue(method = "getEnchantmentValue", at = @At("RETURN"))
    public int changeEnchantmentValue(int original) {
        return ModRegistry.TIER_ENCHANTABILITY_OVERRIDE.getOrDefault(tier.toString().toLowerCase(), original);
    }
}
