package daripher.skilltree.effect;

import daripher.skilltree.skill.bonus.player.DamageBonus;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class DamageBonusEffect extends SkillBonusEffect {
  public DamageBonusEffect() {
    super(MobEffectCategory.BENEFICIAL, 0, new DamageBonus(0.01f, Operation.MULTIPLY_BASE));
  }
}
