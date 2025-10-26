package daripher.skilltree.inventory.slot;

import daripher.skilltree.inventory.menu.WorkbenchContainer;
import daripher.skilltree.recipe.workbench.AbstractWorkbenchRecipe;
import java.util.List;
import java.util.Map;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.NotNull;

public class WorkbenchResultSlot extends Slot {
  private final WorkbenchContainer workbenchContainer;
  private final Player player;
  private int removeCount;

  public WorkbenchResultSlot(
      Player player,
      WorkbenchContainer workbenchContainer,
      ResultContainer container,
      int slot,
      int x,
      int y) {
    super(container, slot, x, y);
    this.player = player;
    this.workbenchContainer = workbenchContainer;
  }

  @Override
  public boolean mayPlace(@NotNull ItemStack itemStack) {
    return false;
  }

  @Override
  public @NotNull ItemStack remove(int amount) {
    if (hasItem()) {
      removeCount += Math.min(amount, getItem().getCount());
    }
    return super.remove(amount);
  }

  @Override
  protected void onQuickCraft(@NotNull ItemStack itemStack, int amount) {
    removeCount += amount;
    checkTakeAchievements(itemStack);
    repeatQuickCraft();
  }

  private void repeatQuickCraft() {
    AbstractWorkbenchRecipe selectedRecipe = workbenchContainer.menu.getSelectedRecipe();
    if (selectedRecipe == null) {
      return;
    }
    int additionalCrafts = -1;
    int requiredBaseItems = selectedRecipe.requiredBaseItemAmount();
    if (requiredBaseItems != 0) {
      additionalCrafts = workbenchContainer.getBaseItem().getCount() / requiredBaseItems;
    }
    List<Map.Entry<Ingredient, Integer>> requiredIngredients =
        selectedRecipe.getAdditionalIngredients().entrySet().stream().toList();
    for (int i = 0; i < requiredIngredients.size(); i++) {
      int requiredAmount = requiredIngredients.get(i).getValue();
      int availableAmount = workbenchContainer.getItem(i + 1).getCount();
      int availableCrafts = availableAmount / requiredAmount;
      if (additionalCrafts == -1 || additionalCrafts > availableCrafts) {
        additionalCrafts = availableCrafts;
      }
    }
    for (int i = 0; i < additionalCrafts; i++) {
      player.addItem(selectedRecipe.assemble(workbenchContainer, player.level().registryAccess()));
      consumeMaterials();
    }
  }

  @Override
  protected void onSwapCraft(int numItemsCrafted) {
    removeCount += numItemsCrafted;
  }

  @Override
  protected void checkTakeAchievements(@NotNull ItemStack itemStack) {
    if (removeCount > 0) {
      itemStack.onCraftedBy(player.level(), player, removeCount);
      ForgeEventFactory.firePlayerCraftingEvent(player, itemStack, workbenchContainer);
      consumeMaterials();
    }
    removeCount = 0;
  }

  @Override
  public void onTake(@NotNull Player player, @NotNull ItemStack itemStack) {
    checkTakeAchievements(itemStack);
  }

  private void consumeMaterials() {
    AbstractWorkbenchRecipe selectedRecipe = workbenchContainer.menu.getSelectedRecipe();
    if (selectedRecipe == null) {
      return;
    }
    if (!workbenchContainer.getItem(0).isEmpty()) {
      workbenchContainer.removeItem(0, selectedRecipe.requiredBaseItemAmount());
    }
    List<Map.Entry<Ingredient, Integer>> requiredIngredients =
        selectedRecipe.getAdditionalIngredients().entrySet().stream().toList();
    for (int i = 0; i < requiredIngredients.size(); i++) {
      int requiredAmount = requiredIngredients.get(i).getValue();
      workbenchContainer.removeItem(i + 1, requiredAmount);
    }
  }
}
