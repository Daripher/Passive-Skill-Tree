package daripher.skilltree.mixin.minecraft;

import daripher.skilltree.api.PlayerContainer;
import daripher.skilltree.util.FoodHelper;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CampfireBlockEntity.class)
public class MixinCampfireBlockEntity implements PlayerContainer {
  private @Shadow @Final NonNullList<ItemStack> items;
  private Optional<Player> player = Optional.empty();

  @Redirect(
      method = "cookTick",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/world/Containers;dropItemStack(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemStack;)V"))
  private static void setCookedFoodBonuses(
      Level level, double x, double y, double z, ItemStack itemStack) {
    BlockPos pos = new BlockPos(x, y, z);
    BlockEntity blockEntity = level.getBlockEntity(pos);
    Optional<Player> player = Optional.empty();
    if (blockEntity instanceof PlayerContainer playerContainer) {
      player = playerContainer.getViewingPlayer();
    }
    if (player.isPresent()) {
      FoodHelper.setCraftedFoodBonuses(itemStack, player.get());
      Containers.dropItemStack(level, x, y, z, itemStack);
    }
  }

  @Override
  public Optional<Player> getViewingPlayer() {
    return player;
  }

  @Override
  public void setViewingPlayer(Optional<Player> player) {
    this.player = player;
  }
}
