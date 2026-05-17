package daripher.skilltree.skill.bonus.player;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.predicate.effect.EffectType;
import daripher.skilltree.skill.bonus.predicate.living.LivingEntityPredicate;
import daripher.skilltree.skill.bonus.predicate.living.NoneLivingEntityPredicate;
import daripher.skilltree.skill.bonus.multiplier.LivingMultiplier;
import daripher.skilltree.skill.bonus.multiplier.NoneLivingMultiplier;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public final class EffectDurationBonus implements SkillBonus<EffectDurationBonus> {
  private EffectType effectType;
  private float duration;
  private @Nonnull LivingMultiplier playerMultiplier = NoneLivingMultiplier.INSTANCE;
  private @Nonnull LivingEntityPredicate playerCondition = NoneLivingEntityPredicate.INSTANCE;
  private SkillBonus.Target target;
  private @Nonnull LivingMultiplier enemyMultiplier = NoneLivingMultiplier.INSTANCE;
  private @Nonnull LivingEntityPredicate enemyCondition = NoneLivingEntityPredicate.INSTANCE;

  public EffectDurationBonus(EffectType effectType, float duration, SkillBonus.Target target) {
    this.effectType = effectType;
    this.duration = duration;
    this.target = target;
  }

  public float getDuration(@Nullable Player effectSource, LivingEntity entity) {
    if (target == Target.PLAYER) {
      if (!playerCondition.test(entity)) return 0f;
      return duration * playerMultiplier.getValue(entity);
    }
    if (!enemyCondition.test(entity)) return 0f;
    float duration = this.duration;
    if (effectSource != null && !playerCondition.test(effectSource)) return 0f;
    return duration * playerMultiplier.getValue(entity) * enemyMultiplier.getValue(entity);
  }

  @Override
  public SkillBonus.Serializer getSerializer() {
    return PSTSkillBonuses.EFFECT_DURATION.get();
  }

  @Override
  public EffectDurationBonus copy() {
    EffectDurationBonus bonus = new EffectDurationBonus(effectType, duration, target);
    bonus.playerMultiplier = this.playerMultiplier;
    bonus.playerCondition = this.playerCondition;
    bonus.enemyCondition = this.enemyCondition;
    bonus.enemyMultiplier = this.enemyMultiplier;
    return bonus;
  }

  @Override
  public EffectDurationBonus multiply(double multiplier) {
    duration = (float) (duration * multiplier);
    return this;
  }

  @Override
  public boolean canMerge(SkillBonus<?> other) {
    if (!(other instanceof EffectDurationBonus otherBonus)) return false;
    if (otherBonus.playerCondition != playerCondition) return false;
    if (otherBonus.playerMultiplier != playerMultiplier) return false;
    if (otherBonus.target != target) return false;
    if (otherBonus.enemyCondition != enemyCondition) return false;
    if (otherBonus.enemyMultiplier != enemyMultiplier) return false;
    return otherBonus.effectType == this.effectType;
  }

  @Override
  public SkillBonus<EffectDurationBonus> merge(SkillBonus<?> other) {
    if (!(other instanceof EffectDurationBonus otherBonus)) {
      throw new IllegalArgumentException();
    }
    EffectDurationBonus mergedBonus = new EffectDurationBonus(effectType, duration + otherBonus.duration, target);
    mergedBonus.playerCondition = this.playerCondition;
    mergedBonus.playerMultiplier = this.playerMultiplier;
    mergedBonus.enemyCondition = this.enemyCondition;
    mergedBonus.enemyMultiplier = this.enemyMultiplier;
    return mergedBonus;
  }

  @Override
  public MutableComponent getTooltip() {
    Component effectTypeDescription = Component.translatable(effectType.getDescriptionId() + ".plural");
    String key = getDescriptionId() + "." + target.getName();
    MutableComponent tooltip = Component.translatable(key, effectTypeDescription);
    tooltip = TooltipHelper.getSkillBonusTooltip(tooltip, duration, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    tooltip = playerMultiplier.getTooltip(tooltip, target);
    tooltip = playerCondition.getTooltip(tooltip, target);
    tooltip = enemyMultiplier.getTooltip(tooltip, target);
    tooltip = enemyCondition.getTooltip(tooltip, target);
    return tooltip.withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
  }

  @Override
  public boolean isPositive() {
    return duration > 0 ^ target == Target.PLAYER ^ effectType != EffectType.HARMFUL;
  }


  @Override
  public void addEditorWidgets(SkillTreeEditor editor, int index, Consumer<EffectDurationBonus> consumer) {
    editor.addLabel(0, 0, "Effect Type", ChatFormatting.GREEN);
    editor.increaseHeight(19);
    editor.addSelectionMenu(0, 0, 200, effectType)
        .setElementNameGetter(effectType -> Component.literal(effectType.name()))
        .setResponder(effectType -> selectEffectType(consumer, effectType));
    editor.increaseHeight(19);
    editor.addLabel(110, 0, "Duration", ChatFormatting.GOLD);
    editor.addLabel(0, 0, "Target", ChatFormatting.GOLD);
    editor.increaseHeight(19);
    editor.addNumericTextField(110, 0, 50, 14, duration)
        .setNumericResponder(value -> selectDuration(consumer, value));
    editor.addSelection(0, 0, 80, 1, target)
        .setNameGetter(target -> Component.literal(target.toString()))
        .setResponder(target -> selectTarget(editor, consumer, target));
    editor.increaseHeight(29);
    editor.addLabel(0, 0, "Player Condition", ChatFormatting.GOLD);
    editor.increaseHeight(19);
    editor.addSelectionMenu(0, 0, 200, playerCondition)
        .setResponder(condition -> selectPlayerCondition(editor, consumer, condition))
        .setMenuInitFunc(() -> addPlayerConditionWidgets(editor, consumer));
    editor.increaseHeight(19);
    editor.addLabel(0, 0, "Player Multiplier", ChatFormatting.GOLD);
    editor.increaseHeight(19);
    editor.addSelectionMenu(0, 0, 200, playerMultiplier)
        .setResponder(multiplier -> selectPlayerMultiplier(editor, consumer, multiplier))
        .setMenuInitFunc(() -> addPlayerMultiplierWidgets(editor, consumer));
    editor.increaseHeight(19);
    if (target == Target.ENEMY) {
      editor.addLabel(0, 0, "Enemy Condition", ChatFormatting.GOLD);
      editor.increaseHeight(19);
      editor.addSelectionMenu(0, 0, 200, enemyCondition)
          .setResponder(condition -> selectEnemyCondition(editor, consumer, condition))
          .setMenuInitFunc(() -> addEnemyConditionWidgets(editor, consumer));
      editor.increaseHeight(19);
      editor.addLabel(0, 0, "Enemy Multiplier", ChatFormatting.GOLD);
      editor.increaseHeight(19);
      editor.addSelectionMenu(0, 0, 200, enemyMultiplier)
          .setResponder(multiplier -> selectEnemyMultiplier(editor, consumer, multiplier))
          .setMenuInitFunc(() -> addEnemyMultiplierWidgets(editor, consumer));
      editor.increaseHeight(19);
    }
  }

  private void selectPlayerMultiplier(SkillTreeEditor editor, Consumer<EffectDurationBonus> consumer, LivingMultiplier multiplier) {
    setPlayerMultiplier(multiplier);
    consumer.accept(this.copy());
    editor.rebuildWidgets();
  }

  private void selectPlayerCondition(SkillTreeEditor editor, Consumer<EffectDurationBonus> consumer, LivingEntityPredicate condition) {
    setPlayerCondition(condition);
    consumer.accept(this.copy());
    editor.rebuildWidgets();
  }

  private void selectEnemyMultiplier(SkillTreeEditor editor, Consumer<EffectDurationBonus> consumer, LivingMultiplier multiplier) {
    setEnemyMultiplier(multiplier);
    consumer.accept(this.copy());
    editor.rebuildWidgets();
  }

  private void selectEnemyCondition(SkillTreeEditor editor, Consumer<EffectDurationBonus> consumer, LivingEntityPredicate condition) {
    setEnemyCondition(condition);
    consumer.accept(this.copy());
    editor.rebuildWidgets();
  }

  private void selectEffectType(Consumer<EffectDurationBonus> consumer, EffectType effectType) {
    setEffectType(effectType);
    consumer.accept(this.copy());
  }

  private void selectDuration(Consumer<EffectDurationBonus> consumer, Double duration) {
    setDuration(duration.floatValue());
    consumer.accept(this.copy());
  }

  private void selectTarget(SkillTreeEditor editor, Consumer<EffectDurationBonus> consumer, SkillBonus.Target target) {
    setTarget(target);
    consumer.accept(this.copy());
    editor.rebuildWidgets();
  }

  private void addPlayerConditionWidgets(SkillTreeEditor editor, Consumer<EffectDurationBonus> consumer) {
    playerCondition.addEditorWidgets(editor, c -> {
      setPlayerCondition(c);
      consumer.accept(this.copy());
    });
  }

  private void addPlayerMultiplierWidgets(SkillTreeEditor editor, Consumer<EffectDurationBonus> consumer) {
    playerMultiplier.addEditorWidgets(editor, m -> {
      setPlayerMultiplier(m);
      consumer.accept(this.copy());
    });
  }

  private void addEnemyConditionWidgets(SkillTreeEditor editor, Consumer<EffectDurationBonus> consumer) {
    enemyCondition.addEditorWidgets(editor, c -> {
      setPlayerCondition(c);
      consumer.accept(this.copy());
    });
  }

  private void addEnemyMultiplierWidgets(SkillTreeEditor editor, Consumer<EffectDurationBonus> consumer) {
    enemyMultiplier.addEditorWidgets(editor, m -> {
      setPlayerMultiplier(m);
      consumer.accept(this.copy());
    });
  }

  public void setDuration(float duration) {
    this.duration = duration;
  }

  public void setEffectType(EffectType effectType) {
    this.effectType = effectType;
  }

  public SkillBonus<?> setPlayerCondition(LivingEntityPredicate condition) {
    this.playerCondition = condition;
    return this;
  }

  public SkillBonus<?> setPlayerMultiplier(LivingMultiplier multiplier) {
    this.playerMultiplier = multiplier;
    return this;
  }

  public void setTarget(Target target) {
    this.target = target;
  }

  public void setEnemyCondition(@Nonnull LivingEntityPredicate enemyCondition) {
    this.enemyCondition = enemyCondition;
  }

  public void setEnemyMultiplier(@Nonnull LivingMultiplier enemyMultiplier) {
    this.enemyMultiplier = enemyMultiplier;
  }

  public Target getTarget() {
    return target;
  }

  public static class Serializer implements SkillBonus.Serializer {
    @Override
    public EffectDurationBonus deserialize(JsonObject json) throws JsonParseException {
      EffectType effectType = EffectType.fromName(json.get("effect_type")
          .getAsString());
      float duration = json.get("duration")
          .getAsFloat();
      SkillBonus.Target target = Target.fromName(json.get("target")
          .getAsString());
      EffectDurationBonus bonus = new EffectDurationBonus(effectType, duration, target);
      bonus.playerMultiplier = SerializationHelper.deserializeLivingMultiplier(json, "player_multiplier");
      bonus.playerCondition = SerializationHelper.deserializeLivingCondition(json, "player_condition");
      bonus.enemyMultiplier = SerializationHelper.deserializeLivingMultiplier(json, "enemy_multiplier");
      bonus.enemyCondition = SerializationHelper.deserializeLivingCondition(json, "enemy_condition");
      return bonus;
    }

    @Override
    public void serialize(JsonObject json, SkillBonus<?> bonus) {
      if (!(bonus instanceof EffectDurationBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      json.addProperty("effect_type", aBonus.effectType.getName());
      json.addProperty("duration", aBonus.duration);
      json.addProperty("target", aBonus.target.getName());
      SerializationHelper.serializeLivingMultiplier(json, aBonus.playerMultiplier, "player_multiplier");
      SerializationHelper.serializeLivingCondition(json, aBonus.playerCondition, "player_condition");
      SerializationHelper.serializeLivingMultiplier(json, aBonus.enemyMultiplier, "enemy_multiplier");
      SerializationHelper.serializeLivingCondition(json, aBonus.enemyCondition, "enemy_condition");
    }

    @Override
    public EffectDurationBonus deserialize(CompoundTag tag) {
      EffectType effectType = EffectType.fromName(tag.getString("effect_type"));
      float duration = tag.getFloat("duration");
      SkillBonus.Target target = Target.fromName(tag.getString("target"));
      EffectDurationBonus bonus = new EffectDurationBonus(effectType, duration, target);
      bonus.playerMultiplier = SerializationHelper.deserializeLivingMultiplier(tag, "player_multiplier");
      bonus.playerCondition = SerializationHelper.deserializeLivingCondition(tag, "player_condition");
      bonus.enemyMultiplier = SerializationHelper.deserializeLivingMultiplier(tag, "enemy_multiplier");
      bonus.enemyCondition = SerializationHelper.deserializeLivingCondition(tag, "enemy_condition");
      return bonus;
    }

    @Override
    public CompoundTag serialize(SkillBonus<?> bonus) {
      if (!(bonus instanceof EffectDurationBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      tag.putString("effect_type", aBonus.effectType.getName());
      tag.putFloat("duration", aBonus.duration);
      tag.putString("target", aBonus.target.getName());
      SerializationHelper.serializeLivingMultiplier(tag, aBonus.playerMultiplier, "player_multiplier");
      SerializationHelper.serializeLivingCondition(tag, aBonus.playerCondition, "player_condition");
      SerializationHelper.serializeLivingMultiplier(tag, aBonus.enemyMultiplier, "enemy_multiplier");
      SerializationHelper.serializeLivingCondition(tag, aBonus.enemyCondition, "enemy_condition");
      return tag;
    }

    @Override
    public EffectDurationBonus deserialize(FriendlyByteBuf buf) {
      EffectType effectType = EffectType.values()[buf.readInt()];
      float duration = buf.readFloat();
      SkillBonus.Target target = Target.values()[buf.readInt()];
      EffectDurationBonus bonus = new EffectDurationBonus(effectType, duration, target);
      bonus.playerMultiplier = NetworkHelper.readLivingMultiplier(buf);
      bonus.playerCondition = NetworkHelper.readLivingCondition(buf);
      bonus.enemyMultiplier = NetworkHelper.readLivingMultiplier(buf);
      bonus.enemyCondition = NetworkHelper.readLivingCondition(buf);
      return bonus;
    }

    @Override
    public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
      if (!(bonus instanceof EffectDurationBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      buf.writeInt(aBonus.effectType.ordinal());
      buf.writeFloat(aBonus.duration);
      buf.writeInt(aBonus.target.ordinal());
      NetworkHelper.writeLivingMultiplier(buf, aBonus.playerMultiplier);
      NetworkHelper.writeLivingCondition(buf, aBonus.playerCondition);
      NetworkHelper.writeLivingMultiplier(buf, aBonus.enemyMultiplier);
      NetworkHelper.writeLivingCondition(buf, aBonus.enemyCondition);
    }

    @Override
    public SkillBonus<?> createDefaultInstance() {
      return new EffectDurationBonus(EffectType.BENEFICIAL, 0.1f, Target.PLAYER);
    }
  }
}
