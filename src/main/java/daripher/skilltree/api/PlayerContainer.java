package daripher.skilltree.api;

import java.util.Optional;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public interface PlayerContainer {
  Optional<Player> getViewingPlayer();

  void setViewingPlayer(@Nullable Player player);
}
