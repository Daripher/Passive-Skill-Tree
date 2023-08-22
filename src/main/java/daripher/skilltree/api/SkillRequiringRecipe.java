package daripher.skilltree.api;

import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;

public interface SkillRequiringRecipe {
	default boolean canCraftIn(Container container) {
		if (!(container instanceof PlayerContainer)) return false;
		return canCraftIn((PlayerContainer) container);
	}

	default boolean canCraftIn(PlayerContainer container) {
		Player player = container.getViewingPlayer().orElse(null);
		if (player == null) return false;
		return PlayerSkillsProvider.get(player).hasSkill(getRequiredSkillId());
	}

	ResourceLocation getRequiredSkillId();
}
