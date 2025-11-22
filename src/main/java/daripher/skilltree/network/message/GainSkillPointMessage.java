package daripher.skilltree.network.message;

import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.config.ServerConfig;
import daripher.skilltree.exp.ExpHelper;
import daripher.skilltree.network.NetworkDispatcher;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkEvent.Context;
import net.minecraftforge.network.PacketDistributor;

public class GainSkillPointMessage {
  public static void receive(Supplier<NetworkEvent.Context> ctxSupplier) {
    Context ctx = ctxSupplier.get();
    ctx.setPacketHandled(true);
    ServerPlayer player = Objects.requireNonNull(ctx.getSender());
    IPlayerSkills capability = PlayerSkillsProvider.get(player);
    int skills = capability.getPlayerSkills().size();
    int points = capability.getSkillPoints();
    int level = skills + points;
    if (level >= ServerConfig.max_skill_points) {
      return;
    }
    int cost = ServerConfig.getSkillPointCost(level);
    if (ExpHelper.getPlayerExp(player) < cost) {
      return;
    }
    player.giveExperiencePoints(-cost);
    capability.grantSkillPoints(1);
    NetworkDispatcher.network_channel.send(
        PacketDistributor.PLAYER.with(() -> player), new SyncPlayerSkillsMessage(player));
  }
}
