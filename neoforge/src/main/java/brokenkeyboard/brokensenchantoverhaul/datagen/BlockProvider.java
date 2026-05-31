package brokenkeyboard.brokensenchantoverhaul.datagen;

import brokenkeyboard.brokensenchantoverhaul.Constants;
import brokenkeyboard.brokensenchantoverhaul.ModRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class BlockProvider extends BlockTagsProvider {

    public BlockProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper fileHelper) {
        super(output, provider, Constants.MOD_ID, fileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ModRegistry.DAMAGE_TEMPERED)
                .addTag(BlockTags.COAL_ORES).addTag(BlockTags.COPPER_ORES).addTag(BlockTags.IRON_ORES)
                .addTag(BlockTags.GOLD_ORES).addTag(BlockTags.DIAMOND_ORES).addTag(BlockTags.EMERALD_ORES)
                .addTag(BlockTags.LAPIS_ORES).addTag(Tags.Blocks.ORES_REDSTONE).addTag(Tags.Blocks.ORES_QUARTZ)
                .add(Blocks.SMALL_AMETHYST_BUD, Blocks.MEDIUM_AMETHYST_BUD, Blocks.LARGE_AMETHYST_BUD, Blocks.AMETHYST_CLUSTER, Blocks.BUDDING_AMETHYST,
                        Blocks.ANCIENT_DEBRIS, Blocks.RAW_COPPER_BLOCK, Blocks.RAW_IRON_BLOCK, Blocks.RAW_GOLD_BLOCK)
                .addTag(BlockTags.SAND).add(Blocks.CLAY, Blocks.GRAVEL, Blocks.SUSPICIOUS_GRAVEL, Blocks.SOUL_SAND)
                .addTag(BlockTags.LOGS).add(Blocks.PUMPKIN, Blocks.MELON, Blocks.BROWN_MUSHROOM_BLOCK, Blocks.RED_MUSHROOM_BLOCK, Blocks.MUSHROOM_STEM)
                .add(Blocks.SCULK, Blocks.SCULK_CATALYST, Blocks.SCULK_SHRIEKER);

        tag(ModRegistry.PROSPECTING_DETECTS)
                .addTag(BlockTags.COAL_ORES).addTag(BlockTags.COPPER_ORES).addTag(BlockTags.IRON_ORES)
                .addTag(BlockTags.GOLD_ORES).addTag(BlockTags.DIAMOND_ORES).addTag(BlockTags.EMERALD_ORES)
                .addTag(BlockTags.LAPIS_ORES).addTag(Tags.Blocks.ORES_REDSTONE).addTag(Tags.Blocks.ORES_QUARTZ)
                .add(Blocks.SMALL_AMETHYST_BUD, Blocks.MEDIUM_AMETHYST_BUD, Blocks.LARGE_AMETHYST_BUD, Blocks.AMETHYST_CLUSTER, Blocks.BUDDING_AMETHYST);

        tag(ModRegistry.SPELUNKER_DETECTS)
                .add(Blocks.SUSPICIOUS_GRAVEL, Blocks.SUSPICIOUS_SAND, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.SPAWNER);
    }
}
