package brokenkeyboard.brokensenchantoverhaul.predicate;

import com.mojang.serialization.MapCodec;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public record HasNegativeEffectPredicate() implements EntitySubPredicate {

    public static final MapCodec<HasNegativeEffectPredicate> CODEC = MapCodec.unit(new HasNegativeEffectPredicate());

    public static HasNegativeEffectPredicate hasNegativeEffect() {
        return new HasNegativeEffectPredicate();
    }

    @Override
    public MapCodec<? extends EntitySubPredicate> codec() {
        return CODEC;
    }

    @Override
    public boolean matches(Entity entity, ServerLevel serverLevel, @Nullable Vec3 vec3) {
        return entity instanceof LivingEntity living && living.getActiveEffects().stream().anyMatch(effect ->
                effect.getEffect().value().getCategory() == MobEffectCategory.HARMFUL);
    }
}
