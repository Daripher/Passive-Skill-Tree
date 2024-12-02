package daripher.skilltree.recipe;

import daripher.itemproduction.block.entity.Interactive;
import daripher.skilltree.skill.bonus.SkillBonusHandler;
import daripher.skilltree.skill.bonus.player.RecipeUnlockBonus;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Recipe;

public interface SkillRequiringRecipe {
  default boolean canUseRecipe(Container container, Recipe<?> recipe) {
    if (!(container instanceof Interactive interactive)) return false;
    return canUseRecipe(interactive, recipe);
  }

  default boolean canUseRecipe(Interactive container, Recipe<?> recipe) {
    Player player = container.getUser();
    if (player == null) return false;
    return hasRecipeUnlocked(recipe, player);
  }

  default boolean hasRecipeUnlocked(Recipe<?> recipe, Player player) {
    return SkillBonusHandler.getSkillBonuses(player, RecipeUnlockBonus.class)
        .stream()
        .anyMatch(bonus -> bonus.getRecipeId()
            .equals(recipe.getId()));
  }
}
