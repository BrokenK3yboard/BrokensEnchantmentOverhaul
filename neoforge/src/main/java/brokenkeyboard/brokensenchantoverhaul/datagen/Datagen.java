package brokenkeyboard.brokensenchantoverhaul.datagen;

import brokenkeyboard.brokensenchantoverhaul.Constants;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = Constants.MOD_ID)
public class Datagen {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper helper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> provider = event.getLookupProvider();
        DatapackProvider dataProvider = new DatapackProvider(output, provider);

        generator.addProvider(event.includeServer(), dataProvider);

        BlockProvider blockTags = generator.addProvider(event.includeServer(), new BlockProvider(output, event.getLookupProvider(), helper));
        generator.addProvider(event.includeServer(), new ItemProvider(output, dataProvider.getRegistryProvider(), blockTags.contentsGetter()));
        generator.addProvider(event.includeServer(), new EnchantProvider(output, dataProvider.getRegistryProvider(), helper));
        generator.addProvider(event.includeServer(), new DamageProvider(output, dataProvider.getRegistryProvider(), helper));
    }
}
