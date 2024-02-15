package daripher.skilltree.recipe;

import com.google.gson.JsonObject;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.container.ContainerHelper;
import daripher.skilltree.init.PSTItems;
import daripher.skilltree.init.PSTRecipeSerializers;
import daripher.skilltree.item.gem.GemItem;
import java.util.Objects;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.UpgradeRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class GemInsertionRecipe extends UpgradeRecipe {
  public GemInsertionRecipe() {
    super(
        new ResourceLocation(SkillTreeMod.MOD_ID, "gem_insertion"),
        Ingredient.EMPTY,
        Ingredient.EMPTY,
        ItemStack.EMPTY);
  }

  @Override
  public boolean matches(@NotNull Container container, @NotNull Level level) {
    if (SkillTreeMod.apotheosisEnabled()) return false;
    return canCraftIn(container);
  }

  @Override
  public @NotNull ItemStack assemble(@NotNull Container container) {
    if (SkillTreeMod.apotheosisEnabled() || !canCraftIn(container)) return ItemStack.EMPTY;
    ItemStack base = container.getItem(0);
    ItemStack ingredient = container.getItem(1);
    ItemStack result = base.copy();
    result.setCount(1);
    Player player = ContainerHelper.getViewingPlayer(container);
    Objects.requireNonNull(player);
    GemItem.insertGem(player, result, ingredient);
    return result;
  }

  private boolean canCraftIn(@NotNull Container container) {
    Player player = ContainerHelper.getViewingPlayer(container);
    if (player == null) return false;
    ItemStack base = container.getItem(0);
    ItemStack ingredient = container.getItem(1);
    if (ingredient.getItem() != PSTItems.GEM.get()) return false;
    return GemItem.canInsertGem(player, base, ingredient);
  }

  @Override
  public @NotNull ItemStack getResultItem() {
    return ItemStack.EMPTY;
  }

  @Override
  public boolean isSpecial() {
    return true;
  }

  @Override
  public @NotNull RecipeSerializer<?> getSerializer() {
    return PSTRecipeSerializers.GEM_INSERTION.get();
  }

  @Override
  public boolean isIncomplete() {
    return false;
  }

  public static class Serializer implements RecipeSerializer<GemInsertionRecipe> {
    @Override
    public @NotNull GemInsertionRecipe fromJson(
        @NotNull ResourceLocation id, @NotNull JsonObject jsonObject) {
      return new GemInsertionRecipe();
    }

    @Override
    public GemInsertionRecipe fromNetwork(
        @NotNull ResourceLocation id, @NotNull FriendlyByteBuf buf) {
      return new GemInsertionRecipe();
    }

    @Override
    public void toNetwork(@NotNull FriendlyByteBuf buf, @NotNull GemInsertionRecipe recipe) {}
  }
}
