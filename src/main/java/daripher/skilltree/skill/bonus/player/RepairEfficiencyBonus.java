package daripher.skilltree.skill.bonus.player;

import com.google.gson.*;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.predicate.item.ItemStackPredicate;
import daripher.skilltree.skill.bonus.predicate.item.NoneItemStackPredicate;
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
  private @Nonnull ItemStackPredicate itemStackPredicate;
  private float multiplier;

  public RepairEfficiencyBonus(@Nonnull ItemStackPredicate itemStackPredicate, float multiplier) {
    this.itemStackPredicate = itemStackPredicate;
    this.multiplier = multiplier;
  }

  @Override
  public SkillBonus.Serializer getSerializer() {
    return PSTSkillBonuses.REPAIR_EFFICIENCY.get();
  }

  @Override
  public RepairEfficiencyBonus copy() {
    return new RepairEfficiencyBonus(itemStackPredicate, multiplier);
  }

  @Override
  public RepairEfficiencyBonus multiply(double multiplier) {
    return new RepairEfficiencyBonus(itemStackPredicate, (float) (multiplier * multiplier));
  }

  @Override
  public boolean canMerge(SkillBonus<?> other) {
    if (!(other instanceof RepairEfficiencyBonus otherBonus)) return false;
    return Objects.equals(otherBonus.itemStackPredicate, this.itemStackPredicate);
  }

  @Override
  public SkillBonus<RepairEfficiencyBonus> merge(SkillBonus<?> other) {
    if (!(other instanceof RepairEfficiencyBonus otherBonus)) {
      throw new IllegalArgumentException();
    }
    return new RepairEfficiencyBonus(itemStackPredicate, otherBonus.multiplier + this.multiplier);
  }

  @Override
  public MutableComponent getTooltip() {
    Component itemDescription = itemStackPredicate.getTooltip("plural.type");
    AttributeModifier.Operation operation = AttributeModifier.Operation.ADD_MULTIPLIED_BASE;
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
        .addSelectionMenu(0, 0, 200, itemStackPredicate)
        .setResponder(condition -> selectItemCondition(editor, consumer, condition))
        .setMenuInitFunc(() -> addItemConditionWidgets(editor, consumer));
    editor.increaseHeight(19);
  }

  private void addItemConditionWidgets(
      SkillTreeEditor editor, Consumer<RepairEfficiencyBonus> consumer) {
    itemStackPredicate.addEditorWidgets(
        editor,
        condition -> {
          setItemCondition(condition);
          consumer.accept(this.copy());
        });
  }

  private void selectItemCondition(
      SkillTreeEditor editor, Consumer<RepairEfficiencyBonus> consumer, ItemStackPredicate condition) {
    setItemCondition(condition);
    consumer.accept(this.copy());
    editor.rebuildWidgets();
  }

  private void selectMultiplier(Consumer<RepairEfficiencyBonus> consumer, Double value) {
    setMultiplier(value.floatValue());
    consumer.accept(this.copy());
  }

  public void setItemCondition(@Nonnull ItemStackPredicate itemStackPredicate) {
    this.itemStackPredicate = itemStackPredicate;
  }

  public void setMultiplier(float multiplier) {
    this.multiplier = multiplier;
  }

  @Nonnull
  public ItemStackPredicate getItemCondition() {
    return itemStackPredicate;
  }

  public float getMultiplier() {
    return multiplier;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj == null || obj.getClass() != this.getClass()) return false;
    RepairEfficiencyBonus that = (RepairEfficiencyBonus) obj;
    if (!Objects.equals(this.itemStackPredicate, that.itemStackPredicate)) return false;
    return this.multiplier == that.multiplier;
  }

  @Override
  public int hashCode() {
    return Objects.hash(itemStackPredicate, multiplier);
  }

  public static class Serializer implements SkillBonus.Serializer {
    @Override
    public RepairEfficiencyBonus deserialize(JsonObject json) throws JsonParseException {
      ItemStackPredicate condition = SerializationHelper.deserializeItemCondition(json);
      float multiplier = SerializationHelper.getElement(json, "multiplier").getAsFloat();
      return new RepairEfficiencyBonus(condition, multiplier);
    }

    @Override
    public void serialize(JsonObject json, SkillBonus<?> bonus) {
      if (!(bonus instanceof RepairEfficiencyBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      SerializationHelper.serializeItemCondition(json, aBonus.itemStackPredicate);
      json.addProperty("multiplier", aBonus.multiplier);
    }

    @Override
    public RepairEfficiencyBonus deserialize(CompoundTag tag) {
      ItemStackPredicate condition = SerializationHelper.deserializeItemCondition(tag);
      float multiplier = tag.getFloat("multiplier");
      return new RepairEfficiencyBonus(condition, multiplier);
    }

    @Override
    public CompoundTag serialize(SkillBonus<?> bonus) {
      if (!(bonus instanceof RepairEfficiencyBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      SerializationHelper.serializeItemCondition(tag, aBonus.itemStackPredicate);
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
      NetworkHelper.writeItemCondition(buf, aBonus.itemStackPredicate);
      buf.writeFloat(aBonus.multiplier);
    }

    @Override
    public SkillBonus<?> createDefaultInstance() {
      return new RepairEfficiencyBonus(NoneItemStackPredicate.INSTANCE, 0.1f);
    }
  }
}
