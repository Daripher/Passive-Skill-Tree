package daripher.skilltree.recipe.builder;

import daripher.skilltree.recipe.workbench.WorkbenchCraftingRecipe;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WorkbenchCraftingRecipeBuilder {
    private final ResourceLocation id;
    private final Map<Ingredient, Integer> ingredients = new HashMap<>();
    private @Nullable Pair<Ingredient, Integer> baseIngredient;
    private boolean requiresPassiveSkill;
    private ItemStack result;

    private WorkbenchCraftingRecipeBuilder(ResourceLocation id) {
        this.id = id;
    }

    public static WorkbenchCraftingRecipeBuilder create(ResourceLocation id) {
        return new WorkbenchCraftingRecipeBuilder(id);
    }

    public WorkbenchCraftingRecipeBuilder setBaseIngredient(
            Ingredient ingredient, int requiredAmount) {
        this.baseIngredient = Pair.of(ingredient, requiredAmount);
        return this;
    }

    public WorkbenchCraftingRecipeBuilder addIngredients(
            Ingredient ingredient, int requiredAmount) {
        this.ingredients.put(ingredient, requiredAmount);
        return this;
    }

    public WorkbenchCraftingRecipeBuilder setRequiresPassiveSkill() {
        this.requiresPassiveSkill = true;
        return this;
    }

    public WorkbenchCraftingRecipeBuilder setResult(@NotNull ItemStack result) {
        this.result = result;
        return this;
    }

    public void save(RecipeOutput recipeOutput) {
        validate();
        recipeOutput.accept(
                id,
                new WorkbenchCraftingRecipe(
                        id,
                        baseIngredient,
                        Map.copyOf(ingredients),
                        requiresPassiveSkill,
                        result.copy()),
                null);
    }

    private void validate() {
        if (ingredients.isEmpty()) {
            throw new IllegalStateException("No ingredients set for recipe " + id);
        }
        if (ingredients.size() > 9) {
            throw new IllegalStateException("Too many ingredients set for recipe " + id);
        }
        if (result == null) {
            throw new IllegalStateException("No result item set for recipe " + id);
        }
    }
}
