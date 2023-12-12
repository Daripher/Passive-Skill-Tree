package daripher.skilltree.item.necklace;

import daripher.skilltree.item.HasAdditionalSockets;

public class SimpleNecklace extends NecklaceItem implements HasAdditionalSockets {
  @Override
  public int getAdditionalSockets() {
    return 1;
  }
}
