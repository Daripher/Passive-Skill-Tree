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
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class EnchantmentAmountFunction implements FloatFunction<EnchantmentAmountFunction> {
    private @Nonnull ItemStackPredicate itemStackPredicate;

    public EnchantmentAmountFunction(@Nonnull ItemStackPredicate itemStackPredicate) {
        this.itemStackPredicate = itemStackPredicate;
    }

    @Override
    public float apply(LivingEntity entity) {
        return getEnchants(PlayerHelper.getAllEquipment(entity).filter(itemStackPredicate));
    }

    private int getEnchants(Stream<ItemStack> items) {
        return items.map(EnchantmentHelper::getEnchantments).map(Map::size).reduce(Integer::sum).orElse(0);
    }

    @Override
    public MutableComponent getMultiplierTooltip(SkillBonus.Target target, float divisor, Component bonusTooltip) {
        String key = "%s.multiplier.%s".formatted(getDescriptionId(), target.getName());
        Component itemDescription = itemStackPredicate.getTooltip("prepositional");
        if (divisor != 1) {
            key += ".plural";
            return Component.translatable(key, bonusTooltip, formatNumber(divisor), itemDescription);
        } else {
            return Component.translatable(key, bonusTooltip, itemDescription);
        }
    }

    @Override
    public MutableComponent getPredicateTooltip(SkillBonus.Target target, FloatFunctionEntityPredicate.Logic logic, Component bonusTooltip, float requiredValue) {
        String key = "%s.condition.%s".formatted(getDescriptionId(), target.getName());
        String enchantmentsKey = getDescriptionId() + ".enchantment";
        if (requiredValue != 1) {
            enchantmentsKey += ".plural";
        }
        Component enchantmentsDescription = Component.translatable(enchantmentsKey);
        Component itemDescription = itemStackPredicate.getTooltip();
        if (requiredValue == 0 && logic == FloatFunctionEntityPredicate.Logic.EQUAL) {
            return Component.translatable(key + ".none", bonusTooltip, itemDescription);
        }
        if (requiredValue == 0 && logic == FloatFunctionEntityPredicate.Logic.MORE) {
            return Component.translatable(key + ".any", bonusTooltip, itemDescription);
        }
        String valueDescription = formatNumber(requiredValue);
        Component logicDescription = logic.getTooltip("enchantment_amount", valueDescription);
        return Component.translatable(key, bonusTooltip, logicDescription, enchantmentsDescription, itemDescription);
    }

    @Override
    public MutableComponent getRequirementTooltip(FloatFunctionEntityPredicate.Logic logic, float requiredValue) {
        String key = "%s.requirement".formatted(getDescriptionId());
        String enchantmentsKey = getDescriptionId() + ".enchantment";
        if (requiredValue != 1) {
            enchantmentsKey += ".plural";
        }
        Component enchantmentsDescription = Component.translatable(enchantmentsKey);
        Component itemDescription = itemStackPredicate.getTooltip();
        if (requiredValue == 0 && logic == FloatFunctionEntityPredicate.Logic.EQUAL) {
            return Component.translatable(key + ".none", itemDescription);
        }
        if (requiredValue == 0 && logic == FloatFunctionEntityPredicate.Logic.MORE) {
            return Component.translatable(key + ".any", itemDescription);
        }
        String valueDescription = formatNumber(requiredValue);
        Component logicDescription = logic.getTooltip("enchantment_amount", valueDescription);
        return Component.translatable(key, logicDescription, enchantmentsDescription, itemDescription);
    }

    @Override
    public FloatFunction.Serializer getSerializer() {
        return PSTFloatFunctions.ENCHANTMENT_AMOUNT.get();
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<FloatFunction<?>> consumer) {
        editor.addLabel(0, 0, "Item Condition", ChatFormatting.GREEN);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, itemStackPredicate).setResponder(condition -> selectItemCondition(editor, consumer, condition))
                .setMenuInitFunc(() -> addItemConditionWidgets(editor, consumer));
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
        EnchantmentAmountFunction that = (EnchantmentAmountFunction) o;
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
            ItemStackPredicate itemStackPredicate = SerializationHelper.deserializeItemPredicate(json);
            return new EnchantmentAmountFunction(itemStackPredicate);
        }

        @Override
        public void serialize(JsonObject json, FloatFunction<?> provider) {
            if (!(provider instanceof EnchantmentAmountFunction aProvider)) {
                throw new IllegalArgumentException();
            }
            SerializationHelper.serializeItemPredicate(json, aProvider.itemStackPredicate);
        }

        @Override
        public FloatFunction<?> deserialize(CompoundTag tag) {
            ItemStackPredicate itemStackPredicate = SerializationHelper.deserializeItemPredicate(tag);
            return new EnchantmentAmountFunction(itemStackPredicate);
        }

        @Override
        public CompoundTag serialize(FloatFunction<?> provider) {
            if (!(provider instanceof EnchantmentAmountFunction aProvider)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            SerializationHelper.serializeItemPredicate(tag, aProvider.itemStackPredicate);
            return tag;
        }

        @Override
        public FloatFunction<?> deserialize(FriendlyByteBuf buf) {
            ItemStackPredicate itemStackPredicate = NetworkHelper.readItemPredicate(buf);
            return new EnchantmentAmountFunction(itemStackPredicate);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, FloatFunction<?> provider) {
            if (!(provider instanceof EnchantmentAmountFunction aProvider)) {
                throw new IllegalArgumentException();
            }
            NetworkHelper.writeItemPredicate(buf, aProvider.itemStackPredicate);
        }

        @Override
        public FloatFunction<?> createDefaultInstance() {
            return new EnchantmentAmountFunction(new EquipmentPredicate(EquipmentPredicate.Type.WEAPON));
        }
    }
}
