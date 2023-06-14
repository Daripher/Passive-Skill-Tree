package daripher.skilltree.item.gem;

import java.util.List;

import daripher.skilltree.init.SkillTreeAttributes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class AdamiteItem extends SimpleGemItem {
	public AdamiteItem() {
		super(0x62B643);
		setWeaponBonus(SkillTreeAttributes.DAMAGE_AGAINST_POISONED.get(), 0.1F, Operation.MULTIPLY_BASE);
		setHelmetBonus(SkillTreeAttributes.CHANCE_TO_BREW_STRONGER_HARMFUL_POTION.get(), 0.05F, Operation.MULTIPLY_BASE);
	}

	@Override
	protected void appenBonusesTooltip(List<Component> components) {
		components.add(getBonusTooltip("weapon", getWeaponBonus()));
		components.add(getBonusTooltip("helmet", getHelmetBonus()));
	}
}
