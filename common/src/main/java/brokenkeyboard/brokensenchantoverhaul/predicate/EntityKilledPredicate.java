package brokenkeyboard.brokensenchantoverhaul.predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public record EntityKilledPredicate(boolean hostileOrPlayer) implements EntitySubPredicate {

    public static final MapCodec<EntityKilledPredicate> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(Codec.BOOL.fieldOf("hostileOrPlayer").forGetter(EntityKilledPredicate::hostileOrPlayer))
                    .apply(instance, EntityKilledPredicate::new));

    public static EntityKilledPredicate entityKilled(boolean hostileOrPlayer) {
        return new EntityKilledPredicate(hostileOrPlayer);
    }

    @Override
    public MapCodec<? extends EntitySubPredicate> codec() {
        return CODEC;
    }

    @Override
    public boolean matches(Entity entity, ServerLevel level, @Nullable Vec3 vec3) {
        if (!(entity instanceof LivingEntity living && living.isDeadOrDying())) return false;
        return !hostileOrPlayer || (living instanceof Player || living instanceof Monster || living instanceof NeutralMob);
    }
}