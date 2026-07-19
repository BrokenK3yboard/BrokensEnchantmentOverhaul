package brokenkeyboard.brokensenchantoverhaul.enchantment;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;

public record DeflectDamageEffect(LevelBasedValue amount) implements ConditionalProtectionEffect {

    public static final MapCodec<DeflectDamageEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LevelBasedValue.CODEC.fieldOf("amount").forGetter(DeflectDamageEffect::amount)
    ).apply(instance, DeflectDamageEffect::new));

    @Override
    public float apply(int enchantLevel, EnchantedItemInUse item, LivingEntity user, float damageAmount, DamageSource source, float damageProtection) {
        if (source.getEntity() instanceof LivingEntity attacker) {
            float reduction = amount.calculate(enchantLevel) * 0.04F;
            float damage = damageAmount * reduction;
            attacker.hurt(attacker.damageSources().thorns(user), damage);
            return damageProtection + (25 - damageProtection) * reduction;
        }
        return damageProtection;
    }

    @Override
    public MapCodec<? extends ConditionalProtectionEffect> codec() {
        return CODEC;
    }
}
