package brokenkeyboard.brokensenchantoverhaul.predicate;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public record IsLowHealthPredicate(float amount) implements EntitySubPredicate {

    public static final MapCodec<IsLowHealthPredicate> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(ExtraCodecs.POSITIVE_FLOAT.fieldOf("amount").forGetter(IsLowHealthPredicate::amount))
                    .apply(instance, IsLowHealthPredicate::new));

    public static IsLowHealthPredicate isLowHealth(float amount) {
        return new IsLowHealthPredicate(amount);
    }

    @Override
    public MapCodec<? extends EntitySubPredicate> codec() {
        return CODEC;
    }

    @Override
    public boolean matches(Entity entity, ServerLevel level, @Nullable Vec3 vec3) {
        return entity instanceof LivingEntity living && (living.getHealth() <= living.getMaxHealth() * amount);
    }
}
