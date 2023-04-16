package daripher.skilltree.network.message;

import java.util.function.Supplier;

import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public class SyncPlayerSkillsMessage {
	private CompoundTag skillsTag;

	private SyncPlayerSkillsMessage() {
	}

	public SyncPlayerSkillsMessage(Player player) {
		skillsTag = PlayerSkillsProvider.get(player).serializeNBT();
	}

	public static SyncPlayerSkillsMessage decode(FriendlyByteBuf buf) {
		var result = new SyncPlayerSkillsMessage();
		result.skillsTag = buf.readAnySizeNbt();
		return result;
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeNbt(skillsTag);
	}

	public static void receive(SyncPlayerSkillsMessage message, Supplier<NetworkEvent.Context> ctxSupplier) {
		var ctx = ctxSupplier.get();
		ctx.setPacketHandled(true);
		ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> handlePacket(message, ctx)));
	}

	@OnlyIn(value = Dist.CLIENT)
	private static void handlePacket(SyncPlayerSkillsMessage message, NetworkEvent.Context ctx) {
		var minecraft = Minecraft.getInstance();
		PlayerSkillsProvider.get(minecraft.player).deserializeNBT(message.skillsTag);
	}
}
