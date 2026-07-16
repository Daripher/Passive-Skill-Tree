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
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

public final class InflictIgniteBonus implements EventListenerBonus<InflictIgniteBonus> {
    private float chance;
    private int duration;
    private SkillEventListener eventListener;

    public InflictIgniteBonus(float chance, int duration, SkillEventListener eventListener) {
        this.chance = chance;
        this.duration = duration;
        this.eventListener = eventListener;
    }

    public InflictIgniteBonus(float chance, int duration) {
        this(chance, duration, new OutgoingDamageEventListener());
    }

    @Override
    public void applyEffect(LivingEntity target, @Nullable LivingEntity source) {
        if (target.getRandom().nextFloat() < chance) {
            target.igniteForSeconds(duration);
        }
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return PSTSkillBonuses.INFLICT_IGNITE.get();
    }

    @Override
    public InflictIgniteBonus copy() {
        return new InflictIgniteBonus(chance, duration, eventListener);
    }

    @Override
    public InflictIgniteBonus multiply(double multiplier) {
        chance *= (float) multiplier;
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof InflictIgniteBonus otherBonus)) {
            return false;
        }
        if (otherBonus.duration != this.duration) {
            return false;
        }
        return Objects.equals(otherBonus.eventListener, this.eventListener);
    }

    @Override
    public SkillBonus<EventListenerBonus<InflictIgniteBonus>> merge(SkillBonus<?> other) {
        if (!(other instanceof InflictIgniteBonus otherBonus)) {
            throw new IllegalArgumentException();
        }
        return new InflictIgniteBonus(otherBonus.chance + this.chance, duration, eventListener);
    }

    @Override
    public MutableComponent getSimpleTooltip() {
        String durationDescription = StringUtil.formatTickDuration(duration * 20, 20f);
        String targetDescription = eventListener.getTarget().name().toLowerCase(Locale.ROOT);
        String bonusDescription = getDescriptionId() + "." + targetDescription;
        if (chance < 1) {
            bonusDescription += ".chance";
        }
        MutableComponent tooltip = Component.translatable(bonusDescription, durationDescription);
        if (chance < 1) {
            tooltip = TooltipHelper.getSkillBonusTooltip(tooltip, chance, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        }
        tooltip = eventListener.getTooltip(tooltip);
        return tooltip.withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
    }

    @Override
    public boolean isPositive() {
        return chance > 0 ^ eventListener.getTarget() == Target.PLAYER;
    }

    @Override
    public SkillEventListener getEventListener() {
        return eventListener;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<EventListenerBonus<InflictIgniteBonus>> consumer) {
        editor.addLabel(0, 0, "Chance", ChatFormatting.GOLD);
        editor.addLabel(110, 0, "Duration", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 90, 14, chance).setNumericResponder(value -> selectChance(consumer, value));
        editor.addNumericTextField(110, 0, 90, 14, duration).setNumericResponder(value -> selectDuration(consumer, value));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Event", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, eventListener)
                .setResponder(eventListener -> selectEventListener(editor, consumer, eventListener))
                .setMenuInitFunc(() -> addEventListenerWidgets(editor, consumer));
        editor.increaseHeight(19);
    }

    private void addEventListenerWidgets(SkillTreeEditor editor, Consumer<EventListenerBonus<InflictIgniteBonus>> consumer) {
        eventListener.addEditorWidgets(editor, eventListener -> {
            setEventListener(eventListener);
            consumer.accept(this.copy());
        });
    }

    private void selectEventListener(SkillTreeEditor editor, Consumer<EventListenerBonus<InflictIgniteBonus>> consumer, SkillEventListener eventListener) {
        setEventListener(eventListener);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void selectDuration(Consumer<EventListenerBonus<InflictIgniteBonus>> consumer, Double value) {
        setDuration(value.intValue());
        consumer.accept(this.copy());
    }

    private void selectChance(Consumer<EventListenerBonus<InflictIgniteBonus>> consumer, Double value) {
        setChance(value.floatValue());
        consumer.accept(this.copy());
    }

    public void setEventListener(SkillEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void setChance(float chance) {
        this.chance = chance;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public static class Serializer implements SkillBonus.Serializer {
        @Override
        public InflictIgniteBonus deserialize(JsonObject json) throws JsonParseException {
            float chance = SerializationHelper.getElement(json, "chance").getAsFloat();
            int duration = SerializationHelper.getElement(json, "duration").getAsInt();
            InflictIgniteBonus bonus = new InflictIgniteBonus(chance, duration);
            bonus.eventListener = SerializationHelper.deserializeEventListener(json);
            return bonus;
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof InflictIgniteBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            json.addProperty("chance", aBonus.chance);
            json.addProperty("duration", aBonus.duration);
            SerializationHelper.serializeEventListener(json, aBonus.eventListener);
        }

        @Override
        public InflictIgniteBonus deserialize(CompoundTag tag) {
            float chance = tag.getFloat("chance");
            int duration = tag.getInt("duration");
            InflictIgniteBonus bonus = new InflictIgniteBonus(chance, duration);
            bonus.eventListener = SerializationHelper.deserializeEventListener(tag);
            return bonus;
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof InflictIgniteBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            tag.putFloat("chance", aBonus.chance);
            tag.putInt("duration", aBonus.duration);
            SerializationHelper.serializeEventListener(tag, aBonus.eventListener);
            return tag;
        }

        @Override
        public InflictIgniteBonus deserialize(FriendlyByteBuf buf) {
            float amount = buf.readFloat();
            int duration = buf.readInt();
            InflictIgniteBonus bonus = new InflictIgniteBonus(amount, duration);
            bonus.eventListener = NetworkHelper.readEventListener(buf);
            return bonus;
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof InflictIgniteBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            buf.writeFloat(aBonus.chance);
            buf.writeInt(aBonus.duration);
            NetworkHelper.writeEventListener(buf, aBonus.eventListener);
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new InflictIgniteBonus(0.05f, 5);
        }
    }
}
