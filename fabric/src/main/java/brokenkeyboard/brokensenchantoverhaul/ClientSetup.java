package brokenkeyboard.brokensenchantoverhaul;

import brokenkeyboard.brokensenchantoverhaul.enchantment.WallSlideEffect;
import brokenkeyboard.brokensenchantoverhaul.network.WallJumpPayload;
import brokenkeyboard.brokensenchantoverhaul.render.BarrierLayer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

public class ClientSetup implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register(this::addEntityLayers);
        EntityModelLayerRegistry.registerModelLayer(BarrierLayer.LAYER, () -> LayerDefinition.create(HumanoidModel.createMesh(new CubeDeformation(1.1F), 0F), 64, 64));

        ClientTickEvents.END_CLIENT_TICK.register(minecraft -> {
            if (minecraft.player instanceof LocalPlayer player && player.input.jumping && WallSlideEffect.shouldSlide(player.level(), player)) {
                ClientPlayNetworking.send(new WallJumpPayload());
            }
        });
    }

    @SuppressWarnings({"unchecked"})
    public void addEntityLayers(EntityType<? extends LivingEntity> entity, LivingEntityRenderer<?, ?> renderer, LivingEntityFeatureRendererRegistrationCallback.RegistrationHelper helper, EntityRendererProvider.Context context) {
        if (renderer.getModel() instanceof PlayerModel) {
            helper.register(new BarrierLayer<>((RenderLayerParent<LivingEntity, PlayerModel<LivingEntity>>) renderer));
        }
    }
}
