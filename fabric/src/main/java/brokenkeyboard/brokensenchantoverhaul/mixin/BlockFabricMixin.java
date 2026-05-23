package brokenkeyboard.brokensenchantoverhaul.mixin;

import brokenkeyboard.brokensenchantoverhaul.ModEnchantmentHelper;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Mixin(Block.class)
public class BlockFabricMixin {

    @Unique
    @Nullable
    private static List<ItemEntity> capturedDrops = null;

    @Unique
    private static void beginCapturingDrops() {
        capturedDrops = new ArrayList<>();
    }

    @Unique
    private static List<ItemEntity> stopCapturingDrops() {
        List<ItemEntity> drops = capturedDrops;
        capturedDrops = null;
        return drops;
    }

    @Inject(method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;"))
    private static void captureDrops(BlockState state, Level level, BlockPos pos, BlockEntity blockEntity, Entity entity, ItemStack tool, CallbackInfo ci) {
        beginCapturingDrops();
    }

    @Inject(method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;spawnAfterBreak(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;Z)V"))
    private static void modifyDrops(BlockState state, Level level, BlockPos pos, BlockEntity blockEntity, Entity entity, ItemStack tool, CallbackInfo ci) {
        List<ItemEntity> drops = stopCapturingDrops();
        ModEnchantmentHelper.handleBlockDrops(drops, level, state, pos, entity, tool);
    }

    @WrapOperation(method = "popResource(Lnet/minecraft/world/level/Level;Ljava/util/function/Supplier;Lnet/minecraft/world/item/ItemStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private static boolean popResources(Level level, Entity entity, Operation<Boolean> original) {
        if (capturedDrops != null) {
            capturedDrops.add((ItemEntity) entity);
        }
        return original.call(level, entity);
    }
}