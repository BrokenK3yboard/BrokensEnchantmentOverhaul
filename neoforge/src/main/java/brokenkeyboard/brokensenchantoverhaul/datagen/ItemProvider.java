package brokenkeyboard.brokensenchantoverhaul.datagen;

import brokenkeyboard.brokensenchantoverhaul.ModRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

public class ItemProvider extends ItemTagsProvider {

    public ItemProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, CompletableFuture<TagLookup<Block>> blockTags) {
        super(output, provider, blockTags);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(ModRegistry.WEAPON_DURABILITY_BONUS)
                .add(Items.BOW, Items.CROSSBOW, Items.MACE, Items.TRIDENT)
                .addOptionalTag(ItemTags.SWORDS);
        this.tag(ModRegistry.TOOL_EFFICIENCY_BONUS)
                .addOptionalTag(ItemTags.PICKAXES).addOptionalTag(ItemTags.AXES)
                .addOptionalTag(ItemTags.SHOVELS).addOptionalTag(ItemTags.HOES);
        this.tag(ModRegistry.EXCAVATE_ENCHANTABLE)
                .addOptionalTag(ItemTags.PICKAXES).addOptionalTag(ItemTags.SHOVELS);
    }
}
