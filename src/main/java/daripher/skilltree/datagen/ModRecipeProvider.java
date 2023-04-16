package daripher.skilltree.datagen;

import java.util.function.Consumer;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;

public class ModRecipeProvider extends RecipeProvider {
	public ModRecipeProvider(DataGenerator dataGenerator) {
		super(dataGenerator);
	}

	@Override
	protected void buildCraftingRecipes(Consumer<FinishedRecipe> recipesConsumer) {
	}
}
