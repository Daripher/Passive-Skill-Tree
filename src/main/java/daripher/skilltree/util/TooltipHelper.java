package daripher.skilltree.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;

public class TooltipHelper {
  public static MutableComponent getEffectTooltip(MobEffectInstance effectInstance) {
    MutableComponent tooltip = Component.translatable(effectInstance.getDescriptionId());
    MobEffect effect = effectInstance.getEffect();
    if (effectInstance.getAmplifier() > 0) {
      MutableComponent amplifier =
          Component.translatable("potion.potency." + effectInstance.getAmplifier());
      tooltip = Component.translatable("potion.withAmplifier", tooltip, amplifier);
    }
    if (effectInstance.getDuration() > 20) {
      String duration = MobEffectUtil.formatDuration(effectInstance, 1F);
      tooltip = Component.translatable("potion.withDuration", tooltip, duration);
    }
    tooltip = tooltip.withStyle(effect.getCategory().getTooltipFormatting());
    return Component.literal(" ").append(tooltip);
  }
}
