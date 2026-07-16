package daripher.skilltree.network.message;

import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.config.ServerConfig;
import daripher.skilltree.exp.ExpHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record GainSkillPointMessage() implements CustomPacketPayload {
    public static final Type<GainSkillPointMessage> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("skilltree", "gain_skill_point"));
    public static final StreamCodec<ByteBuf, GainSkillPointMessage> STREAM_CODEC =
            StreamCodec.unit(new GainSkillPointMessage());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(GainSkillPointMessage message, IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            IPlayerSkills capability = PlayerSkillsProvider.get(player);
            int level = capability.getPlayerSkills().size() + capability.getSkillPoints();
            if (level >= ServerConfig.max_skill_points) {
                return;
            }
            int cost = ServerConfig.getSkillPointCost(level);
            if (ExpHelper.getPlayerExp(player) < cost) {
                return;
            }
            player.giveExperiencePoints(-cost);
            capability.grantSkillPoints(1);
            PlayerSkillsProvider.sendPlayerSkills(player);
        });
    }
}
