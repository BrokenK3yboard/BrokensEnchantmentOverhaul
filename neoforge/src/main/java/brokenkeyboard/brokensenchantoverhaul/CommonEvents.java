package brokenkeyboard.brokensenchantoverhaul;

import brokenkeyboard.brokensenchantoverhaul.enchantment.ConditionalAttributeEffect;
import brokenkeyboard.brokensenchantoverhaul.network.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.SweepAttackEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.Map;
import java.util.Optional;

import static brokenkeyboard.brokensenchantoverhaul.Constants.MOD_ID;
import static brokenkeyboard.brokensenchantoverhaul.ModRegistry.BARRIER_STRENGTH;
import static brokenkeyboard.brokensenchantoverhaul.ModRegistry.MAX_LEVELS;

@EventBusSubscriber(modid = MOD_ID)
public class CommonEvents {

    @SubscribeEvent
    public static void addAttributes(EntityAttributeModificationEvent event) {
        event.getTypes().forEach(livingEntity -> {
            if (!event.has(livingEntity, ModRegistry.HEALING_EFFICIENCY)) {
                event.add(livingEntity, ModRegistry.HEALING_EFFICIENCY);
            }

            if (!event.has(livingEntity, ModRegistry.POSITIVE_EFFECT_DURATION)) {
                event.add(livingEntity, ModRegistry.POSITIVE_EFFECT_DURATION);
            }

            if (!event.has(livingEntity, ModRegistry.NEGATIVE_EFFECT_DURATION)) {
                event.add(livingEntity, ModRegistry.NEGATIVE_EFFECT_DURATION);
            }

            if (!event.has(livingEntity, ModRegistry.MONSTER_AWARENESS_RANGE)) {
                event.add(livingEntity, ModRegistry.MONSTER_AWARENESS_RANGE);
            }

            if (!event.has(livingEntity, BARRIER_STRENGTH)) {
                event.add(livingEntity, BARRIER_STRENGTH);
            }
        });

        if (!event.has(EntityType.PLAYER, ModRegistry.LOOTING_LEVEL)) {
            event.add(EntityType.PLAYER, ModRegistry.LOOTING_LEVEL);
        }
    }

    @SubscribeEvent
    public static void registerPayloads(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");

        registrar.playToClient(S2CEnchantmentSyncPayload.TYPE, S2CEnchantmentSyncPayload.STREAM_CODEC, (data, context) -> {
            Map<ResourceKey<Enchantment>, Integer> source = data.enchantments();
            context.enqueueWork(() -> {
                MAX_LEVELS.clear();
                MAX_LEVELS.putAll(source);
            });
        });

        registrar.playToServer(C2SWallJumpPayload.TYPE, C2SWallJumpPayload.STREAM_CODEC, (data, context) ->
                context.enqueueWork(() -> ServerPayloadHandler.handleWallJump((ServerLevel) context.player().level(), (ServerPlayer) context.player())));

        registrar.playToServer(C2SBarrierSyncPayload.TYPE, C2SBarrierSyncPayload.STREAM_CODEC, (payload, context) -> {
            if (!(context.player() instanceof ServerPlayer serverPlayer)) return;
            ServerPayloadHandler.sendS2CAttachmentSync(payload.entityID(), serverPlayer, entity ->
                    PacketDistributor.sendToPlayer(serverPlayer, new S2CBarrierSyncPayload(entity.getId(), entity.getData(EnchantOverhaul.BARRIER_AMOUNT))));
        });

        registrar.playToClient(S2CBarrierSyncPayload.TYPE, S2CBarrierSyncPayload.STREAM_CODEC, (payload, context) -> {
            Entity entity = context.player().level().getEntity(payload.entityID());
            if (entity != null) {
                entity.setData(EnchantOverhaul.BARRIER_AMOUNT, payload.amount());
            }
        });
    }

