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
import daripher.skilltree.skill.bonus.predicate.damage.MagicDamageCondition;
import daripher.skilltree.skill.bonus.predicate.damage.MeleeDamageCondition;
import daripher.skilltree.skill.bonus.predicate.living.LivingEntityPredicate;
import daripher.skilltree.skill.bonus.predicate.living.NoneLivingEntityPredicate;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Consumer;

public final class DamageConversionBonus implements SkillBonus<DamageConversionBonus> {
  private float amount;
  private @Nonnull LivingMultiplier playerMultiplier = NoneLivingMultiplier.INSTANCE;
  private @Nonnull LivingMultiplier targetMultiplier = NoneLivingMultiplier.INSTANCE;
  private @Nonnull LivingEntityPredicate playerCondition = NoneLivingEntityPredicate.INSTANCE;
  private @Nonnull LivingEntityPredicate targetCondition = NoneLivingEntityPredicate.INSTANCE;
  private @Nonnull DamageCondition originalDamageCondition;
  private @Nonnull DamageCondition resultDamageCondition;

  public DamageConversionBonus(
      float amount,
      @NotNull DamageCondition originalDamageCondition,
      @NotNull DamageCondition resultDamageCondition) {
    this.amount = amount;
    this.originalDamageCondition = originalDamageCondition;
    this.resultDamageCondition = resultDamageCondition;
  }

