package daripher.skilltree.datagen;

import java.util.concurrent.CompletableFuture;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.datagen.translation.PSTEnglishTranslationProvider;
import daripher.skilltree.datagen.translation.PSTRussianTranslationProvider;
import net.minecraft.core.HolderLookup.Provider;
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
		DataGenerator generator = event.getGenerator();
		ExistingFileHelper fileHelper = event.getExistingFileHelper();
		CompletableFuture<Provider> provider = event.getLookupProvider();
		generator.addProvider(event.includeClient(), new PSTEnglishTranslationProvider(generator));
		generator.addProvider(event.includeClient(), new PSTRussianTranslationProvider(generator));
		generator.addProvider(event.includeClient(), new PSTItemModelsProvider(generator, fileHelper));
		var blockTagsProvider = new PSTBlockTagsProvider(generator, provider, fileHelper);
		generator.addProvider(event.includeServer(), blockTagsProvider);
		generator.addProvider(event.includeServer(), new PSTItemTagsProvider(generator, provider, blockTagsProvider, fileHelper));
		generator.addProvider(event.includeServer(), new PSTSkillsProvider(generator));
		generator.addProvider(event.includeServer(), new PSTLootTablesProvider(generator));
		generator.addProvider(event.includeServer(), new PSTRecipesProvider(generator));
	}
}
