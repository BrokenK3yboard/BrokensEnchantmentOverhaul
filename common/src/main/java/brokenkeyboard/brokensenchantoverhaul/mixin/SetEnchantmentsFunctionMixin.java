package brokenkeyboard.brokensenchantoverhaul.mixin;

import brokenkeyboard.brokensenchantoverhaul.Config;
import brokenkeyboard.brokensenchantoverhaul.ModRegistry;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.SetEnchantmentsFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;
import java.util.stream.Stream;

@Mixin(SetEnchantmentsFunction.class)
public class SetEnchantmentsFunctionMixin {

    @ModifyReturnValue(method = "run", at = @At("RETURN"))
    private ItemStack replaceEnchantment(ItemStack original, @Local(argsOnly = true) LootContext context) {
        if (!original.isEnchanted()) return original;

        Optional.ofNullable(original.get(DataComponents.ENCHANTMENTS)).ifPresent(enchantments -> {
            Stream<Object2IntMap.Entry<Holder<Enchantment>>> filteredEnchantments = enchantments.entrySet().stream()
                    .filter(ench -> !ench.getKey().is(ModRegistry.REMOVED_ENCHANTMENTS));

            ItemEnchantments.Mutable replacement = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);

            if (Config.OVERHAUL_ENCHANTMENTS.get()) {
                filteredEnchantments.findAny().ifPresentOrElse(ench -> replacement.set(ench.getKey(), ench.getIntValue()), () -> {
                    RegistryAccess access = context.getLevel().registryAccess();
                    Optional<HolderSet<Enchantment>> holderSet = Optional.of(access.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(EnchantmentTags.NON_TREASURE));
                    EnchantmentHelper.enchantItem(context.getRandom(), new ItemStack(original.getItem()), 30, access, holderSet).getEnchantments().entrySet().forEach(ench ->
                            replacement.set(ench.getKey(), ench.getIntValue()));
                });
            } else {
                filteredEnchantments.forEach(ench -> replacement.set(ench.getKey(), ench.getIntValue()));
            }
            original.set(DataComponents.ENCHANTMENTS, replacement.toImmutable());
        });
        return original;
    }
}
