package daripher.skilltree.mixin.minecraft;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import daripher.skilltree.container.InteractiveContainer;
import daripher.skilltree.entity.player.PlayerExtension;
import daripher.skilltree.init.PSTItems;
import daripher.skilltree.item.ItemHelper;
import daripher.skilltree.item.gem.GemItem;
import daripher.skilltree.item.gem.bonus.GemBonusProvider;
import daripher.skilltree.item.gem.bonus.RandomGemBonusProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SmithingMenu.class)
public abstract class SmithingMenuMixin extends ItemCombinerMenu {
  @SuppressWarnings("DataFlowIssue")
  private SmithingMenuMixin() {
    super(null, 0, null, null);
  }

  @Inject(
      method =
          "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V",
      at = @At("TAIL"))
  private void setPlayerIntoContainer(
      int windowId,
      Inventory inventory,
      ContainerLevelAccess levelAccess,
      CallbackInfo callbackInfo) {
    ((InteractiveContainer) inputSlots).setUser(inventory.player);
  }

  @Inject(method = "onTake", at = @At("HEAD"))
  private void changeRainbowJewelInsertionSeed(
      Player player, ItemStack itemStack, CallbackInfo callbackInfo) {
    ItemStack gemStack = inputSlots.getItem(1);
    if (gemStack.getItem() != PSTItems.GEM.get()) return;
    GemBonusProvider bonusProvider = GemItem.getGemType(gemStack).getBonusProvider(itemStack);
    if (bonusProvider instanceof RandomGemBonusProvider) {
      ((PlayerExtension) player).updateGemsRandomSeed();
    }
  }

  @Inject(
      method = "createResult",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/world/inventory/ResultContainer;"
                      + "setItem(ILnet/minecraft/world/item/ItemStack;)V",
              shift = At.Shift.BEFORE,
              ordinal = 1))
  private void itemProduced(
      CallbackInfo callbackInfo, @Local(ordinal = 0) LocalRef<ItemStack> stack) {
    ItemHelper.refreshDurabilityBonuses(stack.get());
  }
}
