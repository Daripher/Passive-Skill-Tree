package daripher.skilltree.item;

import daripher.skilltree.init.PSTCreativeTabs;
import net.minecraft.world.item.Item;

public class ResourceItem extends Item {
  public ResourceItem() {
    super(new Properties().tab(PSTCreativeTabs.SKILLTREE));
  }
}
