package daripher.skilltree.mixin.minecraft;

import daripher.skilltree.api.PlayerContainer;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockEntity.class)
public class BlockEntityMixin implements PlayerContainer {
  private @Nullable Player player;

  @Override
  public Optional<Player> getViewingPlayer() {
    return Optional.ofNullable(player);
  }

  @Override
  public void setViewingPlayer(@Nullable Player player) {
    this.player = player;
  }
}
