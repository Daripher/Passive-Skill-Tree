package daripher.skilltree.mixin.minecraft;

import daripher.skilltree.potion.PotionHelper;
import java.util.List;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PotionUtils.class)
public class PotionUtilsMixin {
  @Inject(method = "getMobEffects", at = @At("RETURN"))
  private static void applySuperiorPotionBonuses(
      ItemStack stack, CallbackInfoReturnable<List<MobEffectInstance>> callback) {
    float durationMultiplier = PotionHelper.getDurationMultiplier(stack);
    int amplifierBonus = PotionHelper.getAmplifierBonus(stack);
    if (durationMultiplier == 1f && amplifierBonus == 0) return;
    enhanceEffects(callback.getReturnValue(), durationMultiplier, amplifierBonus);
  }

  private static void enhanceEffects(
      List<MobEffectInstance> effects, float durationMultiplier, int amplifierBonus) {
    effects.replaceAll(
        effect -> {
          int duration = (int) (effect.getDuration() * durationMultiplier);
          int amplifier = effect.getAmplifier() + amplifierBonus;
          return new MobEffectInstance(effect.getEffect(), duration, amplifier);
        });
  }
}
