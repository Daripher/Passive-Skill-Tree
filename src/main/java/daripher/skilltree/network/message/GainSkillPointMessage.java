package daripher.skilltree.network.message;

import java.util.function.Supplier;

import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.config.Config;
import daripher.skilltree.network.NetworkDispatcher;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

public class GainSkillPointMessage {
	public static GainSkillPointMessage decode(FriendlyByteBuf buf) {
		var message = new GainSkillPointMessage();
		return message;
	}

	public void encode(FriendlyByteBuf buf) {
	}

	public static void receive(GainSkillPointMessage message, Supplier<NetworkEvent.Context> ctxSupplier) {
		var ctx = ctxSupplier.get();
		ctx.setPacketHandled(true);
		var player = ctx.getSender();
		var skillsCapability = PlayerSkillsProvider.get(player);
		var learnedSkills = skillsCapability.getPlayerSkills().size();
		var skillPoints = skillsCapability.getSkillPoints();
		var currentLevel = learnedSkills + skillPoints;
		var levelupCosts = Config.COMMON_CONFIG.getSkillPointCosts();
		if (currentLevel >= levelupCosts.size()) {
			return;
		}
		var levelupCost = levelupCosts.get(currentLevel);
		if (player.totalExperience < levelupCost) {
			return;
		}
		player.giveExperiencePoints(-levelupCost);
		skillsCapability.grantSkillPoints(1);
		NetworkDispatcher.network_channel.send(PacketDistributor.PLAYER.with(() -> player), new SyncPlayerSkillsMessage(player));
	}
}
