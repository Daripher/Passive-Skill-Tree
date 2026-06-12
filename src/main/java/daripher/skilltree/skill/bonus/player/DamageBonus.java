package daripher.skilltree.skill.bonus.player;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.multiplier.LivingMultiplier;
import daripher.skilltree.skill.bonus.multiplier.NoneLivingMultiplier;
import daripher.skilltree.skill.bonus.predicate.damage.DamageCondition;
import daripher.skilltree.skill.bonus.predicate.damage.MeleeDamageCondition;
import daripher.skilltree.skill.bonus.predicate.damage.NoneDamageCondition;
import daripher.skilltree.skill.bonus.predicate.living.LivingEntityPredicate;
import daripher.skilltree.skill.bonus.predicate.living.NoneLivingEntityPredicate;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Consumer;

public final class DamageBonus implements SkillBonus<DamageBonus> {
    private float amount;
    private AttributeModifier.Operation operation;
    private @Nonnull LivingMultiplier playerMultiplier = NoneLivingMultiplier.INSTANCE;
    private @Nonnull LivingMultiplier targetMultiplier = NoneLivingMultiplier.INSTANCE;
    private @Nonnull LivingEntityPredicate playerCondition = NoneLivingEntityPredicate.INSTANCE;
    private @Nonnull LivingEntityPredicate targetCondition = NoneLivingEntityPredicate.INSTANCE;
    private @Nonnull DamageCondition damageCondition = NoneDamageCondition.INSTANCE;

    public DamageBonus(float amount, AttributeModifier.Operation operation) {
        this.amount = amount;
        this.operation = operation;
    }

    public float getDamageBonus(AttributeModifier.Operation operation, DamageSource source, Player player, LivingEntity target) {
        if (this.operation != operation) {
            return 0f;
        }
        if (!damageCondition.met(source)) {
            return 0f;
        }
        if (!playerCondition.test(player)) {
            return 0f;
        }
        if (!targetCondition.test(target)) {
            return 0f;
        }
        return amount * playerMultiplier.getValue(player) * targetMultiplier.getValue(target);
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return PSTSkillBonuses.DAMAGE.get();
    }

    @Override
    public DamageBonus copy() {
        DamageBonus bonus = new DamageBonus(amount, operation);
        bonus.playerMultiplier = this.playerMultiplier;
        bonus.targetMultiplier = this.targetMultiplier;
        bonus.playerCondition = this.playerCondition;
        bonus.damageCondition = this.damageCondition;
        bonus.targetCondition = this.targetCondition;
        return bonus;
    }

