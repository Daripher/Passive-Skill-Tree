package daripher.skilltree.mixin.apotheosis;

import daripher.skilltree.compat.apotheosis.ApotheosisCompatibility;
import java.util.List;

import dev.shadowsoffire.apotheosis.adventure.client.AdventureModuleClient;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = AdventureModuleClient.class, remap = false)
public class AdventureModuleClientMixin {
  @Redirect(
      method = "comps",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Ldev/shadowsoffire/apotheosis/adventure/affix/socket/SocketHelper;getGems(Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;"))
  private static List<ItemStack> showPlayerSockets(ItemStack stack) {
    Minecraft minecraft = Minecraft.getInstance();
    int sockets = ApotheosisCompatibility.INSTANCE.getSockets(stack, minecraft.player);
    return ApotheosisCompatibility.INSTANCE.getGems(stack, sockets);
  }

  @Redirect(
      method = "comps",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Ldev/shadowsoffire/apotheosis/adventure/affix/socket/SocketHelper;getSockets(Lnet/minecraft/world/item/ItemStack;)I"))
  private static int addPlayerSockets(ItemStack stack) {
    return ApotheosisCompatibility.INSTANCE.getSockets(stack, Minecraft.getInstance().player);
  }

  @Redirect(
      method = "tooltips",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Ldev/shadowsoffire/apotheosis/adventure/affix/socket/SocketHelper;getSockets(Lnet/minecraft/world/item/ItemStack;)I"))
  private static int addPlayerSockets2(ItemStack stack) {
    return ApotheosisCompatibility.INSTANCE.getSockets(stack, Minecraft.getInstance().player);
  }
}
