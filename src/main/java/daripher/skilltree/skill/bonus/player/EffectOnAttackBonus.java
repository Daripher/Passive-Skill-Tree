package daripher.skilltree.skill.bonus.player;

import com.google.gson.*;
import daripher.skilltree.client.screen.SkillTreeEditorScreen;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.data.SerializationHelper;
import daripher.skilltree.init.PSTLivingConditions;
import daripher.skilltree.init.PSTLivingMultipliers;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.condition.living.LivingCondition;
import daripher.skilltree.skill.bonus.condition.living.NoneLivingCondition;
import daripher.skilltree.skill.bonus.multiplier.LivingMultiplier;
import daripher.skilltree.skill.bonus.multiplier.NoneMultiplier;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.StringUtil;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;

public final class EffectOnAttackBonus implements SkillBonus<EffectOnAttackBonus> {
  private MobEffectInstance effect;
  private float chance;
  private @Nonnull LivingMultiplier playerMultiplier = new NoneMultiplier();
  private @Nonnull LivingCondition playerCondition = new NoneLivingCondition();
  private @Nonnull LivingCondition targetCondition = new NoneLivingCondition();

  public EffectOnAttackBonus(float chance, MobEffectInstance effect) {
    this.chance = chance;
    this.effect = effect;
  }

  public float getChance(Player attacker, LivingEntity target) {
    if (!playerCondition.met(attacker)) return 0f;
    if (!targetCondition.met(target)) return 0f;
    return chance * playerMultiplier.getValue(attacker);
  }

  @Override
  public SkillBonus.Serializer getSerializer() {
    return PSTSkillBonuses.EFFECT_ON_ATTACK.get();
  }

  @Override
  public EffectOnAttackBonus copy() {
    EffectOnAttackBonus bonus = new EffectOnAttackBonus(chance, effect);
    bonus.playerMultiplier = this.playerMultiplier;
    bonus.playerCondition = this.playerCondition;
    bonus.targetCondition = this.targetCondition;
    return bonus;
  }

  @Override
  public EffectOnAttackBonus multiply(double multiplier) {
    chance *= (float) multiplier;
    return this;
  }

  @Override
  public boolean canMerge(SkillBonus<?> other) {
    if (!(other instanceof EffectOnAttackBonus otherBonus)) return false;
    if (!Objects.equals(otherBonus.effect, this.effect)) return false;
    if (!Objects.equals(otherBonus.playerMultiplier, this.playerMultiplier)) return false;
    if (!Objects.equals(otherBonus.playerCondition, this.playerCondition)) return false;
    return Objects.equals(otherBonus.targetCondition, this.targetCondition);
  }

  @Override
  public SkillBonus<EffectOnAttackBonus> merge(SkillBonus<?> other) {
    if (!(other instanceof EffectOnAttackBonus otherBonus)) {
      throw new IllegalArgumentException();
    }
    EffectOnAttackBonus mergedBonus =
        new EffectOnAttackBonus(otherBonus.chance + this.chance, effect);
    mergedBonus.playerMultiplier = this.playerMultiplier;
    mergedBonus.playerCondition = this.playerCondition;
    mergedBonus.targetCondition = this.targetCondition;
    return mergedBonus;
  }

  @Override
  public MutableComponent getTooltip() {
    Component effectDescription = TooltipHelper.getEffectInstanceTooltip(effect);
    String durationDescription = StringUtil.formatTickDuration(effect.getDuration());
    Component bonusDescription =
        Component.translatable(getDescriptionId(), effectDescription, durationDescription);
    MutableComponent tooltip =
        TooltipHelper.getSkillBonusTooltip(
            bonusDescription, chance, AttributeModifier.Operation.MULTIPLY_BASE);
    tooltip = playerMultiplier.getTooltip(tooltip);
    tooltip = playerCondition.getTooltip(tooltip, "you");
    tooltip = targetCondition.getTooltip(tooltip, "target");
    return tooltip.withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
  }

  @Override
  public boolean isPositive() {
    return chance > 0;
  }

