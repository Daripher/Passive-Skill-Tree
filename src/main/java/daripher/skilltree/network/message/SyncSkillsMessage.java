package daripher.skilltree.network.message;

import java.util.function.Supplier;

import daripher.skilltree.client.SkillTreeClientData;
import daripher.skilltree.data.SkillsDataReloader;
import daripher.skilltree.skill.PassiveSkill;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class SyncSkillsMessage {
	public static SyncSkillsMessage decode(FriendlyByteBuf buf) {
		SkillTreeClientData.PASSIVE_SKILLS.clear();
		var skillsCount = buf.readInt();

		for (var i = 0; i < skillsCount; i++) {
			var passiveSkill = PassiveSkill.loadFromByteBuf(buf);
			SkillTreeClientData.PASSIVE_SKILLS.put(passiveSkill.getId(), passiveSkill);
		}

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
