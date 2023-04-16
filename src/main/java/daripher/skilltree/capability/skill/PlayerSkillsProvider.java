package daripher.skilltree.capability.skill;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.network.NetworkDispatcher;
import daripher.skilltree.network.message.SyncPlayerSkillsMessage;
import daripher.skilltree.network.message.SyncSkillsMessage;
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
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.network.PacketDistributor;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class PlayerSkillsProvider implements ICapabilitySerializable<CompoundTag> {
	private static final ResourceLocation CAPABILITY_ID = new ResourceLocation(SkillTreeMod.MOD_ID, "player_skills");
	private static final Capability<IPlayerSkills> PLAYER_SKILLS_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
	});
	private LazyOptional<IPlayerSkills> playerSkillsLazyOptional = LazyOptional.of(() -> new PlayerSkills());

	@SubscribeEvent
	public static void attachCapability(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof Player) {
			var capabilityProvider = new PlayerSkillsProvider();
			event.addCapability(CAPABILITY_ID, capabilityProvider);
		}
	}

	@SubscribeEvent
	public static void removePlayerExp(PlayerXpEvent.PickupXp event) {
		if (event.getEntity().level.isClientSide) {
			return;
		}

		if (!event.getOrb().getTags().contains("FromPlayer")) {
			return;
		}

		var player = event.getEntity();
		var playerSkillsData = get(player);
		playerSkillsData.grantExpirience(-event.getOrb().getValue());
	}

	@SubscribeEvent
	public static void grantSkillPoints(PlayerXpEvent.XpChange event) {
		if (event.getAmount() <= 0) {
			return;
		}

		var player = event.getEntity();
		var playerSkillsData = get(player);
		var skillPoints = playerSkillsData.getSkillPoints();
		playerSkillsData.grantExpirience(event.getAmount());

		if (skillPoints != playerSkillsData.getSkillPoints()) {
			player.sendSystemMessage(Component.translatable("skilltree.message.skillpoint").withStyle(ChatFormatting.YELLOW));
			NetworkDispatcher.network_channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new SyncPlayerSkillsMessage(player));
		}
	}

	@SubscribeEvent
	public static void persistThroughDeath(PlayerEvent.Clone event) {
		if (event.getEntity().level.isClientSide) {
			return;
		}

		if (event.isWasDeath()) {
			event.getOriginal().reviveCaps();
			var originalData = get(event.getOriginal());
			var cloneData = get(event.getEntity());
			cloneData.deserializeNBT(originalData.serializeNBT());
		}
	}

	@SubscribeEvent
	public static void syncSkills(PlayerLoggedInEvent event) {
		if (!event.getEntity().level.isClientSide) {
			NetworkDispatcher.network_channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getEntity()), new SyncSkillsMessage());
		}
	}

	@SubscribeEvent
	public static void syncPlayerSkills(EntityJoinLevelEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}

		var player = (Player) event.getEntity();

		if (!event.getEntity().level.isClientSide) {
			NetworkDispatcher.network_channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new SyncPlayerSkillsMessage(player));
		}
	}

	@Override
	public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		return cap == PLAYER_SKILLS_CAPABILITY ? playerSkillsLazyOptional.cast() : LazyOptional.empty();
	}

	@Override
	public CompoundTag serializeNBT() {
		return playerSkillsLazyOptional.orElseThrow(NullPointerException::new).serializeNBT();
	}

	@Override
	public void deserializeNBT(CompoundTag compoundTag) {
		playerSkillsLazyOptional.orElseThrow(NullPointerException::new).deserializeNBT(compoundTag);
	}

	public static @NotNull IPlayerSkills get(Player player) {
		return player.getCapability(PLAYER_SKILLS_CAPABILITY).orElseThrow(NullPointerException::new);
	}
}
