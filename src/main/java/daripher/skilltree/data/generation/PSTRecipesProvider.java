package daripher.skilltree.data.generation;

import java.util.function.Consumer;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import org.jetbrains.annotations.NotNull;

public class PSTRecipesProvider extends RecipeProvider {
  public PSTRecipesProvider(DataGenerator dataGenerator) {
    super(dataGenerator.getPackOutput());
  }

  @Override
  protected void buildRecipes(@NotNull Consumer<FinishedRecipe> consumer) {

  }
}
