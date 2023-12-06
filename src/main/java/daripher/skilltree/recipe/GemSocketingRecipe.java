package daripher.skilltree.recipe;

import com.google.gson.JsonObject;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.compat.apotheosis.ApotheosisCompatibility;
import daripher.skilltree.container.ContainerHelper;
import daripher.skilltree.init.PSTRecipeSerializers;
import daripher.skilltree.item.ItemHelper;
import daripher.skilltree.item.gem.GemHelper;
import daripher.skilltree.item.gem.GemItem;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.UpgradeRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.NotNull;

public class GemSocketingRecipe extends UpgradeRecipe {
  public GemSocketingRecipe() {
    super(
        new ResourceLocation(SkillTreeMod.MOD_ID, "gem_insertion"),
        Ingredient.EMPTY,
        Ingredient.EMPTY,
        ItemStack.EMPTY);
  }

  @Override
  public boolean matches(@NotNull Container container, @NotNull Level level) {
    if (ModList.get().isLoaded("apotheosis")) {
      if (ApotheosisCompatibility.INSTANCE.adventureModuleEnabled()) return false;
    }
    return canCraftIn(container);
  }

  @Override
  public @NotNull ItemStack assemble(@NotNull Container container) {
    if (ModList.get().isLoaded("apotheosis")) {
      if (ApotheosisCompatibility.INSTANCE.adventureModuleEnabled()) return ItemStack.EMPTY;
    }
    if (!canCraftIn(container)) return ItemStack.EMPTY;
    ItemStack base = container.getItem(0);
    ItemStack ingredient = container.getItem(1);
    ItemStack result = base.copy();
    GemItem gem = (GemItem) ingredient.getItem();
    result.setCount(1);
    CompoundTag itemTag = base.getTag();
    Optional<Player> player = ContainerHelper.getViewingPlayer(container);
    if (itemTag != null) result.setTag(itemTag.copy());
    assert player.isPresent();
    float gemPower = GemHelper.getGemPower(player.get(), result);
    int socket = getEmptySocket(base, player.get());
    gem.insertInto(player.get(), result, ingredient, socket, gemPower);
    return result;
  }

  private boolean canCraftIn(@NotNull Container container) {
    Optional<Player> player = ContainerHelper.getViewingPlayer(container);
    if (player.isEmpty()) return false;
    ItemStack base = container.getItem(0);
    ItemStack ingredient = container.getItem(1);
    if (!ItemHelper.canInsertGem(base)) return false;
    if (!(ingredient.getItem() instanceof GemItem gem)) return false;
    int socket = getEmptySocket(base, player.get());
    return gem.canInsertInto(player.get(), base, ingredient, socket);
  }

  public int getEmptySocket(ItemStack baseItem, Player player) {
    int sockets = GemHelper.getMaximumSockets(baseItem, player);
    int socket = 0;
    for (int i = 0; i < sockets; i++) {
      socket = i;
      if (!GemHelper.hasGem(baseItem, socket)) break;
    }
    return socket;
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
