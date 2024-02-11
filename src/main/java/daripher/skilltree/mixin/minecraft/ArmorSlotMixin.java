package daripher.skilltree.mixin.minecraft;

import daripher.skilltree.skill.bonus.SkillBonusHandler;
import daripher.skilltree.skill.bonus.player.CantUseItemBonus;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.inventory.InventoryMenu$1")
public abstract class ArmorSlotMixin extends Slot {
  @SuppressWarnings("DataFlowIssue")
  public ArmorSlotMixin() {
    super(null, 0, 0, 0);
  }

  @Inject(method = "mayPlace", at = @At("HEAD"), cancellable = true)
  private void preventItemUsage(ItemStack stack, CallbackInfoReturnable<Boolean> callbackInfo) {
    if (!(container instanceof Inventory inventory)) return;
    for (CantUseItemBonus bonus :
        SkillBonusHandler.getSkillBonuses(inventory.player, CantUseItemBonus.class)) {
      if (bonus.getItemCondition().met(stack)) {
        callbackInfo.setReturnValue(false);
        return;
      }
    }
  }
}
