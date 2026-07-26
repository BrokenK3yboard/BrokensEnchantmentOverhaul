package brokenkeyboard.brokensenchantoverhaul.datagen;

import brokenkeyboard.brokensenchantoverhaul.ModRegistry;
import brokenkeyboard.brokensenchantoverhaul.enchantment.*;
import brokenkeyboard.brokensenchantoverhaul.predicate.EntityKilledPredicate;
import brokenkeyboard.brokensenchantoverhaul.predicate.HasNegativeEffectPredicate;
import brokenkeyboard.brokensenchantoverhaul.predicate.IsLowHealthPredicate;
import brokenkeyboard.brokensenchantoverhaul.predicate.PickupArrowPredicate;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderSet;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.*;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.item.enchantment.effects.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.*;
import net.minecraft.world.level.storage.loot.providers.number.EnchantmentLevelProvider;

public class ModEnchantments {

    @SuppressWarnings("deprecation")
    public static void bootstrap(BootstrapContext<Enchantment> context) {
        HolderSet<Enchantment> armor_exclusive = context.lookup(Registries.ENCHANTMENT).getOrThrow(EnchantmentTags.ARMOR_EXCLUSIVE);
        HolderSet<Enchantment> mining_exclusive = context.lookup(Registries.ENCHANTMENT).getOrThrow(EnchantmentTags.MINING_EXCLUSIVE);
        HolderSet.Named<Enchantment> damage_exclusive = context.lookup(Registries.ENCHANTMENT).getOrThrow(EnchantmentTags.DAMAGE_EXCLUSIVE);
        HolderSet.Named<Enchantment> bow_exclusive = context.lookup(Registries.ENCHANTMENT).getOrThrow(EnchantmentTags.BOW_EXCLUSIVE);
        HolderSet.Named<Enchantment> crossbow_exclusive = context.lookup(Registries.ENCHANTMENT).getOrThrow(EnchantmentTags.CROSSBOW_EXCLUSIVE);
        HolderSet.Named<Enchantment> fishing_exclusive = context.lookup(Registries.ENCHANTMENT).getOrThrow(ModRegistry.FISHING_EXCLUSIVE);
        HolderSet.Named<Enchantment> trident_exclusive = context.lookup(Registries.ENCHANTMENT).getOrThrow(EnchantmentTags.RIPTIDE_EXCLUSIVE);
        HolderSet.Named<Enchantment> leggings_exclusive = context.lookup(Registries.ENCHANTMENT).getOrThrow(ModRegistry.LEGGINGS_EXCLUSIVE);
        HolderSet.Named<Enchantment> boots_exclusive = context.lookup(Registries.ENCHANTMENT).getOrThrow(EnchantmentTags.BOOTS_EXCLUSIVE);
        HolderSet.Named<Item> helmet = context.lookup(Registries.ITEM).getOrThrow(ItemTags.HEAD_ARMOR_ENCHANTABLE);
        HolderSet.Named<Item> chestplate = context.lookup(Registries.ITEM).getOrThrow(ItemTags.CHEST_ARMOR_ENCHANTABLE);
        HolderSet.Named<Item> leggings = context.lookup(Registries.ITEM).getOrThrow(ItemTags.LEG_ARMOR_ENCHANTABLE);
        HolderSet.Named<Item> boots = context.lookup(Registries.ITEM).getOrThrow(ItemTags.FOOT_ARMOR_ENCHANTABLE);
        HolderSet.Named<Item> tools = context.lookup(Registries.ITEM).getOrThrow(ItemTags.MINING_LOOT_ENCHANTABLE);
        HolderSet.Named<Item> sword = context.lookup(Registries.ITEM).getOrThrow(ItemTags.SWORD_ENCHANTABLE);
        HolderSet.Named<Item> hoe = context.lookup(Registries.ITEM).getOrThrow(ItemTags.HOES);
        HolderSet.Named<Item> pickaxe = context.lookup(Registries.ITEM).getOrThrow(ItemTags.PICKAXES);
        HolderSet.Named<Item> shovel = context.lookup(Registries.ITEM).getOrThrow(ItemTags.SHOVELS);
        HolderSet.Named<Item> bow = context.lookup(Registries.ITEM).getOrThrow(ItemTags.BOW_ENCHANTABLE);
        HolderSet.Named<Item> crossbow = context.lookup(Registries.ITEM).getOrThrow(ItemTags.CROSSBOW_ENCHANTABLE);
        HolderSet.Named<Item> fishing = context.lookup(Registries.ITEM).getOrThrow(ItemTags.FISHING_ENCHANTABLE);
        HolderSet.Named<Item> mace = context.lookup(Registries.ITEM).getOrThrow(ItemTags.MACE_ENCHANTABLE);
        HolderSet.Named<Item> trident = context.lookup(Registries.ITEM).getOrThrow(ItemTags.TRIDENT_ENCHANTABLE);
        HolderSet.Named<Item> sharp_weapon = context.lookup(Registries.ITEM).getOrThrow(ItemTags.SHARP_WEAPON_ENCHANTABLE);
        HolderSet.Named<Item> excavate = context.lookup(Registries.ITEM).getOrThrow(ModRegistry.EXCAVATE_ENCHANTABLE);

        register(context, ModRegistry.FILTERED, Enchantment.enchantment(
                Enchantment.definition(helmet, 2, 3,
                        Enchantment.dynamicCost(10, 10),
                        Enchantment.dynamicCost(40, 10),
                        4,
                        EquipmentSlotGroup.HEAD))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES,
                        new EnchantmentAttributeEffect(
                                ModRegistry.location("enchantment.filtered_resistance"),
                                ModRegistry.NEGATIVE_EFFECT_DURATION,
                                LevelBasedValue.perLevel(-0.2F, -0.1F),
                                AttributeModifier.Operation.ADD_MULTIPLIED_BASE))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES,
                        new EnchantmentAttributeEffect(
                                ModRegistry.location("enchantment.filtered_bonus"),
                                ModRegistry.POSITIVE_EFFECT_DURATION,
                                LevelBasedValue.perLevel(0.2F, 0.1F),
                                AttributeModifier.Operation.ADD_MULTIPLIED_BASE)));

        register(context, ModRegistry.INSIGHT, Enchantment.enchantment(
                Enchantment.definition(helmet, 2, 1,
                        Enchantment.constantCost(1),
                        Enchantment.constantCost(41),
                        4,
                        EquipmentSlotGroup.HEAD))
                .withEffect(EnchantmentEffectComponents.EQUIPMENT_DROPS,
                        EnchantmentTarget.ATTACKER,
                        EnchantmentTarget.VICTIM,
                        new AddValue(LevelBasedValue.constant(0.01F)),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.ATTACKER,
                        EntityPredicate.Builder.entity().entityType(EntityTypePredicate.of(EntityType.PLAYER))))
                .withEffect(ModRegistry.CONDITIONAL_ATTRIBUTE, new InsightLootingEffect()));

        register(context, ModRegistry.DEXTERITY, Enchantment.enchantment(
                Enchantment.definition(helmet, 2, 1,
                        Enchantment.constantCost(1),
                        Enchantment.constantCost(41),
                        4,
                        EquipmentSlotGroup.HEAD))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES,
                        new EnchantmentAttributeEffect(
                                ModRegistry.location("enchantment.dexterity_mining"),
                                Attributes.SUBMERGED_MINING_SPEED,
                                LevelBasedValue.constant(4.0F),
                                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                .withEffect(ModRegistry.CONDITIONAL_ATTRIBUTE,
                        new FixedAttributeEffect(ModRegistry.location("enchantment.dexterity_range"),
                                Attributes.BLOCK_INTERACTION_RANGE,
                                LevelBasedValue.constant(1F),
                                AttributeModifier.Operation.ADD_VALUE)));

        register(context, ModRegistry.VITALITY, Enchantment.enchantment(
                Enchantment.definition(chestplate, 10, 4,
                        Enchantment.dynamicCost(1, 11),
                        Enchantment.dynamicCost(12, 11),
                        1,
                        EquipmentSlotGroup.ARMOR))
                .exclusiveWith(armor_exclusive)
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES,
                        new EnchantmentAttributeEffect(
                                ModRegistry.location("enchantment.vitality"),
                                Attributes.MAX_HEALTH,
                                LevelBasedValue.perLevel(8, 4),
                                AttributeModifier.Operation.ADD_VALUE))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES,
                        new EnchantmentAttributeEffect(
                                ModRegistry.location("enchantment.vitality_healing"),
                                ModRegistry.HEALING_EFFICIENCY,
                                LevelBasedValue.perLevel(0.2F, 0.1F),
                                AttributeModifier.Operation.ADD_MULTIPLIED_BASE)));

        register(context, ModRegistry.ADAPTIVE, Enchantment.enchantment(
                Enchantment.definition(chestplate, 5, 3,
                        Enchantment.dynamicCost(5, 6),
                        Enchantment.dynamicCost(11, 6),
                        2,
                        EquipmentSlotGroup.ARMOR))
                .withEffect(ModRegistry.CONDITIONAL_PROTECTION,
                        new AdaptiveEffect(
                                LevelBasedValue.perLevel(1F, 0.5F),
                                LevelBasedValue.constant(10F)),
                        DamageSourceCondition.hasDamageSource(
                                DamageSourcePredicate.Builder.damageType().tag(TagPredicate.isNot(DamageTypeTags.BYPASSES_INVULNERABILITY))))
                .withEffect(ModRegistry.CONDITIONAL_ATTRIBUTE,
                        new AdaptiveFREffect(ModRegistry.location("enchantment.adaptive_fire_resistance"),
                                AttributeModifier.Operation.ADD_VALUE))
                .withEffect(ModRegistry.CONDITIONAL_ATTRIBUTE,
                        new AdaptiveBREffect(ModRegistry.location("enchantment.adaptive_blast_resistance"),
                                AttributeModifier.Operation.ADD_VALUE)));

        register(context, ModRegistry.BARRIER, Enchantment.enchantment(
                Enchantment.definition(chestplate, 2, 2,
                        Enchantment.dynamicCost(10, 10),
                        Enchantment.dynamicCost(25, 10),
                        2,
                        EquipmentSlotGroup.ARMOR))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES,
                        new EnchantmentAttributeEffect(
                                ModRegistry.location("enchantment.barrier"),
                                ModRegistry.BARRIER_STRENGTH,
                                LevelBasedValue.perLevel(2),
                                AttributeModifier.Operation.ADD_VALUE)));

        register(context, Enchantments.THORNS, Enchantment.enchantment(
                Enchantment.definition(chestplate, 1, 3,
                        Enchantment.dynamicCost(10, 20),
                        Enchantment.dynamicCost(60, 20),
                        8,
                        EquipmentSlotGroup.ARMOR))
                .withEffect(ModRegistry.CONDITIONAL_PROTECTION,
                        new DeflectDamageEffect(LevelBasedValue.constant(8F)),
                        AllOfCondition.allOf(
                                LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.DIRECT_ATTACKER,
                                        EntityPredicate.Builder.entity().subPredicate(IsLowHealthPredicate.isLowHealth(0.5F))),
                                LootItemRandomChanceCondition.randomChance(
                                        EnchantmentLevelProvider.forEnchantmentLevel(LevelBasedValue.perLevel(0.3F, 0.1F)))))
                .withEffect(ModRegistry.CONDITIONAL_PROTECTION,
                        new DeflectDamageEffect(LevelBasedValue.constant(5F)),
                        AllOfCondition.allOf(
                                LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.DIRECT_ATTACKER,
                                        EntityPredicate.Builder.entity().subPredicate(IsLowHealthPredicate.isLowHealth(0.5F))).invert(),
                                LootItemRandomChanceCondition.randomChance(
                                        EnchantmentLevelProvider.forEnchantmentLevel(LevelBasedValue.perLevel(0.2F, 0.1F))))));

        register(context, ModRegistry.RUSH, Enchantment.enchantment(
                Enchantment.definition(leggings, 5, 3,
                        Enchantment.dynamicCost(5, 8),
                        Enchantment.dynamicCost(65, 9),
                        2,
                        EquipmentSlotGroup.ARMOR))
                .exclusiveWith(leggings_exclusive)
                .withEffect(EnchantmentEffectComponents.DAMAGE_PROTECTION, new AddValue(LevelBasedValue.constant(2F)),
                        AllOfCondition.allOf(
                                DamageSourceCondition.hasDamageSource(
                                        DamageSourcePredicate.Builder.damageType().tag(TagPredicate.isNot(DamageTypeTags.BYPASSES_INVULNERABILITY))),
                                LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                        EntityPredicate.Builder.entity().subPredicate(new IsLowHealthPredicate(0.5F)))))
                .withEffect(EnchantmentEffectComponents.DAMAGE_PROTECTION, new AddValue(LevelBasedValue.perLevel(2F, 1F)),
                        AllOfCondition.allOf(
                                DamageSourceCondition.hasDamageSource(
                                        DamageSourcePredicate.Builder.damageType().tag(TagPredicate.isNot(DamageTypeTags.BYPASSES_INVULNERABILITY))),
                                LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                        EntityPredicate.Builder.entity().subPredicate(new IsLowHealthPredicate(0.5F))),
                                LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                        EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags().setSprinting(true))))));

        register(context, ModRegistry.SCAVENGER, Enchantment.enchantment(
                Enchantment.definition(leggings, 5, 2,
                        Enchantment.dynamicCost(10, 8),
                        Enchantment.dynamicCost(18, 8),
                        4,
                        EquipmentSlotGroup.LEGS))
                .exclusiveWith(leggings_exclusive)
                .withEffect(EnchantmentEffectComponents.TICK,
                        new ScavengerMagnetEffect(
                                LevelBasedValue.perLevel(3F, 2F),
                                LevelBasedValue.perLevel(0.15F, 0.1F)))
                .withEffect(ModRegistry.CONDITIONAL_ATTRIBUTE,
                        new FixedAttributeEffect(
                                ModRegistry.location("enchantment.scavenger"),
                                Attributes.ARMOR_TOUGHNESS,
                                LevelBasedValue.perLevel(2F, 2F),
                                AttributeModifier.Operation.ADD_VALUE),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().effects(MobEffectsPredicate.Builder.effects().and(ModRegistry.SCAVENGER_EFFECT))))
                .withEffect(ModRegistry.LOOT_PICKUP_BONUS,
                        new ApplyMobEffect(HolderSet.direct(ModRegistry.SCAVENGER_EFFECT),
                                LevelBasedValue.constant(30F), LevelBasedValue.constant(30F),
                                LevelBasedValue.constant(0F), LevelBasedValue.constant(0F))));

        register(context, ModRegistry.STABILIZE, Enchantment.enchantment(
                        Enchantment.definition(leggings, 5, 3,
                                Enchantment.dynamicCost(5, 8),
                                Enchantment.dynamicCost(13, 8),
                                2,
                                EquipmentSlotGroup.LEGS))
                .exclusiveWith(leggings_exclusive)
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES,
                        new EnchantmentAttributeEffect(
                                ModRegistry.location("enchantment.inertia_knockback"),
                                Attributes.KNOCKBACK_RESISTANCE,
                                LevelBasedValue.perLevel(0.1F, 0.5F),
                                AttributeModifier.Operation.ADD_VALUE))
                .withEffect(ModRegistry.EXPLOSION_DEFUSE));

        AnyOfCondition.Builder agilityCondition = AnyOfCondition.anyOf(
                LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                        EntityPredicate.Builder.entity().effects(MobEffectsPredicate.Builder.effects().and(MobEffects.MOVEMENT_SPEED))),
                LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                        EntityPredicate.Builder.entity().effects(MobEffectsPredicate.Builder.effects().and(MobEffects.JUMP))));

        register(context, ModRegistry.AGILITY, Enchantment.enchantment(
                Enchantment.definition(boots, 2, 3,
                        Enchantment.dynamicCost(10, 10),
                        Enchantment.dynamicCost(25, 10),
                        4,
                        EquipmentSlotGroup.FEET))
                .exclusiveWith(boots_exclusive)
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES,
                        new EnchantmentAttributeEffect(
                                ModRegistry.location("enchantment.agility_speed"),
                                Attributes.MOVEMENT_SPEED,
                                LevelBasedValue.constant(0.05F),
                                AttributeModifier.Operation.ADD_MULTIPLIED_BASE))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES,
                        new EnchantmentAttributeEffect(
                                ModRegistry.location("enchantment.agility_step_height"),
                                Attributes.STEP_HEIGHT,
                                LevelBasedValue.constant(0.5F),
                                AttributeModifier.Operation.ADD_VALUE))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES,
                        new EnchantmentAttributeEffect(
                                ModRegistry.location("enchantment.jump_strength"),
                                Attributes.JUMP_STRENGTH,
                                LevelBasedValue.constant(0.3F),
                                AttributeModifier.Operation.ADD_MULTIPLIED_BASE))
                .withEffect(ModRegistry.CONDITIONAL_ATTRIBUTE,
                        new FixedAttributeEffect(
                                ModRegistry.location("enchantment.agility_speed_active"),
                                Attributes.MOVEMENT_SPEED,
                                LevelBasedValue.perLevel(0.025F),
                                AttributeModifier.Operation.ADD_MULTIPLIED_BASE),
                        agilityCondition)
                .withEffect(ModRegistry.CONDITIONAL_ATTRIBUTE,
                        new FixedAttributeEffect(
                                ModRegistry.location("enchantment.agility_jump_strength_active"),
                                Attributes.JUMP_STRENGTH,
                                LevelBasedValue.perLevel(0.1F),
                                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL),
                        agilityCondition));

        register(context, ModRegistry.FRICTION, Enchantment.enchantment(
                        Enchantment.definition(boots, 1, 3,
                                Enchantment.dynamicCost(10, 10),
                                Enchantment.dynamicCost(25, 10),
                                8,
                                EquipmentSlotGroup.FEET))
                .withEffect(EnchantmentEffectComponents.TICK,
                        new WallSlideEffect(LevelBasedValue.perLevel(60, 40))));

        register(context, ModRegistry.TEMPERED, createSingleLevelEnch(tools, 8, mining_exclusive).exclusiveWith(mining_exclusive));

        register(context, ModRegistry.EXCAVATE, createSingleLevelEnch(excavate, 8, mining_exclusive).exclusiveWith(mining_exclusive)
                .withEffect(ModRegistry.AREA_MINING));

        register(context, ModRegistry.HARVEST, createSingleLevelEnch(hoe, 8, mining_exclusive).exclusiveWith(mining_exclusive));

        register(context, ModRegistry.PROSPECTING, createSingleLevelEnch(pickaxe, 8, mining_exclusive).exclusiveWith(mining_exclusive)
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new DetectBlocksEffect(LevelBasedValue.constant(5), ModRegistry.PROSPECTING_DETECTS))
                .withEffect(ModRegistry.GLINT_OVERRIDE));

        register(context, ModRegistry.SPELUNKER, createSingleLevelEnch(shovel, 8, mining_exclusive).exclusiveWith(mining_exclusive)
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new DetectBlocksEffect(LevelBasedValue.constant(5), ModRegistry.SPELUNKER_DETECTS))
                .withEffect(ModRegistry.GLINT_OVERRIDE));

        register(context, ModRegistry.POWER_SHOT, Enchantment.enchantment(
                Enchantment.definition(bow, 10, 5,
                        Enchantment.dynamicCost(1, 10),
                        Enchantment.dynamicCost(16, 10),
                        1,
                        EquipmentSlotGroup.MAINHAND))
                .exclusiveWith(bow_exclusive)
                .withEffect(ModRegistry.POWER_SHOT_DAMAGE, new MultiplyValue(LevelBasedValue.constant(0.0625F)))
                .withEffect(ModRegistry.POWER_SHOT_KNOCKBACK, new MultiplyValue(LevelBasedValue.constant(0.05F)))
                .withEffect(ModRegistry.GLINT_OVERRIDE));

        register(context, ModRegistry.BARRAGE, createArrowEnch(bow, bow_exclusive)
                .withEffect(EnchantmentEffectComponents.POST_ATTACK,
                        EnchantmentTarget.ATTACKER,
                        EnchantmentTarget.VICTIM,
                        new ReturnArrowEffect(),
                        AllOfCondition.allOf(
                                LootItemEntityPropertyCondition.hasProperties(
                                        LootContext.EntityTarget.DIRECT_ATTACKER,
                                        EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS).build()),
                                LootItemEntityPropertyCondition.hasProperties(
                                        LootContext.EntityTarget.DIRECT_ATTACKER,
                                        EntityPredicate.Builder.entity().subPredicate(PickupArrowPredicate.shouldReturnArrow())))));

        register(context, Enchantments.FLAME, createArrowEnch(bow, bow_exclusive)
                .withEffect(EnchantmentEffectComponents.POST_ATTACK,
                        EnchantmentTarget.ATTACKER,
                        EnchantmentTarget.VICTIM,
                        new BurnStackEffect(
                                LevelBasedValue.constant(1),
                                LevelBasedValue.constant(3)),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags().setOnFire(true))))
                .withEffect(EnchantmentEffectComponents.PROJECTILE_SPAWNED,
                        new Ignite(LevelBasedValue.constant(100.0F))));

        register(context, ModRegistry.VOLLEY, createArrowEnch(crossbow, crossbow_exclusive)
                .withEffect(EnchantmentEffectComponents.POST_ATTACK,
                        EnchantmentTarget.ATTACKER,
                        EnchantmentTarget.VICTIM,
                        new VolleyEffect(),
                        LootItemEntityPropertyCondition.hasProperties(
                                LootContext.EntityTarget.DIRECT_ATTACKER,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS).build())));

        register(context, ModRegistry.GRAPPLE, createSingleLevelEnch(fishing, 4, fishing_exclusive)
                .withEffect(ModRegistry.HOOK_PULL, new GrappleEffect(LevelBasedValue.constant(1))));

        register(context, ModRegistry.DEEP_FRYER, createSingleLevelEnch(fishing, 4, fishing_exclusive)
                .withEffect(ModRegistry.HOOK_PULL, new HookBurnEffect(LevelBasedValue.constant(100))));

        register(context, ModRegistry.BLACKSMITH, createSingleLevelEnch(mace, 8, damage_exclusive)
                .withEffect(EnchantmentEffectComponents.POST_ATTACK,
                        EnchantmentTarget.ATTACKER,
                        EnchantmentTarget.ATTACKER,
                        new RepairEquippedItem(LevelBasedValue.constant(8)),
                        AllOfCondition.allOf(
                            LootItemEntityPropertyCondition.hasProperties(
                                    LootContext.EntityTarget.THIS,
                                    EntityPredicate.Builder.entity().subPredicate(EntityKilledPredicate.entityKilled(true))),
                            LootItemEntityPropertyCondition.hasProperties(
                                    LootContext.EntityTarget.DIRECT_ATTACKER,
                                    EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags().setIsFlying(false))
                                            .moving(MovementPredicate.fallDistance(MinMaxBounds.Doubles.atLeast(1.5F))))))
                .withEffect(EnchantmentEffectComponents.ITEM_DAMAGE, new SetValue(LevelBasedValue.constant(0))));

        register(context, Enchantments.IMPALING, Enchantment.enchantment(
                Enchantment.definition(trident, 2, 5,
                Enchantment.dynamicCost(1, 8),
                Enchantment.dynamicCost(21, 8),
                4,
                EquipmentSlotGroup.MAINHAND))
                .exclusiveWith(trident_exclusive)
                .withEffect(EnchantmentEffectComponents.DAMAGE,
                        new AddValue(LevelBasedValue.perLevel(2F)),
                        AnyOfCondition.anyOf(
                                LootItemEntityPropertyCondition.hasProperties(
                                        LootContext.EntityTarget.THIS,
                                        EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags().setSwimming(true))),
                                AllOfCondition.allOf(
                                        LootItemEntityPropertyCondition.hasProperties(
                                                LootContext.EntityTarget.THIS,
                                                EntityPredicate.Builder.entity().located(LocationPredicate.Builder.location().setCanSeeSky(true))),
                                        AnyOfCondition.anyOf(
                                                WeatherCheck.weather().setThundering(true),
                                                WeatherCheck.weather().setRaining(true))))));

        register(context, Enchantments.CHANNELING, Enchantment.enchantment(
                Enchantment.definition(trident, 1, 1,
                Enchantment.constantCost(25),
                Enchantment.constantCost(50),
                8,
                EquipmentSlotGroup.MAINHAND))
                .exclusiveWith(trident_exclusive)
                .withEffect(EnchantmentEffectComponents.POST_ATTACK,
                        EnchantmentTarget.ATTACKER,
                        EnchantmentTarget.VICTIM,
                        AllOf.entityEffects(
                                new SummonEntityEffect(HolderSet.direct(EntityType.LIGHTNING_BOLT.builtInRegistryHolder()), false),
                                new PlaySoundEffect(SoundEvents.TRIDENT_THUNDER,
                                        ConstantFloat.of(5.0F),
                                        ConstantFloat.of(1.0F))),
                        AllOfCondition.allOf(
                                LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                        EntityPredicate.Builder.entity().located(LocationPredicate.Builder.location().setCanSeeSky(true))),
                                LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.DIRECT_ATTACKER,
                                        EntityPredicate.Builder.entity().of(EntityType.TRIDENT))))
                .withEffect(EnchantmentEffectComponents.HIT_BLOCK, AllOf.entityEffects(
                        new SummonEntityEffect(HolderSet.direct(EntityType.LIGHTNING_BOLT.builtInRegistryHolder()), false),
                        new PlaySoundEffect(
                                SoundEvents.TRIDENT_THUNDER,
                                ConstantFloat.of(5.0F),
                                ConstantFloat.of(1.0F))),
                        AllOfCondition.allOf(
                                LootItemEntityPropertyCondition.hasProperties(
                                        LootContext.EntityTarget.THIS,
                                        EntityPredicate.Builder.entity().of(EntityType.TRIDENT)),
                                LocationCheck.checkLocation(LocationPredicate.Builder.location().setCanSeeSky(true)),
                                LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.LIGHTNING_ROD))));

        register(context, Enchantments.BREACH, Enchantment.enchantment(
                Enchantment.definition(mace, 2, 4,
                        Enchantment.dynamicCost(15, 9),
                        Enchantment.dynamicCost(65, 9),
                        4,
                        EquipmentSlotGroup.MAINHAND))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK,
                        EnchantmentTarget.ATTACKER,
                        EnchantmentTarget.ATTACKER,
                        new ApplyMobEffect(HolderSet.direct(ModRegistry.BREACH_EFFECT),
                                LevelBasedValue.constant(20F), LevelBasedValue.constant(20F),
                                LevelBasedValue.constant(0F), LevelBasedValue.constant(0F)),
                        LootItemEntityPropertyCondition.hasProperties(
                                LootContext.EntityTarget.DIRECT_ATTACKER,
                                EntityPredicate.Builder.entity().flags(
                                        EntityFlagsPredicate.Builder.flags().setIsFlying(false))
                                        .moving(MovementPredicate.fallDistance(MinMaxBounds.Doubles.atLeast(1.5F)))))
                .withEffect(EnchantmentEffectComponents.ARMOR_EFFECTIVENESS,
                        new AddValue(LevelBasedValue.perLevel(-0.1F)),
                        LootItemEntityPropertyCondition.hasProperties(
                                LootContext.EntityTarget.ATTACKER,
                                EntityPredicate.Builder.entity().effects(MobEffectsPredicate.Builder.effects().and(ModRegistry.BREACH_EFFECT))))
                .withEffect(ModRegistry.CONDITIONAL_ATTRIBUTE,
                        new FixedAttributeEffect(
                                ModRegistry.location("enchantment.breach"),
                                Attributes.ATTACK_SPEED,
                                LevelBasedValue.perLevel(0.15F),
                                AttributeModifier.Operation.ADD_VALUE),
                        LootItemEntityPropertyCondition.hasProperties(
                                LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().effects(MobEffectsPredicate.Builder.effects().and(ModRegistry.BREACH_EFFECT))))
                .withEffect(ModRegistry.GLINT_OVERRIDE));

        register(context, Enchantments.SWEEPING_EDGE, Enchantment.enchantment(
                Enchantment.definition(sword, 2, 3,
                        Enchantment.dynamicCost(5, 9),
                        Enchantment.dynamicCost(20, 9),
                        4,
                        EquipmentSlotGroup.MAINHAND))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES,
                        new EnchantmentAttributeEffect(
                                ResourceLocation.withDefaultNamespace("enchantment.sweeping_edge"),
                                ModRegistry.SWEEPING_DAMAGE_BONUS,
                                LevelBasedValue.perLevel(2F, 1F),
                                AttributeModifier.Operation.ADD_VALUE)));

        register(context, Enchantments.SMITE, Enchantment.enchantment(
                Enchantment.definition(sword, 5, 5,
                        Enchantment.dynamicCost(5, 8),
                        Enchantment.dynamicCost(25, 8), 2, EquipmentSlotGroup.MAINHAND))
                .exclusiveWith(damage_exclusive)
                .withEffect(EnchantmentEffectComponents.DAMAGE,
                        new AddValue(LevelBasedValue.perLevel(2F)),
                        AnyOfCondition.anyOf(
                                LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                        EntityPredicate.Builder.entity().entityType(EntityTypePredicate.of(EntityTypeTags.SENSITIVE_TO_SMITE))),
                                LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                        EntityPredicate.Builder.entity().subPredicate(HasNegativeEffectPredicate.hasNegativeEffect())))));

        register(context, Enchantments.FIRE_ASPECT, Enchantment.enchantment(
                Enchantment.definition(sharp_weapon, 2, 2,
                        Enchantment.dynamicCost(10, 20),
                        Enchantment.dynamicCost(60, 20),
                        4,
                        EquipmentSlotGroup.MAINHAND))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK,
                        EnchantmentTarget.ATTACKER,
                        EnchantmentTarget.VICTIM,
                        new BurnStackEffect(
                                LevelBasedValue.constant(1),
                                LevelBasedValue.perLevel(1, 1)),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags().setOnFire(true))))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK,
                        EnchantmentTarget.ATTACKER,
                        EnchantmentTarget.VICTIM,
                        new Ignite(LevelBasedValue.perLevel(4.0F)),
                        DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType().isDirect(true))));

        register(context, Enchantments.SWIFT_SNEAK, Enchantment.enchantment(
                Enchantment.definition(leggings, 1, 3,
                        Enchantment.dynamicCost(25, 25),
                        Enchantment.dynamicCost(75, 25),
                        8,
                        EquipmentSlotGroup.LEGS))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES,
                        new EnchantmentAttributeEffect(
                                ResourceLocation.withDefaultNamespace("enchantment.swift_sneak"),
                                Attributes.SNEAKING_SPEED,
                                LevelBasedValue.perLevel(0.15F),
                                AttributeModifier.Operation.ADD_VALUE))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES,
                        new EnchantmentAttributeEffect(
                                ResourceLocation.withDefaultNamespace("enchantment.swift_sneak_stealth"),
                                ModRegistry.MONSTER_AWARENESS_RANGE,
                                LevelBasedValue.perLevel(-0.2F, -0.1F),
                                AttributeModifier.Operation.ADD_VALUE)));

        register(context, Enchantments.DEPTH_STRIDER, Enchantment.enchantment(
                Enchantment.definition(boots, 2, 3,
                        Enchantment.dynamicCost(10, 10),
                        Enchantment.dynamicCost(25, 10),
                        4,
                        EquipmentSlotGroup.FEET))
                .exclusiveWith(boots_exclusive)
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES,
                        new EnchantmentAttributeEffect(
                                ResourceLocation.withDefaultNamespace("enchantment.depth_strider"),
                                Attributes.WATER_MOVEMENT_EFFICIENCY,
                                LevelBasedValue.perLevel(0.33333334F),
                                AttributeModifier.Operation.ADD_VALUE))
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new EnchantmentAttributeEffect(
                                ResourceLocation.withDefaultNamespace("enchantment.depth_strider_land_speed"),
                                Attributes.MOVEMENT_SPEED,
                                LevelBasedValue.perLevel(0.2F),
                                AttributeModifier.Operation.ADD_MULTIPLIED_BASE),
                                LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                        EntityPredicate.Builder.entity()
                                                .flags(EntityFlagsPredicate.Builder.flags().setSwimming(false))
                                                .effects(MobEffectsPredicate.Builder.effects().and(MobEffects.DOLPHINS_GRACE))))
                .withEffect(ModRegistry.CHANGE_WATER_EFFECTS));

        EntityPredicate.Builder soul_speed_sand = EntityPredicate.Builder.entity().periodicTick(5)
                .flags(EntityFlagsPredicate.Builder.flags().setIsFlying(false).setOnGround(true))
                .moving(MovementPredicate.horizontalSpeed(MinMaxBounds.Doubles.atLeast(1.0E-5F)))
                .movementAffectedBy(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(BlockTags.SOUL_SPEED_BLOCKS)));

        EntityPredicate.Builder soul_speed_health = EntityPredicate.Builder.entity().periodicTick(5)
                .flags(EntityFlagsPredicate.Builder.flags().setIsFlying(false).setOnGround(true))
                .moving(MovementPredicate.horizontalSpeed(MinMaxBounds.Doubles.atLeast(1.0E-5F)))
                .subPredicate(new IsLowHealthPredicate(0.25F));

        LootItemCondition.Builder soul_sand_speed = AllOfCondition.allOf(
                InvertedLootItemCondition.invert(
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().vehicle(EntityPredicate.Builder.entity()))),
                AnyOfCondition.anyOf(
                        AllOfCondition.allOf(EnchantmentActiveCheck.enchantmentActiveCheck(),
                                LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                        EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags().setIsFlying(false))),
                                AnyOfCondition.anyOf(
                                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                                EntityPredicate.Builder.entity().movementAffectedBy(
                                                        LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(BlockTags.SOUL_SPEED_BLOCKS)))),
                                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                                EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags().setOnGround(false)).build()),
                                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                                EntityPredicate.Builder.entity().subPredicate(new IsLowHealthPredicate(0.25F))))),
                        AllOfCondition.allOf(
                                EnchantmentActiveCheck.enchantmentInactiveCheck(),
                                AnyOfCondition.anyOf(
                                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                                EntityPredicate.Builder.entity().movementAffectedBy(
                                                        LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(BlockTags.SOUL_SPEED_BLOCKS)))
                                                        .flags(EntityFlagsPredicate.Builder.flags().setIsFlying(false))),
                                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                                EntityPredicate.Builder.entity().subPredicate(new IsLowHealthPredicate(0.25F)))))));

        register(context, Enchantments.SOUL_SPEED, Enchantment.enchantment(
                Enchantment.definition(boots, 1, 3,
                        Enchantment.dynamicCost(10, 10),
                        Enchantment.dynamicCost(25, 10),
                        8,
                        EquipmentSlotGroup.FEET))
                .withEffect(
                        EnchantmentEffectComponents.LOCATION_CHANGED,
                        new EnchantmentAttributeEffect(
                                ResourceLocation.withDefaultNamespace("enchantment.soul_speed"),
                                Attributes.MOVEMENT_SPEED,
                                LevelBasedValue.perLevel(0.0405F, 0.0105F),
                                AttributeModifier.Operation.ADD_VALUE),
                        soul_sand_speed)
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new EnchantmentAttributeEffect(
                                ResourceLocation.withDefaultNamespace("enchantment.soul_speed"),
                                Attributes.MOVEMENT_EFFICIENCY,
                                LevelBasedValue.constant(1.0F),
                                AttributeModifier.Operation.ADD_VALUE),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().movementAffectedBy(
                                        LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(BlockTags.SOUL_SPEED_BLOCKS)))))
                .withEffect(EnchantmentEffectComponents.TICK,
                        new SpawnParticlesEffect(
                                ParticleTypes.SOUL,
                                SpawnParticlesEffect.inBoundingBox(),
                                SpawnParticlesEffect.offsetFromEntityPosition(0.1F),
                                SpawnParticlesEffect.movementScaled(-0.2F),
                                SpawnParticlesEffect.fixedVelocity(ConstantFloat.of(0.1F)),
                                ConstantFloat.of(1.0F)),
                        AnyOfCondition.anyOf(
                                LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, soul_speed_sand),
                                LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, soul_speed_health)))
                .withEffect(EnchantmentEffectComponents.TICK,
                        new PlaySoundEffect(
                                SoundEvents.SOUL_ESCAPE,
                                ConstantFloat.of(0.6F),
                                UniformFloat.of(0.6F, 1.0F)),
                        AllOfCondition.allOf(LootItemRandomChanceCondition.randomChance(0.35F),
                                AnyOfCondition.anyOf(
                                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, soul_speed_sand),
                                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, soul_speed_health)))));
    }

    public static Enchantment.Builder createSingleLevelEnch(HolderSet.Named<Item> holder, int cost, HolderSet<Enchantment> exclusive) {
        return Enchantment.enchantment(Enchantment.definition(holder, 1, 1,
                Enchantment.constantCost(15), Enchantment.constantCost(65), cost, EquipmentSlotGroup.MAINHAND)).exclusiveWith(exclusive);
    }

    public static Enchantment.Builder createArrowEnch(HolderSet.Named<Item> holder, HolderSet<Enchantment> exclusive) {
        return Enchantment.enchantment(Enchantment.definition(holder, 1, 1,
                Enchantment.constantCost(20), Enchantment.constantCost(50), 8, EquipmentSlotGroup.MAINHAND, EquipmentSlotGroup.OFFHAND)).exclusiveWith(exclusive);
    }

    private static void register(BootstrapContext<Enchantment> registry, ResourceKey<Enchantment> key, Enchantment.Builder builder) {
        registry.register(key, builder.build(key.location()));
    }
}
