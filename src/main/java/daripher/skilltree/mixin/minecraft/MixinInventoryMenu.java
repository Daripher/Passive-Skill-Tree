package daripher.skilltree.mixin.minecraft;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(InventoryMenu.class)
public abstract class MixinInventoryMenu extends RecipeBookMenu<CraftingContainer> {
  private @Shadow @Final CraftingContainer craftSlots;
  private @Shadow @Final Player owner;
  private @Shadow @Final ResultContainer resultSlots;

  public MixinInventoryMenu() {
    super(null, 0);
  }

  @Override
  protected boolean moveItemStackTo(
      @NotNull ItemStack itemStack, int fromSlot, int toSlot, boolean beginFromEnd) {
    if (itemStack == resultSlots.getItem(0)) {
      ForgeEventFactory.firePlayerCraftingEvent(owner, itemStack, craftSlots);
    }
    return super.moveItemStackTo(itemStack, fromSlot, toSlot, beginFromEnd);
  }
}
