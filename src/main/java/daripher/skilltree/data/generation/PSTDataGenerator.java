package daripher.skilltree.data.generation;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.data.generation.loot.PSTLootTablesProvider;
import daripher.skilltree.data.generation.skills.PSTSkillTreesProvider;
import daripher.skilltree.data.generation.skills.PSTSkillsProvider;
import daripher.skilltree.data.generation.translation.PSTEnglishTranslationProvider;
import daripher.skilltree.data.generation.translation.PSTRussianTranslationProvider;
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

    PSTBlockTagsProvider blockTagsProvider = new PSTBlockTagsProvider(dataGenerator, fileHelper);
    dataGenerator.addProvider(event.includeServer(), blockTagsProvider);
    dataGenerator.addProvider(
        event.includeServer(),
        new PSTItemTagsProvider(dataGenerator, blockTagsProvider, fileHelper));
    PSTGemTypesProvider gemTypesProvider = new PSTGemTypesProvider(dataGenerator);
    dataGenerator.addProvider(event.includeServer(), gemTypesProvider);
    dataGenerator.addProvider(
        event.includeServer(), new PSTLootTablesProvider(dataGenerator, gemTypesProvider));
    dataGenerator.addProvider(event.includeServer(), new PSTRecipesProvider(dataGenerator));

    dataGenerator.addProvider(
        event.includeClient(), new PSTEnglishTranslationProvider(dataGenerator));
    dataGenerator.addProvider(
        event.includeClient(), new PSTRussianTranslationProvider(dataGenerator));
    dataGenerator.addProvider(
        event.includeClient(),
        new PSTItemModelsProvider(dataGenerator, fileHelper, gemTypesProvider));

    PSTSkillsProvider skillsProvider = new PSTSkillsProvider(dataGenerator);
    dataGenerator.addProvider(event.includeServer(), skillsProvider);
    dataGenerator.addProvider(
        event.includeServer(), new PSTSkillTreesProvider(dataGenerator, skillsProvider));
  }
}
