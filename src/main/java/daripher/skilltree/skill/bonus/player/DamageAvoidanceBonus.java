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
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;

public final class DamageAvoidanceBonus implements SkillBonus<DamageAvoidanceBonus> {
  private float chance;
  private @Nonnull LivingMultiplier playerMultiplier = NoneLivingMultiplier.INSTANCE;
  private @Nonnull LivingMultiplier attackerMultiplier = NoneLivingMultiplier.INSTANCE;
  private @Nonnull LivingEntityPredicate playerCondition = NoneLivingEntityPredicate.INSTANCE;
  private @Nonnull LivingEntityPredicate attackerCondition = NoneLivingEntityPredicate.INSTANCE;
  private @Nonnull DamageCondition damageCondition = NoneDamageCondition.INSTANCE;

  public DamageAvoidanceBonus(float chance) {
    this.chance = chance;
  }

  public float getAvoidanceChance(
      DamageSource source, Player player, @Nullable LivingEntity attacker) {
    if (!damageCondition.met(source)) return 0f;
    if (!playerCondition.test(player)) return 0f;
    if (attackerCondition != NoneLivingEntityPredicate.INSTANCE) {
      if (attacker == null || !attackerCondition.test(attacker)) {
        return 0f;
      }
    }
    float result = chance * playerMultiplier.getValue(player);
    if (attacker != null) {
      result *= attackerMultiplier.getValue(attacker);
    }
    return result;
  }

  @Override
  public SkillBonus.Serializer getSerializer() {
    return PSTSkillBonuses.DAMAGE_AVOIDANCE.get();
  }

  @Override
  public DamageAvoidanceBonus copy() {
    DamageAvoidanceBonus bonus = new DamageAvoidanceBonus(chance);
    bonus.playerMultiplier = this.playerMultiplier;
    bonus.attackerMultiplier = this.attackerMultiplier;
    bonus.playerCondition = this.playerCondition;
    bonus.damageCondition = this.damageCondition;
    bonus.attackerCondition = this.attackerCondition;
    return bonus;
  }

  @Override
  public DamageAvoidanceBonus multiply(double multiplier) {
    chance *= (float) multiplier;
    return this;
  }

  @Override
  public boolean canMerge(SkillBonus<?> other) {
    if (!(other instanceof DamageAvoidanceBonus otherBonus)) return false;
    if (!Objects.equals(otherBonus.playerMultiplier, this.playerMultiplier)) return false;
    if (!Objects.equals(otherBonus.attackerMultiplier, this.attackerMultiplier)) return false;
    if (!Objects.equals(otherBonus.playerCondition, this.playerCondition)) return false;
    if (!Objects.equals(otherBonus.damageCondition, this.damageCondition)) return false;
    return Objects.equals(otherBonus.attackerCondition, this.attackerCondition);
  }

  @Override
  public SkillBonus<DamageAvoidanceBonus> merge(SkillBonus<?> other) {
    if (!(other instanceof DamageAvoidanceBonus otherBonus)) {
      throw new IllegalArgumentException();
    }
    float mergedChance = otherBonus.chance + this.chance;
    DamageAvoidanceBonus mergedBonus = new DamageAvoidanceBonus(mergedChance);
    mergedBonus.playerMultiplier = this.playerMultiplier;
    mergedBonus.attackerMultiplier = this.attackerMultiplier;
    mergedBonus.playerCondition = this.playerCondition;
    mergedBonus.damageCondition = this.damageCondition;
    mergedBonus.attackerCondition = this.attackerCondition;
    return mergedBonus;
  }

  @Override
  public MutableComponent getTooltip() {
    MutableComponent tooltip =
        Component.translatable(getDescriptionId(), damageCondition.getTooltip());
    tooltip =
        TooltipHelper.getSkillBonusTooltip(
            tooltip, chance, AttributeModifier.Operation.MULTIPLY_BASE);
    tooltip = playerMultiplier.getTooltip(tooltip, Target.PLAYER);
    tooltip = attackerMultiplier.getTooltip(tooltip, Target.ENEMY);
    tooltip = playerCondition.getTooltip(tooltip, Target.PLAYER);
    tooltip = attackerCondition.getTooltip(tooltip, Target.ENEMY);
    return tooltip.withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
  }

  @Override
  public boolean isPositive() {
    return chance > 0;
  }

