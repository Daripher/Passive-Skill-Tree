package daripher.skilltree.capability.skill;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.init.PSTAttachments;
import daripher.skilltree.network.message.SyncPlayerSkillsMessage;
import daripher.skilltree.network.message.SyncServerDataMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class PlayerSkillsProvider {
  @SubscribeEvent
  public static void copySkills(PlayerEvent.Clone event) {
    if (event.getEntity().level().isClientSide) return;
    PlayerSkills originalData = get(event.getOriginal());
    PlayerSkills cloneData = get(event.getEntity());
    cloneData.deserializeNBT(
        event.getEntity().level().registryAccess(),
        originalData.serializeNBT(event.getOriginal().level().registryAccess()));
  }

  @SubscribeEvent
  public static void syncServerData(PlayerLoggedInEvent event) {
    if (event.getEntity().level().isClientSide) return;
    sendServerData((ServerPlayer) event.getEntity());
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public static void restoreSkillsAttributeModifiers(EntityJoinLevelEvent event) {
    if (!(event.getEntity() instanceof ServerPlayer player)) return;
    get(player).getPlayerSkills().forEach(skill -> skill.learn(player, false));
  }

  @SubscribeEvent
  public static void sendTreeResetMessage(EntityJoinLevelEvent event) {
    if (!(event.getEntity() instanceof Player player)) return;
    if (event.getEntity().level().isClientSide) return;
    IPlayerSkills capability = get(player);
    if (capability.isTreeReset()) {
      player.sendSystemMessage(
          Component.translatable("skilltree.message.reset").withStyle(ChatFormatting.YELLOW));
      capability.setTreeReset(false);
    }
  }

  @SubscribeEvent
  public static void syncPlayerSkills(EntityJoinLevelEvent event) {
    if (!(event.getEntity() instanceof ServerPlayer player)) return;
    sendPlayerSkills(player);
  }

  public static void sendServerData(ServerPlayer player) {
    PacketDistributor.sendToPlayer(player, new SyncServerDataMessage());
  }

  public static void sendPlayerSkills(ServerPlayer player) {
    PacketDistributor.sendToPlayer(player, new SyncPlayerSkillsMessage(player));
  }

  public static @NotNull PlayerSkills get(Player player) {
    return player.getData(PSTAttachments.PLAYER_SKILLS.get());
  }

  public static boolean hasSkills(@NotNull Player player) {
    return player.hasData(PSTAttachments.PLAYER_SKILLS.get());
  }
}
