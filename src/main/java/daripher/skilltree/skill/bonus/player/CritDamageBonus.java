package daripher.skilltree.skill.bonus.player;

import com.google.gson.*;
import daripher.skilltree.client.screen.SkillTreeEditorScreen;
import daripher.skilltree.data.SerializationHelper;
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
import daripher.skilltree.skill.bonus.multiplier.NoneMultiplier;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class CritDamageBonus implements SkillBonus<CritDamageBonus> {
  private float amount;
  private @Nonnull LivingMultiplier playerMultiplier = new NoneMultiplier();
  private @Nonnull LivingCondition playerCondition = new NoneLivingCondition();
  private @Nonnull LivingCondition targetCondition = new NoneLivingCondition();
  private @Nonnull DamageCondition damageCondition = new NoneDamageCondition();

  public CritDamageBonus(float amount) {
    this.amount = amount;
  }

  public float getDamageBonus(DamageSource source, Player attacker, LivingEntity target) {
    if (!damageCondition.met(source)) return 0f;
    if (!playerCondition.met(attacker)) return 0f;
    if (!targetCondition.met(target)) return 0f;
    return amount * playerMultiplier.getValue(attacker);
  }

  @Override
  public SkillBonus.Serializer getSerializer() {
    return PSTSkillBonuses.CRIT_DAMAGE.get();
  }

  @Override
  public CritDamageBonus copy() {
    CritDamageBonus bonus = new CritDamageBonus(amount);
    bonus.playerMultiplier = this.playerMultiplier;
    bonus.playerCondition = this.playerCondition;
    bonus.damageCondition = this.damageCondition;
    bonus.targetCondition = this.targetCondition;
    return bonus;
  }

  @Override
  public CritDamageBonus multiply(double multiplier) {
    amount *= (float) multiplier;
    return this;
  }

  @Override
  public boolean canMerge(SkillBonus<?> other) {
    if (!(other instanceof CritDamageBonus otherBonus)) return false;
    if (!Objects.equals(otherBonus.playerMultiplier, this.playerMultiplier)) return false;
    if (!Objects.equals(otherBonus.playerCondition, this.playerCondition)) return false;
    if (!Objects.equals(otherBonus.damageCondition, this.damageCondition)) return false;
    return Objects.equals(otherBonus.targetCondition, this.targetCondition);
  }

  @Override
  public SkillBonus<CritDamageBonus> merge(SkillBonus<?> other) {
    if (!(other instanceof CritDamageBonus otherBonus)) {
      throw new IllegalArgumentException();
    }
    CritDamageBonus mergedBonus = new CritDamageBonus(otherBonus.amount + this.amount);
    mergedBonus.playerMultiplier = this.playerMultiplier;
    mergedBonus.playerCondition = this.playerCondition;
    mergedBonus.damageCondition = this.damageCondition;
    mergedBonus.targetCondition = this.targetCondition;
    return mergedBonus;
  }

  @Override
  public MutableComponent getTooltip() {
    double visibleAmount = amount * 100;
    if (amount < 0D) visibleAmount *= -1D;
    String operationDescription = amount > 0 ? "plus" : "take";
    Style style = Style.EMPTY.withColor(0x7B7BE5);
    String amountDescription = ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(visibleAmount);
    operationDescription = "attribute.modifier." + operationDescription + ".1";
    MutableComponent bonusDescription = Component.translatable(getDescriptionId());
    bonusDescription = damageCondition.getTooltip(bonusDescription);
    MutableComponent tooltip =
        Component.translatable(operationDescription, amountDescription, bonusDescription);
    tooltip = playerMultiplier.getTooltip(tooltip);
    tooltip = playerCondition.getTooltip(tooltip, "you");
    tooltip = targetCondition.getTooltip(tooltip, "target");
    return tooltip.withStyle(style);
  }

  @Override
  public void addEditorWidgets(
      SkillTreeEditorScreen editor, int row, Consumer<CritDamageBonus> consumer) {
    editor.addLabel(0, 0, "Amount", ChatFormatting.GOLD);
    editor.shiftWidgets(0, 19);
    editor
        .addNumericTextField(0, 0, 50, 14, amount)
        .setNumericResponder(
            v -> {
              setAmount(v.floatValue());
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

  public void setAmount(float amount) {
    this.amount = amount;
  }

  public static class Serializer implements SkillBonus.Serializer {
    @Override
    public CritDamageBonus deserialize(JsonObject json) throws JsonParseException {
      float amount = json.get("chance").getAsFloat();
      CritDamageBonus bonus = new CritDamageBonus(amount);
      bonus.playerMultiplier = SerializationHelper.deserializePlayerMultiplier(json);
      bonus.playerCondition =
          SerializationHelper.deserializeLivingCondition(json, "player_condition");
      bonus.damageCondition = SerializationHelper.deserializeDamageCondition(json);
      bonus.targetCondition =
          SerializationHelper.deserializeLivingCondition(json, "target_condition");
      return bonus;
    }

    @Override
    public void serialize(JsonObject json, SkillBonus<?> bonus) {
      if (!(bonus instanceof CritDamageBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      json.addProperty("chance", aBonus.amount);
      SerializationHelper.serializePlayerMultiplier(json, aBonus.playerMultiplier);
      SerializationHelper.serializeLivingCondition(
          json, aBonus.playerCondition, "player_condition");
      SerializationHelper.serializeDamageCondition(json, aBonus.damageCondition);
      SerializationHelper.serializeLivingCondition(
          json, aBonus.targetCondition, "target_condition");
    }

    @Override
    public CritDamageBonus deserialize(CompoundTag tag) {
      float amount = tag.getFloat("Amount");
      CritDamageBonus bonus = new CritDamageBonus(amount);
      bonus.playerMultiplier = SerializationHelper.deserializePlayerMultiplier(tag);
      bonus.playerCondition =
          SerializationHelper.deserializeLivingCondition(tag, "player_condition");
      bonus.damageCondition = SerializationHelper.deserializeDamageCondition(tag);
      bonus.targetCondition =
          SerializationHelper.deserializeLivingCondition(tag, "target_condition");
      return bonus;
    }

    @Override
    public CompoundTag serialize(SkillBonus<?> bonus) {
      if (!(bonus instanceof CritDamageBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      tag.putFloat("Amount", aBonus.amount);
      SerializationHelper.serializePlayerMultiplier(tag, aBonus.playerMultiplier);
      SerializationHelper.serializeLivingCondition(tag, aBonus.playerCondition, "player_condition");
      SerializationHelper.serializeDamageCondition(tag, aBonus.damageCondition);
      SerializationHelper.serializeLivingCondition(tag, aBonus.targetCondition, "target_condition");
      return tag;
    }

    @Override
    public CritDamageBonus deserialize(FriendlyByteBuf buf) {
      float amount = buf.readFloat();
      CritDamageBonus bonus = new CritDamageBonus(amount);
      bonus.playerMultiplier = NetworkHelper.readBonusMultiplier(buf);
      bonus.playerCondition = NetworkHelper.readLivingCondition(buf);
      bonus.damageCondition = NetworkHelper.readDamageCondition(buf);
      bonus.targetCondition = NetworkHelper.readLivingCondition(buf);
      return bonus;
    }

    @Override
    public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
      if (!(bonus instanceof CritDamageBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      buf.writeFloat(aBonus.amount);
      NetworkHelper.writeBonusMultiplier(buf, aBonus.playerMultiplier);
      NetworkHelper.writeLivingCondition(buf, aBonus.playerCondition);
      NetworkHelper.writeDamageCondition(buf, aBonus.damageCondition);
      NetworkHelper.writeLivingCondition(buf, aBonus.targetCondition);
    }

    @Override
    public SkillBonus<?> createDefaultInstance() {
      return new CritDamageBonus(0.1f);
    }
  }
}
