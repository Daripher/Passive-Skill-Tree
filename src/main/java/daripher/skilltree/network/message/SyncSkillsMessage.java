package daripher.skilltree.network.message;

import java.util.function.Supplier;

import daripher.skilltree.client.SkillTreeClientData;
import daripher.skilltree.data.SkillsDataReloader;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class SyncSkillsMessage {
	public static SyncSkillsMessage decode(FriendlyByteBuf buf) {
		SkillTreeClientData.loadFromByteBuf(buf);
		return new SyncSkillsMessage();
	}

	public void encode(FriendlyByteBuf buf) {
		var skills = SkillsDataReloader.getSkills().values();
		buf.writeInt(skills.size());
		skills.forEach(skill -> skill.writeToByteBuf(buf));
	}

	public static void receive(SyncSkillsMessage message, Supplier<NetworkEvent.Context> ctxSupplier) {
		var ctx = ctxSupplier.get();
		ctx.setPacketHandled(true);
	}
}
