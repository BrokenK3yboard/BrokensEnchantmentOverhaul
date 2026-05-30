package brokenkeyboard.brokensenchantoverhaul.enchantment;

import brokenkeyboard.brokensenchantoverhaul.platform.Services;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentLocationBasedEffect;
import net.minecraft.world.level.block.AbstractChestBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public record DetectBlocksEffect(LevelBasedValue detectionRange, TagKey<Block> detectBlocks) implements EnchantmentLocationBasedEffect {

    public static final MapCodec<DetectBlocksEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LevelBasedValue.CODEC.fieldOf("detection_range").forGetter(DetectBlocksEffect::detectionRange),
            TagKey.codec(Registries.BLOCK).fieldOf("detect_blocks").forGetter(DetectBlocksEffect::detectBlocks)
    ).apply(instance, DetectBlocksEffect::new));

    @Override
    public void onChangedBlock(ServerLevel level, int enchantLevel, EnchantedItemInUse item, Entity entity, Vec3 vec3, boolean applyTransientEffects) {
        if (!(entity instanceof ServerPlayer player)) return;
        boolean isCurrentlyNearbyOre = Services.PLATFORM.getDetectedBlocksNearby(player);
        boolean isNearbyBlock = BlockPos.betweenClosedStream(entity.getBoundingBox().inflate(detectionRange.calculate(enchantLevel))).anyMatch(blockPos -> {
            BlockState state = level.getBlockState(blockPos);
            boolean detected = state.is(detectBlocks);

            if (detected && state.getBlock() instanceof AbstractChestBlock<?> && level.getBlockEntity(blockPos) instanceof RandomizableContainerBlockEntity blockEntity) {
                return (blockEntity.getLootTable() != null);
            }
            return detected;
        });

        if (isCurrentlyNearbyOre != isNearbyBlock) {
            Services.PLATFORM.setDetectedBlocksNearby(player, isNearbyBlock);
        }
    }

    @Override
    public MapCodec<? extends EnchantmentLocationBasedEffect> codec() {
        return CODEC;
    }
}
