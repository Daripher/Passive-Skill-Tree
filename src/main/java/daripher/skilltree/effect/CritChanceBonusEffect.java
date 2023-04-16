package daripher.skilltree.effect;

import daripher.skilltree.init.SkillTreeAttributes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class CritChanceBonusEffect extends MobEffect {
	public CritChanceBonusEffect() {
		super(MobEffectCategory.BENEFICIAL, 0);
		addAttributeModifier(SkillTreeAttributes.CRIT_CHANCE_BONUS.get(), "a9502fbf-27b0-46e3-91bd-e59c3b66520d", 0.01, Operation.ADDITION);
	}
}
