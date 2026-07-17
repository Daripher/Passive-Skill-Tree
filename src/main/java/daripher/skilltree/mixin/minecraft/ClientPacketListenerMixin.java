package daripher.skilltree.mixin.minecraft;

import net.minecraft.client.Minecraft;
import daripher.skilltree.client.screen.StatsUpdateListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundAwardStatsPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {
  @Inject(
      method = "handleAwardStats(Lnet/minecraft/network/protocol/game/ClientboundAwardStatsPacket;)V",
      at = @At("TAIL"),
      remap = false)
  private void notifyStatsScreen(ClientboundAwardStatsPacket packet, CallbackInfo ci) {
    Screen screen = Minecraft.getInstance().screen;
    if (screen instanceof StatsUpdateListener statsUpdateListener) {
      statsUpdateListener.onStatsUpdated();
    }
  }
}
