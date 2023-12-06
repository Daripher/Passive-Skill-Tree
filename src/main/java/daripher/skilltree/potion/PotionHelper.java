package daripher.skilltree.potion;

import daripher.skilltree.config.Config;
import daripher.skilltree.item.ItemHelper;
import daripher.skilltree.skill.bonus.item.PotionAmplificationBonus;
import daripher.skilltree.skill.bonus.item.PotionDurationBonus;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionUtils;

public class PotionHelper {
  private static final String CUSTOM_COLOR_TAG = "CustomPotionColor";
  private static final String MIXED_POTION_TAG = "MixedPotion";

  public static boolean isMixture(ItemStack stack) {
    return stack.hasTag() && Objects.requireNonNull(stack.getTag()).contains(MIXED_POTION_TAG);
  }

  public static boolean isPotion(ItemStack stack) {
    return stack.getItem() instanceof PotionItem;
  }

  public static int getAmplifierBonus(ItemStack stack) {
    return ItemHelper.getItemBonuses(stack, PotionAmplificationBonus.class).stream()
        .map(PotionAmplificationBonus::getChance)
        .reduce(Float::sum)
        .map(int.class::cast)
        .orElse(0);
  }

  public static float getDurationMultiplier(ItemStack stack) {
    return ItemHelper.getItemBonuses(stack, PotionDurationBonus.class).stream()
        .map(PotionDurationBonus::getMultiplier)
        .reduce(Float::sum)
        .orElse(1f);
  }

  public static void setPotionColor(ItemStack itemStack, int color) {
    CompoundTag tag = itemStack.getTag();
    if (tag != null) tag.putInt(CUSTOM_COLOR_TAG, color);
  }

  public static ItemStack mixPotions(ItemStack potion1, ItemStack potion2) {
    List<ItemStack> potions = Arrays.asList(potion1, potion2);
    ItemStack result = new ItemStack(potion1.getItem(), 2);
    List<MobEffectInstance> effects =
        potions.stream()
            .sorted(PotionHelper::comparePotions)
            .map(PotionUtils::getMobEffects)
            .map(PotionHelper::applyMixtureMultipliers)
            .flatMap(List::stream)
            .collect(Collectors.toList());
    PotionUtils.setCustomEffects(result, effects);
    setPotionColor(result, PotionUtils.getColor(potions.get(0)));
    result.getOrCreateTag().putBoolean(MIXED_POTION_TAG, true);
    return result;
  }

  private static List<MobEffectInstance> applyMixtureMultipliers(List<MobEffectInstance> effects) {
    effects.replaceAll(
        effect -> {
          int duration = (int) (effect.getDuration() * Config.mixture_effects_duration);
          int amplifier = (int) (effect.getAmplifier() * Config.mixture_effects_strength);
          return new MobEffectInstance(effect.getEffect(), duration, amplifier);
        });
    return effects;
  }

  private static int comparePotions(ItemStack potionStack1, ItemStack potionStack2) {
    return potionStack1.getDescriptionId().compareTo(potionStack2.getDescriptionId());
  }
}
