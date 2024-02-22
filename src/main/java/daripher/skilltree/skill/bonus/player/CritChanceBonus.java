package daripher.skilltree.skill.bonus.player;

import com.google.gson.*;
import daripher.skilltree.client.screen.SkillTreeEditorScreen;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTDamageConditions;
import daripher.skilltree.init.PSTLivingConditions;
import daripher.skilltree.init.PSTLivingMultipliers;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.condition.damage.DamageCondition;
import daripher.skilltree.skill.bonus.condition.damage.NoneDamageCondition;
import daripher.skilltree.skill.bonus.condition.living.LivingCondition;
import daripher.skilltree.skill.bonus.condition.living.NoneLivingCondition;
import daripher.skilltree.skill.bonus.multiplier.LivingMultiplier;
import daripher.skilltree.skill.bonus.multiplier.NoneLivingMultiplier;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

public final class CritChanceBonus implements SkillBonus<CritChanceBonus> {
  private float chance;
  private @Nonnull LivingMultiplier playerMultiplier = NoneLivingMultiplier.INSTANCE;
  private @Nonnull LivingMultiplier enemyMultiplier = NoneLivingMultiplier.INSTANCE;
  private @Nonnull LivingCondition playerCondition = NoneLivingCondition.INSTANCE;
  private @Nonnull LivingCondition enemyCondition = NoneLivingCondition.INSTANCE;
  private @Nonnull DamageCondition damageCondition = NoneDamageCondition.INSTANCE;

  public CritChanceBonus(float chance) {
    this.chance = chance;
  }

  public float getChanceBonus(DamageSource source, Player attacker, LivingEntity target) {
    if (!damageCondition.met(source)) return 0f;
    if (!playerCondition.met(attacker)) return 0f;
    if (!enemyCondition.met(target)) return 0f;
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
    bonus.enemyMultiplier = this.enemyMultiplier;
    bonus.playerCondition = this.playerCondition;
    bonus.damageCondition = this.damageCondition;
    bonus.enemyCondition = this.enemyCondition;
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
    if (!Objects.equals(otherBonus.enemyMultiplier, this.enemyMultiplier)) return false;
    if (!Objects.equals(otherBonus.playerCondition, this.playerCondition)) return false;
    if (!Objects.equals(otherBonus.damageCondition, this.damageCondition)) return false;
    return Objects.equals(otherBonus.enemyCondition, this.enemyCondition);
  }

  @Override
  public SkillBonus<CritChanceBonus> merge(SkillBonus<?> other) {
    if (!(other instanceof CritChanceBonus otherBonus)) {
      throw new IllegalArgumentException();
    }
    CritChanceBonus mergedBonus = new CritChanceBonus(otherBonus.chance + this.chance);
    mergedBonus.playerMultiplier = this.playerMultiplier;
    mergedBonus.enemyMultiplier = this.enemyMultiplier;
    mergedBonus.playerCondition = this.playerCondition;
    mergedBonus.damageCondition = this.damageCondition;
    mergedBonus.enemyCondition = this.enemyCondition;
    return mergedBonus;
  }

  @Override
  public MutableComponent getTooltip() {
    MutableComponent tooltip =
        TooltipHelper.getSkillBonusTooltip(
            getDescriptionId(), chance, AttributeModifier.Operation.MULTIPLY_BASE);
    tooltip = playerMultiplier.getTooltip(tooltip, Target.PLAYER);
    tooltip = enemyMultiplier.getTooltip(tooltip, Target.ENEMY);
    tooltip = playerCondition.getTooltip(tooltip, "you");
    tooltip = enemyCondition.getTooltip(tooltip, "target");
    return tooltip.withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
  }

  @Override
  public boolean isPositive() {
    return chance > 0;
  }

