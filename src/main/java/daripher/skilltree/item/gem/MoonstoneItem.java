package daripher.skilltree.item.gem;

import java.util.List;

import daripher.skilltree.init.SkillTreeAttributes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class MoonstoneItem extends SimpleGemItem {
	public MoonstoneItem() {
		super(0x579EC2);
		setPickaxeBonus(SkillTreeAttributes.MINING_SPEED.get(), 0.05F, Operation.MULTIPLY_BASE);
		setWeaponBonus(Attributes.ATTACK_SPEED, 0.05F, Operation.MULTIPLY_BASE);
		setBootsBonus(Attributes.MOVEMENT_SPEED, 0.05F, Operation.MULTIPLY_BASE);
	}

	@Override
	protected void appenBonusesTooltip(List<Component> components) {
		components.add(getBonusTooltip("pickaxe", getPickaxeBonus()));
		components.add(getBonusTooltip("weapon", getWeaponBonus()));
		components.add(getBonusTooltip("boots", getBootsBonus()));
	}
}
