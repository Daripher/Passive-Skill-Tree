package daripher.skilltree.skill.bonus.multiplier;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.screen.SkillTreeEditorScreen;
import daripher.skilltree.data.SerializationHelper;
import daripher.skilltree.init.PSTLivingMultipliers;
import daripher.skilltree.network.NetworkHelper;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.registries.ForgeRegistries;

public final class AttributeValueMultiplier implements LivingMultiplier {
  private Attribute attribute;

  public AttributeValueMultiplier(Attribute attribute) {
    this.attribute = attribute;
  }

  @Override
  public float getValue(LivingEntity entity) {
    return (float) entity.getAttributeValue(attribute);
  }

  @Override
  public MutableComponent getTooltip(MutableComponent bonusTooltip) {
    MutableComponent attributeDescription = Component.translatable(attribute.getDescriptionId());
    return Component.translatable(getDescriptionId(), bonusTooltip, attributeDescription);
  }

  @Override
  public void addEditorWidgets(SkillTreeEditorScreen editor, Consumer<LivingMultiplier> consumer) {
    editor.addLabel(0, 0, "Attribute", ChatFormatting.GREEN);
    editor.shiftWidgets(0, 19);
    editor
        .addDropDownList(0, 0, 200, 14, 10, attribute, ForgeRegistries.ATTRIBUTES.getValues())
        .setToNameFunc(a -> Component.translatable(a.getDescriptionId()))
        .setResponder(
            a -> {
              setAttribute(a);
              consumer.accept(this);
            });
    editor.shiftWidgets(0, 19);
  }

  @Override
  public LivingMultiplier.Serializer getSerializer() {
    return PSTLivingMultipliers.ATTRIBUTE_VALUE.get();
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

  public void setAttribute(Attribute attribute) {
    this.attribute = attribute;
  }

  public Attribute getAttribute() {
    return attribute;
  }

  public static class Serializer implements LivingMultiplier.Serializer {
    @Override
    public LivingMultiplier deserialize(JsonObject json) throws JsonParseException {
      Attribute attribute = SerializationHelper.deserializeAttribute(json);
      return new AttributeValueMultiplier(attribute);
    }

    @Override
    public void serialize(JsonObject json, LivingMultiplier multiplier) {
      if (!(multiplier instanceof AttributeValueMultiplier aMultiplier)) {
        throw new IllegalArgumentException();
      }
      SerializationHelper.serializeAttribute(json, aMultiplier.attribute);
    }

    @Override
    public LivingMultiplier deserialize(CompoundTag tag) {
      Attribute attribute = SerializationHelper.deserializeAttribute(tag);
      return new AttributeValueMultiplier(attribute);
    }

    @Override
    public CompoundTag serialize(LivingMultiplier multiplier) {
      if (!(multiplier instanceof AttributeValueMultiplier aMultiplier)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      SerializationHelper.serializeAttribute(tag, aMultiplier.attribute);
      return tag;
    }

    @Override
    public LivingMultiplier deserialize(FriendlyByteBuf buf) {
      Attribute attribute = NetworkHelper.readAttribute(buf);
      return new AttributeValueMultiplier(attribute);
    }

    @Override
    public void serialize(FriendlyByteBuf buf, LivingMultiplier multiplier) {
      if (!(multiplier instanceof AttributeValueMultiplier aMultiplier)) {
        throw new IllegalArgumentException();
      }
      NetworkHelper.writeAttribute(buf, aMultiplier.attribute);
    }

    @Override
    public LivingMultiplier createDefaultInstance() {
      return new AttributeValueMultiplier(Attributes.ARMOR);
    }
  }
}
