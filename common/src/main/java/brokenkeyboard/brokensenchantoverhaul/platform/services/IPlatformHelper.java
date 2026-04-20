package brokenkeyboard.brokensenchantoverhaul.platform.services;

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

import java.util.function.UnaryOperator;

public interface IPlatformHelper {

    /**
     * Gets the name of the current platform
     *
     * @return The name of the current platform.
     */
    String getPlatformName();

    /**
     * Checks if a mod with the given id is loaded.
     *
     * @param modId The mod to check if it is loaded.
     * @return True if the mod is loaded, false otherwise.
     */
    boolean isModLoaded(String modId);

    /**
     * Check if the game is currently in a development environment.
     *
     * @return True if in a development environment, false otherwise.
     */
    boolean isDevelopmentEnvironment();

    /**
     * Gets the name of the environment type as a string.
     *
     * @return The name of the environment type.
     */
    default String getEnvironmentName() {
        return isDevelopmentEnvironment() ? "development" : "production";
    }

    <T> Registry<T> createRegistry(ResourceKey<Registry<T>> key);
    <T> DataComponentType<T> createDataComponent(String name, UnaryOperator<DataComponentType.Builder<T>> operator);
    <T> DataComponentType<T> createEnchantmentComponent(String name, UnaryOperator<DataComponentType.Builder<T>> operator);
    <T extends EnchantmentLocationBasedEffect> void createLocationComponent(String name, MapCodec<T> codec);
    <T extends EnchantmentEntityEffect> void createEntityEffectComponent(String name, MapCodec<T> codec);
    <T extends EntitySubPredicate> void createEntitySubPredicate(String name, MapCodec<T> codec);
    <T extends ItemSubPredicate> ItemSubPredicate.Type<T> createItemSubPredicate(String name, MapCodec<T> codec);
    Holder<MobEffect> createEffectHolder(String name, MobEffect effect);
    Holder<Attribute> createAttribute(String name, Attribute attribute);
    void setWallSlideTicks(LivingEntity entity, int amount);
    int getWallSlideTicks(LivingEntity entity);
    void setPowerShotTicks(AbstractArrow arrow, int ticks);
    int getPowerShotTicks(AbstractArrow arrow);
    void setBurnStacks(Entity entity, int stacks);
    int getBurnStacks(Entity entity);
    void markScavengerLoot(ItemEntity entity, Entity breaker);
}