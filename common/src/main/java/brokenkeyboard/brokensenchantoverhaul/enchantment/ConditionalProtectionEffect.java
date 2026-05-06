package brokenkeyboard.brokensenchantoverhaul.enchantment;

import brokenkeyboard.brokensenchantoverhaul.Config;
import brokenkeyboard.brokensenchantoverhaul.ModRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.apache.commons.lang3.mutable.MutableFloat;

import java.util.function.Function;

public interface ConditionalProtectionEffect {

    Codec<ConditionalProtectionEffect> CODEC = ModRegistry.PROTECTION_REGISTRY.byNameCodec().dispatch(ConditionalProtectionEffect::codec, Function.identity());

    float apply(ServerLevel level, int enchantLevel, EnchantedItemInUse item, LivingEntity user, float damageAmount, DamageSource source, float damageProtection);

    MapCodec<? extends ConditionalProtectionEffect> codec();

    static float modifyEnchantmentProtection(ServerLevel level, LivingEntity entity, float damageAmount, DamageSource source) {
        MutableFloat mutableFloat = new MutableFloat(0F);
        EnchantmentHelper.runIterationOnEquipment(entity, (enchantHolder, enchantLevel, item) -> {
            ItemStack stack = item.itemStack();

            if (Config.OVERHAUL_ENCHANTMENTS.get() && stack.isEnchanted() && stack.getItem() instanceof ArmorItem armorItem) {
                mutableFloat.add(armorItem.getMaterial().value().enchantmentValue() * 0.1F);
            }

            enchantHolder.value().getEffects(ModRegistry.CONDITIONAL_PROTECTION).forEach(effect -> {
                if (effect.matches(Enchantment.damageContext(level, enchantLevel, entity, source))) {
                    mutableFloat.setValue(effect.effect().apply(level, enchantLevel, item, entity, damageAmount, source, mutableFloat.floatValue()));
                }
            });
        });
        return mutableFloat.floatValue();
    }
}
