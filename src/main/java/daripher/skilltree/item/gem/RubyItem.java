package daripher.skilltree.item.gem;

import java.util.List;

import daripher.skilltree.init.SkillTreeAttributes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class RubyItem extends SimpleGemItem {
	public RubyItem() {
		super(0xFE3521);
		setArmorBonus(SkillTreeAttributes.LIFE_REGENERATION.get(), 0.5F, Operation.ADDITION);
		setShieldBonus(Attributes.MAX_HEALTH, 2.5F, Operation.ADDITION);
	}

	@Override
	protected void appenBonusesTooltip(List<Component> components) {
		components.add(getBonusTooltip("armor", getHelmetBonus()));
		components.add(getBonusTooltip("shield", getShieldBonus()));
	}
}
