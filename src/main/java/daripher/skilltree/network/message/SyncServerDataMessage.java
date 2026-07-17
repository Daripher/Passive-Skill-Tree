package daripher.skilltree.network.message;

import daripher.skilltree.data.reloader.SkillTreesReloader;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.network.NetworkHelper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SyncServerDataMessage implements CustomPacketPayload {
    public static final Type<SyncServerDataMessage> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("skilltree", "sync_server_data"));
    public static final StreamCodec<ByteBuf, SyncServerDataMessage> STREAM_CODEC =
            ByteBufCodecs.BYTE_ARRAY.map(SyncServerDataMessage::new, SyncServerDataMessage::payload);

    private final byte[] payload;

    public SyncServerDataMessage() {
        this(encodeServerData());
    }

    public SyncServerDataMessage(byte[] payload) {
        this.payload = payload;
    }

    public byte[] payload() {
        return payload;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SyncServerDataMessage message, IPayloadContext context) {
        context.enqueueWork(() -> {
            FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.wrappedBuffer(message.payload));
            SkillsReloader.loadFromByteBuf(buffer);
            SkillTreesReloader.loadFromByteBuf(buffer);
        });
    }

    private static byte[] encodeServerData() {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        NetworkHelper.writePassiveSkills(buffer, SkillsReloader.getSkills().values());
        NetworkHelper.writePassiveSkillTrees(buffer, SkillTreesReloader.getSkillTrees().values());
        byte[] payload = new byte[buffer.readableBytes()];
        buffer.readBytes(payload);
        return payload;
    }
}
