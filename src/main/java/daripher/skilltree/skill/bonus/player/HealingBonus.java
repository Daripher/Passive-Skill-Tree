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
import daripher.skilltree.skill.bonus.event.AttackEventListener;
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
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

public final class HealingBonus implements EventListenerBonus<HealingBonus> {
    private float chance;
    private float amount;
    private SkillEventListener eventListener;
    private boolean isPercentageHealing;

    public HealingBonus(float chance, float amount, SkillEventListener eventListener, boolean isPercentageHealing) {
        this.chance = chance;
        this.amount = amount;
        this.eventListener = eventListener;
        this.isPercentageHealing = isPercentageHealing;
    }

    public HealingBonus(float chance, float amount) {
        this(chance, amount, new AttackEventListener().setTarget(Target.PLAYER), false);
    }

    @Override
    public void applyEffect(LivingEntity target, @Nullable LivingEntity source) {
        if (target.getRandom().nextFloat() < chance) {
            float healAmount = amount;
            if (isPercentageHealing) {
                healAmount = amount * target.getMaxHealth();
            }
            if (target instanceof Player player && target.getHealth() < target.getMaxHealth()) {
                player.getFoodData().addExhaustion(healAmount / 2);
            }
            target.heal(healAmount);
        }
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return PSTSkillBonuses.HEALING.get();
    }

    @Override
    public HealingBonus copy() {
        return new HealingBonus(chance, amount, eventListener, isPercentageHealing);
    }

