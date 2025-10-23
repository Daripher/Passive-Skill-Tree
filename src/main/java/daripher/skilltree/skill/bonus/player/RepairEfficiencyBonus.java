package daripher.skilltree.skill.bonus.player;

import com.google.gson.*;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
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

public final class RepairEfficiencyBonus implements SkillBonus<RepairEfficiencyBonus> {
  private @Nonnull ItemCondition itemCondition;
  private float multiplier;

  public RepairEfficiencyBonus(@Nonnull ItemCondition itemCondition, float multiplier) {
    this.itemCondition = itemCondition;
    this.multiplier = multiplier;
  }

  @Override
  public SkillBonus.Serializer getSerializer() {
    return PSTSkillBonuses.REPAIR_EFFICIENCY.get();
  }

  @Override
  public RepairEfficiencyBonus copy() {
    return new RepairEfficiencyBonus(itemCondition, multiplier);
  }

  @Override
  public RepairEfficiencyBonus multiply(double multiplier) {
    return new RepairEfficiencyBonus(itemCondition, (float) (multiplier * multiplier));
  }

  @Override
  public boolean canMerge(SkillBonus<?> other) {
    if (!(other instanceof RepairEfficiencyBonus otherBonus)) return false;
    return Objects.equals(otherBonus.itemCondition, this.itemCondition);
  }

  @Override
  public SkillBonus<RepairEfficiencyBonus> merge(SkillBonus<?> other) {
    if (!(other instanceof RepairEfficiencyBonus otherBonus)) {
      throw new IllegalArgumentException();
    }
    return new RepairEfficiencyBonus(itemCondition, otherBonus.multiplier + this.multiplier);
  }

  @Override
  public MutableComponent getTooltip() {
    Component itemDescription = itemCondition.getTooltip("plural.type");
    AttributeModifier.Operation operation = AttributeModifier.Operation.MULTIPLY_BASE;
    Component bonusDescription = Component.translatable(getDescriptionId() + ".bonus");
    bonusDescription =
        TooltipHelper.getSkillBonusTooltip(bonusDescription, multiplier, operation)
            .withStyle(TooltipHelper.getSkillBonusSecondStyle(isPositive()));
    return Component.translatable(getDescriptionId(), itemDescription, bonusDescription)
        .withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
  }

  @Override
  public boolean isPositive() {
    return multiplier > 0;
  }

  @Override
  public void addEditorWidgets(
      SkillTreeEditor editor, int row, Consumer<RepairEfficiencyBonus> consumer) {
    editor.addLabel(0, 0, "Multiplier", ChatFormatting.GOLD);
    editor.increaseHeight(19);
    editor
        .addNumericTextField(0, 0, 50, 14, multiplier)
        .setNumericResponder(value -> selectMultiplier(consumer, value));
    editor.increaseHeight(19);
    editor.addLabel(0, 0, "Item Condition", ChatFormatting.GOLD);
    editor.increaseHeight(19);
    editor
        .addSelectionMenu(0, 0, 200, itemCondition)
        .setResponder(condition -> selectItemCondition(editor, consumer, condition))
        .setMenuInitFunc(() -> addItemConditionWidgets(editor, consumer));
    editor.increaseHeight(19);
  }

  private void addItemConditionWidgets(
      SkillTreeEditor editor, Consumer<RepairEfficiencyBonus> consumer) {
    itemCondition.addEditorWidgets(
        editor,
        condition -> {
          setItemCondition(condition);
          consumer.accept(this.copy());
        });
  }

  private void selectItemCondition(
      SkillTreeEditor editor, Consumer<RepairEfficiencyBonus> consumer, ItemCondition condition) {
    setItemCondition(condition);
    consumer.accept(this.copy());
    editor.rebuildWidgets();
  }

  private void selectMultiplier(Consumer<RepairEfficiencyBonus> consumer, Double value) {
    setMultiplier(value.floatValue());
    consumer.accept(this.copy());
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
    RepairEfficiencyBonus that = (RepairEfficiencyBonus) obj;
    if (!Objects.equals(this.itemCondition, that.itemCondition)) return false;
    return this.multiplier == that.multiplier;
  }

  @Override
  public int hashCode() {
    return Objects.hash(itemCondition, multiplier);
  }

  public static class Serializer implements SkillBonus.Serializer {
    @Override
    public RepairEfficiencyBonus deserialize(JsonObject json) throws JsonParseException {
      ItemCondition condition = SerializationHelper.deserializeItemCondition(json);
      float multiplier = SerializationHelper.getElement(json, "multiplier").getAsFloat();
      return new RepairEfficiencyBonus(condition, multiplier);
    }

    @Override
    public void serialize(JsonObject json, SkillBonus<?> bonus) {
      if (!(bonus instanceof RepairEfficiencyBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      SerializationHelper.serializeItemCondition(json, aBonus.itemCondition);
      json.addProperty("multiplier", aBonus.multiplier);
    }

    @Override
    public RepairEfficiencyBonus deserialize(CompoundTag tag) {
      ItemCondition condition = SerializationHelper.deserializeItemCondition(tag);
      float multiplier = tag.getFloat("multiplier");
      return new RepairEfficiencyBonus(condition, multiplier);
    }

    @Override
    public CompoundTag serialize(SkillBonus<?> bonus) {
      if (!(bonus instanceof RepairEfficiencyBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      SerializationHelper.serializeItemCondition(tag, aBonus.itemCondition);
      tag.putFloat("multiplier", aBonus.multiplier);
      return tag;
    }

    @Override
    public RepairEfficiencyBonus deserialize(FriendlyByteBuf buf) {
      return new RepairEfficiencyBonus(NetworkHelper.readItemCondition(buf), buf.readFloat());
    }

    @Override
    public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
      if (!(bonus instanceof RepairEfficiencyBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      NetworkHelper.writeItemCondition(buf, aBonus.itemCondition);
      buf.writeFloat(aBonus.multiplier);
    }

    @Override
    public SkillBonus<?> createDefaultInstance() {
      return new RepairEfficiencyBonus(NoneItemCondition.INSTANCE, 0.1f);
    }
  }
}
