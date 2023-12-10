package daripher.skilltree.skill.bonus.player;

import com.google.gson.*;
import daripher.skilltree.client.screen.SkillTreeEditorScreen;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.data.SerializationHelper;
import daripher.skilltree.init.PSTItemConditions;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.condition.item.ItemCondition;
import daripher.skilltree.skill.bonus.condition.item.NoneItemCondition;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public final class GemPowerBonus implements SkillBonus<GemPowerBonus> {
  private @Nonnull ItemCondition itemCondition;
  private float multiplier;

  public GemPowerBonus(@Nonnull ItemCondition itemCondition, float multiplier) {
    this.itemCondition = itemCondition;
    this.multiplier = multiplier;
  }

  @Override
  public SkillBonus.Serializer getSerializer() {
    return PSTSkillBonuses.GEM_POWER.get();
  }

  @Override
  public GemPowerBonus copy() {
    return new GemPowerBonus(itemCondition, multiplier);
  }

  @Override
  public GemPowerBonus multiply(double multiplier) {
    return new GemPowerBonus(itemCondition, (float) (getMultiplier() * multiplier));
  }

  @Override
  public boolean canMerge(SkillBonus<?> other) {
    if (!(other instanceof GemPowerBonus otherBonus)) return false;
    return Objects.equals(otherBonus.itemCondition, this.itemCondition);
  }

  @Override
  public SkillBonus<GemPowerBonus> merge(SkillBonus<?> other) {
    if (!(other instanceof GemPowerBonus otherBonus)) {
      throw new IllegalArgumentException();
    }
    return new GemPowerBonus(itemCondition, otherBonus.multiplier + this.multiplier);
  }

  @Override
  public MutableComponent getTooltip() {
    MutableComponent itemDescription =
        TooltipHelper.getOptionalTooltip(itemCondition.getDescriptionId(), "crafted");
    MutableComponent bonusDescription =
        TooltipHelper.getSkillBonusTooltip(
                getDescriptionId() + ".bonus",
                multiplier,
                AttributeModifier.Operation.MULTIPLY_BASE)
            .withStyle(TooltipHelper.getItemBonusStyle(isPositive()));
    return Component.translatable(getDescriptionId(), itemDescription, bonusDescription)
        .withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
  }

  @Override
  public boolean isPositive() {
    return multiplier > 0;
  }

  @Override
  public void addEditorWidgets(
      SkillTreeEditorScreen editor, int row, Consumer<GemPowerBonus> consumer) {
    editor.addLabel(0, 0, "Multiplier", ChatFormatting.GOLD);
    editor.shiftWidgets(0, 19);
    editor
        .addNumericTextField(0, 0, 50, 14, multiplier)
        .setNumericResponder(
            v -> {
              setMultiplier(v.floatValue());
              consumer.accept(this.copy());
            });
    editor.shiftWidgets(0, 19);
    editor.addLabel(0, 0, "Item Condition", ChatFormatting.GOLD);
    editor.shiftWidgets(0, 19);
    editor
        .addDropDownList(0, 0, 200, 14, 10, itemCondition, PSTItemConditions.conditionsList())
        .setToNameFunc(a -> Component.literal(PSTItemConditions.getName(a)))
        .setResponder(
            c -> {
              setItemCondition(c);
              consumer.accept(this.copy());
              editor.rebuildWidgets();
            });
    editor.shiftWidgets(0, 19);
    itemCondition.addEditorWidgets(
        editor,
        c -> {
          setItemCondition(c);
          consumer.accept(this.copy());
        });
  }

  public void setItemCondition(@Nonnull ItemCondition itemCondition) {
    this.itemCondition = itemCondition;
  }

  public void setMultiplier(float multiplier) {
    this.multiplier = multiplier;
  }

  @Nonnull
  public ItemCondition getItemCondition() {
    return itemCondition;
  }

  public float getMultiplier() {
    return multiplier;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj == null || obj.getClass() != this.getClass()) return false;
    GemPowerBonus that = (GemPowerBonus) obj;
    if (!Objects.equals(this.itemCondition, that.itemCondition)) return false;
    return this.multiplier == that.multiplier;
  }

  @Override
  public int hashCode() {
    return Objects.hash(itemCondition, multiplier);
  }

  public static class Serializer implements SkillBonus.Serializer {
    @Override
    public GemPowerBonus deserialize(JsonObject json) throws JsonParseException {
      ItemCondition condition = SerializationHelper.deserializeItemCondition(json);
      float multiplier = json.get("multiplier").getAsFloat();
      return new GemPowerBonus(condition, multiplier);
    }

    @Override
    public void serialize(JsonObject json, SkillBonus<?> bonus) {
      if (!(bonus instanceof GemPowerBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      SerializationHelper.serializeItemCondition(json, aBonus.itemCondition);
      json.addProperty("multiplier", aBonus.multiplier);
    }

    @Override
    public GemPowerBonus deserialize(CompoundTag tag) {
      ItemCondition condition = SerializationHelper.deserializeItemCondition(tag);
      float multiplier = tag.getFloat("multiplier");
      return new GemPowerBonus(condition, multiplier);
    }

    @Override
    public CompoundTag serialize(SkillBonus<?> bonus) {
      if (!(bonus instanceof GemPowerBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      SerializationHelper.serializeItemCondition(tag, aBonus.itemCondition);
      tag.putFloat("multiplier", aBonus.multiplier);
      return tag;
    }

    @Override
    public GemPowerBonus deserialize(FriendlyByteBuf buf) {
      return new GemPowerBonus(NetworkHelper.readItemCondition(buf), buf.readFloat());
    }

    @Override
    public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
      if (!(bonus instanceof GemPowerBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      NetworkHelper.writeItemCondition(buf, aBonus.itemCondition);
      buf.writeFloat(aBonus.multiplier);
    }

    @Override
    public SkillBonus<?> createDefaultInstance() {
      return new GemPowerBonus(new NoneItemCondition(), 0.1f);
    }
  }
}
