package daripher.skilltree.container;

import daripher.itemproduction.block.entity.Interactive;
import javax.annotation.Nullable;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class ContainerHelper {
  public static @Nullable Player getViewingPlayer(AbstractContainerMenu menu) {
    return ((Interactive) menu).getUser();
  }

  public static @Nullable Player getViewingPlayer(Container container) {
    if (!(container instanceof Interactive interactive)) return null;
    return interactive.getUser();
  }
}
