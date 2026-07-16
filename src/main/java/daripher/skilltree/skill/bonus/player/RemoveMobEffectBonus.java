package daripher.skilltree.skill.bonus.player;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.EventListenerBonus;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.event.OutgoingDamageEventListener;
import daripher.skilltree.skill.bonus.event.SkillEventListener;
import daripher.skilltree.skill.bonus.predicate.effect.MobEffectPredicate;
import daripher.skilltree.skill.bonus.predicate.effect.MobEffectType;
import daripher.skilltree.skill.bonus.predicate.effect.MobEffectTypePredicate;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;

public final class RemoveMobEffectBonus implements EventListenerBonus<RemoveMobEffectBonus> {
    private MobEffectPredicate effectPredicate;
    private SkillEventListener eventListener;
    private float chance;

    public RemoveMobEffectBonus(float chance, MobEffectPredicate effectPredicate, SkillEventListener eventListener) {
        this.chance = chance;
        this.effectPredicate = effectPredicate;
        this.eventListener = eventListener;
    }

    @Override
    public void applyEffect(LivingEntity target, @Nullable LivingEntity source) {
        target.getActiveEffects().stream()
                .filter(instance -> effectPredicate.test(instance.getEffect().value()))
                .map(MobEffectInstance::getEffect)
                .toList()
                .forEach(target::removeEffect);
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return PSTSkillBonuses.REMOVE_EFFECT.get();
    }

    @Override
    public RemoveMobEffectBonus copy() {
        return new RemoveMobEffectBonus(chance, effectPredicate, eventListener);
    }

