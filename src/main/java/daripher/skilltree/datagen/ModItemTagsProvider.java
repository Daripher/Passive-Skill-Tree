package daripher.skilltree.datagen;

import org.jetbrains.annotations.Nullable;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.init.SkillTreeTags;
import daripher.skilltree.item.gem.GemItem;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItemTagsProvider extends ItemTagsProvider {
	public ModItemTagsProvider(DataGenerator dataGenerator, BlockTagsProvider blockTagsProvider, @Nullable ExistingFileHelper existingFileHelper) {
		super(dataGenerator, blockTagsProvider, SkillTreeMod.MOD_ID, existingFileHelper);
	}

	@Override
	protected void addTags() {
		var gemstonesTag = tag(SkillTreeTags.GEMSTONES);
		ForgeRegistries.ITEMS.getValues().stream().filter(GemItem.class::isInstance).forEach(gemstonesTag::add);
	}
}
