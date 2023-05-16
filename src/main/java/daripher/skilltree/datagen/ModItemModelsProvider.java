package daripher.skilltree.datagen;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.init.SkillTreeItems;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModItemModelsProvider extends ItemModelProvider {
	public ModItemModelsProvider(DataGenerator dataGenerator, ExistingFileHelper existingFileHelper) {
		super(dataGenerator, SkillTreeMod.MOD_ID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		basicItem(SkillTreeItems.SOOTHING_GEMSTONE.get());
		basicItem(SkillTreeItems.STURDY_GEMSTONE.get());
		basicItem(SkillTreeItems.LIGHT_GEMSTONE.get());
		basicItem(SkillTreeItems.VOID_GEMSTONE.get());
		basicItem(SkillTreeItems.RAINBOW_GEMSTONE.get());
		basicItem(SkillTreeItems.WISDOM_SCROLL.get());
	}
}
