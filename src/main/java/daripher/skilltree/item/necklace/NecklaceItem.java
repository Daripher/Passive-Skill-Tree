package daripher.skilltree.item.necklace;

import net.minecraft.world.item.Item;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class NecklaceItem extends Item implements ICurioItem {
	public NecklaceItem() {
		super(new Properties().stacksTo(1));
	}
}
