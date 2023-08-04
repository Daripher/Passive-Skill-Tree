package daripher.skilltree.datagen;

import java.util.concurrent.CompletableFuture;

import daripher.skilltree.SkillTreeMod;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class PSTBlockTagsProvider extends BlockTagsProvider {
	public PSTBlockTagsProvider(DataGenerator generator, CompletableFuture<Provider> provider, ExistingFileHelper fileHelper) {
		super(generator.getPackOutput(), provider, SkillTreeMod.MOD_ID, fileHelper);
	}

	@Override
	protected void addTags(Provider provider) {
	}
}
