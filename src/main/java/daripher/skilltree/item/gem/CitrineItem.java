package daripher.skilltree.item.gem;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class CitrineItem extends SimpleGemItem {
	public CitrineItem() {
		super(0xF4B223);
		setAllBonuses(Attributes.LUCK, 0.25F, Operation.ADDITION);
	}

	@Override
	protected void appenBonusesTooltip(List<Component> components) {
		components.add(getBonusTooltip("anything", getWeaponBonus()));
	}
}
