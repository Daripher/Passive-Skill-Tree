package daripher.skilltree.mixin.minecraft;

import daripher.skilltree.container.InteractiveContainer;
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
public class AbstractContainerMenuMixin implements InteractiveContainer {
  private @Nullable Player player;

  @Inject(method = "clicked", at = @At("HEAD"))
  private void setViewingPlayer(
      int x, int y, ClickType click, Player player, CallbackInfo callback) {
    setUser(player);
  }

  @Override
  public Optional<Player> getUser() {
    return Optional.ofNullable(player);
  }

  @Override
  public void setUser(@Nullable Player player) {
    this.player = player;
  }
}
