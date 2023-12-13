package daripher.skilltree.recipe;

import com.google.gson.JsonObject;
import daripher.skilltree.init.PSTRecipeSerializers;
import daripher.skilltree.mixin.ShapedRecipeAccessor;
import java.util.Objects;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ShapedSkillRequiringRecipe extends ShapedRecipe implements SkillRequiringRecipe {
  public ShapedSkillRequiringRecipe(ShapedRecipe recipe) {
    super(
        recipe.getId(),
        recipe.getGroup(),
        recipe.category(),
        recipe.getWidth(),
        recipe.getHeight(),
        recipe.getIngredients(),
        ((ShapedRecipeAccessor) recipe).getResult());
  }

  @Override
  public boolean matches(@NotNull CraftingContainer container, @NotNull Level level) {
    if (isUncraftable(container, this)) return false;
    return super.matches(container, level);
  }

  @Override
  public @NotNull ItemStack assemble(@NotNull CraftingContainer container, @NotNull RegistryAccess access) {
    if (isUncraftable(container, this)) return ItemStack.EMPTY;
    return super.assemble(container, access);
  }

  @Override
  public @NotNull RecipeSerializer<?> getSerializer() {
    return PSTRecipeSerializers.SHAPED_CRAFTING.get();
  }

  public static class Serializer implements RecipeSerializer<ShapedSkillRequiringRecipe> {
    public @NotNull ShapedSkillRequiringRecipe fromJson(
        @NotNull ResourceLocation id, @NotNull JsonObject json) {
      ShapedRecipe recipe = SHAPED_RECIPE.fromJson(id, json);
      return new ShapedSkillRequiringRecipe(recipe);
    }

    public ShapedSkillRequiringRecipe fromNetwork(
        @NotNull ResourceLocation id, @NotNull FriendlyByteBuf byteBuf) {
      ShapedRecipe recipe = SHAPED_RECIPE.fromNetwork(id, byteBuf);
      return new ShapedSkillRequiringRecipe(Objects.requireNonNull(recipe));
    }

    public void toNetwork(
        @NotNull FriendlyByteBuf byteBuf, @NotNull ShapedSkillRequiringRecipe recipe) {
      SHAPED_RECIPE.toNetwork(byteBuf, recipe);
    }
  }
}
