package daripher.skilltree.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class AttackSpeedBonusEffect extends MobEffect {
	public AttackSpeedBonusEffect() {
		super(MobEffectCategory.BENEFICIAL, 0);
		addAttributeModifier(Attributes.ATTACK_SPEED, "10c18147-8d41-4a08-be38-52bf4c0eca6d", 0.01, Operation.MULTIPLY_TOTAL);
	}
}
