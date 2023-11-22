package daripher.skilltree.effect;

import daripher.skilltree.init.PSTAttributes;
import daripher.skilltree.skill.bonus.player.AttributeBonus;
import java.util.UUID;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class LifeRegenerationBonusEffect extends SkillBonusEffect {
  public LifeRegenerationBonusEffect() {
    super(
        MobEffectCategory.BENEFICIAL,
        0,
        new AttributeBonus(
            PSTAttributes.LIFE_REGENERATION.get(),
            new AttributeModifier(
                UUID.fromString("f03830ab-d917-4e66-9f18-42f6804725ff"),
                "Effect",
                0.01,
                Operation.ADDITION)));
  }
}
