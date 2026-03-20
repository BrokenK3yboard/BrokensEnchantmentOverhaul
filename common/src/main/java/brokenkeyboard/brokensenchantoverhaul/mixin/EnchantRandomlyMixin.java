package brokenkeyboard.brokensenchantoverhaul.mixin;

import brokenkeyboard.brokensenchantoverhaul.ModRegistry;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.stream.Stream;

@Mixin(EnchantRandomlyFunction.class)
public class EnchantRandomlyMixin {

    // Remove blacklisted enchantments from EnchantmentTags.ON_RANDOM_LOOT
    @WrapOperation(method = "run", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;toList()Ljava/util/List;"))
    private List<Holder<Enchantment>> filterEnchantments(Stream<Holder<Enchantment>> stream, Operation<List<Holder<Enchantment>>> original) {
        return original.call(stream.filter(enchantment -> !enchantment.is(ModRegistry.REMOVED_ENCHANTMENTS)));
    }
}
