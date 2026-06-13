package daripher.skilltree.skill.bonus.predicate.effect;

import java.util.Locale;

public enum MobEffectType {
    NEUTRAL, HARMFUL, BENEFICIAL, ANY;

    public String getName() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static MobEffectType fromName(String name) {
        return MobEffectType.valueOf(name.toUpperCase(Locale.ROOT));
    }

    public String getDescriptionId() {
        return "effect_type." + getName();
    }
}
