package brokenkeyboard.brokensenchantoverhaul.mixin;

import brokenkeyboard.brokensenchantoverhaul.Config;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Holder;
import net.minecraft.core.IdMap;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;
import java.util.stream.Stream;

@Mixin(EnchantmentMenu.class)
public class EnchantmentMenuMixin {

    // Modify experience cost of enchantment
    @ModifyVariable(method = "clickMenuButton", ordinal = 1, at = @At(value = "STORE", ordinal = 0))
    private int modifyCost(int value, @Local(ordinal = 0, argsOnly = true) int ref) {
        if (Config.OVERHAUL_ENCHANTMENTS.get()) {
            EnchantmentMenu menu = (EnchantmentMenu) (Object) this;
            return menu.costs[ref];
        }
        return value;
    }

    @WrapOperation(method = "getEnchantmentList", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;selectEnchantment(Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/item/ItemStack;ILjava/util/stream/Stream;)Ljava/util/List;"))
    private List<EnchantmentInstance> modifyList(RandomSource random, ItemStack stack, int cost, Stream<Holder<Enchantment>> enchantments, Operation<List<EnchantmentInstance>> original,
                                                 @Local(ordinal = 0, argsOnly = true) RegistryAccess access) {
        if (Config.OVERHAUL_ENCHANTMENTS.get()) {
            EnchantmentMenu menu = (EnchantmentMenu) (Object) this;
            IdMap<Holder<Enchantment>> idmap = access.registryOrThrow(Registries.ENCHANTMENT).asHolderIdMap();
            Stream<Holder<Enchantment>> filteredList = enchantments.filter(ench -> {
                Holder<Enchantment> hintID1 = idmap.byId(menu.enchantClue[0]);
                Holder<Enchantment> hintID2 = idmap.byId(menu.enchantClue[1]);
                Holder<Enchantment> hintID3 = idmap.byId(menu.enchantClue[2]);

                boolean flag1 = hintID1 == null || (hintID1 != null && !hintID1.equals(ench));
                boolean flag2 = hintID2 == null || (hintID2 != null && !hintID2.equals(ench));
                boolean flag3 = hintID3 == null || (hintID3 != null && !hintID3.equals(ench));
                return flag1 && flag2 && flag3;
            });
            return original.call(random, stack, 30, filteredList);
        }
        return original.call(random, stack, cost, enchantments);
    }
}