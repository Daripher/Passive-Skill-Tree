package daripher.skilltree.network.message;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.client.screen.SkillTreeScreen;
import daripher.skilltree.client.skill.SkillTreeClientData;
import daripher.skilltree.skill.PassiveSkill;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public class SyncPlayerSkillsMessage {
  private List<ResourceLocation> learnedSkills = new ArrayList<>();
  private int skillPoints;

  private SyncPlayerSkillsMessage() {}

  public SyncPlayerSkillsMessage(Player player) {
    var skillsCapability = PlayerSkillsProvider.get(player);
    learnedSkills = skillsCapability.getPlayerSkills().stream().map(PassiveSkill::getId).toList();
    skillPoints = skillsCapability.getSkillPoints();
  }

  public static SyncPlayerSkillsMessage decode(FriendlyByteBuf buf) {
    var result = new SyncPlayerSkillsMessage();
    var learnedSkillsCount = buf.readInt();
    for (int i = 0; i < learnedSkillsCount; i++) {
      result.learnedSkills.add(new ResourceLocation(buf.readUtf()));
    }
    result.skillPoints = buf.readInt();
    return result;
  }

  public static void receive(
      SyncPlayerSkillsMessage message, Supplier<NetworkEvent.Context> ctxSupplier) {
    var ctx = ctxSupplier.get();
    ctx.setPacketHandled(true);
    ctx.enqueueWork(
        () -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> handlePacket(message, ctx)));
  }

  @OnlyIn(value = Dist.CLIENT)
  private static void handlePacket(SyncPlayerSkillsMessage message, NetworkEvent.Context ctx) {
    var minecraft = Minecraft.getInstance();
    var skillsCapability = PlayerSkillsProvider.get(minecraft.player);
    skillsCapability.getPlayerSkills().clear();
    // TODO: replace with actual tree
    var skillTreeId = new ResourceLocation(SkillTreeMod.MOD_ID, "main_tree");
    message.learnedSkills.stream()
        .map(SkillTreeClientData::getSkill)
        .forEach(skillsCapability.getPlayerSkills()::add);
    skillsCapability.setSkillPoints(message.skillPoints);
    if (minecraft.screen instanceof SkillTreeScreen skillTreeScreen) {
      skillTreeScreen.skillPoints = skillsCapability.getSkillPoints();
      skillTreeScreen.init();
    }
  }

  public void encode(FriendlyByteBuf buf) {
    buf.writeInt(learnedSkills.size());
    learnedSkills.stream().map(ResourceLocation::toString).forEach(buf::writeUtf);
    buf.writeInt(skillPoints);
  }
}
