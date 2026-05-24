package brokenkeyboard.brokensenchantoverhaul.mixin;

import brokenkeyboard.brokensenchantoverhaul.Config;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.IdMap;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EnchantingTableBlock;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;
import java.util.stream.Stream;

@Mixin(EnchantmentMenu.class)
public class EnchantmentMenuMixin {

    @Shadow @Final public int[] levelClue;

    @Shadow @Final private RandomSource random;

    // Modify experience cost of enchantment
    @ModifyVariable(method = "clickMenuButton", at = @At(value = "STORE"), ordinal = 1)
    private int modifyCost(int value) {
        return Config.OVERHAUL_ENCHANTMENTS.get() ? Config.ENCHANTMENT_COST.get() : value;
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

    // Modify the listed cost of an enchantment
    @WrapOperation(method = {"lambda$slotsChanged$0", "method_17411"}, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getEnchantmentCost(Lnet/minecraft/util/RandomSource;IILnet/minecraft/world/item/ItemStack;)I"))
    private int modifyListedExperienceCost(RandomSource random, int enchantNum, int power, ItemStack stack, Operation<Integer> original) {
        return Config.OVERHAUL_ENCHANTMENTS.get() ? Config.ENCHANTMENT_COST.get() : original.call(random, enchantNum, power, stack);
    }

    // Set enchantment to apply to item
    @WrapOperation(method = {"lambda$clickMenuButton$1", "method_17410"}, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/inventory/EnchantmentMenu;getEnchantmentList(Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/world/item/ItemStack;II)Ljava/util/List;"))
    private List<EnchantmentInstance> setEnchantment(EnchantmentMenu menu, RegistryAccess access, ItemStack stack, int slot, int cost, Operation<List<EnchantmentInstance>> original) {
        IdMap<Holder<Enchantment>> idmap = access.registryOrThrow(Registries.ENCHANTMENT).asHolderIdMap();
        Holder<Enchantment> slotValue = idmap.byId(menu.enchantClue[slot]);
        return Config.OVERHAUL_ENCHANTMENTS.get() && slotValue != null ? List.of(new EnchantmentInstance(slotValue, levelClue[slot])) : original.call(menu, access, stack, slot, cost);
    }

    @WrapOperation(method = {"lambda$clickMenuButton$1", "method_17410"}, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;onEnchantmentPerformed(Lnet/minecraft/world/item/ItemStack;I)V"))
    private void applyBookshelfExperienceBonus(Player player, ItemStack stack, int levelCost, Operation<Void> original, @Local(argsOnly = true) Level level, @Local(argsOnly = true) BlockPos blockPos) {
        int bookshelves = 0;

        for (BlockPos blockPos1 : EnchantingTableBlock.BOOKSHELF_OFFSETS) {
            if (EnchantingTableBlock.isValidBookShelf(level, blockPos, blockPos1)) {
                bookshelves++;
            }
        }
        original.call(player, stack, random.nextDouble() < 0.01 * bookshelves ? 0 : levelCost);
    }
}