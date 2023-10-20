package daripher.skilltree.recipe;

import com.google.gson.JsonObject;
import daripher.skilltree.api.SkillRequiringRecipe;
import daripher.skilltree.init.PSTRecipeSerializers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class PSTShapelessRecipe extends ShapelessRecipe implements SkillRequiringRecipe {
  private final ResourceLocation requiredSkillId;

  public PSTShapelessRecipe(ShapelessRecipe recipe, ResourceLocation requiredSkillId) {
    super(recipe.getId(), recipe.getGroup(), recipe.getResultItem(), recipe.getIngredients());
    this.requiredSkillId = requiredSkillId;
  }

  @Override
  public boolean matches(@NotNull CraftingContainer container, @NotNull Level level) {
    if (!canCraftIn(container)) return false;
    return super.matches(container, level);
  }

  @Override
  public @NotNull ItemStack assemble(@NotNull CraftingContainer container) {
    if (!canCraftIn(container)) return ItemStack.EMPTY;
    return super.assemble(container);
  }

  @Override
  public @NotNull RecipeSerializer<?> getSerializer() {
    return PSTRecipeSerializers.SHAPELESS_CRAFTING.get();
  }

  @Override
  public ResourceLocation getRequiredSkillId() {
    return requiredSkillId;
  }

  public static class Serializer implements RecipeSerializer<PSTShapelessRecipe> {
    public @NotNull PSTShapelessRecipe fromJson(
        @NotNull ResourceLocation id, @NotNull JsonObject json) {
      ShapelessRecipe recipe = SHAPELESS_RECIPE.fromJson(id, json);
      var requiredSkillId = new ResourceLocation(GsonHelper.getAsString(json, "required_skill"));
      return new PSTShapelessRecipe(recipe, requiredSkillId);
    }

    public PSTShapelessRecipe fromNetwork(
        @NotNull ResourceLocation id, @NotNull FriendlyByteBuf byteBuf) {
      ShapelessRecipe recipe = SHAPELESS_RECIPE.fromNetwork(id, byteBuf);
      var requiredSkillId = new ResourceLocation(byteBuf.readUtf());
      return new PSTShapelessRecipe(recipe, requiredSkillId);
    }

    public void toNetwork(@NotNull FriendlyByteBuf byteBuf, @NotNull PSTShapelessRecipe recipe) {
      SHAPELESS_RECIPE.toNetwork(byteBuf, recipe);
      byteBuf.writeUtf(recipe.requiredSkillId.toString());
    }
  }
}
