package brokenkeyboard.brokensenchantoverhaul;

import brokenkeyboard.brokensenchantoverhaul.platform.Services;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.apache.commons.lang3.mutable.MutableFloat;

import java.util.List;
import java.util.function.Predicate;

public class CommonHandler {

    public static int modifyDurabilityLoss(RandomSource random, ItemStack weapon, int damage) {
        if (!Config.OVERHAUL_ENCHANTMENTS.get() || !weapon.isEnchanted() || !weapon.is(ModRegistry.WEAPON_DURABILITY_BONUS)) return damage;
        float enchantPower = weapon.getItem().getEnchantmentValue();
        float chance = 0.0012F * enchantPower * enchantPower + 0.006F * enchantPower + 0.2F;
        int toReduce = 0;

        for (int i = 0; i < damage; ++i) {
            if (random.nextFloat() < chance) {
                ++toReduce;
            }
        }
        return damage - toReduce;
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

    public static float getSweepingEdgeBonus(List<LivingEntity> entities, Player attacker, Entity target, ItemStack weapon, DamageSource source, float damage) {
        MutableFloat damageBonus = new MutableFloat(damage);
        if (attacker.level() instanceof ServerLevel level) {
            EnchantmentHelper.runIterationOnItem(weapon, (enchantHolder, enchantLevel) ->
                    enchantHolder.value().getEffects(ModRegistry.SWEEPING_DAMAGE_BONUS).forEach(effect -> {
                        if (effect.matches(Enchantment.damageContext(level, enchantLevel, target, source))) {
                            long entityCount = entities.stream().filter(target1 -> !target1.is(attacker) && !target1.isAlliedTo(attacker) &&
                                    (!(target1 instanceof ArmorStand) || !((ArmorStand) target1).isMarker()) && attacker.distanceToSqr(target1) < 9.0D).count();
                            damageBonus.add(effect.effect().process(enchantLevel, attacker.getRandom(), entityCount - 1));
                        }
                    }));
            return damageBonus.floatValue() + 2;
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

    public static final Predicate<LivingEntity> HAS_STABILIZE = entity ->
            EnchantmentHelper.getRandomItemWith(ModRegistry.EXPLOSION_DEFUSE, entity, stack -> true).isPresent();
}