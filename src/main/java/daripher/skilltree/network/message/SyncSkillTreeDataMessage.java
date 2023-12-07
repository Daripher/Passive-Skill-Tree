package daripher.skilltree.network.message;

import daripher.skilltree.client.skill.SkillTreeClientData;
import daripher.skilltree.data.reloader.SkillTreesReloader;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.network.NetworkHelper;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class SyncSkillTreeDataMessage {
  public static SyncSkillTreeDataMessage decode(FriendlyByteBuf buf) {
    NetworkHelper.loadSkillTreeConfig(buf);
    SkillTreeClientData.loadFromByteBuf(buf);
    return new SyncSkillTreeDataMessage();
  }

  public static void receive(
      SyncSkillTreeDataMessage message, Supplier<NetworkEvent.Context> ctxSupplier) {
    ctxSupplier.get().setPacketHandled(true);
  }

  public void encode(FriendlyByteBuf buf) {
    NetworkHelper.writeSkillTreeConfig(buf);
    NetworkHelper.writePassiveSkills(buf, SkillsReloader.getSkills().values());
    NetworkHelper.writePassiveSkillTrees(buf, SkillTreesReloader.getSkillTrees());
  }
}
