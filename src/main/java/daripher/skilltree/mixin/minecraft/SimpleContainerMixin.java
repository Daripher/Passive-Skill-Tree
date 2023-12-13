package daripher.skilltree.mixin.minecraft;

import daripher.skilltree.container.InteractiveContainer;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SimpleContainer.class)
public class SimpleContainerMixin implements InteractiveContainer {
  private @Nullable Player player;

  @Override
  public Optional<Player> getUser() {
    return Optional.ofNullable(player);
  }

  @Override
  public void setUser(@Nullable Player player) {
    this.player = player;
  }
}