  @Override
  public void addEditorWidgets(
      SkillTreeEditorScreen editor, int row, Consumer<CritChanceBonus> consumer) {
    editor.addLabel(0, 0, "Chance", ChatFormatting.GOLD);
    editor.shiftWidgets(0, 19);
    editor
        .addNumericTextField(0, 0, 50, 14, chance)
        .setNumericResponder(
            v -> {
              setChance(v.floatValue());
              consumer.accept(this.copy());
            });
    editor.shiftWidgets(0, 19);
    editor.addLabel(0, 0, "Damage Condition", ChatFormatting.GOLD);
    editor.shiftWidgets(0, 19);
    editor
        .addDropDownList(0, 0, 200, 14, 10, damageCondition, PSTDamageConditions.conditionsList())
        .setToNameFunc(c -> Component.literal(PSTDamageConditions.getName(c)))
        .setResponder(
            c -> {
              setDamageCondition(c);
              consumer.accept(this.copy());
            });
    editor.shiftWidgets(0, 19);
    editor.addLabel(0, 0, "Player Condition", ChatFormatting.GOLD);
    editor.shiftWidgets(0, 19);
    editor
        .addDropDownList(0, 0, 200, 14, 10, playerCondition, PSTLivingConditions.conditionsList())
        .setToNameFunc(c -> Component.literal(PSTLivingConditions.getName(c)))
        .setResponder(
            c -> {
              setPlayerCondition(c);
              consumer.accept(this.copy());
              editor.rebuildWidgets();
            });
    editor.shiftWidgets(0, 19);
    playerCondition.addEditorWidgets(
        editor,
        c -> {
          setPlayerCondition(c);
          consumer.accept(this.copy());
        });
    editor.addLabel(0, 0, "Target Condition", ChatFormatting.GOLD);
    editor.shiftWidgets(0, 19);
    editor
        .addDropDownList(0, 0, 200, 14, 10, enemyCondition, PSTLivingConditions.conditionsList())
        .setToNameFunc(c -> Component.literal(PSTLivingConditions.getName(c)))
        .setResponder(
            c -> {
              setTargetCondition(c);
              consumer.accept(this.copy());
              editor.rebuildWidgets();
            });
    editor.shiftWidgets(0, 19);
    enemyCondition.addEditorWidgets(
        editor,
        c -> {
          setTargetCondition(c);
          consumer.accept(this.copy());
        });
    editor.addLabel(0, 0, "Player Multiplier", ChatFormatting.GOLD);
    editor.shiftWidgets(0, 19);
    editor
        .addDropDownList(0, 0, 200, 14, 10, playerMultiplier, PSTLivingMultipliers.multiplierList())
        .setToNameFunc(m -> Component.literal(PSTLivingMultipliers.getName(m)))
        .setResponder(
            m -> {
              setPlayerMultiplier(m);
              consumer.accept(this.copy());
              editor.rebuildWidgets();
            });
    editor.shiftWidgets(0, 19);
    playerMultiplier.addEditorWidgets(
        editor,
        m -> {
          setPlayerMultiplier(m);
          consumer.accept(this.copy());
        });
    editor.addLabel(0, 0, "Enemy Multiplier", ChatFormatting.GOLD);
    editor.shiftWidgets(0, 19);
    editor
        .addDropDownList(0, 0, 200, 14, 10, enemyMultiplier, PSTLivingMultipliers.multiplierList())
        .setToNameFunc(m -> Component.literal(PSTLivingMultipliers.getName(m)))
        .setResponder(
            m -> {
              setEnemyMultiplier(m);
              consumer.accept(this.copy());
              editor.rebuildWidgets();
            });
    editor.shiftWidgets(0, 19);
    enemyMultiplier.addEditorWidgets(
        editor,
        m -> {
          setEnemyMultiplier(m);
          consumer.accept(this.copy());
        });
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
    this.enemyCondition = condition;
    return this;
  }

  public SkillBonus<?> setPlayerMultiplier(LivingMultiplier multiplier) {
    this.playerMultiplier = multiplier;
    return this;
  }

  public SkillBonus<?> setEnemyMultiplier(@Nonnull LivingMultiplier multiplier) {
    this.enemyMultiplier = multiplier;
    return this;
  }

  public void setChance(float chance) {
    this.chance = chance;
  }

