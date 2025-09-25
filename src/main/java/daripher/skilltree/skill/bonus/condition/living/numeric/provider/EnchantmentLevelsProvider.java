package daripher.skilltree.skill.bonus.condition.living.numeric.provider;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.entity.player.PlayerHelper;
import daripher.skilltree.init.PSTNumericValueProviders;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.condition.item.EquipmentCondition;
import daripher.skilltree.skill.bonus.condition.item.ItemCondition;
import daripher.skilltree.skill.bonus.condition.living.numeric.NumericValueCondition;
import daripher.skilltree.skill.bonus.condition.living.numeric.NumericValueProvider;
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

public class EnchantmentLevelsProvider implements NumericValueProvider<EnchantmentLevelsProvider> {
  private @Nonnull ItemCondition itemCondition;

  public EnchantmentLevelsProvider(@Nonnull ItemCondition itemCondition) {
    this.itemCondition = itemCondition;
  }

  @Override
  public float getValue(LivingEntity entity) {
    return getEnchantLevels(PlayerHelper.getAllEquipment(entity).filter(itemCondition::met));
  }

  private int getEnchantLevels(Stream<ItemStack> items) {
    return items.map(EnchantmentHelper::getEnchantments).mapToInt(m -> m.values().stream().reduce(Integer::sum).orElse(0)).reduce(Integer::sum).orElse(0);
  }

  @Override
  public MutableComponent getMultiplierTooltip(SkillBonus.Target target, float divisor, Component bonusTooltip) {
    String key = "%s.multiplier.%s".formatted(getDescriptionId(), target.getName());
    Component itemDescription = itemCondition.getTooltip();
    if (divisor != 1) {
      key += ".plural";
      return Component.translatable(key, bonusTooltip, formatNumber(divisor), itemDescription);
    } else {
      return Component.translatable(key, bonusTooltip, itemDescription);
    }
  }

  @Override
  public MutableComponent getConditionTooltip(SkillBonus.Target target, NumericValueCondition.Logic logic, Component bonusTooltip,
                                              float requiredValue) {
    String key = "%s.condition.%s".formatted(getDescriptionId(), target.getName());
    String levelsKey = key + ".level";
    if (requiredValue != 1) {
      levelsKey += ".plural";
    }
    Component levelsDescription = Component.translatable(levelsKey);
    Component itemDescription = itemCondition.getTooltip();
    if (requiredValue == 0 && logic == NumericValueCondition.Logic.EQUAL) {
      return Component.translatable(key + ".none", bonusTooltip, itemDescription);
    }
    if (requiredValue == 0 && logic == NumericValueCondition.Logic.MORE) {
      return Component.translatable(key + ".any", bonusTooltip, itemDescription);
    }
    String valueDescription = formatNumber(requiredValue);
    Component logicDescription = logic.getTooltip("enchantment_amount", valueDescription);
    return Component.translatable(key, bonusTooltip, logicDescription, levelsDescription, itemDescription);
  }

  @Override
  public MutableComponent getRequirementTooltip(NumericValueCondition.Logic logic, float requiredValue) {
    String key = "%s.requirement".formatted(getDescriptionId());
    String levelsKey = key + ".level";
    if (requiredValue != 1) {
      levelsKey += ".plural";
    }
    Component levelsDescription = Component.translatable(levelsKey);
    Component itemDescription = itemCondition.getTooltip();
    if (requiredValue == 0 && logic == NumericValueCondition.Logic.EQUAL) {
      return Component.translatable(key + ".none", itemDescription);
    }
    if (requiredValue == 0 && logic == NumericValueCondition.Logic.MORE) {
      return Component.translatable(key + ".any", itemDescription);
    }
    String valueDescription = formatNumber(requiredValue);
    Component logicDescription = logic.getTooltip("enchantment_amount", valueDescription);
    return Component.translatable(key, logicDescription, levelsDescription, itemDescription);
  }

  @Override
  public NumericValueProvider.Serializer getSerializer() {
    return PSTNumericValueProviders.ENCHANTMENT_LEVELS.get();
  }

  @Override
  public void addEditorWidgets(SkillTreeEditor editor, Consumer<NumericValueProvider<?>> consumer) {
    editor.addLabel(0, 0, "Item Condition", ChatFormatting.GREEN);
    editor.increaseHeight(19);
    editor.addSelectionMenu(0, 0, 200, itemCondition).setResponder(condition -> selectItemCondition(editor, consumer, condition)).setMenuInitFunc(() -> addItemConditionWidgets(editor, consumer));
    editor.increaseHeight(19);
  }

  private void addItemConditionWidgets(SkillTreeEditor editor, Consumer<NumericValueProvider<?>> consumer) {
    itemCondition.addEditorWidgets(editor, condition -> {
      setItemCondition(condition);
      consumer.accept(this);
    });
  }

  private void selectItemCondition(SkillTreeEditor editor, Consumer<NumericValueProvider<?>> consumer, ItemCondition condition) {
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
    EnchantmentLevelsProvider that = (EnchantmentLevelsProvider) o;
    return Objects.equals(itemCondition, that.itemCondition);
  }

  @Override
  public int hashCode() {
    return Objects.hash(itemCondition);
  }

  public void setItemCondition(@Nonnull ItemCondition itemCondition) {
    this.itemCondition = itemCondition;
  }

  public static class Serializer implements NumericValueProvider.Serializer {
    @Override
    public NumericValueProvider<?> deserialize(JsonObject json) throws JsonParseException {
      ItemCondition itemCondition = SerializationHelper.deserializeItemCondition(json);
      return new EnchantmentLevelsProvider(itemCondition);
    }

    @Override
    public void serialize(JsonObject json, NumericValueProvider<?> provider) {
      if (!(provider instanceof EnchantmentLevelsProvider aProvider)) {
        throw new IllegalArgumentException();
      }
      SerializationHelper.serializeItemCondition(json, aProvider.itemCondition);
    }

    @Override
    public NumericValueProvider<?> deserialize(CompoundTag tag) {
      ItemCondition itemCondition = SerializationHelper.deserializeItemCondition(tag);
      return new EnchantmentLevelsProvider(itemCondition);
    }

    @Override
    public CompoundTag serialize(NumericValueProvider<?> provider) {
      if (!(provider instanceof EnchantmentLevelsProvider aProvider)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      SerializationHelper.serializeItemCondition(tag, aProvider.itemCondition);
      return tag;
    }

    @Override
    public NumericValueProvider<?> deserialize(FriendlyByteBuf buf) {
      ItemCondition itemCondition = NetworkHelper.readItemCondition(buf);
      return new EnchantmentLevelsProvider(itemCondition);
    }

    @Override
    public void serialize(FriendlyByteBuf buf, NumericValueProvider<?> provider) {
      if (!(provider instanceof EnchantmentLevelsProvider aProvider)) {
        throw new IllegalArgumentException();
      }
      NetworkHelper.writeItemCondition(buf, aProvider.itemCondition);
    }

    @Override
    public NumericValueProvider<?> createDefaultInstance() {
      return new EnchantmentLevelsProvider(new EquipmentCondition(EquipmentCondition.Type.WEAPON));
    }
  }
}
