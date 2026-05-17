package daripher.skilltree.data.serializers;

import com.google.gson.*;
import daripher.skilltree.init.PSTRegistries;
import daripher.skilltree.init.PSTSkillRequirements;
import daripher.skilltree.skill.requirement.SkillRequirement;
import java.lang.reflect.Type;
import java.util.Objects;
import net.minecraft.resources.ResourceLocation;

public class SkillRequirementSerializer
    implements JsonSerializer<SkillRequirement<?>>, JsonDeserializer<SkillRequirement<?>> {
  @Override
  public SkillRequirement<?> deserialize(
      JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    JsonObject jsonObj = (JsonObject) json;
    String type;
    if (!jsonObj.has("type")) {
      ResourceLocation defaultRequirementType =
          PSTRegistries.SKILL_REQUIREMENTS.get().getKey(PSTSkillRequirements.STAT_VALUE.get());
      type = Objects.requireNonNull(defaultRequirementType).toString();
    } else {
      type = jsonObj.get("type").getAsString();
    }
    ResourceLocation serializerId = ResourceLocation.parse(type);
    SkillRequirement.Serializer serializer =
        PSTRegistries.SKILL_REQUIREMENTS.get().getValue(serializerId);
    Objects.requireNonNull(serializer, "Unknown skill requirement: " + serializerId);
    return serializer.deserialize(jsonObj);
  }

  @Override
  public JsonElement serialize(
      SkillRequirement<?> src, Type typeOfSrc, JsonSerializationContext context) {
    JsonObject json = new JsonObject();
    ResourceLocation serializerId =
        PSTRegistries.SKILL_REQUIREMENTS.get().getKey(src.getSerializer());
    Objects.requireNonNull(serializerId);
    json.addProperty("type", serializerId.toString());
    src.getSerializer().serialize(json, src);
    return json;
  }
}
