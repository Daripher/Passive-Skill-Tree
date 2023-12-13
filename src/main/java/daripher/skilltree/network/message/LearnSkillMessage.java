package daripher.skilltree.network.message;

import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.network.NetworkDispatcher;
import daripher.skilltree.skill.PassiveSkill;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkEvent.Context;
import net.minecraftforge.network.PacketDistributor;

public class LearnSkillMessage {
  private ResourceLocation skillId;

  public LearnSkillMessage(PassiveSkill passiveSkill) {
    skillId = passiveSkill.getId();
  }

  private LearnSkillMessage() {}

  public static LearnSkillMessage decode(FriendlyByteBuf buf) {
    LearnSkillMessage message = new LearnSkillMessage();
    message.skillId = new ResourceLocation(buf.readUtf());
    return message;
  }

  public static void receive(
      LearnSkillMessage message, Supplier<NetworkEvent.Context> ctxSupplier) {
    Context ctx = ctxSupplier.get();
    ctx.setPacketHandled(true);
    ServerPlayer player = ctx.getSender();
    Objects.requireNonNull(player);
    IPlayerSkills capability = PlayerSkillsProvider.get(player);
    PassiveSkill skill = SkillsReloader.getSkillById(message.skillId);
    Objects.requireNonNull(skill);
    if (capability.learnSkill(skill)) {
      skill.learn(player, true);
    }
    NetworkDispatcher.network_channel.send(
        PacketDistributor.PLAYER.with(() -> player), new SyncPlayerSkillsMessage(player));
  }

  public void encode(FriendlyByteBuf buf) {
    buf.writeUtf(skillId.toString());
  }
}
