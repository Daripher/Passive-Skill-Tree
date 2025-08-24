package daripher.skilltree.mixin.minecraft;

import daripher.skilltree.skill.bonus.SkillBonusHandler;
import daripher.skilltree.skill.bonus.player.ItemDurabilityLossAvoidanceBonus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin {
  @Inject(
      method = "hurt",
      at = @At("HEAD"),
      cancellable = true)
  public void preventDurabilityLoss(
      int amount,
      RandomSource random,
      @Nullable ServerPlayer user,
      CallbackInfoReturnable<Boolean> callbackInfo) {
    if (user == null) {
      return;
    }
    float chance = SkillBonusHandler.getSkillBonuses(user, ItemDurabilityLossAvoidanceBonus.class)
        .stream()
        .map(bonus -> bonus.getChance(user))
        .reduce(Float::sum)
        .orElse(0f);
    if (random.nextFloat() < chance) {
      callbackInfo.setReturnValue(false);
    }
  }
}
