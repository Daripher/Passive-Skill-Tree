package daripher.skilltree.skill.bonus.predicate.effect;

import java.util.Locale;

public enum EffectType {
  NEUTRAL, HARMFUL, BENEFICIAL, ANY;

  public String getName() {
    return name().toLowerCase(Locale.ROOT);
  }

  public static EffectType fromName(String name) {
    return EffectType.valueOf(name.toUpperCase(Locale.ROOT));
  }

  public String getDescriptionId() {
    return "effect_type." + getName();
  }
}
