package daripher.skilltree.network;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.network.message.GainSkillPointMessage;
import daripher.skilltree.network.message.LearnSkillMessage;
import daripher.skilltree.network.message.SyncPlayerSkillsMessage;
import daripher.skilltree.network.message.SyncServerDataMessage;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(bus = Bus.MOD, modid = SkillTreeMod.MOD_ID)
public class NetworkDispatcher {
  @SubscribeEvent
  public static void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
    PayloadRegistrar registrar = event.registrar("1");
    registrar.playToClient(
        SyncServerDataMessage.TYPE, SyncServerDataMessage.STREAM_CODEC, SyncServerDataMessage::handle);
    registrar.playToClient(
        SyncPlayerSkillsMessage.TYPE,
        SyncPlayerSkillsMessage.STREAM_CODEC,
        SyncPlayerSkillsMessage::handle);
    registrar.playToServer(
        LearnSkillMessage.TYPE, LearnSkillMessage.STREAM_CODEC, LearnSkillMessage::handle);
    registrar.playToServer(
        GainSkillPointMessage.TYPE, GainSkillPointMessage.STREAM_CODEC, GainSkillPointMessage::handle);
  }
}
