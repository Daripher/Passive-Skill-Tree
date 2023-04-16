package daripher.skilltree.network;

import java.util.Optional;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.network.message.LearnSkillMessage;
import daripher.skilltree.network.message.SyncPlayerSkillsMessage;
import daripher.skilltree.network.message.SyncSkillsMessage;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

@EventBusSubscriber(bus = Bus.MOD, modid = SkillTreeMod.MOD_ID)
public class NetworkDispatcher {
	public static SimpleChannel network_channel;

	@SubscribeEvent
	public static void registerNetworkChannel(FMLCommonSetupEvent event) {
		network_channel = NetworkRegistry.newSimpleChannel(new ResourceLocation(SkillTreeMod.MOD_ID, "channel"), () -> "1.0", s -> true, s -> true);
		network_channel.registerMessage(1, SyncSkillsMessage.class, SyncSkillsMessage::encode, SyncSkillsMessage::decode, SyncSkillsMessage::receive, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		network_channel.registerMessage(2, SyncPlayerSkillsMessage.class, SyncPlayerSkillsMessage::encode, SyncPlayerSkillsMessage::decode, SyncPlayerSkillsMessage::receive, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		network_channel.registerMessage(3, LearnSkillMessage.class, LearnSkillMessage::encode, LearnSkillMessage::decode, LearnSkillMessage::receive, Optional.of(NetworkDirection.PLAY_TO_SERVER));
	}
}
