package daripher.skilltree.item.gem;

import org.apache.commons.lang3.tuple.Triple;

import daripher.skilltree.init.SkillTreeAttributes;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class LightGemstoneItem extends SimpleGemstoneItem {
	public LightGemstoneItem() {
		super(0xD0D0BC);
	}

	@Override
	public Triple<Attribute, Double, Operation> getHelmetBonus() {
		return Triple.of(SkillTreeAttributes.EVASION_CHANCE.get(), 0.01D, Operation.MULTIPLY_BASE);
	}

	@Override
	public Triple<Attribute, Double, Operation> getChestplateBonus() {
		return Triple.of(SkillTreeAttributes.EVASION_CHANCE.get(), 0.02D, Operation.MULTIPLY_BASE);
	}

	@Override
	public Triple<Attribute, Double, Operation> getLeggingsBonus() {
		return Triple.of(SkillTreeAttributes.EVASION_CHANCE.get(), 0.01D, Operation.MULTIPLY_BASE);
	}

	@Override
	public Triple<Attribute, Double, Operation> getBootsBonus() {
		return Triple.of(Attributes.MOVEMENT_SPEED, 0.025D, Operation.MULTIPLY_TOTAL);
	}

	@Override
	public Triple<Attribute, Double, Operation> getWeaponBonus() {
		return Triple.of(Attributes.ATTACK_SPEED, 0.025D, Operation.MULTIPLY_TOTAL);
	}

	@Override
	public Triple<Attribute, Double, Operation> getShieldBonus() {
		return Triple.of(SkillTreeAttributes.BLOCK_CHANCE.get(), 0.01D, Operation.MULTIPLY_BASE);
	}

	@Override
	public Triple<Attribute, Double, Operation> getBowBonus() {
		return Triple.of(Attributes.ATTACK_SPEED, 0.025D, Operation.MULTIPLY_BASE);
	}
}
