package brokenkeyboard.brokensenchantoverhaul.enchantment;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Predicate;

public record ScavengerMagnetEffect(LevelBasedValue range, LevelBasedValue pullStrength) implements EnchantmentEntityEffect {

    public static final MapCodec<ScavengerMagnetEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LevelBasedValue.CODEC.fieldOf("pull_range").forGetter(ScavengerMagnetEffect::range),
            LevelBasedValue.CODEC.fieldOf("pull_strength").forGetter(ScavengerMagnetEffect::pullStrength)
    ).apply(instance, ScavengerMagnetEffect::new));

    public static final Predicate<Entity> CAN_PICKUP = entity -> entity instanceof ItemEntity item && !item.hasPickUpDelay() || entity instanceof ExperienceOrb;

    @Override
    public void apply(ServerLevel level, int enchantLevel, EnchantedItemInUse item, Entity entity, Vec3 pos) {
        List<Entity> pickupItems = level.getEntitiesOfClass(Entity.class, entity.getBoundingBox().inflate(range.calculate(enchantLevel)), CAN_PICKUP);

        for (Entity entity1 : pickupItems) {
            Vec3 velocity = entity1.position().vectorTo(new Vec3(pos.x, pos.y, pos.z)).normalize().scale(pullStrength.calculate(enchantLevel));
            entity1.push(velocity.x, velocity.y, velocity.z);
        }
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}