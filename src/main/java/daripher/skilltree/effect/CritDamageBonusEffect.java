package daripher.skilltree.effect;

import daripher.skilltree.init.PSTAttributes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class CritDamageBonusEffect extends MobEffect {
	public CritDamageBonusEffect() {
		super(MobEffectCategory.BENEFICIAL, 0);
		addAttributeModifier(PSTAttributes.CRIT_DAMAGE.get(), "a9502fbf-27b0-46e3-91bd-e59c3b66520d", 0.01, Operation.MULTIPLY_BASE);
	}
}
