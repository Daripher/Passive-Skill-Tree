package daripher.skilltree.skill.bonus.player;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.condition.damage.DamageCondition;
import daripher.skilltree.skill.bonus.condition.damage.NoneDamageCondition;
import daripher.skilltree.skill.bonus.condition.living.LivingCondition;
import daripher.skilltree.skill.bonus.condition.living.NoneLivingCondition;
import daripher.skilltree.skill.bonus.multiplier.LivingMultiplier;
import daripher.skilltree.skill.bonus.multiplier.NoneLivingMultiplier;
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
import java.util.Objects;
import java.util.function.Consumer;

public final class CritChanceBonus implements SkillBonus<CritChanceBonus> {
  private float chance;
  private @Nonnull LivingMultiplier playerMultiplier = NoneLivingMultiplier.INSTANCE;
  private @Nonnull LivingMultiplier targetMultiplier = NoneLivingMultiplier.INSTANCE;
  private @Nonnull LivingCondition playerCondition = NoneLivingCondition.INSTANCE;
  private @Nonnull LivingCondition targetCondition = NoneLivingCondition.INSTANCE;
  private @Nonnull DamageCondition damageCondition = NoneDamageCondition.INSTANCE;

  public CritChanceBonus(float chance) {
    this.chance = chance;
  }

  public float getChanceBonus(DamageSource source, Player attacker, LivingEntity target) {
    if (!damageCondition.met(source)) return 0f;
    if (!playerCondition.met(attacker)) return 0f;
    if (!targetCondition.met(target)) return 0f;
    return chance * playerMultiplier.getValue(attacker);
  }

  @Override
  public SkillBonus.Serializer getSerializer() {
    return PSTSkillBonuses.CRIT_CHANCE.get();
  }

  @Override
  public CritChanceBonus copy() {
    CritChanceBonus bonus = new CritChanceBonus(chance);
    bonus.playerMultiplier = this.playerMultiplier;
    bonus.targetMultiplier = this.targetMultiplier;
    bonus.playerCondition = this.playerCondition;
    bonus.damageCondition = this.damageCondition;
    bonus.targetCondition = this.targetCondition;
    return bonus;
  }

  @Override
  public CritChanceBonus multiply(double multiplier) {
    chance *= (float) multiplier;
    return this;
  }

  @Override
  public boolean canMerge(SkillBonus<?> other) {
    if (!(other instanceof CritChanceBonus otherBonus)) return false;
    if (!Objects.equals(otherBonus.playerMultiplier, this.playerMultiplier)) return false;
    if (!Objects.equals(otherBonus.targetMultiplier, this.targetMultiplier)) return false;
    if (!Objects.equals(otherBonus.playerCondition, this.playerCondition)) return false;
    if (!Objects.equals(otherBonus.damageCondition, this.damageCondition)) return false;
    return Objects.equals(otherBonus.targetCondition, this.targetCondition);
  }

  @Override
  public SkillBonus<CritChanceBonus> merge(SkillBonus<?> other) {
    if (!(other instanceof CritChanceBonus otherBonus)) {
      throw new IllegalArgumentException();
    }
    CritChanceBonus mergedBonus = new CritChanceBonus(otherBonus.chance + this.chance);
    mergedBonus.playerMultiplier = this.playerMultiplier;
    mergedBonus.targetMultiplier = this.targetMultiplier;
    mergedBonus.playerCondition = this.playerCondition;
    mergedBonus.damageCondition = this.damageCondition;
    mergedBonus.targetCondition = this.targetCondition;
    return mergedBonus;
  }

  @Override
  public MutableComponent getTooltip() {
    AttributeModifier.Operation operation = AttributeModifier.Operation.MULTIPLY_BASE;
    MutableComponent tooltip;
    if (damageCondition == NoneDamageCondition.INSTANCE) {
      tooltip = TooltipHelper.getSkillBonusTooltip(getDescriptionId(), chance, operation);
    } else {
      tooltip = Component.translatable(getDescriptionId() + ".damage", damageCondition.getTooltip("type"));
      tooltip = TooltipHelper.getSkillBonusTooltip(tooltip, chance, operation);
    }
    tooltip = playerMultiplier.getTooltip(tooltip, Target.PLAYER);
    tooltip = targetMultiplier.getTooltip(tooltip, Target.ENEMY);
    tooltip = playerCondition.getTooltip(tooltip, Target.PLAYER);
    tooltip = targetCondition.getTooltip(tooltip, Target.ENEMY);
    return tooltip.withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
  }

