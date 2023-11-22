package daripher.skilltree.skill.bonus.multiplier;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.data.SerializationHelper;
import daripher.skilltree.init.PSTSkillBonusMultipliers;
import daripher.skilltree.network.NetworkHelper;
import java.util.Objects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;

public record AttributeValueMultiplier(Attribute attribute) implements SkillBonusMultiplier {
  @Override
  public float getValue(Player player) {
    return (float) player.getAttributeValue(attribute);
  }

  @Override
  public MutableComponent getTooltip(MutableComponent bonusTooltip) {
    MutableComponent attributeDescription = Component.translatable(attribute.getDescriptionId());
    return Component.translatable(getDescriptionId(), bonusTooltip, attributeDescription);
  }

  @Override
  public SkillBonusMultiplier.Serializer getSerializer() {
    return PSTSkillBonusMultipliers.ATTRIBUTE_VALUE.get();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AttributeValueMultiplier that = (AttributeValueMultiplier) o;
    return Objects.equals(attribute, that.attribute);
  }

  @Override
  public int hashCode() {
    return Objects.hash(attribute);
  }

  public static class Serializer implements SkillBonusMultiplier.Serializer {
    @Override
    public SkillBonusMultiplier deserialize(JsonObject json) throws JsonParseException {
      Attribute attribute = SerializationHelper.deserializeAttribute(json);
      return new AttributeValueMultiplier(attribute);
    }

    @Override
    public void serialize(JsonObject json, SkillBonusMultiplier multiplier) {
      if (!(multiplier instanceof AttributeValueMultiplier aMultiplier)) {
        throw new IllegalArgumentException();
      }
      SerializationHelper.serializeAttribute(json, aMultiplier.attribute);
    }

    @Override
    public SkillBonusMultiplier deserialize(CompoundTag tag) {
      Attribute attribute = SerializationHelper.deserializeAttribute(tag);
      return new AttributeValueMultiplier(attribute);
    }

    @Override
    public CompoundTag serialize(SkillBonusMultiplier multiplier) {
      if (!(multiplier instanceof AttributeValueMultiplier aMultiplier)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      SerializationHelper.serializeAttribute(tag, aMultiplier.attribute);
      return tag;
    }

    @Override
    public SkillBonusMultiplier deserialize(FriendlyByteBuf buf) {
      Attribute attribute = NetworkHelper.readAttribute(buf);
      return new AttributeValueMultiplier(attribute);
    }

    @Override
    public void serialize(FriendlyByteBuf buf, SkillBonusMultiplier multiplier) {
      if (!(multiplier instanceof AttributeValueMultiplier aMultiplier)) {
        throw new IllegalArgumentException();
      }
      NetworkHelper.writeAttribute(buf, aMultiplier.attribute);
    }
  }
}
