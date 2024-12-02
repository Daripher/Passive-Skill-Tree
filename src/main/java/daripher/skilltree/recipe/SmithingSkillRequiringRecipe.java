package daripher.skilltree.recipe;

import com.google.gson.JsonObject;
import daripher.skilltree.init.PSTRecipeSerializers;
import daripher.skilltree.mixin.SmithingTransformRecipeAccessor;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SmithingSkillRequiringRecipe extends SmithingTransformRecipe implements SkillRequiringRecipe {
  public SmithingSkillRequiringRecipe(SmithingTransformRecipe recipe) {
    super(recipe.getId(),
          ((SmithingTransformRecipeAccessor) recipe).getTemplate(),
          ((SmithingTransformRecipeAccessor) recipe).getBase(),
          ((SmithingTransformRecipeAccessor) recipe).getAddition(),
          ((SmithingTransformRecipeAccessor) recipe).getResult());
  }

  @Override
  public boolean matches(@NotNull Container container, @NotNull Level level) {
    if (!canUseRecipe(container, this)) return false;
    return super.matches(container, level);
  }

  @Override
  public @NotNull ItemStack assemble(@NotNull Container container, @NotNull RegistryAccess access) {
    if (!canUseRecipe(container, this)) return ItemStack.EMPTY;
    return super.assemble(container, access);
  }

  @Override
  public boolean isBaseIngredient(ItemStack pStack) {
    if ( pStack.getItem() == Items.ENCHANTED_BOOK) {
      //
    }
    return super.isBaseIngredient(pStack);
  }

  @Override
  public @NotNull RecipeSerializer<?> getSerializer() {
    return PSTRecipeSerializers.SMITHING_TRANSFORM.get();
  }

  public static class Serializer implements RecipeSerializer<SmithingSkillRequiringRecipe> {
    @Override
    public @NotNull SmithingSkillRequiringRecipe fromJson(@NotNull ResourceLocation id, @NotNull JsonObject json) {
      SmithingTransformRecipe recipe = SMITHING_TRANSFORM.fromJson(id, json);
      return new SmithingSkillRequiringRecipe(recipe);
    }

    @Override
    public SmithingSkillRequiringRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf byteBuf) {
      SmithingTransformRecipe recipe = SMITHING_TRANSFORM.fromNetwork(id, byteBuf);
      return new SmithingSkillRequiringRecipe(Objects.requireNonNull(recipe));
    }

    @Override
    public void toNetwork(@NotNull FriendlyByteBuf byteBuf, @NotNull SmithingSkillRequiringRecipe recipe) {
      SMITHING_TRANSFORM.toNetwork(byteBuf, recipe);
    }
  }
}
