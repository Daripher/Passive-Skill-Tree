package daripher.skilltree.data.serializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.function.Supplier;

/** Generic class to serialize and deserialize {@link Supplier} objects in JSON format. */
public class SupplierSerializer<T>
    implements JsonSerializer<Supplier<T>>, JsonDeserializer<Supplier<T>> {
  private final Type type;

  public SupplierSerializer(Class<T> type) {
    this.type = type;
  }

  @Override
  public Supplier<T> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    return () -> context.deserialize(json, this.type);
  }

  @Override
  public JsonElement serialize(Supplier<T> src, Type typeOfSrc, JsonSerializationContext context) {
    return context.serialize(src.get(), this.type);
  }
}
