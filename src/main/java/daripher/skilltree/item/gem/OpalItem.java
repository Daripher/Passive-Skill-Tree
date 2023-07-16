package daripher.skilltree.item.gem;

import java.util.List;

import daripher.skilltree.init.SkillTreeAttributes;
import daripher.skilltree.util.TooltipHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class OpalItem extends SimpleGemItem {
	public OpalItem() {
		super(0x4A749E);
		setWeaponBonus(SkillTreeAttributes.LIFE_PER_HIT.get(), 0.5F, Operation.ADDITION);
		setShieldBonus(SkillTreeAttributes.LIFE_ON_BLOCK.get(), 0.5F, Operation.ADDITION);
	}

	@Override
	protected void appenBonusesTooltip(List<Component> components) {
		components.add(TooltipHelper.getAttributeBonusTooltip("weapon", getWeaponBonus()));
		components.add(TooltipHelper.getAttributeBonusTooltip("shield", getShieldBonus()));
	}
}
