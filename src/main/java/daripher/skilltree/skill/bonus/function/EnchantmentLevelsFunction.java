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
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class EnchantmentLevelsFunction implements FloatFunction<EnchantmentLevelsFunction> {
  private @Nonnull ItemStackPredicate itemStackPredicate;

  public EnchantmentLevelsFunction(@Nonnull ItemStackPredicate itemStackPredicate) {
    this.itemStackPredicate = itemStackPredicate;
  }

  @Override
  public float apply(LivingEntity entity) {
    return getEnchantLevels(PlayerHelper.getAllEquipment(entity).filter(itemStackPredicate));
  }

  private int getEnchantLevels(Stream<ItemStack> items) {
    return items
        .map(ItemStack::getEnchantments)
        .mapToInt(
            enchantments ->
                enchantments.entrySet().stream().mapToInt(entry -> entry.getIntValue()).sum())
        .sum();
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
  public MutableComponent getConditionTooltip(SkillBonus.Target target, FloatFunctionEntityPredicate.Logic logic, Component bonusTooltip,
                                              float requiredValue) {
    String key = "%s.condition.%s".formatted(getDescriptionId(), target.getName());
    String levelsKey = key + ".level";
    if (requiredValue != 1) {
      levelsKey += ".plural";
    }
    Component levelsDescription = Component.translatable(levelsKey);
    Component itemDescription = itemStackPredicate.getTooltip();
    if (requiredValue == 0 && logic == FloatFunctionEntityPredicate.Logic.EQUAL) {
      return Component.translatable(key + ".none", bonusTooltip, itemDescription);
    }
    if (requiredValue == 0 && logic == FloatFunctionEntityPredicate.Logic.MORE) {
      return Component.translatable(key + ".any", bonusTooltip, itemDescription);
    }
    String valueDescription = formatNumber(requiredValue);
    Component logicDescription = logic.getTooltip("enchantment_amount", valueDescription);
    return Component.translatable(key, bonusTooltip, logicDescription, levelsDescription, itemDescription);
  }

  @Override
  public MutableComponent getRequirementTooltip(FloatFunctionEntityPredicate.Logic logic, float requiredValue) {
    String key = "%s.requirement".formatted(getDescriptionId());
    String levelsKey = getDescriptionId() + ".level";
    if (requiredValue != 1) {
      levelsKey += ".plural";
    }
    Component levelsDescription = Component.translatable(levelsKey);
    Component itemDescription = itemStackPredicate.getTooltip();
    if (requiredValue == 0 && logic == FloatFunctionEntityPredicate.Logic.EQUAL) {
      return Component.translatable(key + ".none", itemDescription);
    }
    if (requiredValue == 0 && logic == FloatFunctionEntityPredicate.Logic.MORE) {
      return Component.translatable(key + ".any", itemDescription);
    }
    String valueDescription = formatNumber(requiredValue);
    Component logicDescription = logic.getTooltip("enchantment_amount", valueDescription);
    return Component.translatable(key, logicDescription, levelsDescription, itemDescription);
  }

  @Override
  public FloatFunction.Serializer getSerializer() {
    return PSTFloatFunctions.ENCHANTMENT_LEVELS.get();
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
    EnchantmentLevelsFunction that = (EnchantmentLevelsFunction) o;
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
      return new EnchantmentLevelsFunction(itemStackPredicate);
    }

    @Override
    public void serialize(JsonObject json, FloatFunction<?> provider) {
      if (!(provider instanceof EnchantmentLevelsFunction aProvider)) {
        throw new IllegalArgumentException();
      }
      SerializationHelper.serializeItemCondition(json, aProvider.itemStackPredicate);
    }

    @Override
    public FloatFunction<?> deserialize(CompoundTag tag) {
      ItemStackPredicate itemStackPredicate = SerializationHelper.deserializeItemCondition(tag);
      return new EnchantmentLevelsFunction(itemStackPredicate);
    }

    @Override
    public CompoundTag serialize(FloatFunction<?> provider) {
      if (!(provider instanceof EnchantmentLevelsFunction aProvider)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      SerializationHelper.serializeItemCondition(tag, aProvider.itemStackPredicate);
      return tag;
    }

    @Override
    public FloatFunction<?> deserialize(FriendlyByteBuf buf) {
      ItemStackPredicate itemStackPredicate = NetworkHelper.readItemCondition(buf);
      return new EnchantmentLevelsFunction(itemStackPredicate);
    }

    @Override
    public void serialize(FriendlyByteBuf buf, FloatFunction<?> provider) {
      if (!(provider instanceof EnchantmentLevelsFunction aProvider)) {
        throw new IllegalArgumentException();
      }
      NetworkHelper.writeItemCondition(buf, aProvider.itemStackPredicate);
    }

    @Override
    public FloatFunction<?> createDefaultInstance() {
      return new EnchantmentLevelsFunction(new EquipmentPredicate(EquipmentPredicate.Type.WEAPON));
    }
  }
}
