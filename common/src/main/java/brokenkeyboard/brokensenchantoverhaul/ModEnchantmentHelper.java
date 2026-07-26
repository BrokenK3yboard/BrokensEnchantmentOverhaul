package brokenkeyboard.brokensenchantoverhaul;

import brokenkeyboard.brokensenchantoverhaul.platform.Services;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.apache.commons.lang3.mutable.MutableFloat;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

import static brokenkeyboard.brokensenchantoverhaul.ModRegistry.HARVEST;
import static brokenkeyboard.brokensenchantoverhaul.ModRegistry.SCAVENGER;

public class ModEnchantmentHelper {

    private static final HashSet<UUID> ACTIVE_MINERS = new HashSet<>();
    public static final Predicate<LivingEntity> HAS_STABILIZE = entity -> EnchantmentHelper.getRandomItemWith(ModRegistry.EXPLOSION_DEFUSE, entity, stack -> true).isPresent();

    public static float modifyMiningEfficiency(ItemStack tool) {
        if (!Config.OVERHAUL_ENCHANTMENTS.get() || !tool.isEnchanted() || !tool.is(ModRegistry.TOOL_EFFICIENCY_BONUS)) return 0;
        int enchantPower = tool.getItem().getEnchantmentValue();
        return -0.04F * enchantPower * enchantPower + 2F * enchantPower;
    }

    public static void handleExcavator(Player player, BlockState state, Level level, BlockPos pos) {
        if (!(player instanceof ServerPlayer serverPlayer && !ACTIVE_MINERS.contains(serverPlayer.getUUID()))) return;

        ItemStack stack = serverPlayer.getMainHandItem();
        if (!(EnchantmentHelper.has(stack, ModRegistry.AREA_MINING) && isProperTool(state,stack))) return;

        HitResult result = serverPlayer.pick(serverPlayer.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE), 1F, false);
        if (!(result instanceof BlockHitResult blockHit && level.getBlockState(blockHit.getBlockPos()).equals(state))) return;

        Direction direction = blockHit.getDirection();
        UUID uuid = serverPlayer.getUUID();
        ACTIVE_MINERS.add(uuid);

        for (BlockPos currentPos : getBlocks(pos, direction)) {
            BlockState currentState = level.getBlockState(currentPos);
            if (isProperTool(currentState, stack)) {
                serverPlayer.gameMode.destroyBlock(currentPos);
            }
        }
        ACTIVE_MINERS.remove(uuid);
    }

    public static void handleBlockDrops(List<ItemEntity> list, Level level, BlockState state, BlockPos pos, Entity breaker, ItemStack tool) {
        RegistryAccess access = level.registryAccess();
        HolderLookup.RegistryLookup<Enchantment> registry = access.lookupOrThrow(Registries.ENCHANTMENT);

        for (ItemEntity itemEntity : list) {
            if (EnchantmentHelper.hasTag(tool, EnchantmentTags.SMELTS_LOOT) && isProperTool(state, tool)) {
                Optional<RecipeHolder<SmeltingRecipe>> recipe = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(itemEntity.getItem()), level);
                recipe.ifPresent(recipeHolder -> itemEntity.setItem(recipeHolder.value().getResultItem(access)));
            }

            if (EnchantmentHelper.getItemEnchantmentLevel(registry.getOrThrow(HARVEST), tool) > 0 && state.getBlock() instanceof CropBlock crop && crop.isMaxAge(state)) {
                ItemStack stack = itemEntity.getItem();
                if (stack.is(crop.getCloneItemStack(level, pos, state).getItem())) {
                    stack.shrink(1);
                    level.setBlockAndUpdate(pos, crop.defaultBlockState());
                }
            }

            if (breaker instanceof LivingEntity living && EnchantmentHelper.getEnchantmentLevel(registry.getOrThrow(SCAVENGER), living) > 0 && state.getBlock() instanceof DropExperienceBlock) {
                Services.PLATFORM.markScavengerLoot(itemEntity, breaker);
            }
        }
    }

    public static boolean preventDamageTempered(HolderLookup.Provider provider, ItemStack stack, BlockState state) {
        Holder<Enchantment> enchantment = provider.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(ModRegistry.TEMPERED);
        return EnchantmentHelper.getItemEnchantmentLevel(enchantment, stack) > 0 && !state.is(ModRegistry.DAMAGE_TEMPERED) && isProperTool(state, stack);
    }

    private static boolean isProperTool(BlockState state, ItemStack stack) {
        Tool tool = stack.getComponents().get(DataComponents.TOOL);
        return tool != null && tool.damagePerBlock() < 2 && tool.getMiningSpeed(state) > 1 && tool.isCorrectForDrops(state);
    }

    private static Iterable<BlockPos> getBlocks(BlockPos pos, Direction direction) {
        return switch (direction) {
            case UP, DOWN -> BlockPos.betweenClosed(new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ() - 1), new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ() + 1));
            case NORTH, SOUTH -> BlockPos.betweenClosed(new BlockPos(pos.getX() - 1, pos.getY() - 1, pos.getZ()), new BlockPos(pos.getX() + 1, pos.getY() + 1, pos.getZ()));
            case EAST, WEST -> BlockPos.betweenClosed(new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ() - 1), new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ() + 1));
        };
    }

    public static void postLootPickup(ServerLevel level, Player player) {
        EnchantmentHelper.runIterationOnEquipment(player, (enchantHolder, enchantLevel, itemInUse) ->
                enchantHolder.value().getEffects(ModRegistry.LOOT_PICKUP_BONUS).forEach(effect -> {
                    if (effect.matches(Enchantment.entityContext(level, enchantLevel, player, player.position()))) {
                        effect.effect().apply(level, enchantLevel, itemInUse, player, player.position());
                    }
                }));
    }

    public static float handleBarrierDamage(LivingEntity entity, float damage) {
        int barrierAmount = Services.PLATFORM.getBarrierAmount(entity);
        Services.PLATFORM.setBarrierTimestamp(entity);

        if (barrierAmount > 0) {
            Services.PLATFORM.setBarrierAmount(entity, barrierAmount - 1);
            return 1;
        }
        return damage;
    }

    public static float getPowerShotDamage(ItemStack weapon, Entity target, int ticks) {
        int powerShotTicks = Math.clamp(ticks - 20, 0, 40);
        if (powerShotTicks == 0) return 0;

        MutableFloat damage = new MutableFloat(0);
        EnchantmentHelper.runIterationOnItem(weapon, (enchantHolder, enchantLevel) ->
                enchantHolder.value().getEffects(ModRegistry.POWER_SHOT_DAMAGE).forEach(effect ->
                        damage.setValue(effect.effect().process(enchantLevel, target.getRandom(), powerShotTicks))));
        return damage.floatValue();
    }

    public static float getPowerShotKnockback(ItemStack weapon, Entity target, int ticks) {
        int powerShotTicks = Math.clamp(ticks - 20, 0, 40);
        if (powerShotTicks == 0) return 0;

        MutableFloat knockback = new MutableFloat(0);
        EnchantmentHelper.runIterationOnItem(weapon, (enchantHolder, enchantLevel) ->
                enchantHolder.value().getEffects(ModRegistry.POWER_SHOT_KNOCKBACK).forEach(effect ->
                        knockback.setValue(effect.effect().process(enchantLevel, target.getRandom(), powerShotTicks))));
        return knockback.floatValue();
    }
}