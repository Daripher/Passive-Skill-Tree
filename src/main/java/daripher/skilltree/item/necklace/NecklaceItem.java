package daripher.skilltree.item.necklace;

import daripher.skilltree.init.PSTCreativeTabs;
import net.minecraft.world.item.Item;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class NecklaceItem extends Item implements ICurioItem {
  public NecklaceItem() {
    super(new Properties().tab(PSTCreativeTabs.SKILLTREE).stacksTo(1));
  }
}
