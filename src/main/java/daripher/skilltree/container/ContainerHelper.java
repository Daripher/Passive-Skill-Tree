package daripher.skilltree.container;

import daripher.skilltree.api.PlayerContainer;
import java.util.Optional;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ContainerHelper {
  public static Optional<Player> getViewingPlayer(AbstractContainerMenu menu) {
    return ((PlayerContainer) menu).getViewingPlayer();
  }

  public static Optional<Player> getViewingPlayer(BlockEntity entity) {
    return ((PlayerContainer) entity).getViewingPlayer();
  }

  public static Optional<Player> getViewingPlayer(Container container) {
    if (!(container instanceof PlayerContainer playerContainer)) return Optional.empty();
    return playerContainer.getViewingPlayer();
  }
}
