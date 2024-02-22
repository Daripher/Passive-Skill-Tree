package daripher.skilltree.skill.bonus.condition.living;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.screen.SkillTreeEditorScreen;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.entity.player.PlayerHelper;
import daripher.skilltree.init.PSTItemConditions;
import daripher.skilltree.init.PSTLivingConditions;
import daripher.skilltree.item.gem.GemItem;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.condition.item.ItemCondition;
import daripher.skilltree.skill.bonus.condition.item.NoneItemCondition;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public final class HasGemsCondition implements LivingCondition {
  private int min;
  private int max;
  private @Nonnull ItemCondition itemCondition;

  public HasGemsCondition(int min, int max, @Nonnull ItemCondition itemCondition) {
    this.min = min;
    this.max = max;
    this.itemCondition = itemCondition;
  }

  @Override
  public boolean met(LivingEntity living) {
    int gems = getGems(PlayerHelper.getAllEquipment(living).filter(itemCondition::met));
    if (min == -1) {
      return gems <= max;
    }
    if (max == -1) {
      return gems >= min;
    }
    return gems <= max && gems >= min;
  }

  private int getGems(Stream<ItemStack> items) {
    return items.map(GemItem::getGems).map(List::size).reduce(Integer::sum).orElse(0);
  }

  @Override
  public MutableComponent getTooltip(MutableComponent bonusTooltip, String target) {
    String key = getDescriptionId();
    Component targetDescription = Component.translatable("%s.target.%s".formatted(key, target));
    Component itemDescription = itemCondition.getTooltip("where");
    if (min == -1) {
      return Component.translatable(
          key + ".max", bonusTooltip, targetDescription, max, itemDescription);
    }
    if (max == -1) {
      if (min == 1)
        return Component.translatable(
            key + ".min.1", bonusTooltip, targetDescription, itemDescription);
      return Component.translatable(
          key + ".min", bonusTooltip, targetDescription, min, itemDescription);
    }
    return Component.translatable(
        key + ".range", bonusTooltip, targetDescription, min, max, itemDescription);
  }

  @Override
  public LivingCondition.Serializer getSerializer() {
    return PSTLivingConditions.HAS_GEMS.get();
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
              editor.rebuildWidgets();
            });
    editor.shiftWidgets(0, 19);
    itemCondition.addEditorWidgets(
        editor,
        c -> {
          setItemCondition(c);
          consumer.accept(this);
        });
    editor.addLabel(0, 0, "Min", ChatFormatting.GREEN);
    editor.addLabel(55, 0, "Max", ChatFormatting.GREEN);
    editor.shiftWidgets(0, 19);
    editor
        .addNumericTextField(0, 0, 50, 14, min)
        .setNumericResponder(
            a -> {
              setMin(a.intValue());
              consumer.accept(this);
            });
    editor
        .addNumericTextField(55, 0, 50, 14, max)
        .setNumericResponder(
            a -> {
              setMax(a.intValue());
              consumer.accept(this);
            });
    editor.shiftWidgets(0, 19);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    HasGemsCondition that = (HasGemsCondition) o;
    return min == that.min && max == that.max && Objects.equals(itemCondition, that.itemCondition);
  }

  @Override
  public int hashCode() {
    return Objects.hash(min, max, itemCondition);
  }

  public void setItemCondition(@Nonnull ItemCondition itemCondition) {
    this.itemCondition = itemCondition;
  }

  public void setMax(int max) {
    this.max = max;
  }

  public void setMin(int min) {
    this.min = min;
  }

  public static class Serializer implements LivingCondition.Serializer {
    @Override
    public LivingCondition deserialize(JsonObject json) throws JsonParseException {
      int min = json.has("min") ? json.get("min").getAsInt() : -1;
      int max = json.has("max") ? json.get("max").getAsInt() : -1;
      ItemCondition itemCondition = SerializationHelper.deserializeItemCondition(json);
      return new HasGemsCondition(min, max, itemCondition);
    }

    @Override
    public void serialize(JsonObject json, LivingCondition condition) {
      if (!(condition instanceof HasGemsCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      if (aCondition.min != -1) {
        json.addProperty("min", aCondition.min);
      }
      if (aCondition.max != -1) {
        json.addProperty("max", aCondition.max);
      }
      SerializationHelper.serializeItemCondition(json, aCondition.itemCondition);
    }

    @Override
    public LivingCondition deserialize(CompoundTag tag) {
      int min = tag.contains("min") ? tag.getInt("min") : -1;
      int max = tag.contains("max") ? tag.getInt("max") : -1;
      ItemCondition itemCondition = SerializationHelper.deserializeItemCondition(tag);
      return new HasGemsCondition(min, max, itemCondition);
    }

    @Override
    public CompoundTag serialize(LivingCondition condition) {
      if (!(condition instanceof HasGemsCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      if (aCondition.min != -1) {
        tag.putInt("min", aCondition.min);
      }
      if (aCondition.max != -1) {
        tag.putInt("max", aCondition.max);
      }
      SerializationHelper.serializeItemCondition(tag, aCondition.itemCondition);
      return tag;
    }

    @Override
    public LivingCondition deserialize(FriendlyByteBuf buf) {
      return new HasGemsCondition(
          buf.readInt(), buf.readInt(), NetworkHelper.readItemCondition(buf));
    }

    @Override
    public void serialize(FriendlyByteBuf buf, LivingCondition condition) {
      if (!(condition instanceof HasGemsCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      buf.writeInt(aCondition.min);
      buf.writeInt(aCondition.max);
      NetworkHelper.writeItemCondition(buf, aCondition.itemCondition);
    }

    @Override
    public LivingCondition createDefaultInstance() {
      return new HasGemsCondition(1, -1, NoneItemCondition.INSTANCE);
    }
  }
}
