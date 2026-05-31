package brokenkeyboard.brokensenchantoverhaul;

import brokenkeyboard.brokensenchantoverhaul.component.DamageTypeResist;
import brokenkeyboard.brokensenchantoverhaul.effect.EnchantmentMobEffect;
import brokenkeyboard.brokensenchantoverhaul.enchantment.*;
import brokenkeyboard.brokensenchantoverhaul.platform.Services;
import brokenkeyboard.brokensenchantoverhaul.predicate.EntityKilledPredicate;
import brokenkeyboard.brokensenchantoverhaul.predicate.HasNegativeEffectPredicate;
import brokenkeyboard.brokensenchantoverhaul.predicate.IsLowHealthPredicate;
import brokenkeyboard.brokensenchantoverhaul.predicate.PickupArrowPredicate;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Unit;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Map;

public class ModRegistry {

    public static final ResourceKey<Registry<MapCodec<? extends ConditionalProtectionEffect>>> CONDITIONAL_PROTECTION_EFFECT = ResourceKey.createRegistryKey(location("enchantment_conditional_protection"));
    public static final ResourceKey<Registry<MapCodec<? extends ConditionalAttributeEffect>>> CONDITIONAL_ATTRIBUTE_EFFECT = ResourceKey.createRegistryKey(location("enchantment_conditional_attribute"));
    public static final ResourceKey<Registry<MapCodec<? extends HookPullEffect>>> HOOK_PULL_EFFECT = ResourceKey.createRegistryKey(location("enchantment_hook_pull_effect"));

    public static final ResourceKey<DamageType> ARROW_MULTISHOT = ResourceKey.create(Registries.DAMAGE_TYPE, location("arrow_multishot"));

    public static final Registry<MapCodec<? extends ConditionalProtectionEffect>> PROTECTION_REGISTRY = Services.PLATFORM.createRegistry(CONDITIONAL_PROTECTION_EFFECT);
    public static final Registry<MapCodec<? extends ConditionalAttributeEffect>> ATTRIBUTE_REGISTRY = Services.PLATFORM.createRegistry(CONDITIONAL_ATTRIBUTE_EFFECT);
    public static final Registry<MapCodec<? extends HookPullEffect>> HOOK_PULL_REGISTRY = Services.PLATFORM.createRegistry(HOOK_PULL_EFFECT);

    public static final TagKey<Block> DAMAGE_TEMPERED = TagKey.create(Registries.BLOCK, location("damage_tempered"));
    public static final TagKey<Block> PROSPECTING_DETECTS = TagKey.create(Registries.BLOCK, location("prospecting_detects"));
    public static final TagKey<Block> SPELUNKER_DETECTS = TagKey.create(Registries.BLOCK, location("spelunker_detects"));
    public static final TagKey<Item> WEAPON_DURABILITY_BONUS = TagKey.create(Registries.ITEM, location("weapon_durability_bonus"));
    public static final TagKey<Item> TOOL_EFFICIENCY_BONUS = TagKey.create(Registries.ITEM, location("tool_efficiency_bonus"));
    public static final TagKey<Item> EXCAVATE_ENCHANTABLE = TagKey.create(Registries.ITEM, location("enchantable/excavate"));
    public static final TagKey<Enchantment> REMOVED_ENCHANTMENTS = TagKey.create(Registries.ENCHANTMENT, location("removed_enchantments"));

    public static final TagKey<Enchantment> LEGGINGS_EXCLUSIVE = TagKey.create(Registries.ENCHANTMENT, location("exclusive_set/leggings"));
    public static final TagKey<Enchantment> FISHING_EXCLUSIVE = TagKey.create(Registries.ENCHANTMENT, location("exclusive_set/fishing"));

    public static final Holder<Attribute> HEALING_EFFICIENCY = Services.PLATFORM.createAttribute("generic.healing_efficiency",
            new RangedAttribute("attribute.name.generic.healing_efficiency", 1, 0, 1024)
                    .setSentiment(Attribute.Sentiment.POSITIVE).setSyncable(true));

    public static final Holder<Attribute> LOOTING_LEVEL = Services.PLATFORM.createAttribute("generic.looting_level",
            new RangedAttribute("attribute.name.generic.looting_level", 0, 0, 1024)
                    .setSentiment(Attribute.Sentiment.POSITIVE).setSyncable(true));

    public static final Holder<Attribute> POSITIVE_EFFECT_DURATION = Services.PLATFORM.createAttribute("generic.positive_effect_duration",
            new RangedAttribute("attribute.name.generic.positive_effect_duration", 1, 0, 1024)
                    .setSentiment(Attribute.Sentiment.POSITIVE).setSyncable(true));

