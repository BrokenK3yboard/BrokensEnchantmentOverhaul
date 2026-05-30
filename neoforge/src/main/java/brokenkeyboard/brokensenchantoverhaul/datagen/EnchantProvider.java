package brokenkeyboard.brokensenchantoverhaul.datagen;

import brokenkeyboard.brokensenchantoverhaul.Constants;
import brokenkeyboard.brokensenchantoverhaul.ModRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EnchantmentTagsProvider;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class EnchantProvider extends EnchantmentTagsProvider {

    public EnchantProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper fileHelper) {
        super(output, provider, Constants.MOD_ID, fileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(EnchantmentTags.NON_TREASURE)
                .add(ModRegistry.FILTERED).add(ModRegistry.INSIGHT).add(ModRegistry.DEXTERITY)
                .add(ModRegistry.VITALITY).add(ModRegistry.BARRIER).add(ModRegistry.ADAPTIVE)
                .add(ModRegistry.SCAVENGER).add(ModRegistry.STABILIZE).add(ModRegistry.RUSH)
                .add(ModRegistry.AGILITY).add(ModRegistry.FRICTION)
                .add(ModRegistry.POWER_SHOT).add(ModRegistry.BARRAGE)
                .add(ModRegistry.VOLLEY)
                .add(ModRegistry.EXCAVATE).add(ModRegistry.TEMPERED)
                .add(ModRegistry.PROSPECTING)
                .add(ModRegistry.SPELUNKER)
                .add(ModRegistry.HARVEST)
                .add(ModRegistry.GRAPPLE).add(ModRegistry.DEEP_FRYER)
                .add(ModRegistry.BLACKSMITH);
        tag(EnchantmentTags.ARMOR_EXCLUSIVE)
                .add(ModRegistry.VITALITY).add(ModRegistry.BARRIER).add(ModRegistry.ADAPTIVE).add(Enchantments.THORNS);
        tag(EnchantmentTags.MINING_EXCLUSIVE)
                .add(ModRegistry.EXCAVATE);
        tag(ModRegistry.LEGGINGS_EXCLUSIVE)
                .add(ModRegistry.RUSH).add(ModRegistry.SCAVENGER).add(ModRegistry.STABILIZE);
        tag(ModRegistry.FISHING_EXCLUSIVE)
                .add(ModRegistry.GRAPPLE).add(ModRegistry.DEEP_FRYER);

        tag(ModRegistry.REMOVED_ENCHANTMENTS)
                .add(Enchantments.PROTECTION).add(Enchantments.FIRE_PROTECTION).add(Enchantments.BLAST_PROTECTION)
                .add(Enchantments.PROJECTILE_PROTECTION).add(Enchantments.FEATHER_FALLING)
                .add(Enchantments.UNBREAKING).add(Enchantments.EFFICIENCY).add(Enchantments.MENDING)
                .add(Enchantments.FORTUNE).add(Enchantments.LOOTING)
                .add(Enchantments.SHARPNESS).add(Enchantments.KNOCKBACK).add(Enchantments.BANE_OF_ARTHROPODS)
                .add(Enchantments.AQUA_AFFINITY).add(Enchantments.RESPIRATION)
                .add(Enchantments.POWER).add(Enchantments.PUNCH).add(Enchantments.INFINITY)
                .add(Enchantments.QUICK_CHARGE)
                .add(Enchantments.LURE)
                .add(Enchantments.LOYALTY)
                .add(Enchantments.BINDING_CURSE).add(Enchantments.VANISHING_CURSE);
    }
}
