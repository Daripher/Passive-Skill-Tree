package daripher.skilltree.item.gem;

import daripher.skilltree.init.PSTAttributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class TourmalineItem extends SimpleGemItem {
	public TourmalineItem() {
		super();
		setBonuses(PSTAttributes.COOKED_FOOD_SATURATION.get(), 0.02F, Operation.MULTIPLY_BASE, "necklace");
		setBonuses(PSTAttributes.COOKED_FOOD_LIFE_REGENERATION.get(), 0.1, Operation.ADDITION, "ring");
	}
}
