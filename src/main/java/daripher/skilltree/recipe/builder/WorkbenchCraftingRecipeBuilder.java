package daripher.skilltree.recipe.builder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import daripher.skilltree.init.PSTRecipeSerializers;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

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

    public WorkbenchCraftingRecipeBuilder setBaseIngredient(Ingredient ingredient, int requiredAmount) {
        this.baseIngredient = Pair.of(ingredient, requiredAmount);
        return this;
    }

    public WorkbenchCraftingRecipeBuilder addIngredients(Ingredient ingredient, int requiredAmount) {
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

    public void save(Consumer<FinishedRecipe> finishedRecipeConsumer) {
        validate();
        finishedRecipeConsumer.accept(new Result(id, baseIngredient, ingredients, requiresPassiveSkill, result));
    }

    private void validate() {
        if (ingredients.isEmpty()) {
            throw new IllegalStateException("No ingredients set for recipe " + id);
        }
        if (ingredients.size() > 6) {
            throw new IllegalStateException("Too many ingredients set for recipe " + id);
        }
        if (result == null) {
            throw new IllegalStateException("No result item set for recipe " + id);
        }
    }

    private static class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final Map<Ingredient, Integer> ingredients;
        private final @Nullable Pair<Ingredient, Integer> baseIngredient;
        private final boolean requiresPassiveSkill;
        private final ItemStack result;

        private Result(ResourceLocation id, @Nullable Pair<Ingredient, Integer> baseIngredient, Map<Ingredient, Integer> ingredients, boolean requiresPassiveSkill, ItemStack result) {
            this.id = id;
            this.baseIngredient = baseIngredient;
            this.ingredients = ingredients;
            this.requiresPassiveSkill = requiresPassiveSkill;
            this.result = result;
        }

        @Override
        public void serializeRecipeData(@NotNull JsonObject jsonObject) {
            JsonArray ingredientsJson = new JsonArray();
            ingredients.forEach(((ingredient, requiredAmount) -> {
                JsonObject ingredientJson = new JsonObject();
                ingredientJson.add("ingredient", ingredient.toJson());
                ingredientJson.addProperty("required_amount", requiredAmount);
                ingredientsJson.add(ingredientJson);
            }));
            jsonObject.addProperty("requires_passive_skill", requiresPassiveSkill);
            jsonObject.add("ingredients", ingredientsJson);
            if (baseIngredient != null) {
                JsonObject baseIngredientJson = new JsonObject();
                baseIngredientJson.add("ingredient", baseIngredient.getLeft().toJson());
                baseIngredientJson.addProperty("required_amount", baseIngredient.getRight());
                jsonObject.add("base_ingredient", baseIngredientJson);
            }
            JsonObject resultJson = new JsonObject();
            Item resultItem = this.result.getItem();
            ResourceLocation itemId = Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(resultItem));
            resultJson.addProperty("item", itemId.toString());
            if (result.getCount() > 1) {
                resultJson.addProperty("count", result.getCount());
            }
            if (result.getTag() != null) {
                resultJson.addProperty("nbt", result.getTag().toString());
            }
            jsonObject.add("result", resultJson);
        }

        @Override
        public @NotNull ResourceLocation getId() {
            return id;
        }

        @Override
        public @NotNull RecipeSerializer<?> getType() {
            return PSTRecipeSerializers.WORKBENCH_CRAFTING.get();
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            return null;
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return null;
        }
    }
}
