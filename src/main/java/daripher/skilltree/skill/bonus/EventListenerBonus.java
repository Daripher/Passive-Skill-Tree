package daripher.skilltree.skill.bonus;

import daripher.skilltree.skill.bonus.event.SkillEventListener;
import net.minecraft.world.entity.LivingEntity;

public interface EventListenerBonus<T> extends SkillBonus<EventListenerBonus<T>> {
  SkillEventListener getEventListener();

  void applyEffect(LivingEntity target);
}
