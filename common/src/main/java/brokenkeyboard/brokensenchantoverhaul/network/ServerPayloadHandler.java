package brokenkeyboard.brokensenchantoverhaul.network;

import brokenkeyboard.brokensenchantoverhaul.ModRegistry;
import brokenkeyboard.brokensenchantoverhaul.enchantment.WallSlideEffect;
import brokenkeyboard.brokensenchantoverhaul.platform.Services;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public class ServerPayloadHandler {

    public static void sendS2CAttachmentSync(int entityID, ServerPlayer sender, Consumer<Entity> toSend) {
        Level level = sender.server.getLevel(sender.level().dimension());

        if (level != null) {
            Entity entity = level.getEntity(entityID);
            if (entity != null) {
                toSend.accept(entity);
            }
        }
    }

    public static void handleWallJump(ServerLevel level, ServerPlayer player) {
        Holder<Enchantment> holder = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(ModRegistry.FRICTION);

        if (WallSlideEffect.shouldSlide(level, player) && EnchantmentHelper.getEnchantmentLevel(holder, player) > 0) {
            player.setDeltaMovement(player.getViewVector(1));
            Services.PLATFORM.setWallSlideTicks(player, -1);
            RandomSource random = level.random;
            for (int i = 0; i < 20; ++i) {
                double xOffset = random.nextGaussian() * 0.02D;
                double yOffset = random.nextGaussian() * 0.02D;
                double zOffset = random.nextGaussian() * 0.02D;
                double bbWidth = player.getBbWidth();
                level.sendParticles(ParticleTypes.POOF,
                        player.getX() + (random.nextFloat() * bbWidth * 2.0F) - bbWidth - xOffset * 10.0D,
                        player.getY() - yOffset * 10.0D,
                        player.getZ() + (random.nextFloat() * bbWidth * 2.0F) - bbWidth - zOffset * 10.0D,
                        1, xOffset, yOffset, zOffset, 0.0F);
            }
        }
    }
}
