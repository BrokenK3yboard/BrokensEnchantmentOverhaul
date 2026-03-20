package brokenkeyboard.brokensenchantoverhaul.mixin;

import brokenkeyboard.brokensenchantoverhaul.Config;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EnchantmentScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Arrays;
import java.util.List;

@Mixin(EnchantmentScreen.class)
public class EnchantmentScreenMixin {

    @Unique
    private static final List<ResourceLocation> EC$ENABLED = Arrays.stream(getENABLED_LEVEL_SPRITES()).toList();
    @Unique
    private static final List<ResourceLocation> EC$DISABLED = Arrays.stream(getDISABLED_LEVEL_SPRITES()).toList();

    @Accessor
    static ResourceLocation[] getENABLED_LEVEL_SPRITES() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static ResourceLocation[] getDISABLED_LEVEL_SPRITES() {
        throw new UnsupportedOperationException();
    }

    // Prevent the experience orb graphic from being drawn in the enchanting table
    @WrapOperation(method = "renderBg", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V"))
    private void cancelDraw(GuiGraphics graphics, ResourceLocation sprite, int x, int y, int width, int height, Operation<Void> original) {
        if (Config.OVERHAUL_ENCHANTMENTS.get()) {
            if (!EC$ENABLED.contains(sprite) && !EC$DISABLED.contains(sprite)) {
                original.call(graphics, sprite, x, y, width, height);
            }
        } else {
            original.call(graphics, sprite, x, y, width, height);
        }
    }

    // Draw text further left as experience orb graphic is no longer present
    @WrapOperation(method = "renderBg", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;drawWordWrap(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/FormattedText;IIII)V"))
    private void adjustText(GuiGraphics graphics, Font font, FormattedText text, int x, int y, int lineWidth, int color, Operation<Void> original) {
        original.call(graphics, font, text, Config.OVERHAUL_ENCHANTMENTS.get() ? x - 12 : x, y, lineWidth, color);
    }

    // Modify tooltips of enchantment and lapis cost to match cost, not slot number
    @WrapOperation(method = "render", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;"))
    private MutableComponent changeTextSingle(String key, Operation<MutableComponent> original, @Local(ordinal = 3) int cost) {
        if (Config.OVERHAUL_ENCHANTMENTS.get()) {
            if (key.equals("container.enchant.level.one")) {
                return Component.translatable("container.enchant.level.many", ((EnchantmentScreen) (Object) this).getMenu().costs[cost]);
            } else if (key.equals("container.enchant.lapis.one")) {
                return Component.translatable("container.enchant.lapis.many", ((EnchantmentScreen) (Object) this).getMenu().costs[cost]);
            }
        }
        return original.call(key);
    }

    // Modify tooltips of enchantment and lapis cost to match cost, not slot number
    @WrapOperation(method = "render", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/network/chat/MutableComponent;"))
    private MutableComponent changeTextMulti(String key, Object[] args, Operation<MutableComponent> original, @Local(ordinal = 3) int cost) {
        if (Config.OVERHAUL_ENCHANTMENTS.get()) {
            if (key.equals("container.enchant.level.many")) {
                return Component.translatable("container.enchant.level.many", ((EnchantmentScreen) (Object) this).getMenu().costs[cost]);
            } else if (key.equals("container.enchant.lapis.many")) {
                return Component.translatable("container.enchant.lapis.many", ((EnchantmentScreen) (Object) this).getMenu().costs[cost]);
            }
        }
        return original.call(key, args);
    }
}