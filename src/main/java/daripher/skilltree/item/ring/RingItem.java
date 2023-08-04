package daripher.skilltree.item.ring;

import net.minecraft.world.item.Item;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class RingItem extends Item implements ICurioItem {
	public RingItem() {
		super(new Properties().stacksTo(1));
	}
}
