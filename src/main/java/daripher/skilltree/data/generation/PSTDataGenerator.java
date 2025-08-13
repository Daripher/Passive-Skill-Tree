package daripher.skilltree.data.generation;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.data.generation.loot.PSTLootTablesProvider;
import daripher.skilltree.data.generation.translation.PSTEnglishTranslationProvider;
import daripher.skilltree.data.generation.translation.PSTRussianTranslationProvider;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID, bus = Bus.MOD)
public class PSTDataGenerator {
  @SubscribeEvent
  public static void onGatherData(GatherDataEvent event) {
    DataGenerator dataGenerator = event.getGenerator();
    ExistingFileHelper fileHelper = event.getExistingFileHelper();
    CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

    boolean includeServer = event.includeServer();
    PSTBlockTagsProvider blockTagsProvider =
        new PSTBlockTagsProvider(dataGenerator, lookupProvider, fileHelper);
    dataGenerator.addProvider(includeServer, blockTagsProvider);
    dataGenerator.addProvider(
        includeServer,
        new PSTItemTagsProvider(dataGenerator, lookupProvider, blockTagsProvider, fileHelper));
    dataGenerator.addProvider(
        includeServer, new PSTLootTablesProvider(dataGenerator));
    dataGenerator.addProvider(includeServer, new PSTGlobalLootModifierProvider(dataGenerator));
    dataGenerator.addProvider(
        includeServer, new PSTDamageTagsProvider(dataGenerator, lookupProvider, fileHelper));

    boolean includeClient = event.includeClient();
    dataGenerator.addProvider(includeClient, new PSTEnglishTranslationProvider(dataGenerator));
    dataGenerator.addProvider(includeClient, new PSTRussianTranslationProvider(dataGenerator));
  }
}
