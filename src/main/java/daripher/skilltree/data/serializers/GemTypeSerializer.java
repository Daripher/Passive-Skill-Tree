package daripher.skilltree.data.serializers;

import com.google.gson.*;
import daripher.skilltree.item.gem.GemType;
import daripher.skilltree.item.gem.bonus.GemBonusProvider;
import daripher.skilltree.skill.bonus.condition.item.ItemCondition;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;

public class GemTypeSerializer implements JsonSerializer<GemType>, JsonDeserializer<GemType> {
  @Override
  public GemType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    JsonObject jsonObj = (JsonObject) json;
    Map<ItemCondition, GemBonusProvider> bonuses = new HashMap<>();
    jsonObj
        .getAsJsonArray("bonuses")
        .forEach(
            jsonElement -> {
              JsonObject jsonBonus = (JsonObject) jsonElement;
              ItemCondition itemCondition = SerializationHelper.deserializeItemCondition(jsonBonus);
              GemBonusProvider bonusProvider =
                  SerializationHelper.deserializeGemBonusProvider(jsonBonus);
              bonuses.put(itemCondition, bonusProvider);
            });
    ResourceLocation id = new ResourceLocation(jsonObj.get("id").getAsString());
    return new GemType(id, bonuses);
  }

  @Override
  public JsonElement serialize(GemType src, Type typeOfSrc, JsonSerializationContext context) {
    JsonObject json = new JsonObject();
    JsonArray jsonBonuses = new JsonArray();
    src.bonuses()
        .forEach(
            (c, p) -> {
              JsonObject jsonBonus = new JsonObject();
              SerializationHelper.serializeItemCondition(jsonBonus, c);
              SerializationHelper.serializeGemBonusProvider(jsonBonus, p);
              jsonBonuses.add(jsonBonus);
            });
    json.add("bonuses", jsonBonuses);
    json.addProperty("id", src.id().toString());
    return json;
  }
}
