package daripher.skilltree.compat.attributeslib;

import daripher.skilltree.skill.PassiveSkill;
import dev.shadowsoffire.attributeslib.client.ModifierSourceType;

public enum AttributesLibCompatibility {
  INSTANCE;

  public static final ModifierSourceType<PassiveSkill> SKILL_MODIFIER_TYPE =
      ModifierSourceType.register(new SkillModifierSourceType());

  public void register() {}
}
