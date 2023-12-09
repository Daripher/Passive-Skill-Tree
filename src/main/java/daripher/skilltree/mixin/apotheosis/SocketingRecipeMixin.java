package daripher.skilltree.mixin.apotheosis;

import daripher.skilltree.compat.apotheosis.ApotheosisCompatibility;
import daripher.skilltree.container.ContainerHelper;
import daripher.skilltree.item.gem.GemBonusHandler;
import java.util.List;
import java.util.Optional;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import shadows.apotheosis.adventure.affix.socket.SocketHelper;
import shadows.apotheosis.adventure.affix.socket.SocketingRecipe;

@Mixin(value = SocketingRecipe.class, remap = false)
public class SocketingRecipeMixin {
  @Redirect(
      method = {"matches", "m_5818_"},
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lshadows/apotheosis/adventure/affix/socket/SocketHelper;hasEmptySockets(Lnet/minecraft/world/item/ItemStack;)Z"))
  private boolean checkPlayerSockets(ItemStack stack, Container container, Level level) {
    return ContainerHelper.getViewingPlayer(container)
        .map(player -> ApotheosisCompatibility.INSTANCE.hasEmptySockets(stack, player))
        .orElseGet(() -> SocketHelper.hasEmptySockets(stack));
  }

  @Redirect(
      method = {"assemble", "m_5874_"},
      at =
          @At(
              value = "INVOKE",
              target = "Ljava/util/List;set(ILjava/lang/Object;)Ljava/lang/Object;"))
  private Object applyGemPower(List<Object> gems, int index, Object gem, Container container) {
    Optional<Player> player = ContainerHelper.getViewingPlayer(container);
    if (player.isPresent()) {
      ItemStack result = container.getItem(0);
      float power = GemBonusHandler.getGemPower(player.get(), result);
      ((ItemStack) gem).getOrCreateTag().putFloat("gem_power", power);
    }
    return gems.set(index, gem);
  }

  @Redirect(
      method = {"assemble", "m_5874_"},
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lshadows/apotheosis/adventure/affix/socket/SocketHelper;getFirstEmptySocket(Lnet/minecraft/world/item/ItemStack;)I"))
  private int applyPlayerSockets(ItemStack stack, Container container) {
    Optional<Player> player = ContainerHelper.getViewingPlayer(container);
    if (player.isEmpty()) return SocketHelper.getFirstEmptySocket(stack);
    int sockets = ApotheosisCompatibility.INSTANCE.getSockets(stack, player.get());
    return ApotheosisCompatibility.INSTANCE.getFirstEmptySocket(stack, sockets);
  }

  @Redirect(
      method = {"assemble", "m_5874_"},
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lshadows/apotheosis/adventure/affix/socket/SocketHelper;getGems(Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;"))
  private List<ItemStack> applyPlayerSockets2(ItemStack stack, Container container) {
    Optional<Player> player = ContainerHelper.getViewingPlayer(container);
    if (player.isEmpty()) return SocketHelper.getGems(stack);
    int sockets = ApotheosisCompatibility.INSTANCE.getSockets(stack, player.get());
    return ApotheosisCompatibility.INSTANCE.getGems(stack, sockets);
  }
}
