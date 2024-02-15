package daripher.skilltree.mixin.minecraft;

import daripher.itemproduction.block.entity.Interactive;

import javax.annotation.Nullable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerMenu.class)
public class AbstractContainerMenuMixin implements Interactive {
  private @Nullable Player player;

  @Inject(method = "clicked", at = @At("HEAD"))
  private void setViewingPlayer(
      int x, int y, ClickType click, Player player, CallbackInfo callback) {
    setUser(player);
  }

  @Override
  public @Nullable Player getUser() {
    return player;
  }

  @Override
  public void setUser(@Nullable Player player) {
    this.player = player;
  }
}
