package daripher.skilltree.skill.bonus.player;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.predicate.PSTDamagePredicates;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.EventListenerBonus;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.event.ShieldBlockEventListener;
import daripher.skilltree.skill.bonus.event.SkillEventListener;
import daripher.skilltree.skill.bonus.predicate.damage.DamageCondition;
import daripher.skilltree.skill.bonus.predicate.damage.MagicDamageCondition;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class InflictDamageBonus implements EventListenerBonus<InflictDamageBonus> {
    private float chance;
    private float damage;
    private SkillEventListener eventListener;
    private DamageCondition damageType;

    public InflictDamageBonus(float chance, float damage, SkillEventListener eventListener, DamageCondition damageType) {
        this.chance = chance;
        this.damage = damage;
        this.eventListener = eventListener;
        this.damageType = damageType;
    }

    public InflictDamageBonus(float chance, float damage) {
        this(chance, damage, new ShieldBlockEventListener(), new MagicDamageCondition());
    }

    @Override
    public void applyEffect(LivingEntity target, @Nullable LivingEntity source) {
        target.hurt(target.level().damageSources().magic(), damage);
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return PSTSkillBonuses.INFLICT_DAMAGE.get();
    }

    @Override
    public InflictDamageBonus copy() {
        return new InflictDamageBonus(chance, damage, eventListener, damageType);
    }

    @Override
    public InflictDamageBonus multiply(double multiplier) {
        if (chance < 1) {
            chance *= (float) multiplier;
        } else {
            damage *= (float) multiplier;
        }
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof InflictDamageBonus otherBonus)) {
            return false;
        }
        if (otherBonus.chance < 1 && this.chance < 1 && otherBonus.damage != this.damage) {
            return false;
        }
        if (!Objects.equals(otherBonus.eventListener, this.eventListener)) {
            return false;
        }
        return Objects.equals(damageType, otherBonus.damageType);
    }

    @Override
    public SkillBonus<EventListenerBonus<InflictDamageBonus>> merge(SkillBonus<?> other) {
        if (!(other instanceof InflictDamageBonus otherBonus)) {
            throw new IllegalArgumentException();
        }
        if (otherBonus.chance < 1 && this.chance < 1) {
            return new InflictDamageBonus(otherBonus.chance + this.chance, damage, eventListener, damageType);
        } else {
            return new InflictDamageBonus(chance, otherBonus.damage + this.damage, eventListener, damageType);
        }
    }

    @Override
    public MutableComponent getSimpleTooltip() {
        String targetDescription = eventListener.getTarget().getName();
        String key = getDescriptionId() + "." + targetDescription;
        String damageDescription = TooltipHelper.formatNumber(damage);
        Component damageTypeDescription = damageType.getTooltip("type");
        if (chance < 1) {
            key += ".chance";
        }
        MutableComponent tooltip = Component.translatable(key, damageDescription, damageTypeDescription);
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
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<EventListenerBonus<InflictDamageBonus>> consumer) {
        editor.addLabel(0, 0, "Chance", ChatFormatting.GOLD);
        editor.addLabel(110, 0, "Damage", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 90, 14, chance).setNumericResponder(value -> selectChance(consumer, value));
        editor.addNumericTextField(110, 0, 90, 14, damage).setNumericResponder(value -> selectDamage(consumer, value));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Damage Type", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        List<DamageCondition> damageTypes = PSTDamagePredicates.conditionsList().stream().filter(DamageCondition::canCreateDamageSource)
                .toList();
        editor.addSelectionMenu(0, 0, 200, damageTypes).setValue(damageType)
                .setElementNameGetter(c -> Component.translatable(PSTDamagePredicates.getName(c)))
                .setResponder(damageType -> selectDamageType(editor, consumer, damageType));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Event", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, eventListener)
                .setResponder(eventListener -> selectEventListener(editor, consumer, eventListener))
                .setMenuInitFunc(() -> addEventListenerWidgets(editor, consumer));
        editor.increaseHeight(19);
    }

    private void addEventListenerWidgets(SkillTreeEditor editor, Consumer<EventListenerBonus<InflictDamageBonus>> consumer) {
        eventListener.addEditorWidgets(editor, eventListener -> {
            setEventListener(eventListener);
            consumer.accept(this.copy());
        });
    }

    private void selectEventListener(SkillTreeEditor editor, Consumer<EventListenerBonus<InflictDamageBonus>> consumer, SkillEventListener eventListener) {
        setEventListener(eventListener);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void selectDamageType(SkillTreeEditor editor, Consumer<EventListenerBonus<InflictDamageBonus>> consumer, DamageCondition damageType) {
        setDamageType(damageType);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void selectDamage(Consumer<EventListenerBonus<InflictDamageBonus>> consumer, Double value) {
        setDamage(value.intValue());
        consumer.accept(this.copy());
    }

    private void selectChance(Consumer<EventListenerBonus<InflictDamageBonus>> consumer, Double value) {
        setChance(value.floatValue());
        consumer.accept(this.copy());
    }

    public void setEventListener(SkillEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void setDamageType(DamageCondition damageType) {
        this.damageType = damageType;
    }

    public void setChance(float chance) {
        this.chance = chance;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public static class Serializer implements SkillBonus.Serializer {
        @Override
        public InflictDamageBonus deserialize(JsonObject json) throws JsonParseException {
            float chance = json.get("chance").getAsFloat();
            float damage = json.get("damage").getAsInt();
            InflictDamageBonus bonus = new InflictDamageBonus(chance, damage);
            bonus.eventListener = SerializationHelper.deserializeEventListener(json);
            if (json.has("damage_type")) {
                bonus.damageType = SerializationHelper.deserializeDamageCondition(json, "damage_type");
            }
            return bonus;
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof InflictDamageBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            json.addProperty("chance", aBonus.chance);
            json.addProperty("damage", aBonus.damage);
            SerializationHelper.serializeEventListener(json, aBonus.eventListener);
            SerializationHelper.serializeDamageCondition(json, aBonus.damageType, "damage_type");
        }

        @Override
        public InflictDamageBonus deserialize(CompoundTag tag) {
            float chance = tag.getFloat("chance");
            float damage = tag.getFloat("damage");
            InflictDamageBonus bonus = new InflictDamageBonus(chance, damage);
            bonus.eventListener = SerializationHelper.deserializeEventListener(tag);
            if (tag.contains("damage_type")) {
                bonus.damageType = SerializationHelper.deserializeDamageCondition(tag, "damage_type");
            }
            return bonus;
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof InflictDamageBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            tag.putFloat("chance", aBonus.chance);
            tag.putFloat("damage", aBonus.damage);
            SerializationHelper.serializeEventListener(tag, aBonus.eventListener);
            SerializationHelper.serializeDamageCondition(tag, aBonus.damageType, "damage_type");
            return tag;
        }

        @Override
        public InflictDamageBonus deserialize(FriendlyByteBuf buf) {
            float amount = buf.readFloat();
            float damage = buf.readFloat();
            InflictDamageBonus bonus = new InflictDamageBonus(amount, damage);
            bonus.eventListener = NetworkHelper.readEventListener(buf);
            bonus.damageType = NetworkHelper.readDamageCondition(buf);
            return bonus;
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof InflictDamageBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            buf.writeFloat(aBonus.chance);
            buf.writeFloat(aBonus.damage);
            NetworkHelper.writeEventListener(buf, aBonus.eventListener);
            NetworkHelper.writeDamageCondition(buf, aBonus.damageType);
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new InflictDamageBonus(0.05f, 5f);
        }
    }
}
