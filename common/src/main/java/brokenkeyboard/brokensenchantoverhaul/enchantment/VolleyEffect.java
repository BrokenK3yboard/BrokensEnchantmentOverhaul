package brokenkeyboard.brokensenchantoverhaul.enchantment;

import brokenkeyboard.brokensenchantoverhaul.mixin.CrossbowItemAccessor;
import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;

public record VolleyEffect() implements EnchantmentEntityEffect {

    public static final MapCodec<VolleyEffect> CODEC = MapCodec.unit(VolleyEffect::new);

    @Override
    public void apply(ServerLevel level, int enchantLevel, EnchantedItemInUse item, Entity entity, Vec3 vec3) {
        LivingEntity living = item.owner();
        if (living != null && item.inSlot() != null) {
            CrossbowItemAccessor.callTryLoadProjectiles(living, living.getItemBySlot(item.inSlot()));
        }
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
