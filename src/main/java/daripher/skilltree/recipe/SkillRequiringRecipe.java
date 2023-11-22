package daripher.skilltree.recipe;

import daripher.skilltree.api.PlayerContainer;
import daripher.skilltree.skill.bonus.SkillBonusHandler;
import daripher.skilltree.skill.bonus.player.RecipeUnlockBonus;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;

public interface SkillRequiringRecipe {
  default boolean isUncraftable(Container container) {
    if (!(container instanceof PlayerContainer)) return true;
    return isUncraftable((PlayerContainer) container);
  }

  default boolean isUncraftable(PlayerContainer container) {
    Player player = container.getViewingPlayer().orElse(null);
    if (player == null) return true;
    return SkillBonusHandler.getSkillBonuses(player, RecipeUnlockBonus.class).stream()
        .noneMatch(bonus -> bonus.recipeId().equals(getId()));
  }

  ResourceLocation getId();
}
