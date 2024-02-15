package daripher.skilltree.mixin.minecraft;

import daripher.itemproduction.block.entity.Interactive;
import javax.annotation.Nullable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CraftingContainer.class)
public class CraftingContainerMixin implements Interactive {
  private @Shadow @Final AbstractContainerMenu menu;

  @Override
  public @Nullable Player getUser() {
    return ((Interactive) menu).getUser();
  }

  @Override
  public void setUser(@Nullable Player player) {}
}
