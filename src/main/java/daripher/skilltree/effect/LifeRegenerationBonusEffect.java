package daripher.skilltree.effect;

import daripher.skilltree.init.PSTAttributes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class LifeRegenerationBonusEffect extends MobEffect {
	public LifeRegenerationBonusEffect() {
		super(MobEffectCategory.BENEFICIAL, 0);
		addAttributeModifier(PSTAttributes.LIFE_REGENERATION.get(), "bd211b5c-f6f1-48a4-8578-0af495d0649a", 0.01, Operation.ADDITION);
	}
}
