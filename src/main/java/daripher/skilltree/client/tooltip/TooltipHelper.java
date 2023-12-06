package daripher.skilltree.client.tooltip;

import daripher.skilltree.effect.SkillBonusEffect;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class TooltipHelper {
  public static Component getEffectInstanceTooltip(MobEffectInstance effect) {
    Component effectDescription;
    if (effect.getEffect() instanceof SkillBonusEffect skillEffect) {
      effectDescription =
          skillEffect
              .getBonus()
              .copy()
              .multiply(effect.getAmplifier() + 1)
              .getTooltip()
              .setStyle(Style.EMPTY);
    } else {
      effectDescription = effect.getEffect().getDisplayName();
      if (effect.getAmplifier() == 0) return effectDescription;
      MutableComponent amplifier =
          Component.translatable("potion.potency." + effect.getAmplifier());
      effectDescription =
          Component.translatable("potion.withAmplifier", effectDescription, amplifier);
    }
    return effectDescription;
  }

  public static Component getEffectTooltip(MobEffect effect) {
    if (effect instanceof SkillBonusEffect skillEffect) {
      return skillEffect.getBonus().getTooltip().setStyle(Style.EMPTY);
    }
    return effect.getDisplayName();
  }

  public static Component getOperationName(AttributeModifier.Operation operation) {
    return Component.literal(
        switch (operation) {
          case ADDITION -> "Addition";
          case MULTIPLY_BASE -> "Multiply Base";
          case MULTIPLY_TOTAL -> "Multiply Total";
        });
  }

  public static MutableComponent getOptionalTooltip(String descriptionId, String subtype) {
    String key = "%s.%s".formatted(descriptionId, subtype);
    MutableComponent tooltip = Component.translatable(key);
    if (!tooltip.getString().equals(key)) {
      return tooltip;
    }
    return Component.translatable(descriptionId);
  }
}
