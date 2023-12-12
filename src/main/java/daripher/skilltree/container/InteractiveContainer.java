package daripher.skilltree.container;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.world.entity.player.Player;

public interface InteractiveContainer {
  Optional<Player> getUser();

  void setUser(@Nullable Player player);
}
