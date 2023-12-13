package daripher.skilltree.capability.skill;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.network.NetworkDispatcher;
import daripher.skilltree.network.message.SyncPlayerSkillsMessage;
import daripher.skilltree.network.message.SyncSkillTreeDataMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class PlayerSkillsProvider implements ICapabilitySerializable<CompoundTag> {
  private static final ResourceLocation CAPABILITY_ID =
      new ResourceLocation(SkillTreeMod.MOD_ID, "player_skills");
  private static final Capability<IPlayerSkills> CAPABILITY =
      CapabilityManager.get(new CapabilityToken<>() {});
  private final LazyOptional<IPlayerSkills> optionalCapability = LazyOptional.of(PlayerSkills::new);

  @SubscribeEvent
  public static void attachCapability(AttachCapabilitiesEvent<Entity> event) {
    if (!(event.getObject() instanceof Player)) return;
    PlayerSkillsProvider provider = new PlayerSkillsProvider();
    event.addCapability(CAPABILITY_ID, provider);
  }

  @SubscribeEvent
  public static void persistThroughDeath(PlayerEvent.Clone event) {
    if (event.getEntity().level.isClientSide) return;
    event.getOriginal().reviveCaps();
    IPlayerSkills originalData = get(event.getOriginal());
    IPlayerSkills cloneData = get(event.getEntity());
    cloneData.deserializeNBT(originalData.serializeNBT());
    event.getOriginal().invalidateCaps();
  }

  @SubscribeEvent
  public static void syncSkills(PlayerLoggedInEvent event) {
    if (event.getEntity().level.isClientSide) return;
    NetworkDispatcher.network_channel.send(
        PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getEntity()),
        new SyncSkillTreeDataMessage());
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public static void restoreSkillsAttributeModifiers(EntityJoinLevelEvent event) {
    if (!(event.getEntity() instanceof ServerPlayer player)) return;
    get(player).getPlayerSkills().forEach(skill -> skill.learn(player, false));
  }

  @SubscribeEvent
  public static void sendTreeResetMessage(EntityJoinLevelEvent event) {
    if (!(event.getEntity() instanceof Player player)) return;
    if (event.getEntity().level.isClientSide) return;
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
    NetworkDispatcher.network_channel.send(
        PacketDistributor.PLAYER.with(() -> player), new SyncPlayerSkillsMessage(player));
  }

  public static @NotNull IPlayerSkills get(Player player) {
    return player.getCapability(CAPABILITY).orElseThrow(NullPointerException::new);
  }

  public static boolean hasSkills(Player player) {
    return player.getCapability(CAPABILITY).isPresent();
  }

  @Override
  public <T> @NotNull LazyOptional<T> getCapability(
      @NotNull Capability<T> cap, @Nullable Direction side) {
    return cap == CAPABILITY ? optionalCapability.cast() : LazyOptional.empty();
  }

  @Override
  public CompoundTag serializeNBT() {
    return optionalCapability.orElseThrow(NullPointerException::new).serializeNBT();
  }

  @Override
  public void deserializeNBT(CompoundTag compoundTag) {
    optionalCapability.orElseThrow(NullPointerException::new).deserializeNBT(compoundTag);
  }
}
