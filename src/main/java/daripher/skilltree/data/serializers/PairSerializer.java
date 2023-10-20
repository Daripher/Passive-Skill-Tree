package daripher.skilltree.data.serializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Generic class for serialization and deserialization of key-value {@link Pair} objects in JSON
 * format.
 */
public class PairSerializer<K, V>
    implements JsonSerializer<Pair<K, V>>, JsonDeserializer<Pair<K, V>> {
  private final String keyName;
  private final String valueName;
  private final Type keyType;
  private final Type valueType;

  public PairSerializer(String keyName, String valueName, Class<K> keyType, Class<V> valueType) {
    this.keyName = keyName;
    this.valueName = valueName;
    this.keyType = keyType;
    this.valueType = valueType;
  }

  @Override
  public Pair<K, V> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    JsonObject jsonObject = json.getAsJsonObject();
    K key = context.deserialize(jsonObject.get(keyName), keyType);
    V value = context.deserialize(jsonObject.get(valueName), valueType);
    return Pair.of(key, value);
  }

  @Override
  public JsonElement serialize(Pair<K, V> src, Type typeOfSrc, JsonSerializationContext context) {
    JsonObject json = new JsonObject();
    json.add(keyName, context.serialize(src.getLeft(), keyType));
    json.add(valueName, context.serialize(src.getRight(), valueType));
    return json;
  }
}