  @Override
  public boolean isPositive() {
    return chance > 0;
  }

  @Override
  public void addEditorWidgets(SkillTreeEditor editor, int row, Consumer<CritChanceBonus> consumer) {
    editor.addLabel(0, 0, "Chance", ChatFormatting.GOLD);
    editor.increaseHeight(19);
    editor.addNumericTextField(0, 0, 50, 14, chance)
        .setNumericResponder(value -> selectChance(consumer, value));
    editor.increaseHeight(19);
    editor.addLabel(0, 0, "Damage Condition", ChatFormatting.GOLD);
    editor.increaseHeight(19);
    editor.addSelectionMenu(0, 0, 200, damageCondition)
        .setResponder(condition -> selectDamageCondition(consumer, condition));
    editor.increaseHeight(19);
    editor.addLabel(0, 0, "Player Condition", ChatFormatting.GOLD);
    editor.increaseHeight(19);
    editor.addSelectionMenu(0, 0, 200, playerCondition)
        .setResponder(condition -> selectPlayerCondition(editor, consumer, condition))
        .setMenuInitFunc(() -> addPlayerConditionWidgets(editor, consumer));
    editor.increaseHeight(19);
    editor.addLabel(0, 0, "Target Condition", ChatFormatting.GOLD);
    editor.increaseHeight(19);
    editor.addSelectionMenu(0, 0, 200, targetCondition)
        .setResponder(condition -> selectTargetCondition(editor, consumer, condition))
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

  private void addTargetMultiplierWidgets(SkillTreeEditor editor, Consumer<CritChanceBonus> consumer) {
    targetMultiplier.addEditorWidgets(editor, multiplier -> {
      setEnemyMultiplier(multiplier);
      consumer.accept(this.copy());
    });
  }

  private void selectTargetMultiplier(SkillTreeEditor editor, Consumer<CritChanceBonus> consumer, LivingMultiplier multiplier) {
    setEnemyMultiplier(multiplier);
    consumer.accept(this.copy());
    editor.rebuildWidgets();
  }

  private void addPlayerMultiplierWidgets(SkillTreeEditor editor, Consumer<CritChanceBonus> consumer) {
    playerMultiplier.addEditorWidgets(editor, multiplier -> {
      setPlayerMultiplier(multiplier);
      consumer.accept(this.copy());
    });
  }

  private void selectPlayerMultiplier(SkillTreeEditor editor, Consumer<CritChanceBonus> consumer, LivingMultiplier multiplier) {
    setPlayerMultiplier(multiplier);
    consumer.accept(this.copy());
    editor.rebuildWidgets();
  }

  private void addTargetConditionWidgets(SkillTreeEditor editor, Consumer<CritChanceBonus> consumer) {
    targetCondition.addEditorWidgets(editor, c -> {
      setTargetCondition(c);
      consumer.accept(this.copy());
    });
  }

  private void selectTargetCondition(SkillTreeEditor editor, Consumer<CritChanceBonus> consumer, LivingCondition condition) {
    setTargetCondition(condition);
    consumer.accept(this.copy());
    editor.rebuildWidgets();
  }

  private void addPlayerConditionWidgets(SkillTreeEditor editor, Consumer<CritChanceBonus> consumer) {
    playerCondition.addEditorWidgets(editor, c -> {
      setPlayerCondition(c);
      consumer.accept(this.copy());
    });
  }

  private void selectPlayerCondition(SkillTreeEditor editor, Consumer<CritChanceBonus> consumer, LivingCondition condition) {
    setPlayerCondition(condition);
    consumer.accept(this.copy());
    editor.rebuildWidgets();
  }

  private void selectDamageCondition(Consumer<CritChanceBonus> consumer, DamageCondition condition) {
    setDamageCondition(condition);
    consumer.accept(this.copy());
  }

  private void selectChance(Consumer<CritChanceBonus> consumer, Double value) {
    setChance(value.floatValue());
    consumer.accept(this.copy());
  }

  public SkillBonus<?> setPlayerCondition(LivingCondition condition) {
    this.playerCondition = condition;
    return this;
  }

  public SkillBonus<?> setDamageCondition(DamageCondition condition) {
    this.damageCondition = condition;
    return this;
  }

  public SkillBonus<?> setTargetCondition(LivingCondition condition) {
    this.targetCondition = condition;
    return this;
  }

  public SkillBonus<?> setPlayerMultiplier(LivingMultiplier multiplier) {
    this.playerMultiplier = multiplier;
    return this;
  }

  public SkillBonus<?> setEnemyMultiplier(@Nonnull LivingMultiplier multiplier) {
    this.targetMultiplier = multiplier;
    return this;
  }

  public void setChance(float chance) {
    this.chance = chance;
  }

  public static class Serializer implements SkillBonus.Serializer {
    @Override
    public CritChanceBonus deserialize(JsonObject json) throws JsonParseException {
      float amount = SerializationHelper.getElement(json, "chance")
          .getAsFloat();
      CritChanceBonus bonus = new CritChanceBonus(amount);
      bonus.playerMultiplier = SerializationHelper.deserializeLivingMultiplier(json, "player_multiplier");
      bonus.targetMultiplier = SerializationHelper.deserializeLivingMultiplier(json, "enemy_multiplier");
      bonus.playerCondition = SerializationHelper.deserializeLivingCondition(json, "player_condition");
      bonus.damageCondition = SerializationHelper.deserializeDamageCondition(json);
      bonus.targetCondition = SerializationHelper.deserializeLivingCondition(json, "target_condition");
      return bonus;
    }

    @Override
    public void serialize(JsonObject json, SkillBonus<?> bonus) {
      if (!(bonus instanceof CritChanceBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      json.addProperty("chance", aBonus.chance);
      SerializationHelper.serializeLivingMultiplier(json, aBonus.playerMultiplier, "player_multiplier");
      SerializationHelper.serializeLivingMultiplier(json, aBonus.targetMultiplier, "enemy_multiplier");
      SerializationHelper.serializeLivingCondition(json, aBonus.playerCondition, "player_condition");
      SerializationHelper.serializeDamageCondition(json, aBonus.damageCondition);
      SerializationHelper.serializeLivingCondition(json, aBonus.targetCondition, "target_condition");
    }

    @Override
    public CritChanceBonus deserialize(CompoundTag tag) {
      float amount = tag.getFloat("chance");
      CritChanceBonus bonus = new CritChanceBonus(amount);
      bonus.playerMultiplier = SerializationHelper.deserializeLivingMultiplier(tag, "player_multiplier");
      bonus.targetMultiplier = SerializationHelper.deserializeLivingMultiplier(tag, "enemy_multiplier");
      bonus.playerCondition = SerializationHelper.deserializeLivingCondition(tag, "player_condition");
      bonus.damageCondition = SerializationHelper.deserializeDamageCondition(tag);
      bonus.targetCondition = SerializationHelper.deserializeLivingCondition(tag, "target_condition");
      return bonus;
    }

    @Override
    public CompoundTag serialize(SkillBonus<?> bonus) {
      if (!(bonus instanceof CritChanceBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      tag.putFloat("chance", aBonus.chance);
      SerializationHelper.serializeLivingMultiplier(tag, aBonus.playerMultiplier, "player_multiplier");
      SerializationHelper.serializeLivingMultiplier(tag, aBonus.targetMultiplier, "enemy_multiplier");
      SerializationHelper.serializeLivingCondition(tag, aBonus.playerCondition, "player_condition");
      SerializationHelper.serializeDamageCondition(tag, aBonus.damageCondition);
      SerializationHelper.serializeLivingCondition(tag, aBonus.targetCondition, "target_condition");
      return tag;
    }

    @Override
    public CritChanceBonus deserialize(FriendlyByteBuf buf) {
      float amount = buf.readFloat();
      CritChanceBonus bonus = new CritChanceBonus(amount);
      bonus.playerMultiplier = NetworkHelper.readLivingMultiplier(buf);
      bonus.targetMultiplier = NetworkHelper.readLivingMultiplier(buf);
      bonus.playerCondition = NetworkHelper.readLivingCondition(buf);
      bonus.damageCondition = NetworkHelper.readDamageCondition(buf);
      bonus.targetCondition = NetworkHelper.readLivingCondition(buf);
      return bonus;
    }

    @Override
    public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
      if (!(bonus instanceof CritChanceBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      buf.writeFloat(aBonus.chance);
      NetworkHelper.writeLivingMultiplier(buf, aBonus.playerMultiplier);
      NetworkHelper.writeLivingMultiplier(buf, aBonus.targetMultiplier);
      NetworkHelper.writeLivingCondition(buf, aBonus.playerCondition);
      NetworkHelper.writeDamageCondition(buf, aBonus.damageCondition);
      NetworkHelper.writeLivingCondition(buf, aBonus.targetCondition);
    }

    @Override
    public SkillBonus<?> createDefaultInstance() {
      return new CritChanceBonus(0.05f);
    }
  }
}
