package daripher.skilltree.data.generation;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.data.generation.loot.PSTGlobalLootModifierProvider;
import daripher.skilltree.data.generation.loot.PSTLootTablesProvider;
import daripher.skilltree.data.generation.translation.PSTEnglishTranslationProvider;
import daripher.skilltree.data.generation.translation.PSTRussianTranslationProvider;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID, bus = Bus.MOD)
public class PSTDataGenerator {
  @SubscribeEvent
  public static void onGatherData(GatherDataEvent event) {
    DataGenerator dataGenerator = event.getGenerator();
    ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
    CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

    boolean includeServer = event.includeServer();
    PSTBlockTagsProvider blockTagsProvider =
        new PSTBlockTagsProvider(dataGenerator, lookupProvider, existingFileHelper);
    dataGenerator.addProvider(includeServer, blockTagsProvider);
    dataGenerator.addProvider(
        includeServer,
        new PSTItemTagsProvider(dataGenerator, lookupProvider, blockTagsProvider, existingFileHelper));
    dataGenerator.addProvider(
        includeServer, new PSTLootTablesProvider(dataGenerator, lookupProvider));
    dataGenerator.addProvider(includeServer, new PSTGlobalLootModifierProvider(dataGenerator, lookupProvider));
    dataGenerator.addProvider(
        includeServer, new PSTDamageTagsProvider(dataGenerator, lookupProvider, existingFileHelper));
    dataGenerator.addProvider(
        includeServer, new PSTRecipesProvider(dataGenerator.getPackOutput(), lookupProvider));

    boolean includeClient = event.includeClient();
    dataGenerator.addProvider(includeClient, new PSTEnglishTranslationProvider(dataGenerator));
    dataGenerator.addProvider(includeClient, new PSTRussianTranslationProvider(dataGenerator));
    dataGenerator.addProvider(
        includeClient, new PSTBlockStatesProvider(dataGenerator, existingFileHelper));
    dataGenerator.addProvider(includeClient, new PSTItemModelsProvider(dataGenerator, existingFileHelper));
  }
}
