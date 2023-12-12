package daripher.skilltree.mixin.minecraft;

import daripher.skilltree.entity.player.PlayerExtension;
import daripher.skilltree.container.InteractiveContainer;
import daripher.skilltree.init.PSTItems;
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
    if (inputSlots.getItem(1).getItem() != PSTItems.IRISCITE.get()) return;
    ((PlayerExtension) player).updateGemsRandomSeed();
  }
}
