package brokenkeyboard.brokensenchantoverhaul.enchantment;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.phys.Vec3;

public record GrappleEffect(LevelBasedValue strength) implements HookPullEffect {

    public static final MapCodec<GrappleEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        LevelBasedValue.CODEC.fieldOf("pull_strength").forGetter(GrappleEffect::strength)
    ).apply(instance, GrappleEffect::new));

    @Override
    public int apply(ServerLevel level, int enchantLevel, ItemStack stack, FishingHook hook, int value) {
        if (!(hook.getOwner() instanceof LivingEntity living)) return value;
        if (hook.getHookedIn() != null) {
            pullEntity(strength.calculate(enchantLevel), living.position(), hook.getHookedIn());
        } else if (hook.onGround()) {
            pullEntity(strength.calculate(enchantLevel), hook.position(), living);
        }
        return 1;
    }

    public static void pullEntity(float pullStrength, Vec3 position, Entity target) {
        double distance = position.distanceTo(target.position());
        double x = (1.0 + 0.07 * distance) * (position.x() - target.getX()) / distance;
        double y = (1.0 + 0.03 * distance) * (position.y() - target.getY()) / distance + 0.04 * distance;
        double z = (1.0 + 0.07 * distance) * (position.z() - target.getZ()) / distance;
        target.setDeltaMovement(new Vec3(x, y, z).scale(pullStrength));

        if (target instanceof Player player) {
            player.hurtMarked = true;
        }
    }

    @Override
    public MapCodec<? extends HookPullEffect> codec() {
        return CODEC;
    }
}
