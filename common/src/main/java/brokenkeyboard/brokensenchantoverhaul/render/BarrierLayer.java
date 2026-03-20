package brokenkeyboard.brokensenchantoverhaul.render;

import brokenkeyboard.brokensenchantoverhaul.ModRegistry;
import brokenkeyboard.brokensenchantoverhaul.component.Barrier;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class BarrierLayer<T extends LivingEntity, M extends HumanoidModel<T>> extends RenderLayer<T, M> {

    public static final ModelLayerLocation LAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("minecraft", "player"), "barrier");
    // private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(ModRegistry.MOD_ID, "textures/layers/player_charged.png");
    private static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/wither/wither_armor.png");
    private final HumanoidModel<T> MODEL;

    public BarrierLayer(RenderLayerParent<T, M> renderer) {
        super(renderer);
        this.MODEL = new HumanoidModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(LAYER));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float partialAgeTicks, float netHeadYaw, float headPitch) {
        if (hasBarrier(entity)) {
            float tick = (float) entity.tickCount + partialTicks;
            EntityModel<T> model = MODEL;
            model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
            poseStack.pushPose();
            this.getParentModel().copyPropertiesTo(model);
            this.MODEL.body.copyFrom(getParentModel().body);
            this.MODEL.leftArm.copyFrom(getParentModel().leftArm);
            this.MODEL.rightArm.copyFrom(getParentModel().rightArm);
            this.MODEL.leftLeg.copyFrom(getParentModel().leftLeg);
            this.MODEL.rightLeg.copyFrom(getParentModel().rightLeg);
            VertexConsumer consumer = buffer.getBuffer(RenderType.energySwirl(TEXTURE, tick * 0.01F % 1.0F, tick * 0.01F % 1.0F));
            model.setupAnim(entity, limbSwing, limbSwingAmount, partialAgeTicks, netHeadYaw, headPitch);
            poseStack.popPose();
            this.MODEL.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, -8355712);
        }
    }

    public static boolean hasBarrier(LivingEntity entity) {
        for (ItemStack stack : entity.getArmorSlots()) {
            Optional<Barrier> barrier = Optional.ofNullable(stack.get(ModRegistry.BARRIER_INSTANCE));
            if (barrier.isPresent() && barrier.get().charges() > 0) return true;
        }
        return false;
    }
}