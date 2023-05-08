package daripher.skilltree.api.player;

import java.util.Optional;

import net.minecraft.world.entity.player.Player;

public interface PlayerContainer {
	Optional<Player> getPlayer();

	void setPlayer(Player player);
}
