package daripher.skilltree.effect;

import daripher.skilltree.skill.bonus.SkillBonus;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public abstract class SkillBonusEffect extends MobEffect {
  private final SkillBonus<?> bonus;

  public SkillBonusEffect(MobEffectCategory category, int color, SkillBonus<?> bonus) {
    super(category, color);
    this.bonus = bonus;
  }

  public SkillBonus<?> getBonus() {
    return bonus;
  }
}
