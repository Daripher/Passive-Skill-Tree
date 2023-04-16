package daripher.skilltree.datagen;

import daripher.skilltree.SkillTreeMod;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModBlockTagsProvider extends BlockTagsProvider {
	public ModBlockTagsProvider(DataGenerator dataGenerator, ExistingFileHelper existingFileHelper) {
		super(dataGenerator, SkillTreeMod.MOD_ID, existingFileHelper);
	}

	@Override
	protected void addTags() {

	}
}
