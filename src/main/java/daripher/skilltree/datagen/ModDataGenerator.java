package daripher.skilltree.datagen;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.datagen.translation.*;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID, bus = Bus.MOD)
public class ModDataGenerator {
	@SubscribeEvent
	public static void onGatherData(GatherDataEvent event) {
		var dataGenerator = event.getGenerator();
		var existingFileHelper = event.getExistingFileHelper();
		dataGenerator.addProvider(event.includeClient(), new ModEnglishTranslationProvider(dataGenerator));
		dataGenerator.addProvider(event.includeClient(), new ModRussianTranslationProvider(dataGenerator));
		dataGenerator.addProvider(event.includeClient(), new ModItemModelsProvider(dataGenerator, existingFileHelper));
		var blockTagsProvider = new ModBlockTagsProvider(dataGenerator, existingFileHelper);
		dataGenerator.addProvider(event.includeServer(), blockTagsProvider);
		dataGenerator.addProvider(event.includeServer(), new ModItemTagsProvider(dataGenerator, blockTagsProvider, existingFileHelper));
		dataGenerator.addProvider(event.includeServer(), new ModSkillsProvider(dataGenerator));
	}
}