    @Override
    public RemoveMobEffectBonus multiply(double multiplier) {
        float chance = (float) (this.chance * multiplier);
        return new RemoveMobEffectBonus(chance, effectPredicate, eventListener);
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof RemoveMobEffectBonus otherBonus)) {
            return false;
        }
        if (!Objects.equals(otherBonus.effectPredicate, this.effectPredicate)) {
            return false;
        }
        return Objects.equals(otherBonus.eventListener, this.eventListener);
    }

    @Override
    public SkillBonus<EventListenerBonus<RemoveMobEffectBonus>> merge(SkillBonus<?> other) {
        if (!(other instanceof RemoveMobEffectBonus otherBonus)) {
            throw new IllegalArgumentException();
        }
        float chance = this.chance + otherBonus.chance;
        return new RemoveMobEffectBonus(chance, effectPredicate, eventListener);
    }

    @Override
    public MutableComponent getSimpleTooltip() {
        Component effectDescription = effectPredicate.getTooltip("plural");
        Target target = eventListener.getTarget();
        String targetDescription = target.getName();
        String descriptioId = getDescriptionId() + "." + targetDescription;
        if (chance < 1) {
            descriptioId += ".chance";
        }
        MutableComponent tooltip = Component.translatable(descriptioId, effectDescription);
        if (chance < 1) {
            tooltip = TooltipHelper.getSkillBonusTooltip(tooltip, chance, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        }
        tooltip = eventListener.getTooltip(tooltip);
        return tooltip.withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
    }

    @Override
    public void gatherInfo(Consumer<MutableComponent> consumer) {
        TooltipHelper.consumeTranslated(effectPredicate.getDescriptionId() + ".info", consumer);
    }

    @Override
    public boolean isPositive() {
        return chance > 0 ^ eventListener.getTarget() == Target.PLAYER ^ effectPredicate.testsForHarmfulEffects();
    }

    @Override
    public SkillEventListener getEventListener() {
        return eventListener;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<EventListenerBonus<RemoveMobEffectBonus>> consumer) {
        editor.addLabel(0, 0, "Effect", ChatFormatting.GOLD);
        editor.addLabel(150, 0, "Chance", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 145, effectPredicate).setRequiresSearch(false)
                .setResponder(effectPredicate -> selectEffectPredicate(editor, consumer, effectPredicate))
                .setMenuInitFunc(() -> addEffectPredicateWidgets(editor, consumer));
        editor.addNumericTextField(150, 0, 50, 14, chance).setNumericResponder(value -> selectChance(consumer, value));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Event", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, eventListener)
                .setResponder(eventListener -> selectEventListener(editor, consumer, eventListener))
                .setMenuInitFunc(() -> addEventListenerWidgets(editor, consumer));
        editor.increaseHeight(19);
    }

    private void addEventListenerWidgets(SkillTreeEditor editor, Consumer<EventListenerBonus<RemoveMobEffectBonus>> consumer) {
        eventListener.addEditorWidgets(editor, eventListener -> {
            setEventListener(eventListener);
            consumer.accept(this.copy());
        });
    }

    private void selectEventListener(SkillTreeEditor editor, Consumer<EventListenerBonus<RemoveMobEffectBonus>> consumer, SkillEventListener eventListener) {
        setEventListener(eventListener);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void selectChance(Consumer<EventListenerBonus<RemoveMobEffectBonus>> consumer, Double value) {
        setChance(value.floatValue());
        consumer.accept(this.copy());
    }

    private void selectEffectPredicate(SkillTreeEditor editor, Consumer<EventListenerBonus<RemoveMobEffectBonus>> consumer, MobEffectPredicate effectPredicate) {
        setEffectPredicate(effectPredicate);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void addEffectPredicateWidgets(SkillTreeEditor editor, Consumer<EventListenerBonus<RemoveMobEffectBonus>> consumer) {
        effectPredicate.addEditorWidgets(editor, predicate -> {
            setEffectPredicate(predicate);
            consumer.accept(this.copy());
        });
    }

    public void setChance(float chance) {
        this.chance = chance;
    }

    public void setEffectPredicate(MobEffectPredicate effectPredicate) {
        this.effectPredicate = effectPredicate;
    }

    public void setEventListener(SkillEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public static class Serializer implements SkillBonus.Serializer {
        @Override
        public RemoveMobEffectBonus deserialize(JsonObject json) throws JsonParseException {
            float chance = SerializationHelper.getElement(json, "chance").getAsFloat();
            MobEffectPredicate effectPredicate = SerializationHelper.deserializeMobEffectCondition(json, "effect_condition");
            SkillEventListener eventListener = SerializationHelper.deserializeEventListener(json);
            return new RemoveMobEffectBonus(chance, effectPredicate, eventListener);
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof RemoveMobEffectBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            json.addProperty("chance", aBonus.chance);
            SerializationHelper.serializeMobEffectCondition(json, aBonus.effectPredicate, "effect_condition");
            SerializationHelper.serializeEventListener(json, aBonus.eventListener);
        }

        @Override
        public RemoveMobEffectBonus deserialize(CompoundTag tag) {
            float chance = tag.getFloat("chance");
            MobEffectPredicate effectPredicate = SerializationHelper.deserializeMobEffectCondition(tag, "effect_condition");
            SkillEventListener eventListener = SerializationHelper.deserializeEventListener(tag);
            return new RemoveMobEffectBonus(chance, effectPredicate, eventListener);
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof RemoveMobEffectBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            tag.putFloat("chance", aBonus.chance);
            SerializationHelper.serializeMobEffectCondition(tag, aBonus.effectPredicate, "effect_condition");
            SerializationHelper.serializeEventListener(tag, aBonus.eventListener);
            return tag;
        }

        @Override
        public RemoveMobEffectBonus deserialize(FriendlyByteBuf buf) {
            float amount = buf.readFloat();
            MobEffectPredicate effectPredicate = NetworkHelper.readMobEffectCondition(buf);
            SkillEventListener eventListener = NetworkHelper.readEventListener(buf);
            return new RemoveMobEffectBonus(amount, effectPredicate, eventListener);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof RemoveMobEffectBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            buf.writeFloat(aBonus.chance);
            NetworkHelper.writeMobEffectCondition(buf, aBonus.effectPredicate);
            NetworkHelper.writeEventListener(buf, aBonus.eventListener);
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new RemoveMobEffectBonus(0.05f, new MobEffectTypePredicate(MobEffectType.HARMFUL), new OutgoingDamageEventListener());
        }
    }
}
