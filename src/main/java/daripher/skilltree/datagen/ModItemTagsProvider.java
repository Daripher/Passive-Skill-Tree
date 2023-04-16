package daripher.skilltree.datagen;

import org.jetbrains.annotations.Nullable;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.init.SkillTreeTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModItemTagsProvider extends ItemTagsProvider {
	public ModItemTagsProvider(DataGenerator dataGenerator, BlockTagsProvider blockTagsProvider, @Nullable ExistingFileHelper existingFileHelper) {
		super(dataGenerator, blockTagsProvider, SkillTreeMod.MOD_ID, existingFileHelper);
	}

	@Override
	protected void addTags() {
		tag(SkillTreeTags.MINERALS).add(Items.DIAMOND, Items.EMERALD, Items.COAL, Items.RAW_COPPER, Items.RAW_GOLD, Items.RAW_IRON, Items.LAPIS_LAZULI, Items.QUARTZ);
	}
}
