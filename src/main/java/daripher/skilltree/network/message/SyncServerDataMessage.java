package daripher.skilltree.network.message;

import daripher.skilltree.data.reloader.SkillTreesReloader;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.network.NetworkHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncServerDataMessage {
  public static SyncServerDataMessage decode(FriendlyByteBuf buf) {
    SkillsReloader.loadFromByteBuf(buf);
    SkillTreesReloader.loadFromByteBuf(buf);
    return new SyncServerDataMessage();
  }

  public static void receive(
      SyncServerDataMessage message, Supplier<NetworkEvent.Context> ctxSupplier) {
    ctxSupplier.get().setPacketHandled(true);
  }

  public void encode(FriendlyByteBuf buf) {
    NetworkHelper.writePassiveSkills(buf, SkillsReloader.getSkills().values());
    NetworkHelper.writePassiveSkillTrees(buf, SkillTreesReloader.getSkillTrees().values());
  }
}