    public static final Holder<Attribute> NEGATIVE_EFFECT_DURATION = Services.PLATFORM.createAttribute("generic.negative_effect_duration",
            new RangedAttribute("attribute.name.generic.negative_effect_duration", 1, 0, 1024)
                    .setSentiment(Attribute.Sentiment.NEGATIVE).setSyncable(true));

    public static final Holder<Attribute> MONSTER_AWARENESS_RANGE = Services.PLATFORM.createAttribute("generic.monster_awareness_range",
            new RangedAttribute("attribute.name.generic.monster_awareness_range", 1, 0, 1024)
                    .setSentiment(Attribute.Sentiment.NEGATIVE).setSyncable(true));

    public static final Holder<Attribute> BARRIER_STRENGTH = Services.PLATFORM.createAttribute("generic.barrier_strength",
            new RangedAttribute("attribute.name.generic.barrier_strength", 0, 0, 2048)
                    .setSentiment(Attribute.Sentiment.POSITIVE).setSyncable(true));

    public static final ResourceKey<Enchantment> FILTERED = ResourceKey.create(Registries.ENCHANTMENT, location("filtered"));
    public static final ResourceKey<Enchantment> INSIGHT = ResourceKey.create(Registries.ENCHANTMENT, location("insight"));
    public static final ResourceKey<Enchantment> DEXTERITY = ResourceKey.create(Registries.ENCHANTMENT, location("dexterity"));

    public static final ResourceKey<Enchantment> VITALITY = ResourceKey.create(Registries.ENCHANTMENT, location("vitality"));
    public static final ResourceKey<Enchantment> BARRIER = ResourceKey.create(Registries.ENCHANTMENT, location("barrier"));
    public static final ResourceKey<Enchantment> ADAPTIVE = ResourceKey.create(Registries.ENCHANTMENT, location("adaptive"));

    public static final ResourceKey<Enchantment> RUSH = ResourceKey.create(Registries.ENCHANTMENT, location("rush"));
    public static final ResourceKey<Enchantment> SCAVENGER = ResourceKey.create(Registries.ENCHANTMENT, location("scavenger"));
    public static final ResourceKey<Enchantment> STABILIZE = ResourceKey.create(Registries.ENCHANTMENT, location("stabilize"));

    public static final ResourceKey<Enchantment> AGILITY = ResourceKey.create(Registries.ENCHANTMENT, location("agility"));
    public static final ResourceKey<Enchantment> FRICTION = ResourceKey.create(Registries.ENCHANTMENT, location("friction"));

    public static final ResourceKey<Enchantment> TEMPERED = ResourceKey.create(Registries.ENCHANTMENT, location("tempered"));
    public static final ResourceKey<Enchantment> EXCAVATE = ResourceKey.create(Registries.ENCHANTMENT, location("excavate"));
    public static final ResourceKey<Enchantment> HARVEST = ResourceKey.create(Registries.ENCHANTMENT, location("harvest"));
    public static final ResourceKey<Enchantment> PROSPECTING = ResourceKey.create(Registries.ENCHANTMENT, location("prospecting"));
    public static final ResourceKey<Enchantment> SPELUNKER = ResourceKey.create(Registries.ENCHANTMENT, location("spelunker"));

    public static final ResourceKey<Enchantment> POWER_SHOT = ResourceKey.create(Registries.ENCHANTMENT, location("power_shot"));
    public static final ResourceKey<Enchantment> BARRAGE = ResourceKey.create(Registries.ENCHANTMENT, location("barrage"));
    public static final ResourceKey<Enchantment> VOLLEY = ResourceKey.create(Registries.ENCHANTMENT, location("volley"));

    public static final ResourceKey<Enchantment> GRAPPLE = ResourceKey.create(Registries.ENCHANTMENT, location("grapple"));
    public static final ResourceKey<Enchantment> DEEP_FRYER = ResourceKey.create(Registries.ENCHANTMENT, location("deep_fryer"));

    public static final ResourceKey<Enchantment> BLACKSMITH = ResourceKey.create(Registries.ENCHANTMENT, location("blacksmith"));

    public static final Holder<MobEffect> BREACH_EFFECT = Services.PLATFORM.createEffectHolder("breach", new EnchantmentMobEffect(Enchantments.BREACH, 8028612));
    public static final Holder<MobEffect> SCAVENGER_EFFECT = Services.PLATFORM.createEffectHolder("scavenger", new EnchantmentMobEffect(SCAVENGER, 5525848));

    public static final Map<ResourceKey<Enchantment>, Integer> MAX_LEVELS = new Object2IntOpenHashMap<>();

    public static boolean updateMaxLevels = true;

