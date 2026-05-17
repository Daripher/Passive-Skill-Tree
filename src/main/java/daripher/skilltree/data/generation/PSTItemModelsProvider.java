package daripher.skilltree.data.generation;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.init.PSTItems;
import net.minecraft.data.DataGenerator;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class PSTItemModelsProvider extends ItemModelProvider {
  public PSTItemModelsProvider(DataGenerator dataGenerator, ExistingFileHelper existingFileHelper) {
    super(dataGenerator.getPackOutput(), SkillTreeMod.MOD_ID, existingFileHelper);
  }

  @Override
  protected void registerModels() {
    basicItem(PSTItems.AMNESIA_SCROLL.get());
    basicItem(PSTItems.WISDOM_SCROLL.get());
  }
}
