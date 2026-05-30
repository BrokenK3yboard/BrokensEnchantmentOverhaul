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
        tag(ModRegistry.TEMPERED_AFFECTS)
                .addTag(BlockTags.BASE_STONE_OVERWORLD).addTag(BlockTags.BASE_STONE_NETHER)
                .addTag(BlockTags.DIRT)
                .addTag(BlockTags.LEAVES);

        tag(ModRegistry.PROSPECTING_DETECTS)
                .addTag(BlockTags.COAL_ORES).addTag(BlockTags.COPPER_ORES).addTag(BlockTags.IRON_ORES)
                .addTag(BlockTags.GOLD_ORES).addTag(BlockTags.DIAMOND_ORES).addTag(BlockTags.EMERALD_ORES)
                .addTag(BlockTags.LAPIS_ORES).addTag(Tags.Blocks.ORES_QUARTZ).add(Blocks.CHEST);

        tag(ModRegistry.SPELUNKER_DETECTS)
                .add(Blocks.SUSPICIOUS_GRAVEL, Blocks.SUSPICIOUS_SAND, Blocks.CHEST, Blocks.SPAWNER);
    }
}