    @SubscribeEvent
    public static void modifyVisibility(LivingEvent.LivingVisibilityEvent event) {
        Optional<Double> attribute = Optional.of(event.getEntity().getAttributeValue(ModRegistry.MONSTER_AWARENESS_RANGE));
        attribute.ifPresent(event::modifyVisibility);
    }

    @SubscribeEvent
    public static void onBlockDrops(BlockDropsEvent event) {
        MiningHandler.handleBlockDrops(event.getDrops(), event.getLevel(), event.getState(), event.getPos(), event.getBreaker(), event.getTool());
    }

    @SubscribeEvent
    public static void onEntityDrops(LivingDropsEvent event) {
        if (event.getSource().getEntity() instanceof LivingEntity entity &&
                EnchantmentHelper.getEnchantmentLevel(entity.level().registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(ModRegistry.SCAVENGER), entity) > 0) {
            event.getDrops().forEach(itemEntity -> itemEntity.setData(EnchantOverhaul.SCAVENGER_LOOT, entity.getStringUUID()));
        }
    }

    @SubscribeEvent
    public static void itemPickupEvent(ItemEntityPickupEvent.Post event) {
        Player player = event.getPlayer();
        if (player.level() instanceof ServerLevel level && Optional.of(event.getItemEntity().getData(EnchantOverhaul.SCAVENGER_LOOT)).get().equals(player.getStringUUID())) {
            CommonHandler.postLootPickup(level, player);
        }
    }

    @SubscribeEvent
    public static void equipmentChanged(LivingEquipmentChangeEvent event) {
        ConditionalAttributeEffect.removeAttribute(event.getFrom(), event.getEntity(), event.getSlot());
    }

    @SubscribeEvent
    public static void preHurtEvent(LivingDamageEvent.Pre event) {
        event.setNewDamage(CommonHandler.handleBarrierDamage(event.getEntity(), event.getOriginalDamage()));
    }

    @SubscribeEvent
    public static void entityUseEvent(LivingEntityUseItemEvent.Tick event) {
        ItemStack useItem = event.getItem();
        if (useItem.getItem() instanceof BowItem && EnchantmentHelper.getTagEnchantmentLevel(event.getEntity()
                .level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(ModRegistry.BARRAGE), useItem) > 0) {
            event.setDuration(event.getDuration() - 1);
        }
    }

    @SubscribeEvent
    public static void isSweeping(SweepAttackEvent event) {
        event.setSweeping(EnchantmentHelper.has(event.getEntity().getWeaponItem(), ModRegistry.SWEEPING_DAMAGE_BONUS));
    }

    @SubscribeEvent
    public static void handleExcavatorEvent(BlockEvent.BreakEvent event) {
        MiningHandler.handleExcavator(event.getPlayer(), event.getState(), event.getPlayer().level(), event.getPos());
    }

    @SubscribeEvent
    public static void handleBreakSpeedEvent(PlayerEvent.BreakSpeed event) {
        event.setNewSpeed(event.getOriginalSpeed() + MiningHandler.modifyMiningEfficiency(event.getEntity().getMainHandItem()));
    }

    @SubscribeEvent
    public static void serverStart(ServerStartingEvent event) {
        event.getServer().registryAccess().registryOrThrow(Registries.ENCHANTMENT).entrySet().forEach(enchantment ->
                ModRegistry.MAX_LEVELS.put(enchantment.getKey(), enchantment.getValue().getMaxLevel()));
        ModRegistry.updateMaxLevels = false;
    }

    @SubscribeEvent
    public static void playerJoined(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(serverPlayer, new S2CEnchantmentSyncPayload(MAX_LEVELS));
            PacketDistributor.sendToPlayer(serverPlayer, new S2CBarrierSyncPayload(serverPlayer.getId(), Optional.of(serverPlayer.getData(EnchantOverhaul.BARRIER_AMOUNT)).orElse(0)));
        }
    }
}