  public float getConversionRate(DamageSource source, Player player, LivingEntity target) {
    if (!originalDamageCondition.met(source)) {
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
    return PSTSkillBonuses.DAMAGE_CONVERSION.get();
  }

  @Override
  public DamageConversionBonus copy() {
    DamageConversionBonus bonus =
        new DamageConversionBonus(amount, originalDamageCondition, resultDamageCondition);
    bonus.playerMultiplier = this.playerMultiplier;
    bonus.targetMultiplier = this.targetMultiplier;
    bonus.playerCondition = this.playerCondition;
    bonus.targetCondition = this.targetCondition;
    return bonus;
  }

  @Override
  public DamageConversionBonus multiply(double multiplier) {
    amount *= (float) multiplier;
    return this;
  }

  @Override
  public boolean canMerge(SkillBonus<?> other) {
    if (!(other instanceof DamageConversionBonus otherBonus)) {
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
    if (!Objects.equals(otherBonus.originalDamageCondition, this.originalDamageCondition)) {
        return false;
    }
    if (!Objects.equals(otherBonus.resultDamageCondition, this.resultDamageCondition)) {
        return false;
    }
    return Objects.equals(otherBonus.targetCondition, this.targetCondition);
  }

  @Override
  public SkillBonus<DamageConversionBonus> merge(SkillBonus<?> other) {
    if (!(other instanceof DamageConversionBonus otherBonus)) {
      throw new IllegalArgumentException();
    }
    float mergedAmount = otherBonus.amount + this.amount;
    DamageConversionBonus mergedBonus =
        new DamageConversionBonus(mergedAmount, originalDamageCondition, resultDamageCondition);
    mergedBonus.playerMultiplier = this.playerMultiplier;
    mergedBonus.targetMultiplier = this.targetMultiplier;
    mergedBonus.playerCondition = this.playerCondition;
    mergedBonus.targetCondition = this.targetCondition;
    return mergedBonus;
  }

  @Override
  public MutableComponent getTooltip() {
    String formattedAmount = TooltipHelper.formatNumber(amount * 100);
    MutableComponent tooltip =
        Component.translatable(
            getDescriptionId(),
            formattedAmount,
            originalDamageCondition.getTooltip(),
            resultDamageCondition.getTooltip());
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
  public void addEditorWidgets(
      SkillTreeEditor editor, int row, Consumer<DamageConversionBonus> consumer) {
    editor.addLabel(0, 0, "Conversion", ChatFormatting.GOLD);
    editor.increaseHeight(19);
    editor
        .addNumericTextField(0, 0, 50, 14, amount)
        .setNumericResponder(value -> selectAmount(consumer, value));
    editor.increaseHeight(19);
    editor.addLabel(0, 0, "Original Damage", ChatFormatting.GOLD);
    editor.increaseHeight(19);
    editor
        .addSelectionMenu(0, 0, 200, originalDamageCondition)
        .setResponder(condition -> selectDamageCondition(consumer, condition));
    editor.increaseHeight(19);
    editor.addLabel(0, 0, "Result Damage", ChatFormatting.GOLD);
    editor.increaseHeight(19);
    editor
        .addSelectionMenu(0, 0, 200, resultDamageCondition)
        .setResponder(condition -> selectResultDamageCondition(consumer, condition));
    editor.increaseHeight(19);
    editor.addLabel(0, 0, "Player Condition", ChatFormatting.GOLD);
    editor.increaseHeight(19);
    editor
        .addSelectionMenu(0, 0, 200, playerCondition)
        .setResponder(condition -> selectPlayerCondition(editor, consumer, condition))
        .setMenuInitFunc(() -> addPlayerConditionWidgets(editor, consumer));
    editor.increaseHeight(19);
    editor.addLabel(0, 0, "Target Condition", ChatFormatting.GOLD);
    editor.increaseHeight(19);
    editor
        .addSelectionMenu(0, 0, 200, targetCondition)
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
    editor.addLabel(0, 0, "Target Multiplier", ChatFormatting.GOLD);
    editor.increaseHeight(19);
    editor
        .addSelectionMenu(0, 0, 200, targetMultiplier)
        .setResponder(multiplier -> selectTargetMultiplier(editor, consumer, multiplier))
        .setMenuInitFunc(() -> addTargetMultiplierWidgets(editor, consumer));
    editor.increaseHeight(19);
  }

  private void selectAmount(Consumer<DamageConversionBonus> consumer, Double value) {
    setAmount(value.floatValue());
    consumer.accept(this.copy());
  }

  private void addTargetMultiplierWidgets(
      SkillTreeEditor editor, Consumer<DamageConversionBonus> consumer) {
    targetMultiplier.addEditorWidgets(
        editor,
        multiplier -> {
          setEnemyMultiplier(multiplier);
          consumer.accept(this.copy());
        });
  }

  private void selectTargetMultiplier(
      SkillTreeEditor editor,
      Consumer<DamageConversionBonus> consumer,
      LivingMultiplier multiplier) {
    setEnemyMultiplier(multiplier);
    consumer.accept(this.copy());
    editor.rebuildWidgets();
  }

  private void addPlayerMultiplierWidgets(
      SkillTreeEditor editor, Consumer<DamageConversionBonus> consumer) {
    playerMultiplier.addEditorWidgets(
        editor,
        multiplier -> {
          setPlayerMultiplier(multiplier);
          consumer.accept(this.copy());
        });
  }

  private void selectPlayerMultiplier(
      SkillTreeEditor editor,
      Consumer<DamageConversionBonus> consumer,
      LivingMultiplier multiplier) {
    setPlayerMultiplier(multiplier);
    consumer.accept(this.copy());
    editor.rebuildWidgets();
  }

  private void addTargetConditionWidgets(
      SkillTreeEditor editor, Consumer<DamageConversionBonus> consumer) {
    targetCondition.addEditorWidgets(
        editor,
        c -> {
          setTargetCondition(c);
          consumer.accept(this.copy());
        });
  }

  private void selectTargetCondition(
      SkillTreeEditor editor, Consumer<DamageConversionBonus> consumer, LivingEntityPredicate condition) {
    setTargetCondition(condition);
    consumer.accept(this.copy());
    editor.rebuildWidgets();
  }

  private void addPlayerConditionWidgets(
      SkillTreeEditor editor, Consumer<DamageConversionBonus> consumer) {
    playerCondition.addEditorWidgets(
        editor,
        c -> {
          setPlayerCondition(c);
          consumer.accept(this.copy());
        });
  }

  private void selectPlayerCondition(
      SkillTreeEditor editor, Consumer<DamageConversionBonus> consumer, LivingEntityPredicate condition) {
    setPlayerCondition(condition);
    consumer.accept(this.copy());
    editor.rebuildWidgets();
  }

  private void selectDamageCondition(
      Consumer<DamageConversionBonus> consumer, DamageCondition condition) {
    setDamageCondition(condition);
    consumer.accept(this.copy());
  }

  private void selectResultDamageCondition(
      Consumer<DamageConversionBonus> consumer, DamageCondition condition) {
    setResultDamageCondition(condition);
    consumer.accept(this.copy());
  }

  public SkillBonus<?> setPlayerCondition(LivingEntityPredicate condition) {
    this.playerCondition = condition;
    return this;
  }

  public SkillBonus<?> setDamageCondition(DamageCondition condition) {
    this.originalDamageCondition = condition;
    return this;
  }

  public SkillBonus<?> setResultDamageCondition(DamageCondition condition) {
    this.resultDamageCondition = condition;
    return this;
  }

  public SkillBonus<?> setTargetCondition(LivingEntityPredicate condition) {
    this.targetCondition = condition;
    return this;
  }

  public SkillBonus<?> setPlayerMultiplier(LivingMultiplier multiplier) {
    this.playerMultiplier = multiplier;
    return this;
  }

  public SkillBonus<?> setEnemyMultiplier(LivingMultiplier multiplier) {
    this.targetMultiplier = multiplier;
    return this;
  }

  public void setAmount(float amount) {
    this.amount = amount;
  }

  @Nonnull
  public DamageCondition getOriginalDamageCondition() {
    return originalDamageCondition;
  }

  @Nonnull
  public DamageCondition getResultDamageCondition() {
    return resultDamageCondition;
  }

  public static class Serializer implements SkillBonus.Serializer {
    @Override
    public DamageConversionBonus deserialize(JsonObject json) throws JsonParseException {
      float amount = SerializationHelper.getElement(json, "amount").getAsFloat();
      DamageCondition originalDamageCondition =
          SerializationHelper.deserializeDamageCondition(json, "original_damage");
      DamageCondition resultDamageCondition =
          SerializationHelper.deserializeDamageCondition(json, "result_damage");
      DamageConversionBonus bonus =
          new DamageConversionBonus(amount, originalDamageCondition, resultDamageCondition);
      bonus.playerMultiplier =
          SerializationHelper.deserializeLivingMultiplier(json, "player_multiplier");
      bonus.targetMultiplier =
          SerializationHelper.deserializeLivingMultiplier(json, "enemy_multiplier");
      bonus.playerCondition =
          SerializationHelper.deserializeLivingCondition(json, "player_condition");
      bonus.targetCondition =
          SerializationHelper.deserializeLivingCondition(json, "target_condition");
      return bonus;
    }

    @Override
    public void serialize(JsonObject json, SkillBonus<?> bonus) {
      if (!(bonus instanceof DamageConversionBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      json.addProperty("amount", aBonus.amount);
      SerializationHelper.serializeDamageCondition(
          json, aBonus.originalDamageCondition, "original_damage");
      SerializationHelper.serializeDamageCondition(
          json, aBonus.resultDamageCondition, "result_damage");
      SerializationHelper.serializeLivingMultiplier(
          json, aBonus.playerMultiplier, "player_multiplier");
      SerializationHelper.serializeLivingMultiplier(
          json, aBonus.targetMultiplier, "enemy_multiplier");
      SerializationHelper.serializeLivingCondition(
          json, aBonus.playerCondition, "player_condition");
      SerializationHelper.serializeLivingCondition(
          json, aBonus.targetCondition, "target_condition");
    }

    @Override
    public DamageConversionBonus deserialize(CompoundTag tag) {
      float amount = tag.getFloat("amount");
      DamageCondition originalDamageCondition =
          SerializationHelper.deserializeDamageCondition(tag, "original_damage");
      DamageCondition resultDamageCondition =
          SerializationHelper.deserializeDamageCondition(tag, "result_damage");
      DamageConversionBonus bonus =
          new DamageConversionBonus(amount, originalDamageCondition, resultDamageCondition);
      bonus.playerMultiplier =
          SerializationHelper.deserializeLivingMultiplier(tag, "player_multiplier");
      bonus.targetMultiplier =
          SerializationHelper.deserializeLivingMultiplier(tag, "enemy_multiplier");
      bonus.playerCondition =
          SerializationHelper.deserializeLivingCondition(tag, "player_condition");
      bonus.targetCondition =
          SerializationHelper.deserializeLivingCondition(tag, "target_condition");
      return bonus;
    }

    @Override
    public CompoundTag serialize(SkillBonus<?> bonus) {
      if (!(bonus instanceof DamageConversionBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      tag.putFloat("amount", aBonus.amount);
      SerializationHelper.serializeDamageCondition(
          tag, aBonus.originalDamageCondition, "original_damage");
      SerializationHelper.serializeDamageCondition(
          tag, aBonus.resultDamageCondition, "result_damage");
      SerializationHelper.serializeLivingMultiplier(
          tag, aBonus.playerMultiplier, "player_multiplier");
      SerializationHelper.serializeLivingMultiplier(
          tag, aBonus.targetMultiplier, "enemy_multiplier");
      SerializationHelper.serializeLivingCondition(tag, aBonus.playerCondition, "player_condition");
      SerializationHelper.serializeLivingCondition(tag, aBonus.targetCondition, "target_condition");
      return tag;
    }

    @Override
    public DamageConversionBonus deserialize(FriendlyByteBuf buf) {
      float amount = buf.readFloat();
      DamageCondition originalDamageCondition = NetworkHelper.readDamageCondition(buf);
      DamageCondition resultDamageCondition = NetworkHelper.readDamageCondition(buf);
      DamageConversionBonus bonus =
          new DamageConversionBonus(amount, originalDamageCondition, resultDamageCondition);
      bonus.playerMultiplier = NetworkHelper.readLivingMultiplier(buf);
      bonus.targetMultiplier = NetworkHelper.readLivingMultiplier(buf);
      bonus.playerCondition = NetworkHelper.readLivingCondition(buf);
      bonus.targetCondition = NetworkHelper.readLivingCondition(buf);
      return bonus;
    }

    @Override
    public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
      if (!(bonus instanceof DamageConversionBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      buf.writeFloat(aBonus.amount);
      NetworkHelper.writeDamageCondition(buf, aBonus.originalDamageCondition);
      NetworkHelper.writeDamageCondition(buf, aBonus.resultDamageCondition);
      NetworkHelper.writeLivingMultiplier(buf, aBonus.playerMultiplier);
      NetworkHelper.writeLivingMultiplier(buf, aBonus.targetMultiplier);
      NetworkHelper.writeLivingCondition(buf, aBonus.playerCondition);
      NetworkHelper.writeLivingCondition(buf, aBonus.targetCondition);
    }

    @Override
    public SkillBonus<?> createDefaultInstance() {
      return new DamageConversionBonus(
          0.05f, new MeleeDamageCondition(), new MagicDamageCondition());
    }
  }
}
