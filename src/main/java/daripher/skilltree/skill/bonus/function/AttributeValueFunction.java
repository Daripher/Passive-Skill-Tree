package daripher.skilltree.skill.bonus.function;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTFloatFunctions;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.predicate.living.FloatFunctionEntityPredicate;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.function.Consumer;

public class AttributeValueFunction implements FloatFunction<AttributeValueFunction> {
    private Attribute attribute;

    public AttributeValueFunction(Attribute attribute) {
        this.attribute = attribute;
    }

    @Override
    public float apply(LivingEntity entity) {
        AttributeMap attributes = entity.getAttributes();
        return attributes.hasAttribute(attribute) ? (float) attributes.getValue(attribute) : 0f;
    }

    @Override
    public MutableComponent getMultiplierTooltip(SkillBonus.Target target, float divisor, Component bonusTooltip) {
        String key = "%s.multiplier.%s".formatted(getDescriptionId(), target.getName());
        MutableComponent attributeDescription = Component.translatable(attribute.getDescriptionId());
        if (divisor != 1) {
            key += ".plural";
            return Component.translatable(key, bonusTooltip, formatNumber(divisor), attributeDescription);
        } else {
            return Component.translatable(key, bonusTooltip, attributeDescription);
        }
    }

    @Override
    public MutableComponent getPredicateTooltip(SkillBonus.Target target, FloatFunctionEntityPredicate.Logic logic, Component bonusTooltip, float requiredValue) {
        String key = "%s.condition.%s".formatted(getDescriptionId(), target.getName());
        Component attributeDescription = Component.translatable(attribute.getDescriptionId());
        String valueDescription = formatNumber(requiredValue);
        Component logicDescription = logic.getTooltip("attribute_value", valueDescription);
        return Component.translatable(key, bonusTooltip, attributeDescription, logicDescription);
    }

    @Override
    public MutableComponent getRequirementTooltip(FloatFunctionEntityPredicate.Logic logic, float requiredValue) {
        String key = "%s.requirement".formatted(getDescriptionId());
        Component attributeDescription = Component.translatable(attribute.getDescriptionId());
        String valueDescription = formatNumber(requiredValue);
        Component logicDescription = logic.getTooltip("attribute_value", valueDescription);
        return Component.translatable(key, logicDescription, attributeDescription);
    }

    @Override
    public FloatFunction.Serializer getSerializer() {
        return PSTFloatFunctions.ATTRIBUTE_VALUE.get();
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<FloatFunction<?>> consumer) {
        editor.addLabel(0, 0, "Attribute", ChatFormatting.GREEN);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, attribute).setResponder(attribute -> selectAttribute(consumer, attribute));
        editor.increaseHeight(19);
    }

    private void selectAttribute(Consumer<FloatFunction<?>> consumer, Attribute attribute) {
        setAttribute(attribute);
        consumer.accept(this);
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public static class Serializer implements FloatFunction.Serializer {
        @Override
        public FloatFunction<?> deserialize(JsonObject json) throws JsonParseException {
            Attribute attribute = SerializationHelper.deserializeAttribute(json);
            return new AttributeValueFunction(attribute);
        }

        @Override
        public void serialize(JsonObject json, FloatFunction<?> provider) {
            if (!(provider instanceof AttributeValueFunction aProvider)) {
                throw new IllegalArgumentException();
            }
            SerializationHelper.serializeAttribute(json, aProvider.attribute);
        }

        @Override
        public FloatFunction<?> deserialize(CompoundTag tag) {
            Attribute attribute = SerializationHelper.deserializeAttribute(tag);
            return new AttributeValueFunction(attribute);
        }

        @Override
        public CompoundTag serialize(FloatFunction<?> provider) {
            if (!(provider instanceof AttributeValueFunction aProvider)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            SerializationHelper.serializeAttribute(tag, aProvider.attribute);
            return tag;
        }

        @Override
        public FloatFunction<?> deserialize(FriendlyByteBuf buf) {
            Attribute attribute = NetworkHelper.readAttribute(buf);
            return new AttributeValueFunction(attribute);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, FloatFunction<?> provider) {
            if (!(provider instanceof AttributeValueFunction aProvider)) {
                throw new IllegalArgumentException();
            }
            NetworkHelper.writeAttribute(buf, aProvider.attribute);
        }

        @Override
        public FloatFunction<?> createDefaultInstance() {
            return new AttributeValueFunction(Attributes.MAX_HEALTH);
        }
    }
}
