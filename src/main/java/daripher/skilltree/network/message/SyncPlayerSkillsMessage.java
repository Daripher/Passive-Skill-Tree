package daripher.skilltree.network.message;

import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.skill.PassiveSkill;
import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.Objects;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncPlayerSkillsMessage(List<ResourceLocation> learnedSkills, int skillPoints)
    implements CustomPacketPayload {
  private static final String CLIENT_HANDLERS_CLASS =
      "daripher.skilltree.client.network.ClientNetworkPayloadHandlers";

  public static final Type<SyncPlayerSkillsMessage> TYPE =
      new Type<>(ResourceLocation.fromNamespaceAndPath("skilltree", "sync_player_skills"));
  public static final StreamCodec<ByteBuf, SyncPlayerSkillsMessage> STREAM_CODEC =
      StreamCodec.composite(
          ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()),
          SyncPlayerSkillsMessage::learnedSkills,
          ByteBufCodecs.VAR_INT,
          SyncPlayerSkillsMessage::skillPoints,
          SyncPlayerSkillsMessage::new);

  public SyncPlayerSkillsMessage(Player player) {
    this(
        PlayerSkillsProvider.get(player).getPlayerSkills().stream().map(PassiveSkill::getId).toList(),
        PlayerSkillsProvider.get(player).getSkillPoints());
  }

  @Override
  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }

  public static void handle(SyncPlayerSkillsMessage message, IPayloadContext context) {
    context.enqueueWork(
        () -> {
          Player player = context.player();
          if (player == null) return;
          IPlayerSkills capability = PlayerSkillsProvider.get(player);
          capability.getPlayerSkills().clear();
          message.learnedSkills().stream()
              .map(SkillsReloader::getSkillById)
              .filter(Objects::nonNull)
              .forEach(capability.getPlayerSkills()::add);
          capability.setSkillPoints(message.skillPoints());
          refreshSkillTreeScreen(capability.getSkillPoints());
        });
  }

  private static void refreshSkillTreeScreen(int skillPoints) {
    if (FMLEnvironment.dist != Dist.CLIENT) return;
    try {
      Class.forName(CLIENT_HANDLERS_CLASS)
          .getMethod("refreshSkillTreeScreen", int.class)
          .invoke(null, skillPoints);
    } catch (ReflectiveOperationException exception) {
      throw new IllegalStateException("Failed to refresh skill tree screen", exception);
    }
  }
}
