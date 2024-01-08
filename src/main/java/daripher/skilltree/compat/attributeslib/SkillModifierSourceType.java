package daripher.skilltree.compat.attributeslib;

import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.bonus.player.AttributeBonus;
import dev.shadowsoffire.attributeslib.client.ModifierSource;
import dev.shadowsoffire.attributeslib.client.ModifierSourceType;
import java.util.function.BiConsumer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

public class SkillModifierSourceType extends ModifierSourceType<PassiveSkill> {
  @Override
  public void extract(
      LivingEntity entity, BiConsumer<AttributeModifier, ModifierSource<?>> consumer) {
    if (!(entity instanceof Player player)) return;
    if (!PlayerSkillsProvider.hasSkills(player)) return;
    PlayerSkillsProvider.get(player)
        .getPlayerSkills()
        .forEach(skill -> addSkillBonusIcons(consumer, skill));
  }

  private static void addSkillBonusIcons(
      BiConsumer<AttributeModifier, ModifierSource<?>> consumer, PassiveSkill skill) {
    skill.getBonuses().stream()
        .filter(AttributeBonus.class::isInstance)
        .map(AttributeBonus.class::cast)
        .forEach(bonus -> consumer.accept(bonus.getModifier(), new SkillModifierSource(skill)));
  }

  @Override
  public int getPriority() {
    return 1;
  }
}
