package daripher.skilltree.data.serializers;

import com.google.gson.*;
import daripher.skilltree.init.PSTRegistries;
import daripher.skilltree.skill.bonus.SkillBonus;
import java.lang.reflect.Type;
import net.minecraft.resources.ResourceLocation;

public class SkillBonusSerializer
    implements JsonSerializer<SkillBonus<?>>, JsonDeserializer<SkillBonus<?>> {
  @Override
  public SkillBonus<?> deserialize(
      JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    JsonObject jsonObj = (JsonObject) json;
    String type = jsonObj.get("type").getAsString();
    ResourceLocation serializerId = new ResourceLocation(type);
    SkillBonus.Serializer serializer = PSTRegistries.SKILL_BONUSES.get().getValue(serializerId);
    assert serializer != null;
    return serializer.deserialize(jsonObj);
  }

  @Override
  public JsonElement serialize(
      SkillBonus<?> src, Type typeOfSrc, JsonSerializationContext context) {
    JsonObject json = new JsonObject();
    ResourceLocation serializerId = PSTRegistries.SKILL_BONUSES.get().getKey(src.getSerializer());
    assert serializerId != null;
    json.addProperty("type", serializerId.toString());
    src.getSerializer().serialize(json, src);
    return json;
  }
}
