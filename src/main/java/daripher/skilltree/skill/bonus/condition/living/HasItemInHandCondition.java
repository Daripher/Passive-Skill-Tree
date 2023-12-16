package daripher.skilltree.skill.bonus.condition.living;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.screen.SkillTreeEditorScreen;
import daripher.skilltree.data.SerializationHelper;
import daripher.skilltree.init.PSTItemConditions;
import daripher.skilltree.init.PSTLivingConditions;
import daripher.skilltree.network.NetworkHelper;
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
import net.minecraft.world.entity.LivingEntity;

public final class HasItemInHandCondition implements LivingCondition {
  private @Nonnull ItemCondition itemCondition;

  public HasItemInHandCondition(@Nonnull ItemCondition itemCondition) {
    this.itemCondition = itemCondition;
  }

  @Override
  public boolean met(LivingEntity living) {
    return itemCondition.met(living.getMainHandItem())
        || itemCondition.met(living.getOffhandItem());
  }

  @Override
  public MutableComponent getTooltip(MutableComponent bonusTooltip, String target) {
    String key = getDescriptionId();
    MutableComponent targetDescription =
        Component.translatable("%s.target.%s".formatted(key, target));
    MutableComponent itemDescription = Component.translatable(itemCondition.getDescriptionId());
    return Component.translatable(key, bonusTooltip, targetDescription, itemDescription);
  }

  @Override
  public LivingCondition.Serializer getSerializer() {
    return PSTLivingConditions.HAS_ITEM_IN_HAND.get();
  }

  @Override
  public void addEditorWidgets(SkillTreeEditorScreen editor, Consumer<LivingCondition> consumer) {
    editor.addLabel(0, 0, "Item Condition", ChatFormatting.GREEN);
    editor.shiftWidgets(0, 19);
    editor
        .addDropDownList(0, 0, 200, 14, 10, itemCondition, PSTItemConditions.conditionsList())
        .setToNameFunc(a -> Component.translatable(PSTItemConditions.getName(a)))
        .setResponder(
            c -> {
              setItemCondition(c);
              consumer.accept(this);
            });
    editor.shiftWidgets(0, 19);
    itemCondition.addEditorWidgets(
        editor,
        c -> {
          setItemCondition(c);
          consumer.accept(this);
        });
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    HasItemInHandCondition that = (HasItemInHandCondition) o;
    return Objects.equals(itemCondition, that.itemCondition);
  }

  @Override
  public int hashCode() {
    return Objects.hash(itemCondition);
  }

  public void setItemCondition(@Nonnull ItemCondition itemCondition) {
    this.itemCondition = itemCondition;
  }

  public static class Serializer implements LivingCondition.Serializer {
    @Override
    public LivingCondition deserialize(JsonObject json) throws JsonParseException {
      return new HasItemInHandCondition(SerializationHelper.deserializeItemCondition(json));
    }

    @Override
    public void serialize(JsonObject json, LivingCondition condition) {
      if (!(condition instanceof HasItemInHandCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      SerializationHelper.serializeItemCondition(json, aCondition.itemCondition);
    }

    @Override
    public LivingCondition deserialize(CompoundTag tag) {
      return new HasItemInHandCondition(SerializationHelper.deserializeItemCondition(tag));
    }

    @Override
    public CompoundTag serialize(LivingCondition condition) {
      if (!(condition instanceof HasItemInHandCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      SerializationHelper.serializeItemCondition(tag, aCondition.itemCondition);
      return tag;
    }

    @Override
    public LivingCondition deserialize(FriendlyByteBuf buf) {
      return new HasItemInHandCondition(NetworkHelper.readItemCondition(buf));
    }

    @Override
    public void serialize(FriendlyByteBuf buf, LivingCondition condition) {
      if (!(condition instanceof HasItemInHandCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      NetworkHelper.writeItemCondition(buf, aCondition.itemCondition);
    }

    @Override
    public LivingCondition createDefaultInstance() {
      return new HasItemInHandCondition(new NoneItemCondition());
    }
  }
}
