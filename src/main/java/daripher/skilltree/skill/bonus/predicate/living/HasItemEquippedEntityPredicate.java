package daripher.skilltree.skill.bonus.predicate.living;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.entity.player.PlayerHelper;
import daripher.skilltree.init.PSTLivingConditions;
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
import net.minecraft.world.entity.LivingEntity;

public final class HasItemEquippedEntityPredicate implements LivingEntityPredicate {
  private @Nonnull ItemStackPredicate itemStackPredicate;

  public HasItemEquippedEntityPredicate(@Nonnull ItemStackPredicate itemStackPredicate) {
    this.itemStackPredicate = itemStackPredicate;
  }

  @Override
  public boolean test(LivingEntity living) {
    return PlayerHelper.getAllEquipment(living).anyMatch(itemStackPredicate);
  }

  @Override
  public MutableComponent getTooltip(MutableComponent bonusTooltip, SkillBonus.Target target) {
    String key = getDescriptionId();
    Component targetDescription = Component.translatable("%s.target.%s".formatted(key, target.getName()));
    Component itemDescription = itemStackPredicate.getTooltip();
    return Component.translatable(key, bonusTooltip, targetDescription, itemDescription);
  }

  @Override
  public LivingEntityPredicate.Serializer getSerializer() {
    return PSTLivingConditions.HAS_ITEM_EQUIPPED.get();
  }

  @Override
  public void addEditorWidgets(SkillTreeEditor editor, Consumer<LivingEntityPredicate> consumer) {
    editor.addLabel(0, 0, "Item Condition", ChatFormatting.GREEN);
    editor.increaseHeight(19);
    editor
        .addSelectionMenu(0, 0, 200, itemStackPredicate)
        .setResponder(condition -> selectItemCondition(editor, consumer, condition))
        .setMenuInitFunc(() -> addItemConditionWidgets(editor, consumer));
    editor.increaseHeight(19);
  }

  private void addItemConditionWidgets(SkillTreeEditor editor, Consumer<LivingEntityPredicate> consumer) {
    itemStackPredicate.addEditorWidgets(
        editor,
        condition -> {
          setItemCondition(condition);
          consumer.accept(this);
        });
  }

  private void selectItemCondition(
      SkillTreeEditor editor, Consumer<LivingEntityPredicate> consumer, ItemStackPredicate condition) {
    setItemCondition(condition);
    consumer.accept(this);
    editor.rebuildWidgets();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    HasItemEquippedEntityPredicate that = (HasItemEquippedEntityPredicate) o;
    return Objects.equals(itemStackPredicate, that.itemStackPredicate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(itemStackPredicate);
  }

  public void setItemCondition(@Nonnull ItemStackPredicate itemStackPredicate) {
    this.itemStackPredicate = itemStackPredicate;
  }

  public static class Serializer implements LivingEntityPredicate.Serializer {
    @Override
    public LivingEntityPredicate deserialize(JsonObject json) throws JsonParseException {
      return new HasItemEquippedEntityPredicate(SerializationHelper.deserializeItemCondition(json));
    }

    @Override
    public void serialize(JsonObject json, LivingEntityPredicate condition) {
      if (!(condition instanceof HasItemEquippedEntityPredicate aCondition)) {
        throw new IllegalArgumentException();
      }
      SerializationHelper.serializeItemCondition(json, aCondition.itemStackPredicate);
    }

    @Override
    public LivingEntityPredicate deserialize(CompoundTag tag) {
      return new HasItemEquippedEntityPredicate(SerializationHelper.deserializeItemCondition(tag));
    }

    @Override
    public CompoundTag serialize(LivingEntityPredicate condition) {
      if (!(condition instanceof HasItemEquippedEntityPredicate aCondition)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      SerializationHelper.serializeItemCondition(tag, aCondition.itemStackPredicate);
      return tag;
    }

    @Override
    public LivingEntityPredicate deserialize(FriendlyByteBuf buf) {
      return new HasItemEquippedEntityPredicate(NetworkHelper.readItemCondition(buf));
    }

    @Override
    public void serialize(FriendlyByteBuf buf, LivingEntityPredicate condition) {
      if (!(condition instanceof HasItemEquippedEntityPredicate aCondition)) {
        throw new IllegalArgumentException();
      }
      NetworkHelper.writeItemCondition(buf, aCondition.itemStackPredicate);
    }

    @Override
    public LivingEntityPredicate createDefaultInstance() {
      return new HasItemEquippedEntityPredicate(NoneItemStackPredicate.INSTANCE);
    }
  }
}
