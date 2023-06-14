package daripher.skilltree.datagen;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.init.SkillTreeItems;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class ModItemModelsProvider extends ItemModelProvider {
	public ModItemModelsProvider(DataGenerator dataGenerator, ExistingFileHelper existingFileHelper) {
		super(dataGenerator, SkillTreeMod.MOD_ID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		SkillTreeItems.REGISTRY.getEntries().stream().map(RegistryObject::get).forEach(this::basicItem);
	}
}
