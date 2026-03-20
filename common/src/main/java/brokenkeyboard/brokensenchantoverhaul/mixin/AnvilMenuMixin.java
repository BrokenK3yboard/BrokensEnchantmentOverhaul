package brokenkeyboard.brokensenchantoverhaul.mixin;

import brokenkeyboard.brokensenchantoverhaul.Config;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public class AnvilMenuMixin {

    @Shadow
    @Final
    private DataSlot cost;

    @Inject(method = "createResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/AnvilMenu;broadcastChanges()V"))
    private void modifyAnvilResult(CallbackInfo ci, @Local(ordinal = 0) int repairCost) {
        if (Config.OVERHAUL_ENCHANTMENTS.get()) {
            AnvilMenu anvilMenu = (AnvilMenu) (Object) this;
            ItemCombinerMenuAccessor menu = ((ItemCombinerMenuAccessor) anvilMenu);
            Container inputs = menu.getInputSlots();
            ResultContainer outputs = menu.getResultSlots();
            ItemStack left = inputs.getItem(0), right = inputs.getItem(1);
            final ItemStack result = outputs.getItem(0).copy();
            result.set(DataComponents.REPAIR_COST, 0);

            if (right.is(Items.ENCHANTED_BOOK) && left.isEnchanted()) {
                outputs.setItem(0, ItemStack.EMPTY);
                cost.set(0);
            } else if (!outputs.isEmpty()) {
                if (left.is(right.getItem())) {
                    if (left.isEnchanted()) {
                        result.set(DataComponents.ENCHANTMENTS, left.getEnchantments());
                        outputs.setItem(0, result);
                    } else if (!left.isEnchanted() && right.isEnchanted()) {
                        result.set(DataComponents.ENCHANTMENTS, right.getEnchantments());
                        outputs.setItem(0, result);
                    }
                    cost.set(left.isEnchanted() || right.isEnchanted() ? Config.ENCHANTED_ITEM_FULL_COST.get() : 0);
                } else if (left.getItem().isValidRepairItem(left, right)) {
                    cost.set(left.isEnchanted() ? repairCost * Config.ENCHANTED_ITEM_MATERIAL_COST.get() : 0);
                } else if (!left.isEnchanted() && right.is(Items.ENCHANTED_BOOK)) {
                    cost.set(Config.ENCHANTED_BOOK_ANVIL_COST.get());
                }
            }

            ItemStack output = outputs.getItem(0);

            if (!output.isEmpty() && !left.getOrDefault(DataComponents.CUSTOM_NAME, CustomData.EMPTY).equals(output.getOrDefault(DataComponents.CUSTOM_NAME, CustomData.EMPTY))) {
                cost.set(cost.get() + Config.ANVIL_RENAME_COST.get());
            }
        }
    }
}