    @Override
    public DamageBonus multiply(double multiplier) {
        amount *= (float) multiplier;
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof DamageBonus otherBonus)) {
            return false;
        }
        if (!Objects.equals(otherBonus.playerMultiplier, this.playerMultiplier)) {
            return false;
        }
        if (!Objects.equals(otherBonus.targetMultiplier, this.targetMultiplier)) {
            return false;
        }
        if (!Objects.equals(otherBonus.playerCondition, this.playerCondition)) {
            return false;
        }
        if (!Objects.equals(otherBonus.damageCondition, this.damageCondition)) {
            return false;
        }
        if (!Objects.equals(otherBonus.targetCondition, this.targetCondition)) {
            return false;
        }
        return otherBonus.operation == this.operation;
    }

    @Override
    public SkillBonus<DamageBonus> merge(SkillBonus<?> other) {
        if (!(other instanceof DamageBonus otherBonus)) {
            throw new IllegalArgumentException();
        }
        float mergedAmount = otherBonus.amount + this.amount;
        DamageBonus mergedBonus = new DamageBonus(mergedAmount, this.operation);
        mergedBonus.playerMultiplier = this.playerMultiplier;
        mergedBonus.targetMultiplier = this.targetMultiplier;
        mergedBonus.playerCondition = this.playerCondition;
        mergedBonus.damageCondition = this.damageCondition;
        mergedBonus.targetCondition = this.targetCondition;
        return mergedBonus;
    }

    @Override
    public MutableComponent getSimpleTooltip() {
        MutableComponent tooltip = TooltipHelper.getSkillBonusTooltip(damageCondition.getTooltip(), amount, operation);
        tooltip = playerMultiplier.getTooltip(tooltip, Target.PLAYER);
        tooltip = targetMultiplier.getTooltip(tooltip, Target.ENEMY);
        tooltip = playerCondition.getTooltip(tooltip, Target.PLAYER);
        tooltip = targetCondition.getTooltip(tooltip, Target.ENEMY);
        return tooltip.withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
    }

    @Override
    public boolean isPositive() {
        return amount > 0;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<DamageBonus> consumer) {
        editor.addLabel(110, 0, "Amount", ChatFormatting.GOLD);
        editor.addLabel(0, 0, "Operation", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(110, 0, 50, 14, amount).setNumericResponder(value -> selectAmount(consumer, value));
        editor.addOperationSelection(0, 0, 80, operation).setResponder(operation -> selectOperation(consumer, operation));
        editor.increaseHeight(29);
        editor.addLabel(0, 0, "Damage Condition", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, damageCondition).setResponder(condition -> selectDamageCondition(consumer, condition));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Player Condition", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, playerCondition).setResponder(condition -> selectPlayerCondition(editor, consumer, condition))
                .setMenuInitFunc(() -> addPlayerConditionWidgets(editor, consumer));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Target Condition", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, targetCondition).setResponder(condition -> selectTargetCondition(editor, consumer, condition))
                .setMenuInitFunc(() -> addTargetConditionWidgets(editor, consumer));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Player Multiplier", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, playerMultiplier)
                .setResponder(multiplier -> selectPlayerMultiplier(editor, consumer, multiplier))
                .setMenuInitFunc(() -> addPlayerMultiplierWidgets(editor, consumer));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Target Multiplier", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, targetMultiplier)
                .setResponder(multiplier -> selectTargetMultiplier(editor, consumer, multiplier))
                .setMenuInitFunc(() -> addTargetMultiplierWidgets(editor, consumer));
        editor.increaseHeight(19);
    }

    private void selectOperation(Consumer<DamageBonus> consumer, AttributeModifier.Operation operation) {
        setOperation(operation);
        consumer.accept(this.copy());
    }

    private void selectAmount(Consumer<DamageBonus> consumer, Double value) {
        setAmount(value.floatValue());
        consumer.accept(this.copy());
    }

    private void addTargetMultiplierWidgets(SkillTreeEditor editor, Consumer<DamageBonus> consumer) {
        targetMultiplier.addEditorWidgets(editor, multiplier -> {
            setEnemyMultiplier(multiplier);
            consumer.accept(this.copy());
        });
    }

    private void selectTargetMultiplier(SkillTreeEditor editor, Consumer<DamageBonus> consumer, LivingMultiplier multiplier) {
        setEnemyMultiplier(multiplier);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void addPlayerMultiplierWidgets(SkillTreeEditor editor, Consumer<DamageBonus> consumer) {
        playerMultiplier.addEditorWidgets(editor, multiplier -> {
            setPlayerMultiplier(multiplier);
            consumer.accept(this.copy());
        });
    }

    private void selectPlayerMultiplier(SkillTreeEditor editor, Consumer<DamageBonus> consumer, LivingMultiplier multiplier) {
        setPlayerMultiplier(multiplier);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void addTargetConditionWidgets(SkillTreeEditor editor, Consumer<DamageBonus> consumer) {
        targetCondition.addEditorWidgets(editor, c -> {
            setTargetCondition(c);
            consumer.accept(this.copy());
        });
    }

    private void selectTargetCondition(SkillTreeEditor editor, Consumer<DamageBonus> consumer, LivingEntityPredicate condition) {
        setTargetCondition(condition);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void addPlayerConditionWidgets(SkillTreeEditor editor, Consumer<DamageBonus> consumer) {
        playerCondition.addEditorWidgets(editor, c -> {
            setPlayerCondition(c);
            consumer.accept(this.copy());
        });
    }

    private void selectPlayerCondition(SkillTreeEditor editor, Consumer<DamageBonus> consumer, LivingEntityPredicate condition) {
        setPlayerCondition(condition);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void selectDamageCondition(Consumer<DamageBonus> consumer, DamageCondition condition) {
        setDamageCondition(condition);
        consumer.accept(this.copy());
    }

    public DamageBonus setPlayerCondition(LivingEntityPredicate condition) {
        this.playerCondition = condition;
        return this;
    }

    public DamageBonus setDamageCondition(DamageCondition condition) {
        this.damageCondition = condition;
        return this;
    }

    public DamageBonus setTargetCondition(LivingEntityPredicate condition) {
        this.targetCondition = condition;
        return this;
    }

    public DamageBonus setPlayerMultiplier(LivingMultiplier multiplier) {
        this.playerMultiplier = multiplier;
        return this;
    }

    public DamageBonus setEnemyMultiplier(LivingMultiplier multiplier) {
        this.targetMultiplier = multiplier;
        return this;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public void setOperation(AttributeModifier.Operation operation) {
        this.operation = operation;
    }

    public static class Serializer implements SkillBonus.Serializer {
        @Override
        public DamageBonus deserialize(JsonObject json) throws JsonParseException {
            float amount = SerializationHelper.getElement(json, "amount").getAsFloat();
            AttributeModifier.Operation operation = SerializationHelper.deserializeOperation(json);
            DamageBonus bonus = new DamageBonus(amount, operation);
            bonus.playerMultiplier = SerializationHelper.deserializeLivingMultiplier(json, "player_multiplier");
            bonus.targetMultiplier = SerializationHelper.deserializeLivingMultiplier(json, "enemy_multiplier");
            bonus.playerCondition = SerializationHelper.deserializeLivingCondition(json, "player_condition");
            bonus.damageCondition = SerializationHelper.deserializeDamageCondition(json);
            bonus.targetCondition = SerializationHelper.deserializeLivingCondition(json, "target_condition");
            return bonus;
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof DamageBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            json.addProperty("amount", aBonus.amount);
            SerializationHelper.serializeOperation(json, aBonus.operation);
            SerializationHelper.serializeLivingMultiplier(json, aBonus.playerMultiplier, "player_multiplier");
            SerializationHelper.serializeLivingMultiplier(json, aBonus.targetMultiplier, "enemy_multiplier");
            SerializationHelper.serializeLivingCondition(json, aBonus.playerCondition, "player_condition");
            SerializationHelper.serializeDamageCondition(json, aBonus.damageCondition);
            SerializationHelper.serializeLivingCondition(json, aBonus.targetCondition, "target_condition");
        }

        @Override
        public DamageBonus deserialize(CompoundTag tag) {
            float amount = tag.getFloat("amount");
            AttributeModifier.Operation operation = SerializationHelper.deserializeOperation(tag);
            DamageBonus bonus = new DamageBonus(amount, operation);
            bonus.playerMultiplier = SerializationHelper.deserializeLivingMultiplier(tag, "player_multiplier");
            bonus.targetMultiplier = SerializationHelper.deserializeLivingMultiplier(tag, "enemy_multiplier");
            bonus.playerCondition = SerializationHelper.deserializeLivingCondition(tag, "player_condition");
            bonus.damageCondition = SerializationHelper.deserializeDamageCondition(tag);
            bonus.targetCondition = SerializationHelper.deserializeLivingCondition(tag, "target_condition");
            return bonus;
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof DamageBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            tag.putFloat("amount", aBonus.amount);
            SerializationHelper.serializeOperation(tag, aBonus.operation);
            SerializationHelper.serializeLivingMultiplier(tag, aBonus.playerMultiplier, "player_multiplier");
            SerializationHelper.serializeLivingMultiplier(tag, aBonus.targetMultiplier, "enemy_multiplier");
            SerializationHelper.serializeLivingCondition(tag, aBonus.playerCondition, "player_condition");
            SerializationHelper.serializeDamageCondition(tag, aBonus.damageCondition);
            SerializationHelper.serializeLivingCondition(tag, aBonus.targetCondition, "target_condition");
            return tag;
        }

        @Override
        public DamageBonus deserialize(FriendlyByteBuf buf) {
            float amount = buf.readFloat();
            AttributeModifier.Operation operation = AttributeModifier.Operation.fromValue(buf.readInt());
            DamageBonus bonus = new DamageBonus(amount, operation);
            bonus.playerMultiplier = NetworkHelper.readLivingMultiplier(buf);
            bonus.targetMultiplier = NetworkHelper.readLivingMultiplier(buf);
            bonus.playerCondition = NetworkHelper.readLivingCondition(buf);
            bonus.damageCondition = NetworkHelper.readDamageCondition(buf);
            bonus.targetCondition = NetworkHelper.readLivingCondition(buf);
            return bonus;
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof DamageBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            buf.writeFloat(aBonus.amount);
            buf.writeInt(aBonus.operation.toValue());
            NetworkHelper.writeLivingMultiplier(buf, aBonus.playerMultiplier);
            NetworkHelper.writeLivingMultiplier(buf, aBonus.targetMultiplier);
            NetworkHelper.writeLivingCondition(buf, aBonus.playerCondition);
            NetworkHelper.writeDamageCondition(buf, aBonus.damageCondition);
            NetworkHelper.writeLivingCondition(buf, aBonus.targetCondition);
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new DamageBonus(0.1f, AttributeModifier.Operation.MULTIPLY_BASE).setDamageCondition(new MeleeDamageCondition());
        }
    }
}
