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

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public final class EffectImmunityBonus implements SkillBonus<EffectImmunityBonus> {
    private @Nonnull MobEffectPredicate effectPredicate;
    private @Nonnull LivingEntityPredicate playerCondition = NoneLivingEntityPredicate.INSTANCE;

    public EffectImmunityBonus(@Nonnull MobEffectPredicate effectPredicate) {
        this.effectPredicate = effectPredicate;
    }

    public boolean shouldProvideImmunity(MobEffect mobEffect, LivingEntity entity) {
        if (!effectPredicate.test(mobEffect)) {
            return false;
        }
        return playerCondition.test(entity);
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return PSTSkillBonuses.EFFECT_IMMUNITY.get();
    }

    @Override
    public EffectImmunityBonus copy() {
        EffectImmunityBonus copy = new EffectImmunityBonus(effectPredicate);
        copy.playerCondition = playerCondition;
        return copy;
    }

    @Override
    public EffectImmunityBonus multiply(double multiplier) {
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        return false;
    }

    @Override
    public SkillBonus<EffectImmunityBonus> merge(SkillBonus<?> other) {
        return this;
    }

    @Override
    public MutableComponent getSimpleTooltip() {
        Component effectTypeDescription = effectPredicate.getTooltip("plural");
        MutableComponent tooltip = Component.translatable(getDescriptionId(), effectTypeDescription);
        tooltip = playerCondition.getTooltip(tooltip, Target.PLAYER);
        return tooltip.withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
    }

    @Override
    public boolean isPositive() {
        return effectPredicate.testsForHarmfulEffects();
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<EffectImmunityBonus> consumer) {
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
    }

    private void selectPlayerCondition(SkillTreeEditor editor, Consumer<EffectImmunityBonus> consumer, LivingEntityPredicate condition) {
        setPlayerCondition(condition);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void selectEffectPredicate(SkillTreeEditor editor, Consumer<EffectImmunityBonus> consumer, MobEffectPredicate effectPredicate) {
        setEffectPredicate(effectPredicate);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void addEffectPredicateWidgets(SkillTreeEditor editor, Consumer<EffectImmunityBonus> consumer) {
        effectPredicate.addEditorWidgets(editor, predicate -> {
            setEffectPredicate(predicate);
            consumer.accept(this.copy());
        });
    }

    private void addPlayerConditionWidgets(SkillTreeEditor editor, Consumer<EffectImmunityBonus> consumer) {
        playerCondition.addEditorWidgets(editor, c -> {
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

    public static class Serializer implements SkillBonus.Serializer {
        @Override
        public EffectImmunityBonus deserialize(JsonObject json) throws JsonParseException {
            MobEffectPredicate mobEffectPredicate = SerializationHelper.deserializeMobEffectCondition(json, "mob_effect_predicate");
            EffectImmunityBonus bonus = new EffectImmunityBonus(mobEffectPredicate);
            bonus.playerCondition = SerializationHelper.deserializeLivingCondition(json, "player_condition");
            return bonus;
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            EffectImmunityBonus validBonus = validateBonus(bonus);
            SerializationHelper.serializeMobEffectCondition(json, validBonus.effectPredicate, "mob_effect_predicate");
            SerializationHelper.serializeLivingCondition(json, validBonus.playerCondition, "player_condition");
        }

        @Override
        public EffectImmunityBonus deserialize(CompoundTag tag) {
            MobEffectPredicate mobEffectPredicate = SerializationHelper.deserializeMobEffectCondition(tag, "mob_effect_predicate");
            EffectImmunityBonus bonus = new EffectImmunityBonus(mobEffectPredicate);
            bonus.playerCondition = SerializationHelper.deserializeLivingCondition(tag, "player_condition");
            return bonus;
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            EffectImmunityBonus validBonus = validateBonus(bonus);
            CompoundTag compoundTag = new CompoundTag();
            SerializationHelper.serializeMobEffectCondition(compoundTag, validBonus.effectPredicate, "mob_effect_predicate");
            SerializationHelper.serializeLivingCondition(compoundTag, validBonus.playerCondition, "player_condition");
            return compoundTag;
        }

        @Override
        public EffectImmunityBonus deserialize(FriendlyByteBuf buf) {
            MobEffectPredicate mobEffectPredicate = NetworkHelper.readMobEffectCondition(buf);
            EffectImmunityBonus bonus = new EffectImmunityBonus(mobEffectPredicate);
            bonus.playerCondition = NetworkHelper.readLivingCondition(buf);
            return bonus;
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            EffectImmunityBonus validBonus = validateBonus(bonus);
            NetworkHelper.writeMobEffectCondition(buf, validBonus.effectPredicate);
            NetworkHelper.writeLivingCondition(buf, validBonus.playerCondition);
        }

        private EffectImmunityBonus validateBonus(SkillBonus<?> bonus) {
            if (!(bonus instanceof EffectImmunityBonus validBonus)) {
                throw new IllegalArgumentException();
            }
            return validBonus;
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new EffectImmunityBonus(new MobEffectIdPredicate(MobEffects.POISON));
        }
    }
}
