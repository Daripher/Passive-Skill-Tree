package daripher.skilltree.mixin.minecraft;

import daripher.skilltree.skill.bonus.SkillBonusHandler;
import daripher.skilltree.skill.bonus.player.ItemDurabilityLossAvoidanceBonus;
import java.util.function.Consumer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public class ItemStackMixin {
  @Inject(
      method =
          "hurtAndBreak(ILnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;)V",
      at = @At("HEAD"),
      cancellable = true,
      remap = false)
  public void preventDurabilityLoss(
      int amount,
      ServerLevel level,
      @Nullable LivingEntity user,
      Consumer<Item> onBreak,
      CallbackInfo callbackInfo) {
    if (!(user instanceof ServerPlayer player)) {
      return;
    }
    @SuppressWarnings("DataFlowIssue")
    ItemStack itemStack = (ItemStack) (Object) this;
    float chance =
        SkillBonusHandler.getSkillBonuses(player, ItemDurabilityLossAvoidanceBonus.class).stream()
            .map(bonus -> bonus.getChance(player, itemStack))
            .reduce(Float::sum)
            .orElse(0f);
    if (level.getRandom().nextFloat() < chance) {
      callbackInfo.cancel();
    }
  }
}
