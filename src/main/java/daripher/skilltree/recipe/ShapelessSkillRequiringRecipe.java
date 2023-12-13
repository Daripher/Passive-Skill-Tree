package daripher.skilltree.recipe;

import com.google.gson.JsonObject;
import daripher.skilltree.init.PSTRecipeSerializers;
import daripher.skilltree.mixin.ShapelessRecipeAccessor;
import java.util.Objects;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ShapelessSkillRequiringRecipe extends ShapelessRecipe implements SkillRequiringRecipe {
  public ShapelessSkillRequiringRecipe(ShapelessRecipe recipe) {
    super(
        recipe.getId(),
        recipe.getGroup(),
        recipe.category(),
        ((ShapelessRecipeAccessor) recipe).getResult(),
        recipe.getIngredients());
  }

  @Override
  public boolean matches(@NotNull CraftingContainer container, @NotNull Level level) {
    if (isUncraftable(container, this)) return false;
    return super.matches(container, level);
  }

  @Override
  public @NotNull ItemStack assemble(
      @NotNull CraftingContainer container, @NotNull RegistryAccess access) {
    if (isUncraftable(container, this)) return ItemStack.EMPTY;
    return super.assemble(container, access);
  }

  @Override
  public @NotNull RecipeSerializer<?> getSerializer() {
    return PSTRecipeSerializers.SHAPELESS_CRAFTING.get();
  }

  public static class Serializer implements RecipeSerializer<ShapelessSkillRequiringRecipe> {
    public @NotNull ShapelessSkillRequiringRecipe fromJson(
        @NotNull ResourceLocation id, @NotNull JsonObject json) {
      ShapelessRecipe recipe = SHAPELESS_RECIPE.fromJson(id, json);
      return new ShapelessSkillRequiringRecipe(recipe);
    }

    public ShapelessSkillRequiringRecipe fromNetwork(
        @NotNull ResourceLocation id, @NotNull FriendlyByteBuf byteBuf) {
      ShapelessRecipe recipe = SHAPELESS_RECIPE.fromNetwork(id, byteBuf);
      return new ShapelessSkillRequiringRecipe(Objects.requireNonNull(recipe));
    }

    public void toNetwork(
        @NotNull FriendlyByteBuf byteBuf, @NotNull ShapelessSkillRequiringRecipe recipe) {
      SHAPELESS_RECIPE.toNetwork(byteBuf, recipe);
    }
  }
}
