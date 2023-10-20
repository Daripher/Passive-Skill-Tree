package daripher.skilltree.recipe;

import com.google.gson.JsonObject;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.compat.apotheosis.ApotheosisCompatibility;
import daripher.skilltree.container.ContainerHelper;
import daripher.skilltree.init.PSTRecipeSerializers;
import daripher.skilltree.item.ItemHelper;
import daripher.skilltree.item.gem.GemHelper;
import daripher.skilltree.item.gem.GemItem;
import daripher.skilltree.util.PlayerHelper;
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

public class GemInsertionRecipe extends UpgradeRecipe {
  public GemInsertionRecipe() {
    super(
        new ResourceLocation(SkillTreeMod.MOD_ID, "gem_insertion"),
        Ingredient.EMPTY,
        Ingredient.EMPTY,
        ItemStack.EMPTY);
  }

  @Override
  public boolean matches(Container container, Level level) {
    if (ModList.get().isLoaded("apotheosis")) {
      if (ApotheosisCompatibility.ISNTANCE.adventureModuleEnabled()) return false;
    }
    ItemStack base = container.getItem(0);
    if (!isBaseIngredient(base)) return false;
    ItemStack ingredient = container.getItem(1);
    if (!isAdditionIngredient(ingredient)) return false;
    GemItem gem = (GemItem) ingredient.getItem();
    Optional<Player> player = ContainerHelper.getViewingPlayer(container);
    if (!player.isPresent()) return false;
    return gem.canInsertInto(player.get(), base, ingredient, getEmptySocket(base, player.get()));
  }

  @Override
  public ItemStack assemble(Container container) {
    if (ModList.get().isLoaded("apotheosis")) {
      if (ApotheosisCompatibility.ISNTANCE.adventureModuleEnabled()) return ItemStack.EMPTY;
    }
    Optional<Player> player = ContainerHelper.getViewingPlayer(container);
    if (!player.isPresent()) return ItemStack.EMPTY;
    ItemStack base = container.getItem(0);
    int socket = getEmptySocket(base, player.get());
    ItemStack ingredient = container.getItem(1);
    GemItem gem = (GemItem) ingredient.getItem();
    if (!gem.canInsertInto(player.get(), base, ingredient, socket)) return ItemStack.EMPTY;
    ItemStack result = base.copy();
    result.setCount(1);
    CompoundTag itemTag = base.getTag();
    if (itemTag != null) result.setTag(itemTag.copy());
    float gemPower = PlayerHelper.getGemPower(player.get(), base);
    gem.insertInto(player.get(), result, ingredient, socket, gemPower);
    return result;
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
  public ItemStack getResultItem() {
    return ItemStack.EMPTY;
  }

  @Override
  public boolean isSpecial() {
    return true;
  }

  public boolean isBaseIngredient(ItemStack itemStack) {
    return ItemHelper.canInsertGem(itemStack);
  }

  public boolean isAdditionIngredient(ItemStack itemStack) {
    return itemStack.getItem() instanceof GemItem;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return PSTRecipeSerializers.GEM_INSERTION.get();
  }

  @Override
  public boolean isIncomplete() {
    return false;
  }

  public static class Serializer implements RecipeSerializer<GemInsertionRecipe> {
    @Override
    public GemInsertionRecipe fromJson(ResourceLocation id, JsonObject jsonObject) {
      return new GemInsertionRecipe();
    }

    @Override
    public GemInsertionRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
      return new GemInsertionRecipe();
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf, GemInsertionRecipe recipe) {}
  }
}