    public static final DataComponentType<DamageTypeResist> DAMAGETYPE_RESIST = Services.PLATFORM
            .createDataComponent("damagetype_resist", builder ->
                    builder.persistent(DamageTypeResist.CODEC).networkSynchronized(DamageTypeResist.STREAM_CODEC));

    public static final DataComponentType<Unit> EXPLOSION_DEFUSE = Services.PLATFORM
            .createEnchantmentComponent("stabilize_radius", builder ->
                    builder.persistent(Unit.CODEC));

    public static final DataComponentType<Unit> CHANGE_WATER_EFFECTS = Services.PLATFORM
            .createEnchantmentComponent("change_water_effects", builder ->
                    builder.persistent(Unit.CODEC));

    public static final DataComponentType<Unit> AREA_MINING = Services.PLATFORM
            .createEnchantmentComponent("area_mining", builder ->
                    builder.persistent(Unit.CODEC));

    public static final DataComponentType<Unit> GLINT_OVERRIDE = Services.PLATFORM
            .createEnchantmentComponent("glint_override", builder -> builder.persistent(Unit.CODEC));

    public static final DataComponentType<List<ConditionalEffect<EnchantmentEntityEffect>>> LOOT_PICKUP_BONUS = Services.PLATFORM
            .createEnchantmentComponent("loot_pickup_effect", builder ->
                    builder.persistent(ConditionalEffect.codec(EnchantmentEntityEffect.CODEC, LootContextParamSets.ENCHANTED_ENTITY).listOf()));

    public static final DataComponentType<List<ConditionalEffect<ConditionalProtectionEffect>>> CONDITIONAL_PROTECTION = Services.PLATFORM
            .createEnchantmentComponent("conditional_protection", builder ->
                    builder.persistent(ConditionalEffect.codec(ConditionalProtectionEffect.CODEC, LootContextParamSets.ENCHANTED_DAMAGE).listOf()));

    public static final DataComponentType<List<ConditionalEffect<ConditionalAttributeEffect>>> CONDITIONAL_ATTRIBUTE = Services.PLATFORM
            .createEnchantmentComponent("conditional_attribute", builder ->
                    builder.persistent(ConditionalEffect.codec(ConditionalAttributeEffect.CODEC, LootContextParamSets.ENCHANTED_ENTITY).listOf()));

    public static final DataComponentType<List<ConditionalEffect<HookPullEffect>>> HOOK_PULL = Services.PLATFORM
            .createEnchantmentComponent("hook_pull_effect", builder ->
                    builder.persistent(ConditionalEffect.codec(HookPullEffect.CODEC, LootContextParamSets.ENCHANTED_ENTITY).listOf()));

    public static final DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> POWER_SHOT_DAMAGE = Services.PLATFORM
            .createEnchantmentComponent("power_shot_damage", builder ->
                    builder.persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_DAMAGE).listOf()));

    public static final DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> POWER_SHOT_KNOCKBACK = Services.PLATFORM
            .createEnchantmentComponent("power_shot_knockback", builder ->
                    builder.persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_DAMAGE).listOf()));

    public static final DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> SWEEPING_DAMAGE_BONUS = Services.PLATFORM
            .createEnchantmentComponent("sweeping_damage_multiplier", builder ->
                    builder.persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_DAMAGE).listOf()));

    static {
        Services.PLATFORM.createLocationComponent("detect_blocks", DetectBlocksEffect.CODEC);
        Services.PLATFORM.createEntityEffectComponent("return_arrows", ReturnArrowEffect.CODEC);
        Services.PLATFORM.createEntityEffectComponent("burn_stack", BurnStackEffect.CODEC);
        Services.PLATFORM.createEntityEffectComponent("volley", VolleyEffect.CODEC);
        Services.PLATFORM.createEntityEffectComponent("scavenger_magnet", ScavengerMagnetEffect.CODEC);
        Services.PLATFORM.createEntityEffectComponent("wall_slide", WallSlideEffect.CODEC);
        Services.PLATFORM.createEntityEffectComponent("repair_equipped_item", RepairEquippedItem.CODEC);

        Services.PLATFORM.createEntitySubPredicate("arrow_pickup", PickupArrowPredicate.CODEC);
        Services.PLATFORM.createEntitySubPredicate("has_negative", HasNegativeEffectPredicate.CODEC);
        Services.PLATFORM.createEntitySubPredicate("is_low_health", IsLowHealthPredicate.CODEC);
        Services.PLATFORM.createEntitySubPredicate("entity_killed", EntityKilledPredicate.CODEC);
    }

    public static ResourceLocation location(String name) {
        return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name);
    }

    public static void bootstrap() {}
}
