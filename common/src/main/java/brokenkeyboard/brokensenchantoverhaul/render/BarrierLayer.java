package brokenkeyboard.brokensenchantoverhaul.render;

import brokenkeyboard.brokensenchantoverhaul.ModRegistry;
import brokenkeyboard.brokensenchantoverhaul.platform.Services;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class BarrierLayer<T extends LivingEntity, M extends HumanoidModel<T>> extends RenderLayer<T, M> {

    public static final ModelLayerLocation LAYER = new ModelLayerLocation(ModRegistry.location("barrier"), "barrier");
    // private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(ModRegistry.MOD_ID, "textures/layers/player_charged.png");
    private static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/wither/wither_armor.png");
    private final HumanoidModel<T> MODEL;

    public BarrierLayer(RenderLayerParent<T, M> renderer, HumanoidModel<T> model) {
        super(renderer);
        this.MODEL = model;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float partialAgeTicks, float netHeadYaw, float headPitch) {
        if (Services.PLATFORM.getBarrierAmount(entity) > 0) {
            float tick = (float) entity.tickCount + partialTicks;
            HumanoidModel<T> model = MODEL;
            this.getParentModel().copyPropertiesTo(model);
            model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
            VertexConsumer consumer = buffer.getBuffer(RenderType.energySwirl(TEXTURE, tick * 0.01F % 1.0F, tick * 0.01F % 1.0F));
            model.setupAnim(entity, limbSwing, limbSwingAmount, partialAgeTicks, netHeadYaw, headPitch);
            model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, -8355712);
        }
    }
}