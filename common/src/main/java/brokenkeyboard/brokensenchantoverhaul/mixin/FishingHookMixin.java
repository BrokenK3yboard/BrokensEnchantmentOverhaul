package brokenkeyboard.brokensenchantoverhaul.mixin;

import brokenkeyboard.brokensenchantoverhaul.ModRegistry;
import brokenkeyboard.brokensenchantoverhaul.enchantment.HookPullEffect;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.mutable.MutableInt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Optional;

@Mixin(FishingHook.class)
public class FishingHookMixin {

    @Inject(method = "retrieve", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/FishingHook;discard()V"))
    private void runHookPull(ItemStack stack, CallbackInfoReturnable<Integer> cir, @Local LocalIntRef durabilityLoss) {
        FishingHook hook = (FishingHook) (Object) this;
        if (hook.level() instanceof ServerLevel level) {
            durabilityLoss.set(HookPullEffect.applyHookPullEffect(level, stack, hook, new MutableInt(durabilityLoss.get())));
        }
    }

    @ModifyArgs(method = "retrieve", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/item/ItemEntity;<init>(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemStack;)V"))
    private void modifyDrops(Args args, @Local(ordinal = 0, argsOnly = true) ItemStack stack) {
        Level level = ((FishingHook) (Object) this).level();
        RegistryAccess access = level.registryAccess();

        if (args.get(4) instanceof ItemStack &&
                EnchantmentHelper.getItemEnchantmentLevel(access.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(ModRegistry.DEEP_FRYER), stack) > 0) {
            Optional<RecipeHolder<SmeltingRecipe>> recipe = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(args.get(4)), level);
            recipe.ifPresent(recipeHolder -> args.set(4, recipeHolder.value().getResultItem(access)));
        }
    }
}