    @Override
    public HealingBonus multiply(double multiplier) {
        if (chance == 1) {
            amount *= (float) multiplier;
        } else {
            chance *= (float) multiplier;
        }
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof HealingBonus otherBonus)) {
            return false;
        }
        if (otherBonus.amount != this.amount) {
            return false;
        }
        if (otherBonus.isPercentageHealing != isPercentageHealing) {
            return false;
        }
        return Objects.equals(otherBonus.eventListener, this.eventListener);
    }

    @Override
    public HealingBonus merge(SkillBonus<?> other) {
        if (!(other instanceof HealingBonus otherBonus)) {
            throw new IllegalArgumentException();
        }
        if (otherBonus.chance == 1 && this.chance == 1) {
            return new HealingBonus(chance, otherBonus.amount + this.amount, eventListener, isPercentageHealing);
        }
        return new HealingBonus(otherBonus.chance + this.chance, amount, eventListener, isPercentageHealing);
    }

    @Override
    public MutableComponent getSimpleTooltip() {
        String targetDescription = eventListener.getTarget().name().toLowerCase(Locale.ROOT);
        String bonusDescription = getDescriptionId() + "." + targetDescription;
        if (chance < 1) {
            bonusDescription += ".chance";
        }
        String amountDescription;
        if (isPercentageHealing) {
            amountDescription = TooltipHelper.formatNumber(amount * 100) + "%";
        } else {
            amountDescription = TooltipHelper.formatNumber(amount);
        }
        MutableComponent tooltip = Component.translatable(bonusDescription, amountDescription);
        if (chance < 1) {
            tooltip = TooltipHelper.getSkillBonusTooltip(tooltip, chance, AttributeModifier.Operation.MULTIPLY_BASE);
        }
        tooltip = eventListener.getTooltip(tooltip);
        return tooltip.withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
    }

    @Override
    public boolean isPositive() {
        return chance > 0 ^ eventListener.getTarget() == Target.ENEMY;
    }

    @Override
    public SkillEventListener getEventListener() {
        return eventListener;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<EventListenerBonus<HealingBonus>> consumer) {
        editor.addLabel(0, 0, "Chance", ChatFormatting.GOLD);
        editor.addLabel(110, 0, "Amount", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 90, 14, chance).setNumericResponder(value -> selectChance(consumer, value));
        editor.addNumericTextField(110, 0, 90, 14, amount).setNumericResponder(value -> selectAmount(consumer, value));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Percentage Healing", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addCheckBox(0, 0, isPercentageHealing).setResponder(value -> selectPercentageHealing(editor, consumer, value));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Event", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, eventListener)
                .setResponder(eventListener -> selectEventListener(editor, consumer, eventListener))
                .setMenuInitFunc(() -> addEventListenerWidgets(editor, consumer));
        editor.increaseHeight(19);
    }

    private void addEventListenerWidgets(SkillTreeEditor editor, Consumer<EventListenerBonus<HealingBonus>> consumer) {
        eventListener.addEditorWidgets(editor, eventListener -> {
            setEventListener(eventListener);
            consumer.accept(this.copy());
        });
    }

    private void selectEventListener(SkillTreeEditor editor, Consumer<EventListenerBonus<HealingBonus>> consumer, SkillEventListener eventListener) {
        setEventListener(eventListener);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void selectPercentageHealing(SkillTreeEditor editor, Consumer<EventListenerBonus<HealingBonus>> consumer, boolean isPercentageHealing) {
        setPercentageHealing(isPercentageHealing);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void selectAmount(Consumer<EventListenerBonus<HealingBonus>> consumer, Double value) {
        setAmount(value.floatValue());
        consumer.accept(this.copy());
    }

    private void selectChance(Consumer<EventListenerBonus<HealingBonus>> consumer, Double value) {
        setChance(value.floatValue());
        consumer.accept(this.copy());
    }

    public void setPercentageHealing(boolean percentageHealing) {
        isPercentageHealing = percentageHealing;
    }

    public void setEventListener(SkillEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void setChance(float chance) {
        this.chance = chance;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public static class Serializer implements SkillBonus.Serializer {
        @Override
        public HealingBonus deserialize(JsonObject json) throws JsonParseException {
            float chance = SerializationHelper.getElement(json, "chance").getAsFloat();
            float amount = SerializationHelper.getElement(json, "amount").getAsFloat();
            HealingBonus bonus = new HealingBonus(chance, amount);
            bonus.eventListener = SerializationHelper.deserializeEventListener(json);
            if (json.has("percentage_healing")) {
                bonus.setPercentageHealing(json.get("percentage_healing").getAsBoolean());
            }
            return bonus;
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof HealingBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            json.addProperty("chance", aBonus.chance);
            json.addProperty("amount", aBonus.amount);
            SerializationHelper.serializeEventListener(json, aBonus.eventListener);
            json.addProperty("percentage_healing", aBonus.isPercentageHealing);
        }

        @Override
        public HealingBonus deserialize(CompoundTag tag) {
            float chance = tag.getFloat("chance");
            float amount = tag.getFloat("amount");
            HealingBonus bonus = new HealingBonus(chance, amount);
            bonus.eventListener = SerializationHelper.deserializeEventListener(tag);
            if (tag.contains("percentage_healing")) {
                bonus.setPercentageHealing(tag.getBoolean("percentage_healing"));
            }
            return bonus;
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof HealingBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            tag.putFloat("chance", aBonus.chance);
            tag.putFloat("amount", aBonus.amount);
            SerializationHelper.serializeEventListener(tag, aBonus.eventListener);
            tag.putBoolean("percentage_healing", aBonus.isPercentageHealing);
            return tag;
        }

        @Override
        public HealingBonus deserialize(FriendlyByteBuf buf) {
            float chance = buf.readFloat();
            float amount = buf.readFloat();
            HealingBonus bonus = new HealingBonus(chance, amount);
            bonus.eventListener = NetworkHelper.readEventListener(buf);
            bonus.isPercentageHealing = buf.readBoolean();
            return bonus;
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof HealingBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            buf.writeFloat(aBonus.chance);
            buf.writeFloat(aBonus.amount);
            NetworkHelper.writeEventListener(buf, aBonus.eventListener);
            buf.writeBoolean(aBonus.isPercentageHealing);
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new HealingBonus(0.05f, 5);
        }
    }
}
