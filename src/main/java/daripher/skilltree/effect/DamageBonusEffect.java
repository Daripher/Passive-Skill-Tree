package daripher.skilltree.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class DamageBonusEffect extends MobEffect {
	public DamageBonusEffect() {
		super(MobEffectCategory.BENEFICIAL, 0);
		addAttributeModifier(Attributes.ATTACK_DAMAGE, "409c0f40-2d2a-4a6c-91bd-623e6380c084", 0.01, Operation.MULTIPLY_TOTAL);
	}
}
