package brokenkeyboard.brokensenchantoverhaul.predicate;

import com.mojang.serialization.MapCodec;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public record PickupArrowPredicate() implements EntitySubPredicate {

    public static final MapCodec<PickupArrowPredicate> CODEC = MapCodec.unit(new PickupArrowPredicate());

    public static PickupArrowPredicate shouldReturnArrow() {
        return new PickupArrowPredicate();
    }

    @Override
    public MapCodec<? extends EntitySubPredicate> codec() {
        return CODEC;
    }

    @Override
    public boolean matches(Entity entity, ServerLevel serverLevel, @Nullable Vec3 vec3) {
        return entity instanceof AbstractArrow arrow && arrow.pickup == AbstractArrow.Pickup.ALLOWED && arrow.getPickupItemStackOrigin().is(Items.ARROW);
    }
}
