package daripher.skilltree.client.tooltip;

import daripher.skilltree.effect.SkillBonusEffect;
import java.util.Arrays;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TooltipHelper {
  private static final Style SKILL_BONUS_STYLE = Style.EMPTY.withColor(0x7B7BE5);
  private static final Style SKILL_BONUS_STYLE_NEGATIVE = Style.EMPTY.withColor(0xE25A5A);
  private static final Style ITEM_BONUS_STYLE = Style.EMPTY.withColor(0x7AB3E2);
  private static final Style ITEM_BONUS_STYLE_NEGATIVE = Style.EMPTY.withColor(0xDB9792);

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

  public static Component getOptionalTooltip(String descriptionId, String subtype) {
    String key = "%s.%s".formatted(descriptionId, subtype);
    Component tooltip = Component.translatable(key);
    if (!tooltip.getString().equals(key)) {
      return tooltip;
    }
    return Component.translatable(descriptionId);
  }

  public static MutableComponent getSkillBonusTooltip(
      Component bonusDescription, double amount, AttributeModifier.Operation operation) {
    float multiplier = 1;
    if (operation != AttributeModifier.Operation.ADDITION) {
      multiplier = 100;
    }
    double visibleAmount = amount * multiplier;
    if (amount < 0) visibleAmount *= -1;
    String operationDescription = amount > 0 ? "plus" : "take";
    operationDescription = "attribute.modifier." + operationDescription + "." + operation.ordinal();
    String multiplierDescription = ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(visibleAmount);
    return Component.translatable(operationDescription, multiplierDescription, bonusDescription);
  }

  public static MutableComponent getSkillBonusTooltip(
      String bonus, double amount, AttributeModifier.Operation operation) {
    return getSkillBonusTooltip(Component.translatable(bonus), amount, operation);
  }

  public static Style getSkillBonusStyle(boolean positive) {
    return positive ? SKILL_BONUS_STYLE : SKILL_BONUS_STYLE_NEGATIVE;
  }

  public static Style getItemBonusStyle(boolean positive) {
    return positive ? ITEM_BONUS_STYLE : ITEM_BONUS_STYLE_NEGATIVE;
  }

  public static MutableComponent getTextureName(ResourceLocation location) {
    String texture = location.getPath();
    texture = texture.substring(texture.lastIndexOf("/") + 1);
    texture = texture.replace(".png", "");
    texture = TooltipHelper.idToName(texture);
    return Component.literal(texture);
  }

  @NotNull
  public static String idToName(String path) {
    String[] words = path.split("_");
    StringBuilder name = new StringBuilder();
    Arrays.stream(words)
        .map(w -> w.substring(0, 1).toUpperCase() + w.substring(1))
        .forEach(
            w -> {
              name.append(" ");
              name.append(w);
            });
    return name.substring(1);
  }
}
