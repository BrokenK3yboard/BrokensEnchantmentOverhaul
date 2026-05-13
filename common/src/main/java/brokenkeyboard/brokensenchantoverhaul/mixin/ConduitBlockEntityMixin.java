package brokenkeyboard.brokensenchantoverhaul.mixin;

import brokenkeyboard.brokensenchantoverhaul.Config;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ConduitBlockEntity.class)
public class ConduitBlockEntityMixin {

    @Inject(method = "applyEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z"))
    private static void repairTrident(Level level, BlockPos pos, List<BlockPos> positions, CallbackInfo ci, @Local Player player) {
        if (Config.OVERHAUL_ENCHANTMENTS.get()) {
            ItemStack mainHand = player.getMainHandItem();
            ItemStack offHand = player.getOffhandItem();
            if (mainHand.is(Items.TRIDENT) && mainHand.isDamaged()) {
                mainHand.setDamageValue(mainHand.getDamageValue() - 8);
            } else if (offHand.is(Items.TRIDENT) && mainHand.isDamaged()) {
                offHand.setDamageValue(offHand.getDamageValue() - 8);
            }
        }
    }
}
