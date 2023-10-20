package daripher.skilltree.data.serializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.UUID;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class AttributeModifierSerializer
    implements JsonSerializer<AttributeModifier>, JsonDeserializer<AttributeModifier> {
  @Override
  public AttributeModifier deserialize(
      JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    JsonObject jsonObj = (JsonObject) json;
    UUID id = UUID.fromString(jsonObj.get("id").getAsString());
    String name = jsonObj.get("nameGetter").getAsString();
    Operation operation = Operation.valueOf(jsonObj.get("operation").getAsString());
    double amount = jsonObj.get("amount").getAsDouble();
    return new AttributeModifier(id, () -> name, amount, operation);
  }

  @Override
  public JsonElement serialize(
      AttributeModifier src, Type typeOfSrc, JsonSerializationContext context) {
    JsonObject json = new JsonObject();
    json.addProperty("id", src.getId().toString());
    json.addProperty("nameGetter", src.getName());
    json.addProperty("operation", src.getOperation().name());
    json.addProperty("amount", src.getAmount());
    return json;
  }
}
