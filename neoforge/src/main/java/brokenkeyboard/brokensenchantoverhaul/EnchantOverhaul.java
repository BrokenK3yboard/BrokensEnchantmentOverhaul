package brokenkeyboard.brokensenchantoverhaul;

import brokenkeyboard.brokensenchantoverhaul.enchantment.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.advancements.critereon.ItemSubPredicate;
import net.minecraft.client.gui.Gui;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentLocationBasedEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

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
    public static final Supplier<AttachmentType<Integer>> BARRIER_AMOUNT = ATTACHMENT_TYPES.register("barrier_amount", () -> AttachmentType.builder(() -> 0).serialize(ExtraCodecs.NON_NEGATIVE_INT).build());
    public static final Supplier<AttachmentType<Integer>> BARRIER_TIMESTAMP = ATTACHMENT_TYPES.register("barrier_timestamp", () -> AttachmentType.builder(() -> 0).build());

    public EnchantOverhaul(ModContainer container, IEventBus bus) {
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
        bus.addListener((RegisterEvent event) -> event.register(CONDITIONAL_ATTRIBUTE_EFFECT, registry -> registry.register(location("insight_looting"), InsightLootingEffect.CODEC)));
        bus.addListener((RegisterEvent event) -> event.register(CONDITIONAL_ATTRIBUTE_EFFECT, registry -> registry.register(location("adaptive_fire_resistance"), AdaptiveFREffect.CODEC)));
        bus.addListener((RegisterEvent event) -> event.register(CONDITIONAL_ATTRIBUTE_EFFECT, registry -> registry.register(location("adaptive_blast_resistance"), AdaptiveBREffect.CODEC)));
        bus.addListener((RegisterEvent event) -> event.register(HOOK_PULL_EFFECT, registry -> registry.register(location("grapple"), GrappleEffect.CODEC)));
        bus.addListener((RegisterEvent event) -> event.register(HOOK_PULL_EFFECT, registry -> registry.register(location("hook_burn"), HookBurnEffect.CODEC)));

        // Use NeoForge to bootstrap the Common mod.
        Constants.LOG.info("Hello NeoForge world!");
        CommonClass.init();
    }

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
}
