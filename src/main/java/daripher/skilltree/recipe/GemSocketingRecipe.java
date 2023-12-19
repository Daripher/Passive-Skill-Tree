package daripher.skilltree.recipe;

import com.google.gson.JsonObject;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.container.ContainerHelper;
import daripher.skilltree.init.PSTItems;
import daripher.skilltree.init.PSTRecipeSerializers;
import daripher.skilltree.item.ItemHelper;
import daripher.skilltree.item.gem.GemBonusHandler;
import daripher.skilltree.item.gem.GemItem;
import java.util.Optional;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class GemSocketingRecipe extends SmithingTransformRecipe {
  public GemSocketingRecipe() {
    super(
        new ResourceLocation(SkillTreeMod.MOD_ID, "gem_insertion"),
        Ingredient.EMPTY,
        Ingredient.EMPTY,
        Ingredient.of(PSTItems.getItems(GemItem.class).toArray(new GemItem[0])),
        ItemStack.EMPTY);
  }

  @Override
  public boolean matches(@NotNull Container container, @NotNull Level level) {
    if (SkillTreeMod.apotheosisEnabled()) return false;
    return canCraftIn(container);
  }

  @Override
  public @NotNull ItemStack assemble(@NotNull Container container, @NotNull RegistryAccess access) {
    if (SkillTreeMod.apotheosisEnabled() || !canCraftIn(container)) return ItemStack.EMPTY;
    ItemStack base = container.getItem(1);
    ItemStack ingredient = container.getItem(2);
    ItemStack result = base.copy();
    result.setCount(1);
    GemItem gem = (GemItem) ingredient.getItem();
    Optional<Player> player = ContainerHelper.getViewingPlayer(container);
    assert player.isPresent();
    int socket = GemBonusHandler.getFirstEmptySocket(base, player.get());
    gem.insertInto(player.get(), result, ingredient, socket);
    return result;
  }

  private boolean canCraftIn(@NotNull Container container) {
    Optional<Player> player = ContainerHelper.getViewingPlayer(container);
    if (player.isEmpty()) return false;
    ItemStack base = container.getItem(1);
    ItemStack ingredient = container.getItem(2);
    if (!ItemHelper.canInsertGem(base)) return false;
    if (!(ingredient.getItem() instanceof GemItem gem)) return false;
    int socket = GemBonusHandler.getFirstEmptySocket(base, player.get());
    return gem.canInsertInto(player.get(), base, ingredient, socket);
  }

  @Override
  public @NotNull ItemStack getResultItem(@NotNull RegistryAccess access) {
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

  public static class Serializer implements RecipeSerializer<GemSocketingRecipe> {
    @Override
    public @NotNull GemSocketingRecipe fromJson(
        @NotNull ResourceLocation id, @NotNull JsonObject jsonObject) {
      return new GemSocketingRecipe();
    }

    @Override
    public GemSocketingRecipe fromNetwork(
        @NotNull ResourceLocation id, @NotNull FriendlyByteBuf buf) {
      return new GemSocketingRecipe();
    }

    @Override
    public void toNetwork(@NotNull FriendlyByteBuf buf, @NotNull GemSocketingRecipe recipe) {}
  }
}
