package daripher.skilltree.recipe;

import com.google.gson.JsonObject;
import daripher.skilltree.init.PSTRecipeSerializers;
import daripher.skilltree.item.ItemHelper;
import daripher.skilltree.item.quiver.QuiverItem;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class QuiverFillingRecipe extends CustomRecipe {
  public QuiverFillingRecipe(ResourceLocation id) {
    super(id);
  }

  @Override
  public boolean matches(CraftingContainer container, @NotNull Level level) {
    ItemStack quiver = ItemStack.EMPTY;
    ItemStack arrows = ItemStack.EMPTY;
    int itemsCount = 0;
    for (int slot = 0; slot < container.getContainerSize(); slot++) {
      ItemStack stack = container.getItem(slot);
      if (stack.isEmpty()) continue;
      itemsCount++;
      if (ItemHelper.isQuiver(stack)) quiver = stack;
      if (stack.is(ItemTags.ARROWS)) arrows = stack;
    }
    if (itemsCount != 2) return false;
    if (quiver.isEmpty() || arrows.isEmpty()) return false;
    if (QuiverItem.isFull(quiver)) return false;
    return !QuiverItem.containsArrows(quiver)
        || ItemStack.isSameItemSameTags(QuiverItem.getArrows(quiver), arrows);
  }

  @Override
  public @NotNull ItemStack assemble(CraftingContainer container) {
    ItemStack quiver = ItemStack.EMPTY;
    ItemStack arrows = ItemStack.EMPTY;
    for (int slot = 0; slot < container.getContainerSize(); slot++) {
      ItemStack stack = container.getItem(slot);
      if (stack.isEmpty()) continue;
      if (ItemHelper.isQuiver(stack)) quiver = stack;
      if (stack.is(ItemTags.ARROWS)) arrows = stack;
    }
    ItemStack result = quiver.copy();
    int capacity = QuiverItem.getCapacity(result);
    int arrowsTaken = Math.min(capacity - QuiverItem.getArrowsCount(result), arrows.getCount());
    QuiverItem.addArrows(result, arrows, arrowsTaken);
    return result;
  }

  @Override
  public @NotNull NonNullList<ItemStack> getRemainingItems(CraftingContainer container) {
    ItemStack quiver = ItemStack.EMPTY;
    ItemStack arrows = ItemStack.EMPTY;
    int arrowsSlot = 0;
    for (int slot = 0; slot < container.getContainerSize(); slot++) {
      ItemStack stack = container.getItem(slot);
      if (stack.isEmpty()) continue;
      if (ItemHelper.isQuiver(stack)) quiver = stack;
      if (stack.is(ItemTags.ARROWS)) {
        arrows = stack;
        arrowsSlot = slot;
      }
    }
    int capacity = QuiverItem.getCapacity(quiver);
    int arrowsTaken = Math.min(capacity - QuiverItem.getArrowsCount(quiver), arrows.getCount());
    if (arrowsTaken == arrows.getCount()) {
      container.setItem(arrowsSlot, ItemStack.EMPTY);
    } else {
      arrows.shrink(arrowsTaken - 1);
    }
    return super.getRemainingItems(container);
  }

  @Override
  public boolean canCraftInDimensions(int width, int height) {
    return width * height >= 2;
  }

  @Override
  public @NotNull RecipeSerializer<?> getSerializer() {
    return PSTRecipeSerializers.QUIVER_FILLING.get();
  }

  public static class Serializer implements RecipeSerializer<QuiverFillingRecipe> {
    @Override
    public @NotNull QuiverFillingRecipe fromJson(
        @NotNull ResourceLocation id, @NotNull JsonObject jsonObject) {
      return new QuiverFillingRecipe(id);
    }

    @Override
    public QuiverFillingRecipe fromNetwork(
        @NotNull ResourceLocation id, @NotNull FriendlyByteBuf buf) {
      return new QuiverFillingRecipe(id);
    }

    @Override
    public void toNetwork(@NotNull FriendlyByteBuf buf, @NotNull QuiverFillingRecipe recipe) {}
  }
}