  public static class Serializer implements SkillBonus.Serializer {
    @Override
    public CritChanceBonus deserialize(JsonObject json) throws JsonParseException {
      float amount = json.get("chance").getAsFloat();
      CritChanceBonus bonus = new CritChanceBonus(amount);
      bonus.playerMultiplier =
          SerializationHelper.deserializeLivingMultiplier(json, "player_multiplier");
      bonus.enemyMultiplier =
          SerializationHelper.deserializeLivingMultiplier(json, "enemy_multiplier");
      bonus.playerCondition =
          SerializationHelper.deserializeLivingCondition(json, "player_condition");
      bonus.damageCondition = SerializationHelper.deserializeDamageCondition(json);
      bonus.enemyCondition =
          SerializationHelper.deserializeLivingCondition(json, "target_condition");
      return bonus;
    }

    @Override
    public void serialize(JsonObject json, SkillBonus<?> bonus) {
      if (!(bonus instanceof CritChanceBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      json.addProperty("chance", aBonus.chance);
      SerializationHelper.serializeLivingMultiplier(
          json, aBonus.playerMultiplier, "player_multiplier");
      SerializationHelper.serializeLivingMultiplier(
          json, aBonus.enemyMultiplier, "enemy_multiplier");
      SerializationHelper.serializeLivingCondition(
          json, aBonus.playerCondition, "player_condition");
      SerializationHelper.serializeDamageCondition(json, aBonus.damageCondition);
      SerializationHelper.serializeLivingCondition(json, aBonus.enemyCondition, "target_condition");
    }

    @Override
    public CritChanceBonus deserialize(CompoundTag tag) {
      float amount = tag.getFloat("chance");
      CritChanceBonus bonus = new CritChanceBonus(amount);
      bonus.playerMultiplier =
          SerializationHelper.deserializeLivingMultiplier(tag, "player_multiplier");
      bonus.enemyMultiplier =
          SerializationHelper.deserializeLivingMultiplier(tag, "enemy_multiplier");
      bonus.playerCondition =
          SerializationHelper.deserializeLivingCondition(tag, "player_condition");
      bonus.damageCondition = SerializationHelper.deserializeDamageCondition(tag);
      bonus.enemyCondition =
          SerializationHelper.deserializeLivingCondition(tag, "target_condition");
      return bonus;
    }

    @Override
    public CompoundTag serialize(SkillBonus<?> bonus) {
      if (!(bonus instanceof CritChanceBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      tag.putFloat("chance", aBonus.chance);
      SerializationHelper.serializeLivingMultiplier(
          tag, aBonus.playerMultiplier, "player_multiplier");
      SerializationHelper.serializeLivingMultiplier(
          tag, aBonus.enemyMultiplier, "enemy_multiplier");
      SerializationHelper.serializeLivingCondition(tag, aBonus.playerCondition, "player_condition");
      SerializationHelper.serializeDamageCondition(tag, aBonus.damageCondition);
      SerializationHelper.serializeLivingCondition(tag, aBonus.enemyCondition, "target_condition");
      return tag;
    }

    @Override
    public CritChanceBonus deserialize(FriendlyByteBuf buf) {
      float amount = buf.readFloat();
      CritChanceBonus bonus = new CritChanceBonus(amount);
      bonus.playerMultiplier = NetworkHelper.readLivingMultiplier(buf);
      bonus.enemyMultiplier = NetworkHelper.readLivingMultiplier(buf);
      bonus.playerCondition = NetworkHelper.readLivingCondition(buf);
      bonus.damageCondition = NetworkHelper.readDamageCondition(buf);
      bonus.enemyCondition = NetworkHelper.readLivingCondition(buf);
      return bonus;
    }

    @Override
    public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
      if (!(bonus instanceof CritChanceBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      buf.writeFloat(aBonus.chance);
      NetworkHelper.writeLivingMultiplier(buf, aBonus.playerMultiplier);
      NetworkHelper.writeLivingMultiplier(buf, aBonus.enemyMultiplier);
      NetworkHelper.writeLivingCondition(buf, aBonus.playerCondition);
      NetworkHelper.writeDamageCondition(buf, aBonus.damageCondition);
      NetworkHelper.writeLivingCondition(buf, aBonus.enemyCondition);
    }

    @Override
    public SkillBonus<?> createDefaultInstance() {
      return new CritChanceBonus(0.05f);
    }
  }
}
