package daripher.skilltree.item;

import org.apache.commons.lang3.tuple.Triple;

import daripher.skilltree.init.SkillTreeAttributes;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class SturdyGemstoneItem extends GemstoneItem {
	public SturdyGemstoneItem() {
		super(0xD68A48);
	}

	@Override
	public Triple<Attribute, Double, Operation> getHelmetBonus() {
		return Triple.of(Attributes.ARMOR, 1D, Operation.ADDITION);
	}

	@Override
	public Triple<Attribute, Double, Operation> getChestplateBonus() {
		return Triple.of(Attributes.ARMOR_TOUGHNESS, 0.5D, Operation.ADDITION);
	}

	@Override
	public Triple<Attribute, Double, Operation> getLeggingsBonus() {
		return Triple.of(Attributes.ARMOR, 1D, Operation.ADDITION);
	}

	@Override
	public Triple<Attribute, Double, Operation> getBootsBonus() {
		return Triple.of(Attributes.ARMOR, 1D, Operation.ADDITION);
	}

	@Override
	public Triple<Attribute, Double, Operation> getWeaponBonus() {
		return Triple.of(Attributes.ATTACK_DAMAGE, 1D, Operation.ADDITION);
	}

	@Override
	public Triple<Attribute, Double, Operation> getShieldBonus() {
		return Triple.of(Attributes.ARMOR_TOUGHNESS, 0.5D, Operation.ADDITION);
	}

	@Override
	public Triple<Attribute, Double, Operation> getBowBonus() {
		return Triple.of(SkillTreeAttributes.ARROW_ARMOR_REDUCTION_MULTIPLIER.get(), 0.025D, Operation.MULTIPLY_BASE);
	}
}
