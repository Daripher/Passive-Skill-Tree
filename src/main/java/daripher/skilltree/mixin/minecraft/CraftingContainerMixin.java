package daripher.skilltree.mixin.minecraft;

import daripher.skilltree.api.PlayerContainer;
import java.util.Optional;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CraftingContainer.class)
public class CraftingContainerMixin implements PlayerContainer {
  private @Shadow @Final AbstractContainerMenu menu;

  @Override
  public Optional<Player> getViewingPlayer() {
    return Optional.ofNullable(menu).flatMap(menu -> ((PlayerContainer) menu).getViewingPlayer());
  }

  @Override
  public void setViewingPlayer(@Nullable Player player) {}
}
