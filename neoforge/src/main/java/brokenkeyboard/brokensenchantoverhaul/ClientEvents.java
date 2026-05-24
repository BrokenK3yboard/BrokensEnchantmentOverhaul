package brokenkeyboard.brokensenchantoverhaul;

import brokenkeyboard.brokensenchantoverhaul.enchantment.WallSlideEffect;
import brokenkeyboard.brokensenchantoverhaul.network.C2SWallJumpPayload;
import brokenkeyboard.brokensenchantoverhaul.render.BarrierLayer;
import brokenkeyboard.brokensenchantoverhaul.render.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterRenderBuffersEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import static brokenkeyboard.brokensenchantoverhaul.Constants.MOD_ID;
import static brokenkeyboard.brokensenchantoverhaul.ModRegistry.location;

@EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT)
public class ClientEvents {

    public static final EnumProxy<Gui.HeartType> CUSTOM_HEART_TYPE_BARRIER = new EnumProxy<>(
            Gui.HeartType.class,
            location("hud/heart/barrier_full"),
            location("hud/heart/barrier_full_blinking"),
            location("hud/heart/barrier_half"),
            location("hud/heart/barrier_half_blinking"),
            location("hud/heart/barrier_hardcore_full"),
            location("hud/heart/barrier_hardcore_full_blinking"),
            location("hud/heart/barrier_hardcore_half"),
            location("hud/heart/barrier_hardcore_half_blinking"));

    @SubscribeEvent
    public static void armorLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(BarrierLayer.LAYER, () -> LayerDefinition.create(HumanoidModel.createMesh(new CubeDeformation(1.1F), 0F), 64, 64));
    }

    @SubscribeEvent
    public static void addEntityLayers(EntityRenderersEvent.AddLayers event) {
        addPlayerLayer(event, PlayerSkin.Model.WIDE);
        addPlayerLayer(event, PlayerSkin.Model.SLIM);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void addPlayerLayer(EntityRenderersEvent.AddLayers event, PlayerSkin.Model model) {
        EntityRenderer<Player> renderer = event.getSkin(model);
        if (renderer instanceof LivingEntityRenderer livingRenderer) {
            livingRenderer.addLayer(new BarrierLayer(livingRenderer));
        }
    }

    @SubscribeEvent
    public static void registerRenderBuffers(RegisterRenderBuffersEvent event) {
        event.registerRenderBuffer(RenderHelper.ALTERNATE_GLINT);
        event.registerRenderBuffer(RenderHelper.ALTERNATE_GLINT_TRANSLUCENT);
    }

    @SubscribeEvent
    private static void keyPressedEvent(InputEvent.Key event) {
        if (Minecraft.getInstance().player instanceof LocalPlayer player && player.input.jumping && WallSlideEffect.shouldSlide(player.level(), player)) {
            PacketDistributor.sendToServer(new C2SWallJumpPayload());
        }
    }
}