  @Override
  public void addEditorWidgets(
      SkillTreeEditor editor, int row, Consumer<DamageAvoidanceBonus> consumer) {
    editor.addLabel(0, 0, "Chance", ChatFormatting.GOLD);
    editor.increaseHeight(19);
    editor
        .addNumericTextField(0, 0, 50, 14, chance)
        .setNumericResponder(value -> selectChance(consumer, value));
    editor.increaseHeight(19);
    editor.addLabel(0, 0, "Damage Condition", ChatFormatting.GOLD);
    editor.increaseHeight(19);
    editor
        .addSelectionMenu(0, 0, 200, damageCondition)
        .setResponder(condition -> selectDamageCondition(consumer, condition));
    editor.increaseHeight(19);
    editor.addLabel(0, 0, "Player Condition", ChatFormatting.GOLD);
    editor.increaseHeight(19);
    editor
        .addSelectionMenu(0, 0, 200, playerCondition)
        .setResponder(condition -> selectPlayerCondition(editor, consumer, condition))
        .setMenuInitFunc(() -> addPlayerConditionWidgets(editor, consumer));
    editor.increaseHeight(19);
    editor.addLabel(0, 0, "Attacker Condition", ChatFormatting.GOLD);
    editor.increaseHeight(19);
    editor
        .addSelectionMenu(0, 0, 200, attackerCondition)
        .setResponder(condition -> selectTargetCondition(editor, consumer, condition))
        .setMenuInitFunc(() -> addTargetConditionWidgets(editor, consumer));
    editor.increaseHeight(19);
    editor.addLabel(0, 0, "Player Multiplier", ChatFormatting.GOLD);
    editor.increaseHeight(19);
    editor
        .addSelectionMenu(0, 0, 200, playerMultiplier)
        .setResponder(multiplier -> selectPlayerMultiplier(editor, consumer, multiplier))
        .setMenuInitFunc(() -> addPlayerMultiplierWidgets(editor, consumer));
    editor.increaseHeight(19);
    editor.addLabel(0, 0, "Attacker Multiplier", ChatFormatting.GOLD);
    editor.increaseHeight(19);
    editor
        .addSelectionMenu(0, 0, 200, attackerMultiplier)
        .setResponder(multiplier -> selectTargetMultiplier(editor, consumer, multiplier))
        .setMenuInitFunc(() -> addTargetMultiplierWidgets(editor, consumer));
    editor.increaseHeight(19);
  }

  private void selectChance(Consumer<DamageAvoidanceBonus> consumer, Double value) {
    setChance(value.floatValue());
    consumer.accept(this.copy());
  }

  private void addTargetMultiplierWidgets(
      SkillTreeEditor editor, Consumer<DamageAvoidanceBonus> consumer) {
    attackerMultiplier.addEditorWidgets(
        editor,
        multiplier -> {
          setEnemyMultiplier(multiplier);
          consumer.accept(this.copy());
        });
  }

  private void selectTargetMultiplier(
      SkillTreeEditor editor,
      Consumer<DamageAvoidanceBonus> consumer,
      LivingMultiplier multiplier) {
    setEnemyMultiplier(multiplier);
    consumer.accept(this.copy());
    editor.rebuildWidgets();
  }

  private void addPlayerMultiplierWidgets(
      SkillTreeEditor editor, Consumer<DamageAvoidanceBonus> consumer) {
    playerMultiplier.addEditorWidgets(
        editor,
        multiplier -> {
          setPlayerMultiplier(multiplier);
          consumer.accept(this.copy());
        });
  }

  private void selectPlayerMultiplier(
      SkillTreeEditor editor,
      Consumer<DamageAvoidanceBonus> consumer,
      LivingMultiplier multiplier) {
    setPlayerMultiplier(multiplier);
    consumer.accept(this.copy());
    editor.rebuildWidgets();
  }

  private void addTargetConditionWidgets(
      SkillTreeEditor editor, Consumer<DamageAvoidanceBonus> consumer) {
    attackerCondition.addEditorWidgets(
        editor,
        c -> {
          setTargetCondition(c);
          consumer.accept(this.copy());
        });
  }

  private void selectTargetCondition(
      SkillTreeEditor editor, Consumer<DamageAvoidanceBonus> consumer, LivingEntityPredicate condition) {
    setTargetCondition(condition);
    consumer.accept(this.copy());
    editor.rebuildWidgets();
  }

  private void addPlayerConditionWidgets(
      SkillTreeEditor editor, Consumer<DamageAvoidanceBonus> consumer) {
    playerCondition.addEditorWidgets(
        editor,
        c -> {
          setPlayerCondition(c);
          consumer.accept(this.copy());
        });
  }

  private void selectPlayerCondition(
      SkillTreeEditor editor, Consumer<DamageAvoidanceBonus> consumer, LivingEntityPredicate condition) {
    setPlayerCondition(condition);
    consumer.accept(this.copy());
    editor.rebuildWidgets();
  }

  private void selectDamageCondition(
      Consumer<DamageAvoidanceBonus> consumer, DamageCondition condition) {
    setDamageCondition(condition);
    consumer.accept(this.copy());
  }

  public SkillBonus<?> setPlayerCondition(LivingEntityPredicate condition) {
    this.playerCondition = condition;
    return this;
  }

  public SkillBonus<?> setDamageCondition(DamageCondition condition) {
    this.damageCondition = condition;
    return this;
  }

  public SkillBonus<?> setTargetCondition(LivingEntityPredicate condition) {
    this.attackerCondition = condition;
    return this;
  }

