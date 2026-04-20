package brokenkeyboard.brokensenchantoverhaul.platform;

import brokenkeyboard.brokensenchantoverhaul.EnchantOverhaul;
import brokenkeyboard.brokensenchantoverhaul.platform.services.IPlatformHelper;
import com.mojang.serialization.MapCodec;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.advancements.critereon.ItemSubPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentLocationBasedEffect;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.Optional;
import java.util.function.UnaryOperator;

import static brokenkeyboard.brokensenchantoverhaul.EnchantOverhaul.DATA_COMPONENTS;
import static brokenkeyboard.brokensenchantoverhaul.EnchantOverhaul.ENCHANTMENT_COMPONENTS;

public class NeoForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }

    @Override
    public <T> Registry<T> createRegistry(ResourceKey<Registry<T>> key) {
        return new RegistryBuilder<>(key).create();
    }

    @Override
    public <T> DataComponentType<T> createDataComponent(String name, UnaryOperator<DataComponentType.Builder<T>> operator) {
        DataComponentType<T> component = operator.apply(DataComponentType.builder()).build();
        DATA_COMPONENTS.register(name, () -> component);
        return component;
    }

    @Override
    public <T> DataComponentType<T> createEnchantmentComponent(String name, UnaryOperator<DataComponentType.Builder<T>> operator) {
        DataComponentType<T> component = operator.apply(DataComponentType.builder()).build();
        ENCHANTMENT_COMPONENTS.register(name, () -> component);
        return component;
    }

    @Override
    public <T extends EnchantmentLocationBasedEffect> void createLocationComponent(String name, MapCodec<T> codec) {
        EnchantOverhaul.LOCATION_EFFECT_COMPONENTS.register(name, () -> codec);
    }

    @Override
    public <T extends EnchantmentEntityEffect> void createEntityEffectComponent(String name, MapCodec<T> codec) {
        EnchantOverhaul.ENTITY_EFFECT_COMPONENTS.register(name, () -> codec);
    }

    @Override
    public <T extends EntitySubPredicate> void createEntitySubPredicate(String name, MapCodec<T> codec) {
        EnchantOverhaul.ENTITY_SUPREDICATES.register(name, () -> codec);
    }

    @Override
    public <T extends ItemSubPredicate> ItemSubPredicate.Type<T> createItemSubPredicate(String name, MapCodec<T> codec) {
        ItemSubPredicate.Type<T> type = new ItemSubPredicate.Type<>(codec.codec());
        EnchantOverhaul.ITEM_SUBPREDICATES.register(name, () -> type);
        return type;
    }

    @Override
    public Holder<MobEffect> createEffectHolder(String name, MobEffect effect) {
        return EnchantOverhaul.EFFECTS.register(name, () -> effect);
    }

    @Override
    public Holder<Attribute> createAttribute(String name, Attribute attribute) {
        return EnchantOverhaul.ATTRIBUTES.register(name, () -> attribute);
    }

    @Override
    public void setWallSlideTicks(LivingEntity entity, int amount) {
        entity.setData(EnchantOverhaul.WALL_SLIDE_TICKS, amount);
    }

    @Override
    public int getWallSlideTicks(LivingEntity entity) {
        return Optional.of(entity.getData(EnchantOverhaul.WALL_SLIDE_TICKS)).orElse(0);
    }

    @Override
    public void setPowerShotTicks(AbstractArrow arrow, int ticks) {
        arrow.setData(EnchantOverhaul.POWER_SHOT_TICKS, ticks);
    }

    @Override
    public int getPowerShotTicks(AbstractArrow arrow) {
        return Optional.of(arrow.getData(EnchantOverhaul.POWER_SHOT_TICKS)).orElse(0);
    }

    @Override
    public void setBurnStacks(Entity entity, int stacks) {
        entity.setData(EnchantOverhaul.BURN_STACKS, stacks);
    }

    @Override
    public int getBurnStacks(Entity entity) {
        return Optional.of(entity.getData(EnchantOverhaul.BURN_STACKS)).orElse(0);
    }

    @Override
    public void markScavengerLoot(ItemEntity entity, Entity breaker) {
        entity.setData(EnchantOverhaul.SCAVENGER_LOOT, breaker.getStringUUID());
    }
}