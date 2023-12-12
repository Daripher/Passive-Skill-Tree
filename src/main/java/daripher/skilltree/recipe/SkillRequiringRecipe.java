package daripher.skilltree.recipe;

import daripher.skilltree.api.PlayerContainer;
import daripher.skilltree.skill.bonus.SkillBonusHandler;
import daripher.skilltree.skill.bonus.player.RecipeUnlockBonus;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Recipe;

public interface SkillRequiringRecipe {
  default boolean isUncraftable(Container container, Recipe<?> recipe) {
    if (!(container instanceof PlayerContainer)) return true;
    return isUncraftable((PlayerContainer) container, recipe);
  }

  default boolean isUncraftable(PlayerContainer container, Recipe<?> recipe) {
    Player player = container.getViewingPlayer().orElse(null);
    if (player == null) return true;
    return SkillBonusHandler.getSkillBonuses(player, RecipeUnlockBonus.class).stream()
        .noneMatch(bonus -> bonus.getRecipeId().equals(recipe.getId()));
  }
}
