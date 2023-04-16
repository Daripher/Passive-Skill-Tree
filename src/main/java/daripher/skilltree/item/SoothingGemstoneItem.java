package daripher.skilltree.item;

import org.apache.commons.lang3.tuple.Triple;

import daripher.skilltree.init.SkillTreeAttributes;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class SoothingGemstoneItem extends GemstoneItem {
	public SoothingGemstoneItem() {
		super(0xA9DA80);
	}

	@Override
	public Triple<Attribute, Double, Operation> getHelmetBonus() {
		return Triple.of(SkillTreeAttributes.HEALTH_REGENERATION_BONUS.get(), 0.5D, Operation.ADDITION);
	}

	@Override
	public Triple<Attribute, Double, Operation> getChestplateBonus() {
		return Triple.of(Attributes.MAX_HEALTH, 0.05D, Operation.MULTIPLY_TOTAL);
	}

	@Override
	public Triple<Attribute, Double, Operation> getLeggingsBonus() {
		return Triple.of(Attributes.MAX_HEALTH, 5D, Operation.ADDITION);
	}

	@Override
	public Triple<Attribute, Double, Operation> getBootsBonus() {
		return Triple.of(Attributes.MAX_HEALTH, 5D, Operation.ADDITION);
	}

	@Override
	public Triple<Attribute, Double, Operation> getWeaponBonus() {
		return Triple.of(SkillTreeAttributes.HEALING_PER_HIT.get(), 1D, Operation.ADDITION);
	}

	@Override
	public Triple<Attribute, Double, Operation> getShieldBonus() {
		return Triple.of(SkillTreeAttributes.HEALTH_REGENERATION_BONUS.get(), 0.5D, Operation.ADDITION);
	}

	@Override
	public Triple<Attribute, Double, Operation> getBowBonus() {
		return Triple.of(SkillTreeAttributes.HEALING_PER_HIT.get(), 1D, Operation.ADDITION);
	}
}
