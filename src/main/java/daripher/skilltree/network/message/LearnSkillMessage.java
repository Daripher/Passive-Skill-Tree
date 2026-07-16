package daripher.skilltree.network.message;

import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.skill.PassiveSkill;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record LearnSkillMessage(ResourceLocation skillId) implements CustomPacketPayload {
    public static final Type<LearnSkillMessage> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("skilltree", "learn_skill"));
    public static final StreamCodec<ByteBuf, LearnSkillMessage> STREAM_CODEC =
            ResourceLocation.STREAM_CODEC.map(LearnSkillMessage::new, LearnSkillMessage::skillId);

    public LearnSkillMessage(PassiveSkill passiveSkill) {
        this(passiveSkill.getId());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(LearnSkillMessage message, IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            IPlayerSkills capability = PlayerSkillsProvider.get(player);
            PassiveSkill skill = Objects.requireNonNull(SkillsReloader.getSkillById(message.skillId()));
            if (capability.learnSkill(skill)) {
                skill.learn(player, true);
            }
            PlayerSkillsProvider.sendPlayerSkills(player);
        });
    }
}
