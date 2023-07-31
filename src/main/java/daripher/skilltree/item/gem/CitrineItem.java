package daripher.skilltree.item.gem;

import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class CitrineItem extends SimpleGemItem {
	public CitrineItem() {
		super();
		setBonuses(Attributes.LUCK, 0.1F, Operation.ADDITION, "ring");
		setBonuses(Attributes.LUCK, 0.2F, Operation.ADDITION, "necklace");
	}
}
