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
import daripher.skilltree.skill.bonus.predicate.living.LivingEntityPredicate;
import daripher.skilltree.skill.bonus.predicate.living.NoneLivingEntityPredicate;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Consumer;

public final class StealthBonus implements SkillBonus<StealthBonus> {
    private float amount;
    private @Nonnull LivingMultiplier playerMultiplier = NoneLivingMultiplier.INSTANCE;
    private @Nonnull LivingMultiplier enemyMultiplier = NoneLivingMultiplier.INSTANCE;
    private @Nonnull LivingEntityPredicate playerCondition = NoneLivingEntityPredicate.INSTANCE;
    private @Nonnull LivingEntityPredicate enemyCondition = NoneLivingEntityPredicate.INSTANCE;

    public StealthBonus(float amount) {
        this.amount = amount;
    }

    public float getStealthMultiplier(Player player, LivingEntity lookingEntity) {
        if (!playerCondition.test(player)) {
            return 0f;
        }
        if (!enemyCondition.test(lookingEntity)) {
            return 0f;
        }
        return amount * playerMultiplier.getValue(player) * enemyMultiplier.getValue(lookingEntity);
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return PSTSkillBonuses.STEALTH.get();
    }

    @Override
    public StealthBonus copy() {
        StealthBonus bonus = new StealthBonus(amount);
        bonus.playerMultiplier = this.playerMultiplier;
        bonus.enemyMultiplier = this.enemyMultiplier;
        bonus.playerCondition = this.playerCondition;
        bonus.enemyCondition = this.enemyCondition;
        return bonus;
    }

