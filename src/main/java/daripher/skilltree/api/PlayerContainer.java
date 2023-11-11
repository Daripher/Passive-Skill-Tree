package daripher.skilltree.api;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.world.entity.player.Player;

public interface PlayerContainer {
  Optional<Player> getViewingPlayer();

  void setViewingPlayer(@Nullable Player player);
}
