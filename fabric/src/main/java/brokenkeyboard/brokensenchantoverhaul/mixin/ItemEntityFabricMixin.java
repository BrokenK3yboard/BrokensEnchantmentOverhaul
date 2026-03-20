package brokenkeyboard.brokensenchantoverhaul.mixin;

import brokenkeyboard.brokensenchantoverhaul.EnchantOverhaul;
import brokenkeyboard.brokensenchantoverhaul.enchantment.ScavengerToughnessEffect;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@SuppressWarnings("UnstableApiUsage")
@Mixin(ItemEntity.class)
public class ItemEntityFabricMixin {

    @Inject(method = "playerTouch", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;take(Lnet/minecraft/world/entity/Entity;I)V"))
    private void pickupItem(Player player, CallbackInfo ci, @Local(ordinal = 0) int stackCount) {
        Optional<String> attachment = Optional.ofNullable(((ItemEntity) (Object) this).getAttached(EnchantOverhaul.SCAVENGER_LOOT));
        if (attachment.isPresent() && attachment.get().equals(player.getName().getString())) {
            ScavengerToughnessEffect.postLootPickup(player, stackCount);
        }
    }
}