package daripher.skilltree.item.gem;

import daripher.skilltree.init.PSTAttributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class JadeItem extends SimpleGemItem {
	public JadeItem() {
		super();
		setBonuses(PSTAttributes.TRIPLE_LOOT_CHANCE.get(), 0.02F, Operation.MULTIPLY_BASE, "necklace");
		setBonuses(PSTAttributes.DOUBLE_LOOT_CHANCE.get(), 0.02F, Operation.MULTIPLY_BASE, "ring");
	}
}
