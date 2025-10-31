package daripher.skilltree.skill.bonus.predicate.effect;

public enum EffectType {
  NEUTRAL, HARMFUL, BENEFICIAL, ANY;

  public String getName() {
    return name().toLowerCase();
  }

  public static EffectType fromName(String name) {
    return EffectType.valueOf(name.toUpperCase());
  }

  public String getDescriptionId() {
    return "effect_type." + getName();
  }
}
