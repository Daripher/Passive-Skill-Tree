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

public record BlockBreakSpeedBonus(@Nullable LivingCondition livingCondition, float multiplier)
    implements SkillBonus<BlockBreakSpeedBonus> {
  @Override
  public SkillBonus.Serializer getSerializer() {
    return PSTSkillBonuses.BLOCK_BREAK_SPEED.get();
  }

  @Override
  public SkillBonus<BlockBreakSpeedBonus> copy() {
    return new BlockBreakSpeedBonus(livingCondition, multiplier);
  }

  @Override
  public BlockBreakSpeedBonus multiply(double multiplier) {
    return new BlockBreakSpeedBonus(livingCondition, (float) (multiplier() * multiplier));
  }

  @Override
  public boolean canMerge(SkillBonus<?> other) {
    if (!(other instanceof BlockBreakSpeedBonus otherBonus)) return false;
    return Objects.equals(otherBonus.livingCondition, this.livingCondition);
  }

  @Override
  public SkillBonus<BlockBreakSpeedBonus> merge(SkillBonus<?> other) {
    if (!(other instanceof BlockBreakSpeedBonus otherBonus)) {
      throw new IllegalArgumentException();
    }
    return new BlockBreakSpeedBonus(livingCondition, otherBonus.multiplier + this.multiplier);
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
    public BlockBreakSpeedBonus deserialize(JsonObject json) throws JsonParseException {
      LivingCondition condition =
          SerializationHelper.deserializeLivingCondition(json, "player_condition");
      float multiplier = json.get("multiplier").getAsFloat();
      return new BlockBreakSpeedBonus(condition, multiplier);
    }

    @Override
    public void serialize(JsonObject json, SkillBonus<?> bonus) {
      if (!(bonus instanceof BlockBreakSpeedBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      SerializationHelper.serializeLivingCondition(
          json, aBonus.livingCondition, "player_condition");
      json.addProperty("multiplier", aBonus.multiplier);
    }

    @Override
    public BlockBreakSpeedBonus deserialize(CompoundTag tag) {
      LivingCondition condition =
          SerializationHelper.deserializeLivingCondition(tag, "player_condition");
      float multiplier = tag.getFloat("multiplier");
      return new BlockBreakSpeedBonus(condition, multiplier);
    }

    @Override
    public CompoundTag serialize(SkillBonus<?> bonus) {
      if (!(bonus instanceof BlockBreakSpeedBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      SerializationHelper.serializeLivingCondition(tag, aBonus.livingCondition, "player_condition");
      tag.putFloat("multiplier", aBonus.multiplier);
      return tag;
    }

    @Override
    public BlockBreakSpeedBonus deserialize(FriendlyByteBuf buf) {
      return new BlockBreakSpeedBonus(NetworkHelper.readLivingCondition(buf), buf.readFloat());
    }

    @Override
    public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
      if (!(bonus instanceof BlockBreakSpeedBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      NetworkHelper.writeLivingCondition(buf, aBonus.livingCondition);
      buf.writeFloat(aBonus.multiplier);
    }
  }
}
