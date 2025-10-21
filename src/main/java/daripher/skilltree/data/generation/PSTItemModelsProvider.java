package daripher.skilltree.data.generation;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.init.PSTItems;
import java.util.Collection;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class PSTItemModelsProvider extends ItemModelProvider {
  public PSTItemModelsProvider(DataGenerator dataGenerator, ExistingFileHelper existingFileHelper) {
    super(dataGenerator.getPackOutput(), SkillTreeMod.MOD_ID, existingFileHelper);
  }

  @Override
  protected void registerModels() {
    Collection<RegistryObject<Item>> items = PSTItems.REGISTRY.getEntries();
    items.stream().map(RegistryObject::get).forEach(this::basicItem);
  }
}
