package brokenkeyboard.brokensenchantoverhaul.datagen;

import brokenkeyboard.brokensenchantoverhaul.Constants;
import brokenkeyboard.brokensenchantoverhaul.ModRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class DamageProvider extends TagsProvider<DamageType> {

    protected DamageProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper fileHelper) {
        super(output, Registries.DAMAGE_TYPE, provider, Constants.MOD_ID, fileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(DamageTypeTags.ALWAYS_KILLS_ARMOR_STANDS).add(ModRegistry.ARROW_MULTISHOT);
        this.tag(DamageTypeTags.PANIC_ENVIRONMENTAL_CAUSES).add(ModRegistry.ARROW_MULTISHOT);
        this.tag(DamageTypeTags.IS_PROJECTILE).add(ModRegistry.ARROW_MULTISHOT);
        this.tag(DamageTypeTags.BYPASSES_COOLDOWN).add(ModRegistry.ARROW_MULTISHOT);
    }
}
