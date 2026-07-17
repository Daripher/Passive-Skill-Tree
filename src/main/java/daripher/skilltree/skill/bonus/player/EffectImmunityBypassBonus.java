package daripher.skilltree.skill.bonus.player;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.predicate.effect.MobEffectIdPredicate;
import daripher.skilltree.skill.bonus.predicate.effect.MobEffectPredicate;
import daripher.skilltree.skill.bonus.predicate.living.LivingEntityPredicate;
import daripher.skilltree.skill.bonus.predicate.living.NoneLivingEntityPredicate;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public final class EffectImmunityBypassBonus implements SkillBonus<EffectImmunityBypassBonus> {
    private @Nonnull MobEffectPredicate effectPredicate;
    private @Nonnull LivingEntityPredicate playerCondition = NoneLivingEntityPredicate.INSTANCE;
    private @Nonnull LivingEntityPredicate enemyCondition = NoneLivingEntityPredicate.INSTANCE;

    public EffectImmunityBypassBonus(@Nonnull MobEffectPredicate effectPredicate) {
        this.effectPredicate = effectPredicate;
    }

    public boolean shouldIgnoreEffectImmunity(MobEffect mobEffect, @Nullable Player effectSource, LivingEntity entity) {
        if (!effectPredicate.test(mobEffect)) {
            return false;
        }
        if (!enemyCondition.test(entity)) {
            return false;
        }
        return effectSource == null || playerCondition.test(effectSource);
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return PSTSkillBonuses.IGNORE_EFFECT_IMMUNITY.get();
    }

    @Override
    public EffectImmunityBypassBonus copy() {
        EffectImmunityBypassBonus copy = new EffectImmunityBypassBonus(effectPredicate);
        copy.playerCondition = playerCondition;
        copy.enemyCondition = enemyCondition;
        return copy;
    }

    @Override
    public EffectImmunityBypassBonus multiply(double multiplier) {
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        return false;
    }

    @Override
    public SkillBonus<EffectImmunityBypassBonus> merge(SkillBonus<?> other) {
        return this;
    }

    @Override
    public MutableComponent getSimpleTooltip() {
        Component effectTypeDescription = effectPredicate.getTooltip("plural");
        MutableComponent tooltip = Component.translatable(getDescriptionId(), effectTypeDescription);
        tooltip = playerCondition.getTooltip(tooltip, Target.PLAYER);
        tooltip = enemyCondition.getTooltip(tooltip, Target.PLAYER);
        return tooltip.withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
    }

    @Override
    public boolean isPositive() {
        return true;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<EffectImmunityBypassBonus> consumer) {
        editor.addLabel(0, 0, "Effect Condition", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, effectPredicate).setRequiresSearch(false)
                .setResponder(effectPredicate -> selectEffectPredicate(editor, consumer, effectPredicate))
                .setMenuInitFunc(() -> addEffectPredicateWidgets(editor, consumer));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Player Condition", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, playerCondition).setResponder(condition -> selectPlayerCondition(editor, consumer, condition))
                .setMenuInitFunc(() -> addPlayerConditionWidgets(editor, consumer));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Enemy Condition", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, enemyCondition).setResponder(condition -> selectEnemyCondition(editor, consumer, condition))
                .setMenuInitFunc(() -> addEnemyConditionWidgets(editor, consumer));
        editor.increaseHeight(19);
    }

    private void selectPlayerCondition(SkillTreeEditor editor, Consumer<EffectImmunityBypassBonus> consumer, LivingEntityPredicate condition) {
        setPlayerCondition(condition);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void selectEnemyCondition(SkillTreeEditor editor, Consumer<EffectImmunityBypassBonus> consumer, LivingEntityPredicate condition) {
        setEnemyCondition(condition);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void selectEffectPredicate(SkillTreeEditor editor, Consumer<EffectImmunityBypassBonus> consumer, MobEffectPredicate effectPredicate) {
        setEffectPredicate(effectPredicate);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void addEffectPredicateWidgets(SkillTreeEditor editor, Consumer<EffectImmunityBypassBonus> consumer) {
        effectPredicate.addEditorWidgets(editor, predicate -> {
            setEffectPredicate(predicate);
            consumer.accept(this.copy());
        });
    }

    private void addPlayerConditionWidgets(SkillTreeEditor editor, Consumer<EffectImmunityBypassBonus> consumer) {
        playerCondition.addEditorWidgets(editor, c -> {
            setPlayerCondition(c);
            consumer.accept(this.copy());
        });
    }

    private void addEnemyConditionWidgets(SkillTreeEditor editor, Consumer<EffectImmunityBypassBonus> consumer) {
        enemyCondition.addEditorWidgets(editor, c -> {
            setPlayerCondition(c);
            consumer.accept(this.copy());
        });
    }

    public void setEffectPredicate(@Nonnull MobEffectPredicate effectPredicate) {
        this.effectPredicate = effectPredicate;
    }

    public SkillBonus<?> setPlayerCondition(LivingEntityPredicate condition) {
        this.playerCondition = condition;
        return this;
    }

    public void setEnemyCondition(@Nonnull LivingEntityPredicate enemyCondition) {
        this.enemyCondition = enemyCondition;
    }

    public static class Serializer implements SkillBonus.Serializer {
        @Override
        public EffectImmunityBypassBonus deserialize(JsonObject json) throws JsonParseException {
            MobEffectPredicate mobEffectPredicate = SerializationHelper.deserializeMobEffectCondition(json, "mob_effect_predicate");
            EffectImmunityBypassBonus bonus = new EffectImmunityBypassBonus(mobEffectPredicate);
            bonus.playerCondition = SerializationHelper.deserializeLivingCondition(json, "player_condition");
            bonus.enemyCondition = SerializationHelper.deserializeLivingCondition(json, "enemy_condition");
            return bonus;
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            EffectImmunityBypassBonus validBonus = validateBonus(bonus);
            SerializationHelper.serializeMobEffectCondition(json, validBonus.effectPredicate, "mob_effect_predicate");
            SerializationHelper.serializeLivingCondition(json, validBonus.playerCondition, "player_condition");
            SerializationHelper.serializeLivingCondition(json, validBonus.enemyCondition, "enemy_condition");
        }

        @Override
        public EffectImmunityBypassBonus deserialize(CompoundTag tag) {
            MobEffectPredicate mobEffectPredicate = SerializationHelper.deserializeMobEffectCondition(tag, "mob_effect_predicate");
            EffectImmunityBypassBonus bonus = new EffectImmunityBypassBonus(mobEffectPredicate);
            bonus.playerCondition = SerializationHelper.deserializeLivingCondition(tag, "player_condition");
            bonus.enemyCondition = SerializationHelper.deserializeLivingCondition(tag, "enemy_condition");
            return bonus;
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            EffectImmunityBypassBonus validBonus = validateBonus(bonus);
            CompoundTag compoundTag = new CompoundTag();
            SerializationHelper.serializeMobEffectCondition(compoundTag, validBonus.effectPredicate, "mob_effect_predicate");
            SerializationHelper.serializeLivingCondition(compoundTag, validBonus.playerCondition, "player_condition");
            SerializationHelper.serializeLivingCondition(compoundTag, validBonus.enemyCondition, "enemy_condition");
            return compoundTag;
        }

        @Override
        public EffectImmunityBypassBonus deserialize(FriendlyByteBuf buf) {
            MobEffectPredicate mobEffectPredicate = NetworkHelper.readMobEffectCondition(buf);
            EffectImmunityBypassBonus bonus = new EffectImmunityBypassBonus(mobEffectPredicate);
            bonus.playerCondition = NetworkHelper.readLivingCondition(buf);
            bonus.enemyCondition = NetworkHelper.readLivingCondition(buf);
            return bonus;
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            EffectImmunityBypassBonus validBonus = validateBonus(bonus);
            NetworkHelper.writeMobEffectCondition(buf, validBonus.effectPredicate);
            NetworkHelper.writeLivingCondition(buf, validBonus.playerCondition);
            NetworkHelper.writeLivingCondition(buf, validBonus.enemyCondition);
        }

        private EffectImmunityBypassBonus validateBonus(SkillBonus<?> bonus) {
            if (!(bonus instanceof EffectImmunityBypassBonus validBonus)) {
                throw new IllegalArgumentException();
            }
            return validBonus;
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new EffectImmunityBypassBonus(
                    new MobEffectIdPredicate(MobEffects.POISON.value()));
        }
    }
}
