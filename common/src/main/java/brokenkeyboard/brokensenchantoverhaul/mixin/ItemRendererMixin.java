package brokenkeyboard.brokensenchantoverhaul.mixin;

import brokenkeyboard.brokensenchantoverhaul.enchantment.GlintModifier;
import brokenkeyboard.brokensenchantoverhaul.render.RenderHelper;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    @Unique
    private static final ThreadLocal<LivingEntity> enchantmentMod$LIVING = new ThreadLocal<>();

    @Inject(method = "renderStatic(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/level/Level;III)V",
    at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V"))
    private void setLiving(LivingEntity entity, ItemStack itemStack, ItemDisplayContext diplayContext, boolean leftHand, PoseStack poseStack, MultiBufferSource bufferSource, Level level, int combinedLight, int combinedOverlay, int seed, CallbackInfo ci) {
        enchantmentMod$LIVING.set(entity);
    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;getFoilBufferDirect(Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/renderer/RenderType;ZZ)Lcom/mojang/blaze3d/vertex/VertexConsumer;"))
    private VertexConsumer getFoilBufferDirect(MultiBufferSource bufferSource, RenderType type, boolean noEntity, boolean withGlint, Operation<VertexConsumer> original, @Local(argsOnly = true) ItemStack stack) {
        try {
            if (GlintModifier.shouldUseGlint(stack, enchantmentMod$LIVING.get())) {
                return withGlint ? VertexMultiConsumer.create(bufferSource.getBuffer(noEntity ? RenderHelper.ALTERNATE_GLINT : RenderType.entityGlintDirect()), bufferSource.getBuffer(type)) : bufferSource.getBuffer(type);
            }
        } finally {
            enchantmentMod$LIVING.remove();
        }
        return original.call(bufferSource, type, noEntity, withGlint);
    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;getFoilBuffer(Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/renderer/RenderType;ZZ)Lcom/mojang/blaze3d/vertex/VertexConsumer;"))
    private VertexConsumer getEntityGlintDirect(MultiBufferSource bufferSource, RenderType type, boolean isItem, boolean glint, Operation<VertexConsumer> original, @Local(argsOnly = true) ItemStack stack) {
        try {
            if (GlintModifier.shouldUseGlint(stack, enchantmentMod$LIVING.get())) {
                if (!glint) {
                    return bufferSource.getBuffer(type);
                } else {
                    return Minecraft.useShaderTransparency() && type == Sheets.translucentItemSheet()
                            ? VertexMultiConsumer.create(bufferSource.getBuffer(RenderHelper.ALTERNATE_GLINT_TRANSLUCENT), bufferSource.getBuffer(type))
                            : VertexMultiConsumer.create(bufferSource.getBuffer(isItem ? RenderHelper.ALTERNATE_GLINT : RenderType.entityGlint()), bufferSource.getBuffer(type));
                }
            }
        } finally {
            enchantmentMod$LIVING.remove();
        }
        return original.call(bufferSource, type, isItem, glint);
    }
}
