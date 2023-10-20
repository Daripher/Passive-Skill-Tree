package daripher.skilltree.network.message;

import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.config.Config;
import daripher.skilltree.network.NetworkDispatcher;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkEvent.Context;
import net.minecraftforge.network.PacketDistributor;

public class GainSkillPointMessage {
  public static GainSkillPointMessage decode(FriendlyByteBuf buf) {
    return new GainSkillPointMessage();
  }

  public static void receive(
      GainSkillPointMessage message, Supplier<NetworkEvent.Context> ctxSupplier) {
    Context ctx = ctxSupplier.get();
    ctx.setPacketHandled(true);
    ServerPlayer player = ctx.getSender();
    IPlayerSkills capability = PlayerSkillsProvider.get(player);
    int skills = capability.getPlayerSkills().size();
    int points = capability.getSkillPoints();
    int level = skills + points;
    if (level >= Config.max_skill_points) return;
    int cost = Config.getSkillPointCost(level);
    if (player.totalExperience < cost) return;
    player.giveExperiencePoints(-cost);
    capability.grantSkillPoints(1);
    NetworkDispatcher.network_channel.send(
        PacketDistributor.PLAYER.with(() -> player), new SyncPlayerSkillsMessage(player));
  }

  public void encode(FriendlyByteBuf buf) {}
}
