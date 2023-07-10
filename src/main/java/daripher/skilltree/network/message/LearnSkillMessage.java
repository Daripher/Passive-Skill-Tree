package daripher.skilltree.network.message;

import java.util.function.Supplier;

import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.data.SkillsReloader;
import daripher.skilltree.network.NetworkDispatcher;
import daripher.skilltree.skill.PassiveSkill;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

public class LearnSkillMessage {
	private ResourceLocation skillId;

	public LearnSkillMessage(PassiveSkill passiveSkill) {
		skillId = passiveSkill.getId();
	}

	private LearnSkillMessage() {
	}

	public static LearnSkillMessage decode(FriendlyByteBuf buf) {
		var message = new LearnSkillMessage();
		message.skillId = new ResourceLocation(buf.readUtf());
		return message;
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeUtf(skillId.toString());
	}

	public static void receive(LearnSkillMessage message, Supplier<NetworkEvent.Context> ctxSupplier) {
		var ctx = ctxSupplier.get();
		ctx.setPacketHandled(true);
		var player = ctx.getSender();
		var skillsData = PlayerSkillsProvider.get(player);
		var passiveSkill = SkillsReloader.getSkillById(message.skillId);
		var learnedSkill = skillsData.learnSkill(player, passiveSkill);
		if (learnedSkill) {
			passiveSkill.getAttributeModifiers().forEach(pair -> {
				var attribute = pair.getLeft();
				var modifier = pair.getRight();
				var playerAttribute = player.getAttribute(attribute);
				if (!playerAttribute.hasModifier(modifier)) {
					playerAttribute.addTransientModifier(modifier);
				}
			});
		}
		NetworkDispatcher.network_channel.send(PacketDistributor.PLAYER.with(() -> player), new SyncPlayerSkillsMessage(player));
	}
}
