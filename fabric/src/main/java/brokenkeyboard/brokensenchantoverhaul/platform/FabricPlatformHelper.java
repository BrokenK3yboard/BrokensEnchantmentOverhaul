package brokenkeyboard.brokensenchantoverhaul.platform;

import brokenkeyboard.brokensenchantoverhaul.EnchantOverhaul;
import brokenkeyboard.brokensenchantoverhaul.ModRegistry;
import brokenkeyboard.brokensenchantoverhaul.network.C2SBarrierSyncPayload;
import brokenkeyboard.brokensenchantoverhaul.network.S2CBarrierSyncPayload;
import brokenkeyboard.brokensenchantoverhaul.platform.services.IPlatformHelper;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.advancements.critereon.ItemSubPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentLocationBasedEffect;

import java.util.Optional;
import java.util.function.UnaryOperator;

@SuppressWarnings("UnstableApiUsage")
public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public <T> Registry<T> createRegistry(ResourceKey<Registry<T>> key) {
        return FabricRegistryBuilder.createSimple(key).buildAndRegister();
    }

    @Override
    public <T> DataComponentType<T> createDataComponent(String name, UnaryOperator<DataComponentType.Builder<T>> operator) {
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ModRegistry.location(name), operator.apply(DataComponentType.builder()).build());
    }

    @Override
    public <T> DataComponentType<T> createEnchantmentComponent(String name, UnaryOperator<DataComponentType.Builder<T>> operator) {
        return Registry.register(BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, ModRegistry.location(name), operator.apply(DataComponentType.builder()).build());
    }

    @Override
    public <T extends EnchantmentLocationBasedEffect> void createLocationComponent(String name, MapCodec<T> codec) {
        Registry.register(BuiltInRegistries.ENCHANTMENT_LOCATION_BASED_EFFECT_TYPE, ModRegistry.location(name), codec);
    }

    @Override
    public <T extends EnchantmentEntityEffect> void createEntityEffectComponent(String name, MapCodec<T> codec) {
        Registry.register(BuiltInRegistries.ENCHANTMENT_ENTITY_EFFECT_TYPE, ModRegistry.location(name), codec);
    }

    @Override
    public <T extends EntitySubPredicate> void createEntitySubPredicate(String name, MapCodec<T> codec) {
        Registry.register(BuiltInRegistries.ENTITY_SUB_PREDICATE_TYPE, ModRegistry.location(name), codec);
    }

    @Override
    public <T extends ItemSubPredicate> ItemSubPredicate.Type<T> createItemSubPredicate(String name, MapCodec<T> codec) {
        return Registry.register(BuiltInRegistries.ITEM_SUB_PREDICATE_TYPE, ModRegistry.location(name), new ItemSubPredicate.Type<>(codec.codec()));
    }

    @Override
    public Holder<MobEffect> createEffectHolder(String name, MobEffect effect) {
        return Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, ModRegistry.location(name), effect);
    }

    @Override
    public Holder<Attribute> createAttribute(String name, Attribute attribute) {
        return Registry.registerForHolder(BuiltInRegistries.ATTRIBUTE, ModRegistry.location(name), attribute.setSyncable(true).setSentiment(Attribute.Sentiment.POSITIVE));
    }

    @Override
    public void setWallSlideTicks(LivingEntity entity, int amount) {
        entity.setAttached(EnchantOverhaul.WALL_SLIDE_TICKS, amount);
    }

    @Override
    public int getWallSlideTicks(LivingEntity entity) {
        return Optional.ofNullable(entity.getAttached(EnchantOverhaul.WALL_SLIDE_TICKS)).orElse(0);
    }

    @Override
    public void setPowerShotTicks(AbstractArrow arrow, int ticks) {
        arrow.setAttached(EnchantOverhaul.POWER_SHOT_TICKS, ticks);
    }

    @Override
    public int getPowerShotTicks(AbstractArrow arrow) {
        return Optional.ofNullable(arrow.getAttached(EnchantOverhaul.POWER_SHOT_TICKS)).orElse(0);
    }

    @Override
    public void setBurnStacks(Entity entity, int stacks) {
        entity.setAttached(EnchantOverhaul.BURN_STACKS, stacks);
    }

    @Override
    public int getBurnStacks(Entity entity) {
        return Optional.ofNullable(entity.getAttached(EnchantOverhaul.BURN_STACKS)).orElse(0);
    }

    @Override
    public void setBarrierAmount(LivingEntity entity, int amount) {
        entity.setAttached(EnchantOverhaul.BARRIER_AMOUNT, amount);

        if (!entity.level().isClientSide()) {
            if (entity instanceof ServerPlayer serverPlayer) {
                ServerPlayNetworking.send(serverPlayer, new S2CBarrierSyncPayload(serverPlayer.getId(), amount));
            }

            for (ServerPlayer serverPlayer : PlayerLookup.tracking(entity)) {
                ServerPlayNetworking.send(serverPlayer, new S2CBarrierSyncPayload(entity.getId(), amount));
            }
        }
    }

    @Override
    public int getBarrierAmount(LivingEntity entity) {
        return Optional.ofNullable(entity.getAttached(EnchantOverhaul.BARRIER_AMOUNT)).orElse(0);
    }

    @Override
    public void C2SBarrierSync(Entity entity) {
        ClientPlayNetworking.send(new C2SBarrierSyncPayload(entity.getId()));
    }

    @Override
    public void setBarrierTimestamp(LivingEntity entity) {
        entity.setAttached(EnchantOverhaul.BARRIER_TIMESTAMP, entity.tickCount);
    }

    @Override
    public int getBarrierTimeStamp(LivingEntity entity) {
        return Optional.ofNullable(entity.getAttached(EnchantOverhaul.BARRIER_TIMESTAMP)).orElse(0);
    }

    @Override
    public void markScavengerLoot(ItemEntity entity, Entity breaker) {
        entity.setAttached(EnchantOverhaul.SCAVENGER_LOOT, breaker.getName().getString());
    }
}
