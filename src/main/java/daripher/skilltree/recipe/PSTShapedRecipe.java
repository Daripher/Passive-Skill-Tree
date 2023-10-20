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
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;

public class PSTShapedRecipe extends ShapedRecipe implements SkillRequiringRecipe {
  private final ResourceLocation requiredSkillId;

  public PSTShapedRecipe(ShapedRecipe recipe, ResourceLocation requiredSkillId) {
    super(
        recipe.getId(),
        recipe.getGroup(),
        recipe.getWidth(),
        recipe.getHeight(),
        recipe.getIngredients(),
        recipe.getResultItem());
    this.requiredSkillId = requiredSkillId;
  }

  @Override
  public boolean matches(CraftingContainer container, Level level) {
    if (!canCraftIn(container)) return false;
    return super.matches(container, level);
  }

  @Override
  public ItemStack assemble(CraftingContainer container) {
    if (!canCraftIn(container)) return ItemStack.EMPTY;
    return super.assemble(container);
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return PSTRecipeSerializers.SHAPED_CRAFTING.get();
  }

  @Override
  public ResourceLocation getRequiredSkillId() {
    return requiredSkillId;
  }

  public static class Serializer implements RecipeSerializer<PSTShapedRecipe> {
    public PSTShapedRecipe fromJson(ResourceLocation id, JsonObject json) {
      ShapedRecipe recipe = SHAPED_RECIPE.fromJson(id, json);
      var requiredSkillId = new ResourceLocation(GsonHelper.getAsString(json, "required_skill"));
      return new PSTShapedRecipe(recipe, requiredSkillId);
    }

    public PSTShapedRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf byteBuf) {
      ShapedRecipe recipe = SHAPED_RECIPE.fromNetwork(id, byteBuf);
      var requiredSkillId = new ResourceLocation(byteBuf.readUtf());
      return new PSTShapedRecipe(recipe, requiredSkillId);
    }

    public void toNetwork(FriendlyByteBuf byteBuf, PSTShapedRecipe recipe) {
      SHAPED_RECIPE.toNetwork(byteBuf, recipe);
      byteBuf.writeUtf(recipe.requiredSkillId.toString());
    }
  }
}
