package brokenkeyboard.brokensenchantoverhaul;

import brokenkeyboard.brokensenchantoverhaul.enchantment.WallSlideEffect;
import brokenkeyboard.brokensenchantoverhaul.network.C2SWallJumpPayload;
import brokenkeyboard.brokensenchantoverhaul.network.S2CBarrierSyncPayload;
import brokenkeyboard.brokensenchantoverhaul.network.S2CEnchantmentSyncPayload;
import brokenkeyboard.brokensenchantoverhaul.network.S2CDetectedBlocks;
import brokenkeyboard.brokensenchantoverhaul.render.BarrierLayer;
import com.google.common.collect.ImmutableList;
import fuzs.extensibleenums.api.v2.core.EnumAppender;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import static brokenkeyboard.brokensenchantoverhaul.ModRegistry.MAX_LEVELS;
import static brokenkeyboard.brokensenchantoverhaul.ModRegistry.location;

public class ClientSetup implements ClientModInitializer {

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public void onInitializeClient() {
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register(this::addEntityLayers);
        EntityModelLayerRegistry.registerModelLayer(BarrierLayer.LAYER, () -> LayerDefinition.create(HumanoidModel.createMesh(new CubeDeformation(1.1F), 0F), 64, 64));

        try {
            ImmutableList.Builder<EnumAppender.FieldAccess> builder = heartTypeBuilder();
            new EnumAppender<>(Gui.HeartType.class, builder.build()).addEnumConstant("BROKENSENCHANTOVERHAUL_BARRIER",
                    location("hud/heart/barrier_full"),
                    location("hud/heart/barrier_full_blinking"),
                    location("hud/heart/barrier_half"),
                    location("hud/heart/barrier_half_blinking"),
                    location("hud/heart/barrier_hardcore_full"),
                    location("hud/heart/barrier_hardcore_full_blinking"),
                    location("hud/heart/barrier_hardcore_half"),
                    location("hud/heart/barrier_hardcore_half_blinking")).applyTo();

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        ClientTickEvents.END_CLIENT_TICK.register(minecraft -> {
            if (minecraft.player instanceof LocalPlayer player && player.input.jumping && WallSlideEffect.shouldSlide(player.level(), player)) {
                ClientPlayNetworking.send(new C2SWallJumpPayload());
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(S2CEnchantmentSyncPayload.TYPE, (payload, context) -> {
            MAX_LEVELS.clear();
            MAX_LEVELS.putAll(payload.enchantments());
            ModRegistry.updateMaxLevels = false;
        });

        ClientPlayNetworking.registerGlobalReceiver(S2CBarrierSyncPayload.TYPE, (payload, context) -> {
            Entity entity = context.player().level().getEntity(payload.entityID());
            if (entity != null) {
                entity.setAttached(EnchantOverhaul.BARRIER_AMOUNT, payload.amount());
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(S2CDetectedBlocks.TYPE, (payload, context) -> {
            Entity entity = context.player().level().getEntity(payload.entityID());
            if (entity != null) {
                entity.setAttached(EnchantOverhaul.BLOCKS_DETECTED, payload.nearBlocks());
            }
        });
    }

    private static ImmutableList.@NotNull Builder<EnumAppender.FieldAccess> heartTypeBuilder() {
        ImmutableList.Builder<EnumAppender.FieldAccess> builder = ImmutableList.builder();
        builder.add(new EnumAppender.FieldAccess(0, ResourceLocation.class));
        builder.add(new EnumAppender.FieldAccess(1, ResourceLocation.class));
        builder.add(new EnumAppender.FieldAccess(2, ResourceLocation.class));
        builder.add(new EnumAppender.FieldAccess(3, ResourceLocation.class));
        builder.add(new EnumAppender.FieldAccess(4, ResourceLocation.class));
        builder.add(new EnumAppender.FieldAccess(5, ResourceLocation.class));
        builder.add(new EnumAppender.FieldAccess(6, ResourceLocation.class));
        builder.add(new EnumAppender.FieldAccess(7, ResourceLocation.class));
        return builder;
    }

    @SuppressWarnings({"unchecked"})
    public void addEntityLayers(EntityType<? extends LivingEntity> entity, LivingEntityRenderer<?, ?> renderer, LivingEntityFeatureRendererRegistrationCallback.RegistrationHelper helper, EntityRendererProvider.Context context) {
        if (renderer.getModel() instanceof PlayerModel) {
            helper.register(new BarrierLayer<>((RenderLayerParent<LivingEntity, PlayerModel<LivingEntity>>) renderer));
        }
    }
}
