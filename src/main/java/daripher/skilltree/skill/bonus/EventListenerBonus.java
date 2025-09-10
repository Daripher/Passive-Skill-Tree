package daripher.skilltree.skill.bonus;

import daripher.skilltree.skill.bonus.event.SkillEventListener;
import daripher.skilltree.skill.bonus.event.SkillLearnedEventListener;
import daripher.skilltree.skill.bonus.event.SkillRemovedEventListener;
import daripher.skilltree.skill.bonus.event.TickingEventListener;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

public interface EventListenerBonus<T>
    extends TickingSkillBonus, SkillBonus<EventListenerBonus<T>> {
  @Override
  default void onSkillLearned(ServerPlayer player, boolean firstTime) {
    if (firstTime && getEventListener() instanceof SkillLearnedEventListener listener) {
      listener.onEvent(player, this);
    }
  }

  @Override
  default void onSkillRemoved(ServerPlayer player) {
    if (getEventListener() instanceof SkillRemovedEventListener listener) {
      listener.onEvent(player, this);
    }
  }

  @Override
  default void tick(ServerPlayer player) {
    // trigger every 0.5 seconds
    if (player.tickCount % 10 != 0) {
      return;
    }
    if (getEventListener() instanceof TickingEventListener listener) {
      listener.onEvent(player, this);
    }
  }

  SkillEventListener getEventListener();

  void applyEffect(LivingEntity target);
}
