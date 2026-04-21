package brokenkeyboard.brokensenchantoverhaul;

import brokenkeyboard.brokensenchantoverhaul.enchantment.*;
import brokenkeyboard.brokensenchantoverhaul.network.EnchantmentSyncPayload;
import brokenkeyboard.brokensenchantoverhaul.network.WallJumpPayload;
import brokenkeyboard.brokensenchantoverhaul.render.BarrierLayer;
import brokenkeyboard.brokensenchantoverhaul.render.RenderHelper;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.advancements.critereon.ItemSubPredicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentLocationBasedEffect;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterRenderBuffersEvent;
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
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static brokenkeyboard.brokensenchantoverhaul.Constants.MOD_ID;
import static brokenkeyboard.brokensenchantoverhaul.ModRegistry.*;

@Mod(MOD_ID)
public class EnchantOverhaul {

    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, MOD_ID);
    public static final DeferredRegister.DataComponents ENCHANTMENT_COMPONENTS = DeferredRegister.createDataComponents(Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, MOD_ID);
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, MOD_ID);
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, MOD_ID);
    public static final DeferredRegister<MapCodec<? extends EnchantmentLocationBasedEffect>> LOCATION_EFFECT_COMPONENTS = DeferredRegister.create(Registries.ENCHANTMENT_LOCATION_BASED_EFFECT_TYPE, MOD_ID);
    public static final DeferredRegister<MapCodec<? extends EnchantmentEntityEffect>> ENTITY_EFFECT_COMPONENTS = DeferredRegister.create(Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE, MOD_ID);
    public static final DeferredRegister<MapCodec<? extends EntitySubPredicate>> ENTITY_SUPREDICATES = DeferredRegister.create(Registries.ENTITY_SUB_PREDICATE_TYPE, MOD_ID);
    public static final DeferredRegister<ItemSubPredicate.Type<?>> ITEM_SUBPREDICATES = DeferredRegister.create(Registries.ITEM_SUB_PREDICATE_TYPE, MOD_ID);
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, MOD_ID);

    public static final Supplier<AttachmentType<String>> SCAVENGER_LOOT = ATTACHMENT_TYPES.register("scavenger_loot", () -> AttachmentType.builder(() -> "").serialize(Codec.STRING).build());
    public static final Supplier<AttachmentType<Integer>> WALL_SLIDE_TICKS = ATTACHMENT_TYPES.register("wall_slide_ticks", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT).build());
    public static final Supplier<AttachmentType<Integer>> POWER_SHOT_TICKS = ATTACHMENT_TYPES.register("power_shot_ticks", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT).build());
    public static final Supplier<AttachmentType<Integer>> BURN_STACKS = ATTACHMENT_TYPES.register("burn_stacks", () -> AttachmentType.builder(() -> 0).serialize(ExtraCodecs.NON_NEGATIVE_INT).build());

    public EnchantOverhaul(ModContainer container, IEventBus bus) {
        ModRegistry.bootstrap();
        container.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        DATA_COMPONENTS.register(bus);
        ENCHANTMENT_COMPONENTS.register(bus);
        EFFECTS.register(bus);
        ATTRIBUTES.register(bus);
        LOCATION_EFFECT_COMPONENTS.register(bus);
        ENTITY_EFFECT_COMPONENTS.register(bus);
        ENTITY_SUPREDICATES.register(bus);
        ITEM_SUBPREDICATES.register(bus);
        ATTACHMENT_TYPES.register(bus);
        bus.addListener((NewRegistryEvent event) -> event.register(PROTECTION_REGISTRY));
        bus.addListener((NewRegistryEvent event) -> event.register(ATTRIBUTE_REGISTRY));
        bus.addListener((NewRegistryEvent event) -> event.register(HOOK_PULL_REGISTRY));
        bus.addListener((RegisterEvent event) -> event.register(CONDITIONAL_PROTECTION_EFFECT, registry -> registry.register(location("adaptive"), AdaptiveEffect.CODEC)));
        bus.addListener((RegisterEvent event) -> event.register(CONDITIONAL_PROTECTION_EFFECT, registry -> registry.register(location("deflect_damage"), DeflectDamageEffect.CODEC)));
        bus.addListener((RegisterEvent event) -> event.register(CONDITIONAL_ATTRIBUTE_EFFECT, registry -> registry.register(location("fixed_attribute_effect"), FixedAttributeEffect.CODEC)));
        bus.addListener((RegisterEvent event) -> event.register(CONDITIONAL_ATTRIBUTE_EFFECT, registry -> registry.register(location("insight_luck"), InsightLuckEffect.CODEC)));
        bus.addListener((RegisterEvent event) -> event.register(CONDITIONAL_ATTRIBUTE_EFFECT, registry -> registry.register(location("insight_looting"), InsightLootingEffect.CODEC)));
        bus.addListener((RegisterEvent event) -> event.register(CONDITIONAL_ATTRIBUTE_EFFECT, registry -> registry.register(location("adaptive_fire_resistance"), AdaptiveFREffect.CODEC)));
        bus.addListener((RegisterEvent event) -> event.register(CONDITIONAL_ATTRIBUTE_EFFECT, registry -> registry.register(location("adaptive_blast_resistance"), AdaptiveBREffect.CODEC)));
        bus.addListener((RegisterEvent event) -> event.register(CONDITIONAL_ATTRIBUTE_EFFECT, registry -> registry.register(location("scavenger_toughness"), ScavengerToughnessEffect.CODEC)));
        bus.addListener((RegisterEvent event) -> event.register(CONDITIONAL_ATTRIBUTE_EFFECT, registry -> registry.register(location("inertia_knockback_resistance"), StabilizeKnockbackEffect.CODEC)));
        bus.addListener((RegisterEvent event) -> event.register(CONDITIONAL_ATTRIBUTE_EFFECT, registry -> registry.register(location("agility_speed"), AgilitySpeedEffect.CODEC)));
        bus.addListener((RegisterEvent event) -> event.register(HOOK_PULL_EFFECT, registry -> registry.register(location("grapple"), GrappleEffect.CODEC)));
        bus.addListener((RegisterEvent event) -> event.register(HOOK_PULL_EFFECT, registry -> registry.register(location("hook_burn"), HookBurnEffect.CODEC)));

        // Use NeoForge to bootstrap the Common mod.
        Constants.LOG.info("Hello NeoForge world!");
        CommonClass.init();
    }

    @EventBusSubscriber(modid = MOD_ID)
    public static class Events {

        @SubscribeEvent
        public static void modifyDetection(LivingEvent.LivingVisibilityEvent event) {
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
                event.getDrops().forEach(itemEntity -> itemEntity.setData(SCAVENGER_LOOT, entity.getStringUUID()));
            }
        }

        @SubscribeEvent
        public static void itemPickupEvent(ItemEntityPickupEvent.Post event) {
            Player player = event.getPlayer();
            if (Optional.of(event.getItemEntity().getData(SCAVENGER_LOOT)).get().equals(player.getStringUUID())) {
                ScavengerToughnessEffect.postLootPickup(player, event.getOriginalStack().getCount());
            }
        }

        @SubscribeEvent
        public static void equipmentChanged(LivingEquipmentChangeEvent event) {
            ConditionalAttributeEffect.removeAttribute(event.getFrom(), event.getEntity(), event.getSlot());
        }

        @SubscribeEvent
        public static void preHurtEvent(LivingDamageEvent.Pre event) {
            event.setNewDamage(BarrierEffect.modifyDamage(event.getEntity(), event.getSource(), event.getNewDamage()));
        }

        @SubscribeEvent
        public static void postHurtEvent(LivingDamageEvent.Post event) {
            ScavengerToughnessEffect.postHurt(event.getSource(), event.getNewDamage(), event.getEntity());
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
        public static void serverStart(ServerStartingEvent event) {
            event.getServer().registryAccess().registryOrThrow(Registries.ENCHANTMENT).entrySet().forEach(enchantment ->
                    ModRegistry.MAX_LEVELS.put(enchantment.getKey(), enchantment.getValue().getMaxLevel()));
            ModRegistry.updateMaxLevels = false;
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
        public static void playerJoined(PlayerEvent.PlayerLoggedInEvent event) {
            PacketDistributor.sendToPlayer((ServerPlayer) event.getEntity(), new EnchantmentSyncPayload(MAX_LEVELS));
        }
    }

    @EventBusSubscriber(modid = MOD_ID)
    public static class RegistryEvents {

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
            });

            if (!event.has(EntityType.PLAYER, ModRegistry.LOOTING_LEVEL)) {
                event.add(EntityType.PLAYER, ModRegistry.LOOTING_LEVEL);
            }
        }

        @SubscribeEvent
        public static void register(final RegisterPayloadHandlersEvent event) {
            final PayloadRegistrar registrar = event.registrar("1");
            registrar.playToServer(WallJumpPayload.TYPE, WallJumpPayload.STREAM_CODEC, (data, context) -> context.enqueueWork(() ->
                    CommonHandler.handleWallJump((ServerLevel) context.player().level(), (ServerPlayer) context.player())));

            registrar.playToClient(EnchantmentSyncPayload.TYPE, EnchantmentSyncPayload.STREAM_CODEC, (data, context) -> {
                Map<ResourceKey<Enchantment>, Integer> source = data.enchantments();
                context.enqueueWork(() -> {
                    MAX_LEVELS.clear();
                    MAX_LEVELS.putAll(source);
                });
            });
        }
    }

    @EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT)
    public static class ClientEvents {

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
                PacketDistributor.sendToServer(new WallJumpPayload());
            }
        }
    }
}