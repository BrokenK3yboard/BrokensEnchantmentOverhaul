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

import java.util.List;

@Mixin(EnchantmentMenu.class)
public class EnchantmentMenuFabricMixin {

    @Shadow @Final public int[] levelClue;

    @Shadow @Final private RandomSource random;

    // Modify the listed cost of an enchantment
    @WrapOperation(method = "method_17411", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getEnchantmentCost(Lnet/minecraft/util/RandomSource;IILnet/minecraft/world/item/ItemStack;)I"))
    private int modifyCost(RandomSource random, int enchantNum, int power, ItemStack stack, Operation<Integer> original) {
        return Config.OVERHAUL_ENCHANTMENTS.get() ? Config.ENCHANTMENT_COST.get() : original.call(random, enchantNum, power, stack);
    }

    // Set enchantment to apply to item
    @WrapOperation(method = "method_17410", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/inventory/EnchantmentMenu;getEnchantmentList(Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/world/item/ItemStack;II)Ljava/util/List;"))
    private List<EnchantmentInstance> setEnchantment(EnchantmentMenu menu, RegistryAccess access, ItemStack stack, int slot, int cost, Operation<List<EnchantmentInstance>> original) {
        IdMap<Holder<Enchantment>> idmap = access.registryOrThrow(Registries.ENCHANTMENT).asHolderIdMap();
        Holder<Enchantment> slotValue = idmap.byId(menu.enchantClue[slot]);
        return Config.OVERHAUL_ENCHANTMENTS.get() && slotValue != null ? List.of(new EnchantmentInstance(slotValue, levelClue[slot])) : original.call(menu, access, stack, slot, cost);
    }

    @WrapOperation(method = "method_17410", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;onEnchantmentPerformed(Lnet/minecraft/world/item/ItemStack;I)V"))
    private void modifyExperienceCost(Player player, ItemStack stack, int levelCost, Operation<Void> original, @Local(argsOnly = true) Level level, @Local(argsOnly = true) BlockPos blockPos) {
        int bookshelves = 0;

        for (BlockPos blockPos1 : EnchantingTableBlock.BOOKSHELF_OFFSETS) {
            if (EnchantingTableBlock.isValidBookShelf(level, blockPos, blockPos1)) {
                bookshelves++;
            }
        }

        original.call(player, stack, random.nextDouble() < 0.01 * bookshelves ? 0 : levelCost);
    }
}
