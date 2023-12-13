package daripher.skilltree.data.generation;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.init.PSTItems;
import daripher.skilltree.item.gem.SimpleGemItem;
import java.util.Collection;
import java.util.Objects;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class PSTItemModelsProvider extends ItemModelProvider {
  public PSTItemModelsProvider(DataGenerator dataGenerator, ExistingFileHelper existingFileHelper) {
    super(dataGenerator.getPackOutput(), SkillTreeMod.MOD_ID, existingFileHelper);
  }

  @Override
  protected void registerModels() {
    Collection<RegistryObject<Item>> items = PSTItems.REGISTRY.getEntries();
    items.stream().map(RegistryObject::get).forEach(this::basicItem);
    items.stream()
        .map(RegistryObject::get)
        .filter(SimpleGemItem.class::isInstance)
        .forEach(this::apotheosisGem);
  }

  public void apotheosisGem(Item item) {
    ResourceLocation itemId = Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item));
    getBuilder("apotheosis:item/gems/" + itemId.getPath())
        .parent(new ModelFile.UncheckedModelFile("item/generated"))
        .texture("layer0", new ResourceLocation("skilltree", "item/" + itemId.getPath()));
  }
}
