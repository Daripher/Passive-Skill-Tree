package daripher.skilltree.item.gem;

import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class OnyxItem extends SimpleGemItem {
	public OnyxItem() {
		super();
		setBonuses(Attributes.ARMOR, 2F, Operation.ADDITION, "shield");
		setBonuses(Attributes.ARMOR, 1F, Operation.ADDITION, "chestplate");
		setBonuses(Attributes.ARMOR, 0.5F, Operation.ADDITION, "helmet");
	}
}
