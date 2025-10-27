package daripher.skilltree.data.generation;

import daripher.skilltree.init.PSTItems;
import java.util.function.Consumer;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;

public class PSTRecipesProvider extends RecipeProvider {
  public PSTRecipesProvider(DataGenerator dataGenerator) {
    super(dataGenerator.getPackOutput());
  }

  @Override
  protected void buildRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
    workbenchRecipe(consumer);
  }

  private static void workbenchRecipe(@NotNull Consumer<FinishedRecipe> consumer) {
    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PSTItems.WORKBENCH.get())
        .define('I', Tags.Items.INGOTS_IRON)
        .define('G', Tags.Items.INGOTS_GOLD)
        .define('C', Tags.Items.INGOTS_COPPER)
        .define('#', Items.SMITHING_TABLE)
        .pattern("III")
        .pattern("G#G")
        .pattern("CCC")
        .unlockedBy("has_smithing_table", has(Items.SMITHING_TABLE))
        .save(consumer);
  }
}
