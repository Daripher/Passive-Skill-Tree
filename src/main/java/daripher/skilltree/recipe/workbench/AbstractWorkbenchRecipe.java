package daripher.skilltree.recipe.workbench;

import daripher.skilltree.init.PSTRecipeTypes;
import daripher.skilltree.inventory.menu.WorkbenchContainer;
import daripher.skilltree.recipe.SkillRequiringRecipe;
import daripher.skilltree.skill.bonus.SkillBonusHandler;
import daripher.skilltree.skill.bonus.player.RecipeUnlockBonus;
import java.util.Map;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractWorkbenchRecipe
    implements Recipe<WorkbenchContainer>, SkillRequiringRecipe {
  private final Map<Ingredient, Integer> additionalIngredients;
  private final ResourceLocation id;
  private final boolean requiresPassiveSkill;

  public AbstractWorkbenchRecipe(
      ResourceLocation id, Map<Ingredient, Integer> ingredients, boolean requiresPassiveSkill) {
    this.additionalIngredients = ingredients;
    this.requiresPassiveSkill = requiresPassiveSkill;
    this.id = id;
  }

  @Override
  public boolean matches(@NotNull WorkbenchContainer container, @NotNull Level level) {
    if (!isValidBaseItem(container.getBaseItem())) {
      return false;
    }
    if (!canBeUsedBy(container.getPlayer())) {
      return false;
    }
    return hasIngredients(container, additionalIngredients);
  }

  public boolean canBeUsedBy(@NotNull Player player) {
    return !requiresPassiveSkill || hasRecipeLearned(player);
  }

  public abstract boolean isValidBaseItem(ItemStack itemStack);

  public abstract Component getTooltip();

  public abstract @NotNull ItemStack getResult(WorkbenchContainer workbenchContainer);

  public abstract int requiredBaseItemAmount();

  public Map<Ingredient, Integer> getAdditionalIngredients() {
    return additionalIngredients;
  }

  protected final boolean hasRecipeLearned(@NotNull Player player) {
    return SkillBonusHandler.getSkillBonuses(player, RecipeUnlockBonus.class).stream()
        .map(RecipeUnlockBonus::getRecipeId)
        .anyMatch(getId()::equals);
  }

  protected boolean hasIngredients(
      @NotNull WorkbenchContainer container, Map<Ingredient, Integer> ingredients) {
    return container.hasIngredients(ingredients);
  }

  @Override
  public boolean canCraftInDimensions(int width, int height) {
    return width == 7 && height == 1;
  }

  @Override
  public @NotNull ResourceLocation getId() {
    return id;
  }

  @Deprecated
  @Override
  public @NotNull ItemStack getResultItem(@NotNull RegistryAccess registryAccess) {
    return ItemStack.EMPTY;
  }

  @Override
  public @NotNull RecipeType<?> getType() {
    return PSTRecipeTypes.ARTISAN_WORKBENCH;
  }

  @Override
  public boolean requiresPassiveSkill() {
    return requiresPassiveSkill;
  }
}
