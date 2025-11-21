package daripher.skilltree.recipe.workbench;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import daripher.skilltree.init.PSTRecipeSerializers;
import daripher.skilltree.inventory.menu.WorkbenchContainer;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.CraftingHelper;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WorkbenchCraftingRecipe extends AbstractWorkbenchRecipe {
  private final @Nullable Pair<Ingredient, Integer> baseIngredient;
  private final ItemStack result;

  public WorkbenchCraftingRecipe(
      ResourceLocation id,
      @Nullable Pair<Ingredient, Integer> baseIngredient,
      Map<Ingredient, Integer> ingredients,
      boolean requiresPassiveSkill,
      ItemStack result) {
    super(id, ingredients, requiresPassiveSkill);
    this.result = result;
    this.baseIngredient = baseIngredient;
  }

  @Override
  public @NotNull ItemStack assemble(
      @NotNull WorkbenchContainer container, @NotNull RegistryAccess registryAccess) {
    return getResult(container);
  }

  @Override
  public boolean isValidBaseItem(ItemStack itemStack) {
    if (baseIngredient == null) {
      return itemStack.isEmpty();
    }
    return baseIngredient.getLeft().test(itemStack)
        && itemStack.getCount() >= baseIngredient.getRight();
  }

  @Override
  public Component getShortDescription() {
    return result.getHoverName();
  }

  @Override
  public @NotNull ItemStack getResult(WorkbenchContainer workbenchContainer) {
    return result.copy();
  }

  @Override
  public int requiredBaseItemAmount() {
    return baseIngredient == null ? 0 : baseIngredient.getRight();
  }

  @Override
  public @NotNull RecipeSerializer<?> getSerializer() {
    return PSTRecipeSerializers.WORKBENCH_CRAFTING.get();
  }

  public static class Serializer implements RecipeSerializer<WorkbenchCraftingRecipe> {
    @Override
    public @NotNull WorkbenchCraftingRecipe fromJson(
        @NotNull ResourceLocation id, @NotNull JsonObject jsonObject) {
      boolean requiresPassiveSkill = jsonObject.get("requires_passive_skill").getAsBoolean();
      Map<Ingredient, Integer> ingredients = new HashMap<>();
      JsonArray ingredientsJson = jsonObject.getAsJsonArray("ingredients");
      for (JsonElement jsonElement : ingredientsJson) {
        JsonObject ingredientJson = jsonElement.getAsJsonObject();
        Ingredient ingredient = Ingredient.fromJson(ingredientJson.get("ingredient"));
        int requiredAmount = ingredientJson.get("required_amount").getAsInt();
        ingredients.put(ingredient, requiredAmount);
      }
      Pair<Ingredient, Integer> baseIngredient = null;
      if (jsonObject.has("base_ingredient")) {
        JsonObject baseIngredientJson = jsonObject.get("base_ingredient").getAsJsonObject();
        Ingredient ingredient = Ingredient.fromJson(baseIngredientJson.get("ingredient"));
        int requiredAmount = baseIngredientJson.get("required_amount").getAsInt();
        baseIngredient = Pair.of(ingredient, requiredAmount);
      }
      JsonObject resultJson = jsonObject.getAsJsonObject("result");
      ItemStack result = CraftingHelper.getItemStack(resultJson, true, true);
      return new WorkbenchCraftingRecipe(
          id, baseIngredient, ingredients, requiresPassiveSkill, result);
    }

    @Override
    public @Nullable WorkbenchCraftingRecipe fromNetwork(
        @NotNull ResourceLocation id, @NotNull FriendlyByteBuf buf) {
      boolean requiresPassiveSkill = buf.readBoolean();
      Map<Ingredient, Integer> ingredients = new HashMap<>();
      int ingredientsCount = buf.readInt();
      for (int i = 0; i < ingredientsCount; i++) {
        ingredients.put(Ingredient.fromNetwork(buf), buf.readInt());
      }
      Pair<Ingredient, Integer> baseIngredient = null;
      boolean hasBaseIngredient = buf.readBoolean();
      if (hasBaseIngredient) {
        baseIngredient = Pair.of(Ingredient.fromNetwork(buf), buf.readInt());
      }
      ItemStack result = buf.readItem();
      return new WorkbenchCraftingRecipe(
          id, baseIngredient, ingredients, requiresPassiveSkill, result);
    }

    @Override
    public void toNetwork(@NotNull FriendlyByteBuf buf, @NotNull WorkbenchCraftingRecipe recipe) {
      buf.writeBoolean(recipe.requiresPassiveSkill());
      int ingredientsCount = recipe.getAdditionalIngredients().size();
      buf.writeInt(ingredientsCount);
      recipe
          .getAdditionalIngredients()
          .forEach(
              (ingredient, requiredAmount) -> {
                ingredient.toNetwork(buf);
                buf.writeInt(requiredAmount);
              });
      buf.writeBoolean(recipe.baseIngredient != null);
      if (recipe.baseIngredient != null) {
        recipe.baseIngredient.getLeft().toNetwork(buf);
        buf.writeInt(recipe.baseIngredient.getRight());
      }
      buf.writeItem(recipe.result);
    }
  }
}
