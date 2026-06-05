package daripher.skilltree.skill.bonus.multiplier;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTLivingMultipliers;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.function.AttributeValueFunction;
import daripher.skilltree.skill.bonus.function.FloatFunction;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.Objects;
import java.util.function.Consumer;

public final class FloatFunctionMultiplier implements LivingMultiplier {
    private FloatFunction<?> floatFunction;
    private float divisor;

    public FloatFunctionMultiplier(FloatFunction<?> floatFunction, float divisor) {
        this.floatFunction = floatFunction;
        this.divisor = divisor;
    }

    @Override
    public float getValue(LivingEntity entity) {
        return (int) (floatFunction.apply(entity) / divisor);
    }

    @Override
    public MutableComponent getTooltip(MutableComponent bonusTooltip, SkillBonus.Target target) {
        return floatFunction.getMultiplierTooltip(target, divisor, bonusTooltip);
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<LivingMultiplier> consumer) {
        editor.addLabel(0, 0, "Value Type", ChatFormatting.GREEN);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, floatFunction).setResponder(provider -> {
            selectValueProvider(consumer, provider);
            addValueProviderWidgets(editor, consumer);
            editor.rebuildWidgets();
        }).setMenuInitFunc(() -> addValueProviderWidgets(editor, consumer));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Divisor", ChatFormatting.GREEN);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 50, 14, divisor).setNumericFilter(value -> value > 0)
                .setNumericResponder(value -> selectDivisor(consumer, value));
        editor.increaseHeight(19);
    }

    private void addValueProviderWidgets(SkillTreeEditor editor, Consumer<LivingMultiplier> consumer) {
        floatFunction.addEditorWidgets(editor, provider -> selectValueProvider(consumer, provider));
    }

    private void selectDivisor(Consumer<LivingMultiplier> consumer, Double value) {
        setDivisor(value.floatValue());
        consumer.accept(this);
    }

    private void selectValueProvider(Consumer<LivingMultiplier> consumer, FloatFunction<?> valueProvider) {
        setFloatFunction(valueProvider);
        consumer.accept(this);
    }

    public float getDivisor() {
        return divisor;
    }

    @Override
    public LivingMultiplier.Serializer getSerializer() {
        return PSTLivingMultipliers.NUMERIC_VALUE.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FloatFunctionMultiplier that = (FloatFunctionMultiplier) o;
        if (Float.compare(divisor, that.divisor) != 0) {
            return false;
        }
        return Objects.equals(floatFunction, that.floatFunction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(floatFunction, divisor);
    }

    public void setFloatFunction(FloatFunction<?> floatFunction) {
        this.floatFunction = floatFunction;
    }

    public void setDivisor(float divisor) {
        this.divisor = divisor;
    }

    public FloatFunction<?> getFloatFunction() {
        return floatFunction;
    }

    public static class Serializer implements LivingMultiplier.Serializer {
        @Override
        public LivingMultiplier deserialize(JsonObject json) throws JsonParseException {
            FloatFunction<?> valueProvider = SerializationHelper.deserializeValueProvider(json);
            float divisor = !json.has("divisor") ? 1f : json.get("divisor").getAsFloat();
            return new FloatFunctionMultiplier(valueProvider, divisor);
        }

        @Override
        public void serialize(JsonObject json, LivingMultiplier multiplier) {
            if (!(multiplier instanceof FloatFunctionMultiplier aMultiplier)) {
                throw new IllegalArgumentException();
            }
            SerializationHelper.serializeValueProvider(json, aMultiplier.floatFunction);
            json.addProperty("divisor", aMultiplier.divisor);
        }

        @Override
        public LivingMultiplier deserialize(CompoundTag tag) {
            FloatFunction<?> valueProvider = SerializationHelper.deserializeValueProvider(tag);
            float divisor = !tag.contains("divisor") ? 1f : tag.getFloat("divisor");
            return new FloatFunctionMultiplier(valueProvider, divisor);
        }

        @Override
        public CompoundTag serialize(LivingMultiplier multiplier) {
            if (!(multiplier instanceof FloatFunctionMultiplier aMultiplier)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            SerializationHelper.serializeValueProvider(tag, aMultiplier.floatFunction);
            tag.putFloat("divisor", aMultiplier.divisor);
            return tag;
        }

        @Override
        public LivingMultiplier deserialize(FriendlyByteBuf buf) {
            FloatFunction<?> valueProvider = NetworkHelper.readValueProvider(buf);
            float divisor = buf.readFloat();
            return new FloatFunctionMultiplier(valueProvider, divisor);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, LivingMultiplier multiplier) {
            if (!(multiplier instanceof FloatFunctionMultiplier aMultiplier)) {
                throw new IllegalArgumentException();
            }
            NetworkHelper.writeValueProvider(buf, aMultiplier.floatFunction);
            buf.writeFloat(aMultiplier.divisor);
        }

        @Override
        public LivingMultiplier createDefaultInstance() {
            return new FloatFunctionMultiplier(new AttributeValueFunction(Attributes.MAX_HEALTH), 5f);
        }
    }
}
