package daripher.skilltree.recipe;

import com.google.gson.JsonObject;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.compat.apotheosis.ApotheosisCompatibility;
import daripher.skilltree.data.reloader.GemTypesReloader;
import daripher.skilltree.init.PSTRecipeSerializers;
import daripher.skilltree.item.gem.GemItem;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class GemUpgradeRecipe implements CraftingRecipe {
  private final ResourceLocation ingredient;
  private final ResourceLocation result;
  private final ResourceLocation id;

  public GemUpgradeRecipe(
      ResourceLocation id, ResourceLocation ingredient, ResourceLocation result) {
    this.id = id;
    this.ingredient = ingredient;
    this.result = result;
  }

  @Override
  public @NotNull ResourceLocation getId() {
    return id;
  }

  @Override
  public @NotNull String getGroup() {
    return "gem_upgrades";
  }

  @Override
  public @NotNull ItemStack getResultItem() {
    if (SkillTreeMod.apotheosisEnabled()) {
      return ApotheosisCompatibility.INSTANCE.getGemStack(result);
    }
    return GemItem.getDefaultGemStack(GemTypesReloader.getGemTypeById(result));
  }

  @Override
  public @NotNull NonNullList<Ingredient> getIngredients() {
    Ingredient ingredient = getIngredient();
    return NonNullList.withSize(9, ingredient);
  }

  private Ingredient getIngredient() {
    ItemStack gemStack;
    if (SkillTreeMod.apotheosisEnabled()) {
      gemStack = ApotheosisCompatibility.INSTANCE.getGemStack(this.ingredient);
    } else {
      gemStack = GemItem.getDefaultGemStack(GemTypesReloader.getGemTypeById(this.ingredient));
    }
    return Ingredient.of(gemStack);
  }

  @Override
  public boolean matches(CraftingContainer container, @NotNull Level level) {
    int gemsCount = 0;
    for (int j = 0; j < container.getContainerSize(); j++) {
      ItemStack stack = container.getItem(j);
      if (!isIngredient(stack)) return false;
      gemsCount++;
    }
    return gemsCount == 9;
  }

  private boolean isIngredient(ItemStack stack) {
    if (SkillTreeMod.apotheosisEnabled()) {
      return ingredient.equals(ApotheosisCompatibility.INSTANCE.getGemId(stack));
    }
    return GemTypesReloader.getGemTypeById(ingredient) == GemItem.getGemType(stack);
  }

  @Override
  public @NotNull ItemStack assemble(@NotNull CraftingContainer container) {
    return getResultItem().copy();
  }

  @Override
  public boolean canCraftInDimensions(int width, int height) {
    return width * height >= 9;
  }

  @Override
  public @NotNull RecipeSerializer<?> getSerializer() {
    return PSTRecipeSerializers.GEM_UPGRADE.get();
  }

  public static class Serializer implements RecipeSerializer<GemUpgradeRecipe> {
    @Override
    public @NotNull GemUpgradeRecipe fromJson(
        @NotNull ResourceLocation id, @NotNull JsonObject json) {
      ResourceLocation ingredient = new ResourceLocation(json.get("ingredient").getAsString());
      ResourceLocation result = new ResourceLocation(json.get("result").getAsString());
      return new GemUpgradeRecipe(id, ingredient, result);
    }

    @Override
    public GemUpgradeRecipe fromNetwork(
        @NotNull ResourceLocation id, @NotNull FriendlyByteBuf buf) {
      ResourceLocation ingredient = new ResourceLocation(buf.readUtf());
      ResourceLocation result = new ResourceLocation(buf.readUtf());
      return new GemUpgradeRecipe(id, ingredient, result);
    }

    @Override
    public void toNetwork(@NotNull FriendlyByteBuf buf, @NotNull GemUpgradeRecipe recipe) {
      buf.writeUtf(recipe.ingredient.toString());
      buf.writeUtf(recipe.result.toString());
    }
  }
}
