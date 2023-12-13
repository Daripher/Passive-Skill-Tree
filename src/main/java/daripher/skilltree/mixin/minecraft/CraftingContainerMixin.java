package daripher.skilltree.mixin.minecraft;

import daripher.skilltree.container.InteractiveContainer;
import java.util.Optional;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.TransientCraftingContainer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TransientCraftingContainer.class)
public class CraftingContainerMixin implements InteractiveContainer {
  private @Shadow @Final AbstractContainerMenu menu;

  @Override
  public Optional<Player> getUser() {
    return Optional.ofNullable(menu).flatMap(menu -> ((InteractiveContainer) menu).getUser());
  }

  @Override
  public void setUser(@Nullable Player player) {}
}
