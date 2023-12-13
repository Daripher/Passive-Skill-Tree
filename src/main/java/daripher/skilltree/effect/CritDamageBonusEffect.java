package daripher.skilltree.effect;

import daripher.skilltree.skill.bonus.player.CritDamageBonus;
import net.minecraft.world.effect.MobEffectCategory;

public class CritDamageBonusEffect extends SkillBonusEffect {
  public CritDamageBonusEffect() {
    super(MobEffectCategory.BENEFICIAL, 0, new CritDamageBonus(0.01f));
  }
}
