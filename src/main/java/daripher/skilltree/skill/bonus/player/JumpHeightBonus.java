package daripher.skilltree.skill.bonus.player;

import com.google.gson.*;
import daripher.skilltree.client.screen.SkillTreeEditor;
import daripher.skilltree.data.SerializationHelper;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.condition.living.LivingCondition;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;

public record JumpHeightBonus(@Nullable LivingCondition livingCondition, float multiplier)
    implements SkillBonus<JumpHeightBonus> {
  @Override
  public SkillBonus.Serializer getSerializer() {
    return PSTSkillBonuses.JUMP_HEIGHT.get();
  }

  @Override
  public SkillBonus<JumpHeightBonus> copy() {
    return new JumpHeightBonus(livingCondition, multiplier);
  }

  @Override
  public JumpHeightBonus multiply(double multiplier) {
    return new JumpHeightBonus(livingCondition, (float) (multiplier() * multiplier));
  }

  @Override
  public boolean canMerge(SkillBonus<?> other) {
    if (!(other instanceof JumpHeightBonus otherBonus)) return false;
    return Objects.equals(otherBonus.livingCondition, this.livingCondition);
  }

  @Override
  public SkillBonus<JumpHeightBonus> merge(SkillBonus<?> other) {
    if (!(other instanceof JumpHeightBonus otherBonus)) {
      throw new IllegalArgumentException();
    }
    return new JumpHeightBonus(livingCondition, otherBonus.multiplier + this.multiplier);
  }

  @Override
  public MutableComponent getTooltip() {
    double visibleMultiplier = multiplier * 100;
    if (multiplier < 0) visibleMultiplier *= -1;
    String operationDescription = multiplier > 0 ? "plus" : "take";
    operationDescription = "attribute.modifier." + operationDescription + ".1";
    String multiplierDescription = ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(visibleMultiplier);
    MutableComponent bonusDescription = Component.translatable(getDescriptionId());
    bonusDescription =
        Component.translatable(operationDescription, multiplierDescription, bonusDescription);
    if (livingCondition != null) {
      bonusDescription = livingCondition.getTooltip(bonusDescription, "you");
    }
    return bonusDescription.withStyle(Style.EMPTY.withColor(0x7B7BE5));
  }

  @Override
  public void addEditorWidgets(SkillTreeEditor editor, int row) {
    // TODO: add widgets
  }

  public static class Serializer implements SkillBonus.Serializer {
    @Override
    public JumpHeightBonus deserialize(JsonObject json) throws JsonParseException {
      LivingCondition condition =
          SerializationHelper.deserializeLivingCondition(json, "player_condition");
      float multiplier = json.get("multiplier").getAsFloat();
      return new JumpHeightBonus(condition, multiplier);
    }

    @Override
    public void serialize(JsonObject json, SkillBonus<?> bonus) {
      if (!(bonus instanceof JumpHeightBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      SerializationHelper.serializeLivingCondition(
          json, aBonus.livingCondition, "player_condition");
      json.addProperty("multiplier", aBonus.multiplier);
    }

    @Override
    public JumpHeightBonus deserialize(CompoundTag tag) {
      LivingCondition condition =
          SerializationHelper.deserializeLivingCondition(tag, "player_condition");
      float multiplier = tag.getFloat("multiplier");
      return new JumpHeightBonus(condition, multiplier);
    }

    @Override
    public CompoundTag serialize(SkillBonus<?> bonus) {
      if (!(bonus instanceof JumpHeightBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      SerializationHelper.serializeLivingCondition(tag, aBonus.livingCondition, "player_condition");
      tag.putFloat("multiplier", aBonus.multiplier);
      return tag;
    }

    @Override
    public JumpHeightBonus deserialize(FriendlyByteBuf buf) {
      return new JumpHeightBonus(NetworkHelper.readLivingCondition(buf), buf.readFloat());
    }

    @Override
    public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
      if (!(bonus instanceof JumpHeightBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      NetworkHelper.writeLivingCondition(buf, aBonus.livingCondition);
      buf.writeFloat(aBonus.multiplier);
    }
  }
}
