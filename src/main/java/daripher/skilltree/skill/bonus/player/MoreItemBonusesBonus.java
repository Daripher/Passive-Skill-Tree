package daripher.skilltree.skill.bonus.player;

import com.google.gson.*;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.condition.item.EquipmentCondition;
import daripher.skilltree.skill.bonus.condition.item.ItemCondition;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class MoreItemBonusesBonus implements SkillBonus<MoreItemBonusesBonus> {
  private @Nonnull ItemCondition itemCondition;
  private int amount;

  public MoreItemBonusesBonus(@Nonnull ItemCondition itemCondition, int amount) {
    this.itemCondition = itemCondition;
    this.amount = amount;
  }

  @Override
  public SkillBonus.Serializer getSerializer() {
    return PSTSkillBonuses.MORE_ITEM_BONUSES.get();
  }

  @Override
  public MoreItemBonusesBonus copy() {
    return new MoreItemBonusesBonus(itemCondition, amount);
  }

  @Override
  public MoreItemBonusesBonus multiply(double multiplier) {
    return new MoreItemBonusesBonus(itemCondition, (int) (amount * multiplier));
  }

  @Override
  public boolean canMerge(SkillBonus<?> other) {
    if (!(other instanceof MoreItemBonusesBonus otherBonus)) return false;
    return Objects.equals(otherBonus.itemCondition, this.itemCondition);
  }

  @Override
  public SkillBonus<MoreItemBonusesBonus> merge(SkillBonus<?> other) {
    if (!(other instanceof MoreItemBonusesBonus otherBonus)) {
      throw new IllegalArgumentException();
    }
    return new MoreItemBonusesBonus(itemCondition, otherBonus.amount + this.amount);
  }

  @Override
  public MutableComponent getTooltip() {
    Component itemDescription = itemCondition.getTooltip("plural");
    MutableComponent bonusDescription;
    if (amount == 1) {
      bonusDescription = Component.translatable(getDescriptionId() + ".one", itemDescription);
    } else {
      bonusDescription = Component.translatable(getDescriptionId(), itemDescription, amount);
    }
    return bonusDescription.withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
  }

  @Override
  public boolean isPositive() {
    return amount > 0;
  }

  @Override
  public void addEditorWidgets(
      SkillTreeEditor editor, int row, Consumer<MoreItemBonusesBonus> consumer) {
    editor.addLabel(0, 0, "Amount", ChatFormatting.GOLD);
    editor.increaseHeight(19);
    editor
        .addNumericTextField(0, 0, 50, 14, amount)
        .setNumericResponder(value -> selectAmount(consumer, value));
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
      SkillTreeEditor editor, Consumer<MoreItemBonusesBonus> consumer) {
    itemCondition.addEditorWidgets(
        editor,
        condition -> {
          setItemCondition(condition);
          consumer.accept(this.copy());
        });
  }

  private void selectItemCondition(
      SkillTreeEditor editor, Consumer<MoreItemBonusesBonus> consumer, ItemCondition condition) {
    setItemCondition(condition);
    consumer.accept(this.copy());
    editor.rebuildWidgets();
  }

  private void selectAmount(Consumer<MoreItemBonusesBonus> consumer, Double value) {
    setAmount(value.intValue());
    consumer.accept(this.copy());
  }

  public void setItemCondition(@Nonnull ItemCondition itemCondition) {
    this.itemCondition = itemCondition;
  }

  public void setAmount(int amount) {
    this.amount = amount;
  }

  @Nonnull
  public ItemCondition getItemCondition() {
    return itemCondition;
  }

  public int getAmount() {
    return amount;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj == null || obj.getClass() != this.getClass()) return false;
    MoreItemBonusesBonus that = (MoreItemBonusesBonus) obj;
    if (!Objects.equals(this.itemCondition, that.itemCondition)) return false;
    return this.amount == that.amount;
  }

  @Override
  public int hashCode() {
    return Objects.hash(itemCondition, amount);
  }

  public static class Serializer implements SkillBonus.Serializer {
    @Override
    public MoreItemBonusesBonus deserialize(JsonObject json) throws JsonParseException {
      ItemCondition condition = SerializationHelper.deserializeItemCondition(json);
      int amount = SerializationHelper.getElement(json, "amount").getAsInt();
      return new MoreItemBonusesBonus(condition, amount);
    }

    @Override
    public void serialize(JsonObject json, SkillBonus<?> bonus) {
      if (!(bonus instanceof MoreItemBonusesBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      SerializationHelper.serializeItemCondition(json, aBonus.itemCondition);
      json.addProperty("amount", aBonus.amount);
    }

    @Override
    public MoreItemBonusesBonus deserialize(CompoundTag tag) {
      ItemCondition condition = SerializationHelper.deserializeItemCondition(tag);
      int amount = tag.getInt("amount");
      return new MoreItemBonusesBonus(condition, amount);
    }

    @Override
    public CompoundTag serialize(SkillBonus<?> bonus) {
      if (!(bonus instanceof MoreItemBonusesBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      SerializationHelper.serializeItemCondition(tag, aBonus.itemCondition);
      tag.putInt("amount", aBonus.amount);
      return tag;
    }

    @Override
    public MoreItemBonusesBonus deserialize(FriendlyByteBuf buf) {
      return new MoreItemBonusesBonus(NetworkHelper.readItemCondition(buf), buf.readInt());
    }

    @Override
    public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
      if (!(bonus instanceof MoreItemBonusesBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      NetworkHelper.writeItemCondition(buf, aBonus.itemCondition);
      buf.writeInt(aBonus.amount);
    }

    @Override
    public SkillBonus<?> createDefaultInstance() {
      return new MoreItemBonusesBonus(new EquipmentCondition(EquipmentCondition.Type.SHIELD), 1);
    }
  }
}
