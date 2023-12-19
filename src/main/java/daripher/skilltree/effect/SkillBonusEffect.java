package daripher.skilltree.effect;

import daripher.skilltree.skill.bonus.SkillBonus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import org.jetbrains.annotations.NotNull;

public abstract class SkillBonusEffect extends MobEffect {
  private final SkillBonus<?> bonus;

  public SkillBonusEffect(MobEffectCategory category, int color, SkillBonus<?> bonus) {
    super(category, color);
    this.bonus = bonus;
  }

  @Override
  public void removeAttributeModifiers(
      @NotNull LivingEntity entity, @NotNull AttributeMap attributeMap, int amplifier) {
    super.removeAttributeModifiers(entity, attributeMap, amplifier);
    if (entity instanceof ServerPlayer player) {
      bonus.onSkillRemoved(player);
    }
  }

  @Override
  public void addAttributeModifiers(
      @NotNull LivingEntity entity, @NotNull AttributeMap attributeMap, int amplifier) {
    super.addAttributeModifiers(entity, attributeMap, amplifier);
    if (entity instanceof ServerPlayer player) {
      bonus.onSkillLearned(player, true);
    }
  }

  @Override
  public boolean isDurationEffectTick(int duration, int amplifier) {
    return bonus instanceof SkillBonus.Ticking;
  }

  @Override
  public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
    if (entity instanceof ServerPlayer player && bonus instanceof SkillBonus.Ticking ticking) {
      ticking.tick(player);
    }
  }

  public SkillBonus<?> getBonus() {
    return bonus;
  }
}
