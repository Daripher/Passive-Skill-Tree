package daripher.skilltree.mixin.minecraft;

import daripher.itemproduction.block.entity.Interactive;
import javax.annotation.Nullable;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SimpleContainer.class)
public class SimpleContainerMixin implements Interactive {
  private @Nullable Player player;

  @Override
  public @Nullable Player getUser() {
    return player;
  }

  @Override
  public void setUser(@Nullable Player player) {
    this.player = player;
  }
}