  public SkillBonus<?> setPlayerMultiplier(LivingMultiplier multiplier) {
    this.playerMultiplier = multiplier;
    return this;
  }

  public SkillBonus<?> setEnemyMultiplier(LivingMultiplier multiplier) {
    this.attackerMultiplier = multiplier;
    return this;
  }

  public void setChance(float chance) {
    this.chance = chance;
  }

  public static class Serializer implements SkillBonus.Serializer {
    @Override
    public DamageAvoidanceBonus deserialize(JsonObject json) throws JsonParseException {
      float chance = SerializationHelper.getElement(json, "chance").getAsFloat();
      DamageAvoidanceBonus bonus = new DamageAvoidanceBonus(chance);
      bonus.playerMultiplier =
          SerializationHelper.deserializeLivingMultiplier(json, "player_multiplier");
      bonus.attackerMultiplier =
          SerializationHelper.deserializeLivingMultiplier(json, "attacker_multiplier");
      bonus.playerCondition =
          SerializationHelper.deserializeLivingCondition(json, "player_condition");
      bonus.damageCondition = SerializationHelper.deserializeDamageCondition(json);
      bonus.attackerCondition =
          SerializationHelper.deserializeLivingCondition(json, "attacker_condition");
      return bonus;
    }

    @Override
    public void serialize(JsonObject json, SkillBonus<?> bonus) {
      if (!(bonus instanceof DamageAvoidanceBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      json.addProperty("chance", aBonus.chance);
      SerializationHelper.serializeLivingMultiplier(
          json, aBonus.playerMultiplier, "player_multiplier");
      SerializationHelper.serializeLivingMultiplier(
          json, aBonus.attackerMultiplier, "attacker_multiplier");
      SerializationHelper.serializeLivingCondition(
          json, aBonus.playerCondition, "player_condition");
      SerializationHelper.serializeDamageCondition(json, aBonus.damageCondition);
      SerializationHelper.serializeLivingCondition(
          json, aBonus.attackerCondition, "attacker_condition");
    }

    @Override
    public DamageAvoidanceBonus deserialize(CompoundTag tag) {
      float chance = tag.getFloat("chance");
      DamageAvoidanceBonus bonus = new DamageAvoidanceBonus(chance);
      bonus.playerMultiplier =
          SerializationHelper.deserializeLivingMultiplier(tag, "player_multiplier");
      bonus.attackerMultiplier =
          SerializationHelper.deserializeLivingMultiplier(tag, "attacker_multiplier");
      bonus.playerCondition =
          SerializationHelper.deserializeLivingCondition(tag, "player_condition");
      bonus.damageCondition = SerializationHelper.deserializeDamageCondition(tag);
      bonus.attackerCondition =
          SerializationHelper.deserializeLivingCondition(tag, "attacker_condition");
      return bonus;
    }

    @Override
    public CompoundTag serialize(SkillBonus<?> bonus) {
      if (!(bonus instanceof DamageAvoidanceBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      tag.putFloat("chance", aBonus.chance);
      SerializationHelper.serializeLivingMultiplier(
          tag, aBonus.playerMultiplier, "player_multiplier");
      SerializationHelper.serializeLivingMultiplier(
          tag, aBonus.attackerMultiplier, "attacker_multiplier");
      SerializationHelper.serializeLivingCondition(tag, aBonus.playerCondition, "player_condition");
      SerializationHelper.serializeDamageCondition(tag, aBonus.damageCondition);
      SerializationHelper.serializeLivingCondition(
          tag, aBonus.attackerCondition, "attacker_condition");
      return tag;
    }

    @Override
    public DamageAvoidanceBonus deserialize(FriendlyByteBuf buf) {
      float chance = buf.readFloat();
      DamageAvoidanceBonus bonus = new DamageAvoidanceBonus(chance);
      bonus.playerMultiplier = NetworkHelper.readLivingMultiplier(buf);
      bonus.attackerMultiplier = NetworkHelper.readLivingMultiplier(buf);
      bonus.playerCondition = NetworkHelper.readLivingCondition(buf);
      bonus.damageCondition = NetworkHelper.readDamageCondition(buf);
      bonus.attackerCondition = NetworkHelper.readLivingCondition(buf);
      return bonus;
    }

    @Override
    public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
      if (!(bonus instanceof DamageAvoidanceBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      buf.writeFloat(aBonus.chance);
      NetworkHelper.writeLivingMultiplier(buf, aBonus.playerMultiplier);
      NetworkHelper.writeLivingMultiplier(buf, aBonus.attackerMultiplier);
      NetworkHelper.writeLivingCondition(buf, aBonus.playerCondition);
      NetworkHelper.writeDamageCondition(buf, aBonus.damageCondition);
      NetworkHelper.writeLivingCondition(buf, aBonus.attackerCondition);
    }

    @Override
    public SkillBonus<?> createDefaultInstance() {
      return new DamageAvoidanceBonus(0.1f).setDamageCondition(new MeleeDamageCondition());
    }
  }
}