  @Override
  public void addEditorWidgets(
      SkillTreeEditorScreen editor, int row, Consumer<EffectOnAttackBonus> consumer) {
    editor.addLabel(0, 0, "Effect", ChatFormatting.GOLD);
    editor.addLabel(150, 0, "Chance", ChatFormatting.GOLD);
    editor.shiftWidgets(0, 19);
    editor
        .addDropDownList(
            0, 0, 145, 14, 10, effect.getEffect(), ForgeRegistries.MOB_EFFECTS.getValues())
        .setToNameFunc(a -> Component.translatable(a.getDescriptionId()))
        .setResponder(
            e -> {
              setEffect(e);
              consumer.accept(this);
            });
    editor
        .addNumericTextField(150, 0, 50, 14, chance)
        .setNumericResponder(
            v -> {
              setChance(v.floatValue());
              consumer.accept(this.copy());
            });
    editor.shiftWidgets(0, 19);
    editor.addLabel(0, 0, "Duration", ChatFormatting.GOLD);
    editor.addLabel(55, 0, "Amplifier", ChatFormatting.GOLD);
    editor.shiftWidgets(0, 19);
    editor
        .addNumericTextField(0, 0, 50, 14, effect.getDuration())
        .setNumericFilter(v -> v >= 0)
        .setNumericResponder(
            v -> {
              setDuration(v.intValue());
              consumer.accept(this.copy());
            });
    editor
        .addNumericTextField(55, 0, 50, 14, effect.getAmplifier())
        .setNumericFilter(v -> v >= 0)
        .setNumericResponder(
            v -> {
              setAmplifier(v.intValue());
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
        .addDropDownList(0, 0, 200, 14, 10, targetCondition, PSTLivingConditions.conditionsList())
        .setToNameFunc(c -> Component.literal(PSTLivingConditions.getName(c)))
        .setResponder(
            c -> {
              setTargetCondition(c);
              consumer.accept(this.copy());
              editor.rebuildWidgets();
            });
    editor.shiftWidgets(0, 19);
    targetCondition.addEditorWidgets(
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
  }

  public MobEffectInstance getEffect() {
    return effect;
  }

  public SkillBonus<?> setPlayerCondition(LivingCondition condition) {
    this.playerCondition = condition;
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

  public void setChance(float chance) {
    this.chance = chance;
  }

  public void setEffect(MobEffect effect) {
    this.effect =
        new MobEffectInstance(effect, this.effect.getDuration(), this.effect.getAmplifier());
  }

  public void setDuration(int duration) {
    this.effect =
        new MobEffectInstance(this.effect.getEffect(), duration, this.effect.getAmplifier());
  }

  public void setAmplifier(int amplifier) {
    this.effect =
        new MobEffectInstance(this.effect.getEffect(), this.effect.getDuration(), amplifier);
  }

  public static class Serializer implements SkillBonus.Serializer {
    @Override
    public EffectOnAttackBonus deserialize(JsonObject json) throws JsonParseException {
      float chance = json.get("chance").getAsFloat();
      MobEffectInstance effect = SerializationHelper.deserializeEffectInstance(json);
      EffectOnAttackBonus bonus = new EffectOnAttackBonus(chance, effect);
      bonus.playerMultiplier = SerializationHelper.deserializePlayerMultiplier(json);
      bonus.playerCondition =
          SerializationHelper.deserializeLivingCondition(json, "player_condition");
      bonus.targetCondition =
          SerializationHelper.deserializeLivingCondition(json, "target_condition");
      return bonus;
    }

    @Override
    public void serialize(JsonObject json, SkillBonus<?> bonus) {
      if (!(bonus instanceof EffectOnAttackBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      json.addProperty("chance", aBonus.chance);
      SerializationHelper.serializeEffectInstance(json, aBonus.effect);
      SerializationHelper.serializePlayerMultiplier(json, aBonus.playerMultiplier);
      SerializationHelper.serializeLivingCondition(
          json, aBonus.playerCondition, "player_condition");
      SerializationHelper.serializeLivingCondition(
          json, aBonus.targetCondition, "target_condition");
    }

    @Override
    public EffectOnAttackBonus deserialize(CompoundTag tag) {
      float chance = tag.getFloat("chance");
      MobEffectInstance effect = SerializationHelper.deserializeEffectInstance(tag);
      EffectOnAttackBonus bonus = new EffectOnAttackBonus(chance, effect);
      bonus.playerMultiplier = SerializationHelper.deserializePlayerMultiplier(tag);
      bonus.playerCondition =
          SerializationHelper.deserializeLivingCondition(tag, "player_condition");
      bonus.targetCondition =
          SerializationHelper.deserializeLivingCondition(tag, "target_condition");
      return bonus;
    }

    @Override
    public CompoundTag serialize(SkillBonus<?> bonus) {
      if (!(bonus instanceof EffectOnAttackBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      tag.putFloat("chance", aBonus.chance);
      SerializationHelper.serializeEffectInstance(tag, aBonus.effect);
      SerializationHelper.serializePlayerMultiplier(tag, aBonus.playerMultiplier);
      SerializationHelper.serializeLivingCondition(tag, aBonus.playerCondition, "player_condition");
      SerializationHelper.serializeLivingCondition(tag, aBonus.targetCondition, "target_condition");
      return tag;
    }

    @Override
    public EffectOnAttackBonus deserialize(FriendlyByteBuf buf) {
      float amount = buf.readFloat();
      MobEffectInstance effect = NetworkHelper.readEffectInstance(buf);
      EffectOnAttackBonus bonus = new EffectOnAttackBonus(amount, effect);
      bonus.playerMultiplier = NetworkHelper.readBonusMultiplier(buf);
      bonus.playerCondition = NetworkHelper.readLivingCondition(buf);
      bonus.targetCondition = NetworkHelper.readLivingCondition(buf);
      return bonus;
    }

    @Override
    public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
      if (!(bonus instanceof EffectOnAttackBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      buf.writeFloat(aBonus.chance);
      NetworkHelper.writeEffectInstance(buf, aBonus.effect);
      NetworkHelper.writeBonusMultiplier(buf, aBonus.playerMultiplier);
      NetworkHelper.writeLivingCondition(buf, aBonus.playerCondition);
      NetworkHelper.writeLivingCondition(buf, aBonus.targetCondition);
    }

    @Override
    public SkillBonus<?> createDefaultInstance() {
      return new EffectOnAttackBonus(0.05f, new MobEffectInstance(MobEffects.POISON, 100));
    }
  }
}
