package daripher.skilltree.item;

import org.apache.commons.lang3.tuple.Triple;

import daripher.skilltree.init.SkillTreeAttributes;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.common.ForgeMod;

public class LightGemstoneItem extends GemstoneItem {
	public LightGemstoneItem() {
		super(0xE2E2CE);
	}

	@Override
	public Triple<Attribute, Double, Operation> getHelmetBonus() {
		return Triple.of(Attributes.FLYING_SPEED, 0.1D, Operation.MULTIPLY_TOTAL);
	}

	@Override
	public Triple<Attribute, Double, Operation> getChestplateBonus() {
		return Triple.of(ForgeMod.SWIM_SPEED.get(), 0.1D, Operation.MULTIPLY_TOTAL);
	}

	@Override
	public Triple<Attribute, Double, Operation> getLeggingsBonus() {
		return Triple.of(Attributes.MOVEMENT_SPEED, 0.05D, Operation.MULTIPLY_TOTAL);
	}

	@Override
	public Triple<Attribute, Double, Operation> getBootsBonus() {
		return Triple.of(Attributes.MOVEMENT_SPEED, 0.05D, Operation.MULTIPLY_TOTAL);
	}

	@Override
	public Triple<Attribute, Double, Operation> getWeaponBonus() {
		return Triple.of(Attributes.ATTACK_SPEED, 0.1D, Operation.MULTIPLY_TOTAL);
	}

	@Override
	public Triple<Attribute, Double, Operation> getShieldBonus() {
		return Triple.of(Attributes.ATTACK_SPEED, 0.1D, Operation.MULTIPLY_TOTAL);
	}

	@Override
	public Triple<Attribute, Double, Operation> getBowBonus() {
		return Triple.of(SkillTreeAttributes.BOW_CHARGE_SPEED_MULTIPLIER.get(), 0.025D, Operation.MULTIPLY_BASE);
	}
}
