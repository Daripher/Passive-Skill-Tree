package daripher.skilltree.mixin.minecraft;

import daripher.skilltree.container.InteractiveContainer;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockEntity.class)
public class BlockEntityMixin implements InteractiveContainer {
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
