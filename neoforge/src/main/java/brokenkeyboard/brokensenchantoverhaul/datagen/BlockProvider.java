package brokenkeyboard.brokensenchantoverhaul.datagen;

import brokenkeyboard.brokensenchantoverhaul.Constants;
import brokenkeyboard.brokensenchantoverhaul.ModRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
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
    }
}
