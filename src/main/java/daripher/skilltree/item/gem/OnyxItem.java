package daripher.skilltree.item.gem;

import java.util.List;

import daripher.skilltree.util.TooltipHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class OnyxItem extends SimpleGemItem {
	public OnyxItem() {
		super(0x453C34);
		setWeaponBonus(Attributes.ATTACK_DAMAGE, 2.5F, Operation.ADDITION);
		setArmorAndShieldBonus(Attributes.ARMOR, 2.5F, Operation.ADDITION);
	}

	@Override
	protected void appenBonusesTooltip(List<Component> components) {
		components.add(TooltipHelper.getAttributeBonusTooltip("weapon", getWeaponBonus()));
		components.add(TooltipHelper.getAttributeBonusTooltip("armor_and_shield", getShieldBonus()));
	}
}
