package daripher.skilltree.skill.bonus.predicate.living;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.predicate.PSTLivingEntityPredicates;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.function.FloatFunction;
import daripher.skilltree.skill.bonus.function.HealthLevelFunction;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

public class FloatFunctionEntityPredicate implements LivingEntityPredicate {
    private FloatFunction<?> valueProvider;
    private float requiredValue;
    private Logic logic;

    public FloatFunctionEntityPredicate(FloatFunction<?> valueProvider, float requiredValue, Logic logic) {
        this.valueProvider = valueProvider;
        this.requiredValue = requiredValue;
        this.logic = logic;
    }

    @Override
    public boolean test(LivingEntity living) {
        float value = valueProvider.apply(living);
        return switch (logic) {
            case EQUAL -> value == requiredValue;
            case MORE -> value > requiredValue;
            case LESS -> value < requiredValue;
            case AT_LEAST -> value >= requiredValue;
            case AT_MOST -> value <= requiredValue;
        };
    }

    @Override
    public MutableComponent getTooltip(MutableComponent bonusTooltip, SkillBonus.Target target) {
        return valueProvider.getPredicateTooltip(target, logic, bonusTooltip, requiredValue);
    }

    @Override
    public LivingEntityPredicate.Serializer getSerializer() {
        return PSTLivingEntityPredicates.NUMERIC_VALUE.get();
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<LivingEntityPredicate> consumer) {
        editor.addLabel(0, 0, "Value Type", ChatFormatting.GREEN);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, valueProvider).setResponder(provider -> selectValueProvider(editor, consumer, provider))
                .setMenuInitFunc(() -> addValueProviderWidgets(editor, consumer));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Logic", ChatFormatting.GREEN);
        editor.addLabel(100, 0, "Required Value", ChatFormatting.GREEN);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 90, logic).setElementNameGetter(logic -> Component.literal(logic.name()))
                .setResponder(logic -> selectLogic(consumer, logic));
        editor.addNumericTextField(100, 0, 50, 14, requiredValue).setNumericResponder(value -> selectRequiredValue(consumer, value));
        editor.increaseHeight(19);
    }

    private void addValueProviderWidgets(SkillTreeEditor editor, Consumer<LivingEntityPredicate> consumer) {
        valueProvider.addEditorWidgets(editor, provider -> selectValueProvider(editor, consumer, provider));
    }

    private void selectRequiredValue(Consumer<LivingEntityPredicate> consumer, Double value) {
        setRequiredValue(value.floatValue());
        consumer.accept(this);
    }

    private void selectValueProvider(SkillTreeEditor editor, Consumer<LivingEntityPredicate> consumer, FloatFunction<?> provider) {
        setValueProvider(provider);
        consumer.accept(this);
        editor.rebuildWidgets();
    }

    private void selectLogic(Consumer<LivingEntityPredicate> consumer, Logic logic) {
        setLogic(logic);
        consumer.accept(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FloatFunctionEntityPredicate that = (FloatFunctionEntityPredicate) o;
        if (Float.compare(requiredValue, that.requiredValue) != 0) {
            return false;
        }
        if (!Objects.equals(valueProvider, that.valueProvider)) {
            return false;
        }
        return logic == that.logic;
    }

    @Override
    public int hashCode() {
        return Objects.hash(valueProvider, requiredValue, logic);
    }

    public void setValueProvider(FloatFunction<?> provider) {
        this.valueProvider = provider;
    }

    public void setRequiredValue(float requiredValue) {
        this.requiredValue = requiredValue;
    }

    public void setLogic(Logic logic) {
        this.logic = logic;
    }

    public FloatFunction<?> getValueProvider() {
        return valueProvider;
    }

    public Logic getLogic() {
        return logic;
    }

    public float getRequiredValue() {
        return requiredValue;
    }

    public static class Serializer implements LivingEntityPredicate.Serializer {
        @Override
        public LivingEntityPredicate deserialize(JsonObject json) throws JsonParseException {
            FloatFunction<?> valueProvider = SerializationHelper.deserializeValueProvider(json);
            float requiredValue = json.get("required_value").getAsFloat();
            Logic logic = Logic.valueOf(json.get("logic").getAsString());
            return new FloatFunctionEntityPredicate(valueProvider, requiredValue, logic);
        }

        @Override
        public void serialize(JsonObject json, LivingEntityPredicate predicate) {
            FloatFunctionEntityPredicate validPredicate = validatePredicate(predicate);
            SerializationHelper.serializeValueProvider(json, validPredicate.valueProvider);
            json.addProperty("required_value", validPredicate.requiredValue);
            json.addProperty("logic", validPredicate.logic.name());
        }

        @Override
        public LivingEntityPredicate deserialize(CompoundTag tag) {
            FloatFunction<?> valueProvider = SerializationHelper.deserializeValueProvider(tag);
            float requiredValue = tag.getFloat("required_value");
            Logic logic = Logic.valueOf(tag.getString("logic"));
            return new FloatFunctionEntityPredicate(valueProvider, requiredValue, logic);
        }

        @Override
        public CompoundTag serialize(LivingEntityPredicate predicate) {
            FloatFunctionEntityPredicate validPredicate = validatePredicate(predicate);
            CompoundTag tag = new CompoundTag();
            SerializationHelper.serializeValueProvider(tag, validPredicate.valueProvider);
            tag.putFloat("required_value", validPredicate.requiredValue);
            tag.putString("logic", validPredicate.logic.name());
            return tag;
        }

        @Override
        public LivingEntityPredicate deserialize(FriendlyByteBuf buf) {
            FloatFunction<?> valueProvider = NetworkHelper.readValueProvider(buf);
            float requiredValue = buf.readFloat();
            Logic logic = Logic.values()[buf.readInt()];
            return new FloatFunctionEntityPredicate(valueProvider, requiredValue, logic);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, LivingEntityPredicate predicate) {
            FloatFunctionEntityPredicate validPredicate = validatePredicate(predicate);
            NetworkHelper.writeValueProvider(buf, validPredicate.valueProvider);
            buf.writeFloat(validPredicate.requiredValue);
            buf.writeInt(validPredicate.logic.ordinal());
        }

        private static @NotNull FloatFunctionEntityPredicate validatePredicate(LivingEntityPredicate predicate) {
            if (!(predicate instanceof FloatFunctionEntityPredicate validPredicate)) {
                throw new IllegalArgumentException("Expected FloatFunctionEntityPredicate, got: " + predicate);
            }
            return validPredicate;
        }

        @Override
        public LivingEntityPredicate createDefaultInstance() {
            return new FloatFunctionEntityPredicate(new HealthLevelFunction(true, false), 1, Logic.EQUAL);
        }
    }

    public enum Logic {
        MORE, LESS, EQUAL, AT_LEAST, AT_MOST;

        public String getName() {
            return name().toLowerCase(Locale.ROOT);
        }

        public Component getTooltip(String subtype, Object... args) {
            String predicateDescriptionId = PSTLivingEntityPredicates.NUMERIC_VALUE.get().createDefaultInstance().getDescriptionId();
            String key = predicateDescriptionId + "." + getName();
            return TooltipHelper.getOptionalTooltip(key, subtype, args);
        }
    }
}