    @Override
    public StealthBonus multiply(double multiplier) {
        amount *= (float) multiplier;
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof StealthBonus otherBonus)) {
            return false;
        }
        if (!Objects.equals(otherBonus.playerMultiplier, this.playerMultiplier)) {
            return false;
        }
        if (!Objects.equals(otherBonus.enemyMultiplier, this.enemyMultiplier)) {
            return false;
        }
        if (!Objects.equals(otherBonus.playerCondition, this.playerCondition)) {
            return false;
        }
        return Objects.equals(otherBonus.enemyCondition, this.enemyCondition);
    }

    @Override
    public SkillBonus<StealthBonus> merge(SkillBonus<?> other) {
        if (!(other instanceof StealthBonus otherBonus)) {
            throw new IllegalArgumentException();
        }
        float mergedAmount = otherBonus.amount + this.amount;
        StealthBonus mergedBonus = new StealthBonus(mergedAmount);
        mergedBonus.playerMultiplier = this.playerMultiplier;
        mergedBonus.enemyMultiplier = this.enemyMultiplier;
        mergedBonus.playerCondition = this.playerCondition;
        mergedBonus.enemyCondition = this.enemyCondition;
        return mergedBonus;
    }

    @Override
    public MutableComponent getSimpleTooltip() {
        String descriptionId = getDescriptionId() + (!isPositive() ? ".negative" : "");
        MutableComponent tooltip = Component.translatable(descriptionId, TooltipHelper.formatNumber(Mth.abs(amount * 100)));
        tooltip = playerMultiplier.getTooltip(tooltip, Target.PLAYER);
        tooltip = enemyMultiplier.getTooltip(tooltip, Target.ENEMY);
        tooltip = playerCondition.getTooltip(tooltip, Target.PLAYER);
        tooltip = enemyCondition.getTooltip(tooltip, Target.ENEMY);
        return tooltip.withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
    }

    @Override
    public boolean isPositive() {
        return amount > 0;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<StealthBonus> consumer) {
        editor.addLabel(0, 0, "Amount", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 50, 14, amount).setNumericResponder(value -> selectAmount(consumer, value));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Player Condition", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, playerCondition).setResponder(condition -> selectPlayerCondition(editor, consumer, condition))
                .setMenuInitFunc(() -> addPlayerConditionWidgets(editor, consumer));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Target Condition", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, enemyCondition).setResponder(condition -> selectTargetCondition(editor, consumer, condition))
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
        editor.addSelectionMenu(0, 0, 200, enemyMultiplier).setResponder(multiplier -> selectTargetMultiplier(editor, consumer, multiplier))
                .setMenuInitFunc(() -> addTargetMultiplierWidgets(editor, consumer));
        editor.increaseHeight(19);
    }

    private void selectAmount(Consumer<StealthBonus> consumer, Double value) {
        setAmount(value.floatValue());
        consumer.accept(this.copy());
    }

    private void addTargetMultiplierWidgets(SkillTreeEditor editor, Consumer<StealthBonus> consumer) {
        enemyMultiplier.addEditorWidgets(editor, multiplier -> {
            setEnemyMultiplier(multiplier);
            consumer.accept(this.copy());
        });
    }

    private void selectTargetMultiplier(SkillTreeEditor editor, Consumer<StealthBonus> consumer, LivingMultiplier multiplier) {
        setEnemyMultiplier(multiplier);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void addPlayerMultiplierWidgets(SkillTreeEditor editor, Consumer<StealthBonus> consumer) {
        playerMultiplier.addEditorWidgets(editor, multiplier -> {
            setPlayerMultiplier(multiplier);
            consumer.accept(this.copy());
        });
    }

    private void selectPlayerMultiplier(SkillTreeEditor editor, Consumer<StealthBonus> consumer, LivingMultiplier multiplier) {
        setPlayerMultiplier(multiplier);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void addTargetConditionWidgets(SkillTreeEditor editor, Consumer<StealthBonus> consumer) {
        enemyCondition.addEditorWidgets(editor, c -> {
            setEnemyCondition(c);
            consumer.accept(this.copy());
        });
    }

    private void selectTargetCondition(SkillTreeEditor editor, Consumer<StealthBonus> consumer, LivingEntityPredicate condition) {
        setEnemyCondition(condition);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void addPlayerConditionWidgets(SkillTreeEditor editor, Consumer<StealthBonus> consumer) {
        playerCondition.addEditorWidgets(editor, c -> {
            setPlayerCondition(c);
            consumer.accept(this.copy());
        });
    }

    private void selectPlayerCondition(SkillTreeEditor editor, Consumer<StealthBonus> consumer, LivingEntityPredicate condition) {
        setPlayerCondition(condition);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    public StealthBonus setPlayerCondition(LivingEntityPredicate condition) {
        this.playerCondition = condition;
        return this;
    }

    public StealthBonus setEnemyCondition(LivingEntityPredicate condition) {
        this.enemyCondition = condition;
        return this;
    }

    public StealthBonus setPlayerMultiplier(LivingMultiplier multiplier) {
        this.playerMultiplier = multiplier;
        return this;
    }

    public StealthBonus setEnemyMultiplier(LivingMultiplier multiplier) {
        this.enemyMultiplier = multiplier;
        return this;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public static class Serializer implements SkillBonus.Serializer {
        @Override
        public StealthBonus deserialize(JsonObject json) throws JsonParseException {
            float amount = SerializationHelper.getElement(json, "amount").getAsFloat();
            StealthBonus bonus = new StealthBonus(amount);
            bonus.playerMultiplier = SerializationHelper.deserializeLivingMultiplier(json, "player_multiplier");
            bonus.enemyMultiplier = SerializationHelper.deserializeLivingMultiplier(json, "enemy_multiplier");
            bonus.playerCondition = SerializationHelper.deserializeLivingCondition(json, "player_condition");
            bonus.enemyCondition = SerializationHelper.deserializeLivingCondition(json, "target_condition");
            return bonus;
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof StealthBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            json.addProperty("amount", aBonus.amount);
            SerializationHelper.serializeLivingMultiplier(json, aBonus.playerMultiplier, "player_multiplier");
            SerializationHelper.serializeLivingMultiplier(json, aBonus.enemyMultiplier, "enemy_multiplier");
            SerializationHelper.serializeLivingCondition(json, aBonus.playerCondition, "player_condition");
            SerializationHelper.serializeLivingCondition(json, aBonus.enemyCondition, "target_condition");
        }

        @Override
        public StealthBonus deserialize(CompoundTag tag) {
            float amount = tag.getFloat("amount");
            StealthBonus bonus = new StealthBonus(amount);
            bonus.playerMultiplier = SerializationHelper.deserializeLivingMultiplier(tag, "player_multiplier");
            bonus.enemyMultiplier = SerializationHelper.deserializeLivingMultiplier(tag, "enemy_multiplier");
            bonus.playerCondition = SerializationHelper.deserializeLivingCondition(tag, "player_condition");
            bonus.enemyCondition = SerializationHelper.deserializeLivingCondition(tag, "target_condition");
            return bonus;
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof StealthBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            tag.putFloat("amount", aBonus.amount);
            SerializationHelper.serializeLivingMultiplier(tag, aBonus.playerMultiplier, "player_multiplier");
            SerializationHelper.serializeLivingMultiplier(tag, aBonus.enemyMultiplier, "enemy_multiplier");
            SerializationHelper.serializeLivingCondition(tag, aBonus.playerCondition, "player_condition");
            SerializationHelper.serializeLivingCondition(tag, aBonus.enemyCondition, "target_condition");
            return tag;
        }

        @Override
        public StealthBonus deserialize(FriendlyByteBuf buf) {
            float amount = buf.readFloat();
            StealthBonus bonus = new StealthBonus(amount);
            bonus.playerMultiplier = NetworkHelper.readLivingMultiplier(buf);
            bonus.enemyMultiplier = NetworkHelper.readLivingMultiplier(buf);
            bonus.playerCondition = NetworkHelper.readLivingCondition(buf);
            bonus.enemyCondition = NetworkHelper.readLivingCondition(buf);
            return bonus;
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof StealthBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            buf.writeFloat(aBonus.amount);
            NetworkHelper.writeLivingMultiplier(buf, aBonus.playerMultiplier);
            NetworkHelper.writeLivingMultiplier(buf, aBonus.enemyMultiplier);
            NetworkHelper.writeLivingCondition(buf, aBonus.playerCondition);
            NetworkHelper.writeLivingCondition(buf, aBonus.enemyCondition);
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new StealthBonus(0.1f);
        }
    }
}
