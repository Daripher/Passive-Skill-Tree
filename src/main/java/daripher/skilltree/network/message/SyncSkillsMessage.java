package daripher.skilltree.network.message;

import java.util.function.Supplier;

import daripher.skilltree.client.skill.SkillTreeClientData;
import daripher.skilltree.data.reloader.SkillTreesReloader;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.util.ByteBufHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class SyncSkillsMessage {
	public static SyncSkillsMessage decode(FriendlyByteBuf buf) {
		SkillTreeClientData.loadFromByteBuf(buf);
		return new SyncSkillsMessage();
	}

	public void encode(FriendlyByteBuf buf) {
		ByteBufHelper.writePassiveSkills(buf, SkillsReloader.getSkills().values());
		ByteBufHelper.writePassiveSkillTrees(buf, SkillTreesReloader.getSkillTrees());
	}

	public static void receive(SyncSkillsMessage message, Supplier<NetworkEvent.Context> ctxSupplier) {
		ctxSupplier.get().setPacketHandled(true);
	}
}
