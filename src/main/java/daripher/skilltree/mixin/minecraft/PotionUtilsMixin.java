package daripher.skilltree.mixin.minecraft;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import daripher.skilltree.potion.PotionHelper;
import java.util.List;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PotionUtils.class)
public class PotionUtilsMixin {
  @ModifyReturnValue(method = "getMobEffects", at = @At("RETURN"))
  private static List<MobEffectInstance> applyEffectsModifiers(
      List<MobEffectInstance> original, ItemStack stack) {
    float durationMultiplier = PotionHelper.getDurationMultiplier(stack);
    int amplifierBonus = PotionHelper.getAmplifierBonus(stack);
    if (durationMultiplier == 1f && amplifierBonus == 0) return original;
    original.replaceAll(
        effect -> {
          int duration = (int) (effect.getDuration() * durationMultiplier);
          int amplifier = effect.getAmplifier() + amplifierBonus;
          return new MobEffectInstance(effect.getEffect(), duration, amplifier);
        });
    return original;
  }
}
