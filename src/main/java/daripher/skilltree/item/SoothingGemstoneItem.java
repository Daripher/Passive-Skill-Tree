package daripher.skilltree.item;

import org.apache.commons.lang3.tuple.Triple;

import daripher.skilltree.init.SkillTreeAttributes;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class SoothingGemstoneItem extends SimpleGemstoneItem {
	public SoothingGemstoneItem() {
		super(0xB4E58B);
	}

	@Override
	public Triple<Attribute, Double, Operation> getHelmetBonus() {
		return Triple.of(SkillTreeAttributes.LIFE_REGENERATION.get(), 0.5D, Operation.ADDITION);
	}

	@Override
	public Triple<Attribute, Double, Operation> getChestplateBonus() {
		return Triple.of(Attributes.MAX_HEALTH, 5D, Operation.ADDITION);
	}

	@Override
	public Triple<Attribute, Double, Operation> getLeggingsBonus() {
		return Triple.of(Attributes.MAX_HEALTH, 2.5D, Operation.ADDITION);
	}

	@Override
	public Triple<Attribute, Double, Operation> getBootsBonus() {
		return Triple.of(Attributes.MAX_HEALTH, 2.5D, Operation.ADDITION);
	}

	@Override
	public Triple<Attribute, Double, Operation> getWeaponBonus() {
		return Triple.of(SkillTreeAttributes.LIFE_PER_HIT.get(), 1D, Operation.ADDITION);
	}

	@Override
	public Triple<Attribute, Double, Operation> getShieldBonus() {
		return Triple.of(SkillTreeAttributes.LIFE_ON_BLOCK.get(), 1D, Operation.ADDITION);
	}

	@Override
	public Triple<Attribute, Double, Operation> getBowBonus() {
		return Triple.of(SkillTreeAttributes.LIFE_PER_HIT.get(), 1D, Operation.ADDITION);
	}
}
