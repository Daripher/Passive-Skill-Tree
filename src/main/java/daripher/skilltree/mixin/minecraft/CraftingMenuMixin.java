package daripher.skilltree.mixin.minecraft;

import daripher.skilltree.container.InteractiveContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CraftingMenu.class)
public abstract class CraftingMenuMixin extends RecipeBookMenu<CraftingContainer>
    implements InteractiveContainer {
  private @Shadow @Final CraftingContainer craftSlots;
  private @Shadow @Final Player player;
  private @Shadow @Final ResultContainer resultSlots;

  @SuppressWarnings("DataFlowIssue")
  public CraftingMenuMixin() {
    super(null, 0);
  }

  @Override
  protected boolean moveItemStackTo(
      @NotNull ItemStack itemStack, int fromSlot, int toSlot, boolean beginFromEnd) {
    if (itemStack == resultSlots.getItem(0)) {
      ForgeEventFactory.firePlayerCraftingEvent(player, itemStack, craftSlots);
    }
    return super.moveItemStackTo(itemStack, fromSlot, toSlot, beginFromEnd);
  }
}
