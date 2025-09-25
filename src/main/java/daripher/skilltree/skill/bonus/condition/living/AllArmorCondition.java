package daripher.skilltree.skill.bonus.condition.living;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.entity.player.PlayerHelper;
import daripher.skilltree.init.PSTLivingConditions;
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
import net.minecraft.world.entity.LivingEntity;

public final class AllArmorCondition implements LivingCondition {
  private @Nonnull ItemCondition itemCondition;

  public AllArmorCondition(@Nonnull ItemCondition itemCondition) {
    this.itemCondition = itemCondition;
  }

  @Override
  public boolean isConditionMet(LivingEntity living) {
    return PlayerHelper.getArmor(living).allMatch(itemCondition::met);
  }

  @Override
  public MutableComponent getTooltip(MutableComponent bonusTooltip, SkillBonus.Target target) {
    String key = getDescriptionId();
    Component targetDescription = Component.translatable("%s.target.%s".formatted(key, target.getName()));
    Component itemDescription = itemCondition.getTooltip();
    return Component.translatable(key, bonusTooltip, targetDescription, itemDescription);
  }

  @Override
  public LivingCondition.Serializer getSerializer() {
    return PSTLivingConditions.ALL_ARMOR.get();
  }

  @Override
  public void addEditorWidgets(SkillTreeEditor editor, Consumer<LivingCondition> consumer) {
    editor.addLabel(0, 0, "Item Condition", ChatFormatting.GREEN);
    editor.increaseHeight(19);
    editor
        .addSelectionMenu(0, 0, 200, itemCondition)
        .setResponder(condition -> selectItemCondition(editor, consumer, condition))
        .setMenuInitFunc(() -> addItemConditionWidgets(editor, consumer));
    editor.increaseHeight(19);
  }

  private void addItemConditionWidgets(SkillTreeEditor editor, Consumer<LivingCondition> consumer) {
    itemCondition.addEditorWidgets(
        editor,
        condition -> {
          setItemCondition(condition);
          consumer.accept(this);
        });
  }

  private void selectItemCondition(
      SkillTreeEditor editor, Consumer<LivingCondition> consumer, ItemCondition condition) {
    setItemCondition(condition);
    consumer.accept(this);
    editor.rebuildWidgets();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AllArmorCondition that = (AllArmorCondition) o;
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
      return new AllArmorCondition(SerializationHelper.deserializeItemCondition(json));
    }

    @Override
    public void serialize(JsonObject json, LivingCondition condition) {
      if (!(condition instanceof AllArmorCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      SerializationHelper.serializeItemCondition(json, aCondition.itemCondition);
    }

    @Override
    public LivingCondition deserialize(CompoundTag tag) {
      return new AllArmorCondition(SerializationHelper.deserializeItemCondition(tag));
    }

    @Override
    public CompoundTag serialize(LivingCondition condition) {
      if (!(condition instanceof AllArmorCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      SerializationHelper.serializeItemCondition(tag, aCondition.itemCondition);
      return tag;
    }

    @Override
    public LivingCondition deserialize(FriendlyByteBuf buf) {
      return new AllArmorCondition(NetworkHelper.readItemCondition(buf));
    }

    @Override
    public void serialize(FriendlyByteBuf buf, LivingCondition condition) {
      if (!(condition instanceof AllArmorCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      NetworkHelper.writeItemCondition(buf, aCondition.itemCondition);
    }

    @Override
    public LivingCondition createDefaultInstance() {
      return new AllArmorCondition(NoneItemCondition.INSTANCE);
    }
  }
}
