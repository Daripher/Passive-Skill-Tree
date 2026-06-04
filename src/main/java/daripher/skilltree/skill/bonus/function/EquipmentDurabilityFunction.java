package daripher.skilltree.skill.bonus.function;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.entity.player.PlayerHelper;
import daripher.skilltree.init.PSTFloatFunctions;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.predicate.item.EquipmentPredicate;
import daripher.skilltree.skill.bonus.predicate.item.ItemStackPredicate;
import daripher.skilltree.skill.bonus.predicate.living.FloatFunctionEntityPredicate;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Consumer;

public class EquipmentDurabilityFunction implements FloatFunction<EquipmentDurabilityFunction> {
  private @Nonnull ItemStackPredicate itemStackPredicate;

  public EquipmentDurabilityFunction(@Nonnull ItemStackPredicate itemStackPredicate) {
    this.itemStackPredicate = itemStackPredicate;
  }

  @Override
  public float apply(LivingEntity entity) {
    return PlayerHelper.getAllEquipment(entity).filter(itemStackPredicate).map(ItemStack::getMaxDamage).reduce(Integer::sum).orElse(0);
  }

  @Override
  public MutableComponent getMultiplierTooltip(SkillBonus.Target target, float divisor, Component bonusTooltip) {
    String key = "%s.multiplier.%s".formatted(getDescriptionId(), target.getName());
    Component itemDescription = itemStackPredicate.getTooltip();
    if (divisor != 1) {
      key += ".plural";
      return Component.translatable(key, bonusTooltip, formatNumber(divisor), itemDescription);
    } else {
      return Component.translatable(key, bonusTooltip, itemDescription);
    }
  }

  @Override
  public MutableComponent getConditionTooltip(SkillBonus.Target target, FloatFunctionEntityPredicate.Logic logic, Component bonusTooltip, float requiredValue) {
    String key = "%s.condition.%s".formatted(getDescriptionId(), target.getName());
    Component itemDescription = itemStackPredicate.getTooltip();
    String valueDescription = formatNumber(requiredValue);
    Component logicDescription = logic.getTooltip("equipment_durability", valueDescription);
    return Component.translatable(key, bonusTooltip, itemDescription, logicDescription);
  }

  @Override
  public MutableComponent getRequirementTooltip(FloatFunctionEntityPredicate.Logic logic, float requiredValue) {
    String key = "%s.requirement".formatted(getDescriptionId());
    Component itemDescription = itemStackPredicate.getTooltip();
    String valueDescription = formatNumber(requiredValue);
    Component logicDescription = logic.getTooltip("equipment_durability", valueDescription);
    return Component.translatable(key, logicDescription, itemDescription);
  }

  @Override
  public FloatFunction.Serializer getSerializer() {
    return PSTFloatFunctions.EQUIPMENT_DURABILITY.get();
  }

  @Override
  public void addEditorWidgets(SkillTreeEditor editor, Consumer<FloatFunction<?>> consumer) {
    editor.addLabel(0, 0, "Item Condition", ChatFormatting.GREEN);
    editor.increaseHeight(19);
    editor.addSelectionMenu(0, 0, 200, itemStackPredicate).setResponder(condition -> selectItemCondition(editor, consumer, condition)).setMenuInitFunc(() -> addItemConditionWidgets(editor, consumer));
    editor.increaseHeight(19);
  }

  private void addItemConditionWidgets(SkillTreeEditor editor, Consumer<FloatFunction<?>> consumer) {
    itemStackPredicate.addEditorWidgets(editor, condition -> {
      setItemCondition(condition);
      consumer.accept(this);
    });
  }

  private void selectItemCondition(SkillTreeEditor editor, Consumer<FloatFunction<?>> consumer, ItemStackPredicate condition) {
    setItemCondition(condition);
    consumer.accept(this);
    editor.rebuildWidgets();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EquipmentDurabilityFunction that = (EquipmentDurabilityFunction) o;
    return Objects.equals(itemStackPredicate, that.itemStackPredicate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(itemStackPredicate);
  }

  public void setItemCondition(@Nonnull ItemStackPredicate itemStackPredicate) {
    this.itemStackPredicate = itemStackPredicate;
  }

  public static class Serializer implements FloatFunction.Serializer {
    @Override
    public FloatFunction<?> deserialize(JsonObject json) throws JsonParseException {
      ItemStackPredicate itemStackPredicate = SerializationHelper.deserializeItemCondition(json);
      return new EquipmentDurabilityFunction(itemStackPredicate);
    }

    @Override
    public void serialize(JsonObject json, FloatFunction<?> provider) {
      if (!(provider instanceof EquipmentDurabilityFunction aProvider)) {
        throw new IllegalArgumentException();
      }
      SerializationHelper.serializeItemCondition(json, aProvider.itemStackPredicate);
    }

    @Override
    public FloatFunction<?> deserialize(CompoundTag tag) {
      ItemStackPredicate itemStackPredicate = SerializationHelper.deserializeItemCondition(tag);
      return new EquipmentDurabilityFunction(itemStackPredicate);
    }

    @Override
    public CompoundTag serialize(FloatFunction<?> provider) {
      if (!(provider instanceof EquipmentDurabilityFunction aProvider)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      SerializationHelper.serializeItemCondition(tag, aProvider.itemStackPredicate);
      return tag;
    }

    @Override
    public FloatFunction<?> deserialize(FriendlyByteBuf buf) {
      ItemStackPredicate itemStackPredicate = NetworkHelper.readItemCondition(buf);
      return new EquipmentDurabilityFunction(itemStackPredicate);
    }

    @Override
    public void serialize(FriendlyByteBuf buf, FloatFunction<?> provider) {
      if (!(provider instanceof EquipmentDurabilityFunction aProvider)) {
        throw new IllegalArgumentException();
      }
      NetworkHelper.writeItemCondition(buf, aProvider.itemStackPredicate);
    }

    @Override
    public FloatFunction<?> createDefaultInstance() {
      return new EquipmentDurabilityFunction(new EquipmentPredicate(EquipmentPredicate.Type.WEAPON));
    }
  }
}
