package brokenkeyboard.brokensenchantoverhaul.mixin;

import brokenkeyboard.brokensenchantoverhaul.ModRegistry;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(VillagerTrades.EnchantBookForEmeralds.class)
public class EnchantBookForEmeraldsMixin {

    @Shadow
    @Final
    private TagKey<Enchantment> tradeableEnchantments;

    // Removes disabled enchatments from enchanted book trades
    @Inject(method = "getOffer", at = @At(value = "INVOKE", target = "Ljava/util/Optional;isEmpty()Z"))
    private void changeOffer(Entity trader, RandomSource random, CallbackInfoReturnable<MerchantOffer> cir, @Local LocalRef<Optional<Holder<Enchantment>>> enchantment) {
        RegistryAccess access = trader.level().registryAccess();
        if (enchantment.get().isPresent() && enchantment.get().get().is(ModRegistry.REMOVED_ENCHANTMENTS)) {
            enchantment.set(access.registryOrThrow(Registries.ENCHANTMENT).getTag(tradeableEnchantments).map(HolderSet::stream)
                    .orElseGet(() -> access.registryOrThrow(Registries.ENCHANTMENT).holders().map((reference) -> reference))
                    .filter(holder -> !holder.is(ModRegistry.REMOVED_ENCHANTMENTS)).findAny());
        }
    }
}
