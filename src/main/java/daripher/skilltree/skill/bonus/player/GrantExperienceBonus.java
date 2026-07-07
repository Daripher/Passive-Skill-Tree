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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;

public final class GrantExperienceBonus implements EventListenerBonus<GrantExperienceBonus> {
    private float chance;
    private int amount;
    private SkillEventListener eventListener;

    public GrantExperienceBonus(float chance, int amount, SkillEventListener eventListener) {
        this.chance = chance;
        this.amount = amount;
        this.eventListener = eventListener;
    }

    public GrantExperienceBonus(float chance, int amount) {
        this(chance, amount, new OutgoingDamageEventListener().setTarget(Target.PLAYER));
    }

    @Override
    public void applyEffect(LivingEntity target, @Nullable LivingEntity source) {
        if (!(target instanceof Player player)) {
            return;
        }
        if (target.getRandom().nextFloat() < chance) {
            player.giveExperiencePoints(amount);
        }
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return PSTSkillBonuses.GAIN_EXPERIENCE.get();
    }

    @Override
    public GrantExperienceBonus copy() {
        return new GrantExperienceBonus(chance, amount, eventListener);
    }

    @Override
    public GrantExperienceBonus multiply(double multiplier) {
        if (chance == 1) {
            amount = (int) (amount * multiplier);
        } else {
            chance *= (float) multiplier;
        }
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof GrantExperienceBonus otherBonus)) {
            return false;
        }
        if (otherBonus.amount != this.amount) {
            return false;
        }
        return Objects.equals(otherBonus.eventListener, this.eventListener);
    }

    @Override
    public GrantExperienceBonus merge(SkillBonus<?> other) {
        if (!(other instanceof GrantExperienceBonus otherBonus)) {
            throw new IllegalArgumentException();
        }
        if (otherBonus.chance == 1 && this.chance == 1) {
            return new GrantExperienceBonus(chance, otherBonus.amount + this.amount, eventListener);
        }
        return new GrantExperienceBonus(otherBonus.chance + this.chance, amount, eventListener);
    }

    @Override
    public MutableComponent getSimpleTooltip() {
        String bonusDescription = getDescriptionId();
        if (chance < 1) {
            bonusDescription += ".chance";
        }
        MutableComponent tooltip = Component.translatable(bonusDescription, amount);
        if (chance < 1) {
            tooltip = TooltipHelper.getSkillBonusTooltip(tooltip, chance, AttributeModifier.Operation.MULTIPLY_BASE);
        }
        tooltip = eventListener.getTooltip(tooltip);
        return tooltip.withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
    }

    @Override
    public boolean isPositive() {
        return chance > 0;
    }

    @Override
    public SkillEventListener getEventListener() {
        return eventListener;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<EventListenerBonus<GrantExperienceBonus>> consumer) {
        editor.addLabel(0, 0, "Chance", ChatFormatting.GOLD);
        editor.addLabel(110, 0, "Amount", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 90, 14, chance).setNumericResponder(value -> selectChance(consumer, value));
        editor.addNumericTextField(110, 0, 90, 14, amount).setNumericFilter(value -> value.intValue() == value)
                .setNumericResponder(value -> selectAmount(consumer, value));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Event", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, eventListener)
                .setResponder(eventListener -> selectEventListener(editor, consumer, eventListener))
                .setMenuInitFunc(() -> addEventListenerWidgets(editor, consumer));
        editor.increaseHeight(19);
    }

    private void addEventListenerWidgets(SkillTreeEditor editor, Consumer<EventListenerBonus<GrantExperienceBonus>> consumer) {
        eventListener.addEditorWidgets(editor, eventListener -> {
            setEventListener(eventListener);
            consumer.accept(this.copy());
        });
    }

    private void selectEventListener(SkillTreeEditor editor, Consumer<EventListenerBonus<GrantExperienceBonus>> consumer, SkillEventListener eventListener) {
        setEventListener(eventListener);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void selectAmount(Consumer<EventListenerBonus<GrantExperienceBonus>> consumer, Double value) {
        setAmount(value.intValue());
        consumer.accept(this.copy());
    }

    private void selectChance(Consumer<EventListenerBonus<GrantExperienceBonus>> consumer, Double value) {
        setChance(value.floatValue());
        consumer.accept(this.copy());
    }

    public void setEventListener(SkillEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void setChance(float chance) {
        this.chance = chance;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public static class Serializer implements SkillBonus.Serializer {
        @Override
        public GrantExperienceBonus deserialize(JsonObject json) throws JsonParseException {
            float chance = SerializationHelper.getElement(json, "chance").getAsFloat();
            int amount = SerializationHelper.getElement(json, "amount").getAsInt();
            GrantExperienceBonus bonus = new GrantExperienceBonus(chance, amount);
            bonus.eventListener = SerializationHelper.deserializeEventListener(json);
            return bonus;
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof GrantExperienceBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            json.addProperty("chance", aBonus.chance);
            json.addProperty("amount", aBonus.amount);
            SerializationHelper.serializeEventListener(json, aBonus.eventListener);
        }

        @Override
        public GrantExperienceBonus deserialize(CompoundTag tag) {
            float chance = tag.getFloat("chance");
            int amount = tag.getInt("amount");
            GrantExperienceBonus bonus = new GrantExperienceBonus(chance, amount);
            bonus.eventListener = SerializationHelper.deserializeEventListener(tag);
            return bonus;
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof GrantExperienceBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            tag.putFloat("chance", aBonus.chance);
            tag.putInt("amount", aBonus.amount);
            SerializationHelper.serializeEventListener(tag, aBonus.eventListener);
            return tag;
        }

        @Override
        public GrantExperienceBonus deserialize(FriendlyByteBuf buf) {
            float chance = buf.readFloat();
            int amount = buf.readInt();
            GrantExperienceBonus bonus = new GrantExperienceBonus(chance, amount);
            bonus.eventListener = NetworkHelper.readEventListener(buf);
            return bonus;
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof GrantExperienceBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            buf.writeFloat(aBonus.chance);
            buf.writeInt(aBonus.amount);
            NetworkHelper.writeEventListener(buf, aBonus.eventListener);
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new GrantExperienceBonus(0.05f, 5);
        }
    }
}
