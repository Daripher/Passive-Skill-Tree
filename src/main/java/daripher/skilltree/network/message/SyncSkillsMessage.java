package daripher.skilltree.network.message;

import java.util.function.Supplier;

import daripher.skilltree.client.SkillTreeClientData;
import daripher.skilltree.skill.SkillsReloader;
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
	}

	public static void receive(SyncSkillsMessage message, Supplier<NetworkEvent.Context> ctxSupplier) {
		ctxSupplier.get().setPacketHandled(true);
	}
}
