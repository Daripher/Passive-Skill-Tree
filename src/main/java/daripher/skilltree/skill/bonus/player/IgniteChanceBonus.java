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
import daripher.skilltree.skill.bonus.multiplier.NoneLivingMultiplier;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

public final class IgniteChanceBonus implements SkillBonus<IgniteChanceBonus> {
  private float chance;
  private int duration;
  private @Nonnull LivingMultiplier playerMultiplier = NoneLivingMultiplier.INSTANCE;
  private @Nonnull LivingCondition playerCondition = NoneLivingCondition.INSTANCE;
  private @Nonnull LivingCondition targetCondition = NoneLivingCondition.INSTANCE;

  public IgniteChanceBonus(float chance, int duration) {
    this.chance = chance;
    this.duration = duration;
  }

  public float getChance(Player attacker, LivingEntity target) {
    if (!playerCondition.met(attacker)) return 0f;
    if (!targetCondition.met(target)) return 0f;
    return chance * playerMultiplier.getValue(attacker);
  }

  @Override
  public SkillBonus.Serializer getSerializer() {
    return PSTSkillBonuses.IGNITE_CHANCE.get();
  }

  @Override
  public IgniteChanceBonus copy() {
    IgniteChanceBonus bonus = new IgniteChanceBonus(chance, duration);
    bonus.playerMultiplier = this.playerMultiplier;
    bonus.playerCondition = this.playerCondition;
    bonus.targetCondition = this.targetCondition;
    return bonus;
  }

  @Override
  public IgniteChanceBonus multiply(double multiplier) {
    chance *= (float) multiplier;
    return this;
  }

  @Override
  public boolean canMerge(SkillBonus<?> other) {
    if (!(other instanceof IgniteChanceBonus otherBonus)) return false;
    if (otherBonus.duration != this.duration) return false;
    if (!Objects.equals(otherBonus.playerMultiplier, this.playerMultiplier)) return false;
    if (!Objects.equals(otherBonus.playerCondition, this.playerCondition)) return false;
    return Objects.equals(otherBonus.targetCondition, this.targetCondition);
  }

  @Override
  public SkillBonus<IgniteChanceBonus> merge(SkillBonus<?> other) {
    if (!(other instanceof IgniteChanceBonus otherBonus)) {
      throw new IllegalArgumentException();
    }
    IgniteChanceBonus mergedBonus =
        new IgniteChanceBonus(otherBonus.chance + this.chance, duration);
    mergedBonus.playerMultiplier = this.playerMultiplier;
    mergedBonus.playerCondition = this.playerCondition;
    mergedBonus.targetCondition = this.targetCondition;
    return mergedBonus;
  }

  @Override
  public MutableComponent getTooltip() {
    String durationDescription = StringUtil.formatTickDuration(duration * 20);
    MutableComponent bonusDescription =
        Component.translatable(getDescriptionId(), durationDescription);
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
      SkillTreeEditorScreen editor, int row, Consumer<IgniteChanceBonus> consumer) {
    editor.addLabel(0, 0, "Chance", ChatFormatting.GOLD);
    editor.addLabel(110, 0, "Duration", ChatFormatting.GOLD);
    editor.shiftWidgets(0, 19);
    editor
        .addNumericTextField(0, 0, 90, 14, chance)
        .setNumericResponder(
            v -> {
              setChance(v.floatValue());
              consumer.accept(this.copy());
            });
    editor
        .addNumericTextField(110, 0, 90, 14, duration)
        .setNumericResponder(
            v -> {
              setDuration(v.intValue());
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

  public int getDuration() {
    return duration;
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

  public void setDuration(int duration) {
    this.duration = duration;
  }

  public static class Serializer implements SkillBonus.Serializer {
    @Override
    public IgniteChanceBonus deserialize(JsonObject json) throws JsonParseException {
      float chance = json.get("chance").getAsFloat();
      int duration = json.get("duration").getAsInt();
      IgniteChanceBonus bonus = new IgniteChanceBonus(chance, duration);
      bonus.playerMultiplier = SerializationHelper.deserializePlayerMultiplier(json);
      bonus.playerCondition =
          SerializationHelper.deserializeLivingCondition(json, "player_condition");
      bonus.targetCondition =
          SerializationHelper.deserializeLivingCondition(json, "target_condition");
      return bonus;
    }

    @Override
    public void serialize(JsonObject json, SkillBonus<?> bonus) {
      if (!(bonus instanceof IgniteChanceBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      json.addProperty("chance", aBonus.chance);
      json.addProperty("duration", aBonus.duration);
      SerializationHelper.serializePlayerMultiplier(json, aBonus.playerMultiplier);
      SerializationHelper.serializeLivingCondition(
          json, aBonus.playerCondition, "player_condition");
      SerializationHelper.serializeLivingCondition(
          json, aBonus.targetCondition, "target_condition");
    }

    @Override
    public IgniteChanceBonus deserialize(CompoundTag tag) {
      float chance = tag.getFloat("chance");
      int duration = tag.getInt("duration");
      IgniteChanceBonus bonus = new IgniteChanceBonus(chance, duration);
      bonus.playerMultiplier = SerializationHelper.deserializePlayerMultiplier(tag);
      bonus.playerCondition =
          SerializationHelper.deserializeLivingCondition(tag, "player_condition");
      bonus.targetCondition =
          SerializationHelper.deserializeLivingCondition(tag, "target_condition");
      return bonus;
    }

    @Override
    public CompoundTag serialize(SkillBonus<?> bonus) {
      if (!(bonus instanceof IgniteChanceBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      tag.putFloat("chance", aBonus.chance);
      tag.putInt("duration", aBonus.duration);
      SerializationHelper.serializePlayerMultiplier(tag, aBonus.playerMultiplier);
      SerializationHelper.serializeLivingCondition(tag, aBonus.playerCondition, "player_condition");
      SerializationHelper.serializeLivingCondition(tag, aBonus.targetCondition, "target_condition");
      return tag;
    }

    @Override
    public IgniteChanceBonus deserialize(FriendlyByteBuf buf) {
      float amount = buf.readFloat();
      int duration = buf.readInt();
      IgniteChanceBonus bonus = new IgniteChanceBonus(amount, duration);
      bonus.playerMultiplier = NetworkHelper.readBonusMultiplier(buf);
      bonus.playerCondition = NetworkHelper.readLivingCondition(buf);
      bonus.targetCondition = NetworkHelper.readLivingCondition(buf);
      return bonus;
    }

    @Override
    public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
      if (!(bonus instanceof IgniteChanceBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      buf.writeFloat(aBonus.chance);
      buf.writeInt(aBonus.duration);
      NetworkHelper.writeBonusMultiplier(buf, aBonus.playerMultiplier);
      NetworkHelper.writeLivingCondition(buf, aBonus.playerCondition);
      NetworkHelper.writeLivingCondition(buf, aBonus.targetCondition);
    }

    @Override
    public SkillBonus<?> createDefaultInstance() {
      return new IgniteChanceBonus(0.05f, 5);
    }
  }
}
