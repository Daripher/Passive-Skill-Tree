package daripher.skilltree.data.serializers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public interface Serializer<T> {
  T deserialize(JsonObject json) throws JsonParseException;

  void serialize(JsonObject json, T object);

  T deserialize(CompoundTag tag);

  CompoundTag serialize(T object);

  T deserialize(FriendlyByteBuf buf);

  void serialize(FriendlyByteBuf buf, T object);
}
