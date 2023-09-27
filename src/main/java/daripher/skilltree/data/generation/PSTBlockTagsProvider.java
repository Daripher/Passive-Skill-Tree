package daripher.skilltree.data.generation;

import daripher.skilltree.SkillTreeMod;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class PSTBlockTagsProvider extends BlockTagsProvider {
	public PSTBlockTagsProvider(DataGenerator dataGenerator, ExistingFileHelper existingFileHelper) {
		super(dataGenerator, SkillTreeMod.MOD_ID, existingFileHelper);
	}

	@Override
	protected void addTags() {
	}
}
