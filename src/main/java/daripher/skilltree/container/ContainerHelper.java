package daripher.skilltree.container;

import java.util.Optional;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ContainerHelper {
  public static Optional<Player> getViewingPlayer(AbstractContainerMenu menu) {
    return ((InteractiveContainer) menu).getUser();
  }

  public static Optional<Player> getViewingPlayer(BlockEntity entity) {
    return ((InteractiveContainer) entity).getUser();
  }

  public static Optional<Player> getViewingPlayer(Container container) {
    if (!(container instanceof InteractiveContainer aContainer)) return Optional.empty();
    return aContainer.getUser();
  }
}
