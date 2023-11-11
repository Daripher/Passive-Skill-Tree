package daripher.skilltree.mixin.minecraft;

import daripher.skilltree.api.PlayerContainer;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerMenu.class)
public class AbstractContainerMenuMixin implements PlayerContainer {
  private @Nullable Player player;

  @Inject(method = "clicked", at = @At("HEAD"))
  private void setViewingPlayer(
      int x, int y, ClickType click, Player player, CallbackInfo callback) {
    setViewingPlayer(player);
  }

  @Override
  public Optional<Player> getViewingPlayer() {
    return Optional.ofNullable(player);
  }

  @Override
  public void setViewingPlayer(@Nullable Player player) {
    this.player = player;
  }
}
