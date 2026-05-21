package brokenkeyboard.brokensenchantoverhaul;

import brokenkeyboard.brokensenchantoverhaul.enchantment.*;
import brokenkeyboard.brokensenchantoverhaul.network.*;
import com.mojang.serialization.Codec;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.fml.config.ModConfig;

import java.util.Optional;

@SuppressWarnings("UnstableApiUsage")
public class EnchantOverhaul implements ModInitializer {

    public static final AttachmentType<String> SCAVENGER_LOOT = AttachmentRegistry.createPersistent(ModRegistry.location("scavenger_loot"), Codec.STRING);
    public static final AttachmentType<Integer> WALL_SLIDE_TICKS = AttachmentRegistry.createPersistent(ModRegistry.location("wall_side_ticks"), Codec.INT);
    public static final AttachmentType<Integer> POWER_SHOT_TICKS = AttachmentRegistry.createPersistent(ModRegistry.location("power_shot_ticks"), Codec.INT);
    public static final AttachmentType<Integer> BURN_STACKS = AttachmentRegistry.createPersistent(ModRegistry.location("burn_stacks"), ExtraCodecs.NON_NEGATIVE_INT);
    public static final AttachmentType<Integer> BARRIER_AMOUNT = AttachmentRegistry.createPersistent(ModRegistry.location("barrier_amount"), ExtraCodecs.NON_NEGATIVE_INT);
    public static final AttachmentType<Integer> BARRIER_TIMESTAMP = AttachmentRegistry.createPersistent(ModRegistry.location("barrier_timestamp"), ExtraCodecs.NON_NEGATIVE_INT);

    @Override
    public void onInitialize() {
        ModRegistry.bootstrap();
        NeoForgeConfigRegistry.INSTANCE.register(Constants.MOD_ID, ModConfig.Type.COMMON, Config.SPEC);
        Registry.register(ModRegistry.PROTECTION_REGISTRY, ModRegistry.location("adaptive"), AdaptiveEffect.CODEC);
        Registry.register(ModRegistry.PROTECTION_REGISTRY, ModRegistry.location("deflect_damage"), DeflectDamageEffect.CODEC);
        Registry.register(ModRegistry.ATTRIBUTE_REGISTRY, ModRegistry.location("fixed_attribute_effect"), FixedAttributeEffect.CODEC);
        Registry.register(ModRegistry.ATTRIBUTE_REGISTRY, ModRegistry.location("insight_looting"), InsightLootingEffect.CODEC);
        Registry.register(ModRegistry.ATTRIBUTE_REGISTRY, ModRegistry.location("adaptive_fire_resistance"), AdaptiveFREffect.CODEC);
        Registry.register(ModRegistry.ATTRIBUTE_REGISTRY, ModRegistry.location("adaptive_blast_resistance"), AdaptiveBREffect.CODEC);
        Registry.register(ModRegistry.HOOK_PULL_REGISTRY, ModRegistry.location("grapple"), GrappleEffect.CODEC);
        Registry.register(ModRegistry.HOOK_PULL_REGISTRY, ModRegistry.location("hook_burn"), HookBurnEffect.CODEC);

        ServerEntityEvents.EQUIPMENT_CHANGE.register((livingEntity, equipmentSlot, previous, next) ->
                ConditionalAttributeEffect.removeAttribute(previous, livingEntity, equipmentSlot));

        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
                server.registryAccess().registryOrThrow(Registries.ENCHANTMENT).entrySet().forEach(enchantment ->
                        ModRegistry.MAX_LEVELS.put(enchantment.getKey(), enchantment.getValue().getMaxLevel()));
            ModRegistry.updateMaxLevels = false;
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.getPlayer();
            ServerPlayNetworking.send(player, new S2CEnchantmentSyncPayload(ModRegistry.MAX_LEVELS));
            ServerPlayNetworking.send(player, new S2CBarrierSyncPayload(player.getId(), Optional.ofNullable(player.getAttached(BARRIER_AMOUNT)).orElse(0)));
        });

        PlayerBlockBreakEvents.AFTER.register((level, player, blockPos, blockState, blockEntity) ->
                MiningHandler.handleExcavator(player, blockState, level, blockPos));

        PayloadTypeRegistry.playS2C().register(S2CEnchantmentSyncPayload.TYPE, S2CEnchantmentSyncPayload.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(S2CBarrierSyncPayload.TYPE, S2CBarrierSyncPayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(C2SBarrierSyncPayload.TYPE, C2SBarrierSyncPayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(C2SWallJumpPayload.TYPE, C2SWallJumpPayload.STREAM_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(C2SBarrierSyncPayload.TYPE, (payload, context) ->
                context.server().execute(() -> ServerPayloadHandler.sendS2CAttachmentSync(payload.entityID(), context.player(), entity ->
                        ServerPlayNetworking.send(context.player(), new S2CBarrierSyncPayload(entity.getId(), Optional.ofNullable(entity.getAttached(BARRIER_AMOUNT)).orElse(0))))));

        ServerPlayNetworking.registerGlobalReceiver(C2SWallJumpPayload.TYPE, (payload, context) ->
                context.server().execute(() -> ServerPayloadHandler.handleWallJump(context.player().serverLevel(), context.player())));

        // Use Fabric to bootstrap the Common mod.
        Constants.LOG.info("Hello Fabric world!");
        CommonClass.init();
    }
}
