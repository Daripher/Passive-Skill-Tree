package daripher.skilltree.skill.bonus.player;

import com.google.gson.*;
import daripher.skilltree.client.screen.SkillTreeEditor;
import daripher.skilltree.data.SerializationHelper;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.condition.damage.DamageCondition;
import daripher.skilltree.skill.bonus.condition.living.LivingCondition;
import daripher.skilltree.skill.bonus.multiplier.SkillBonusMultiplier;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class DamageBonus implements SkillBonus<DamageBonus> {
  private float amount;
  private final AttributeModifier.Operation operation;
  private @Nullable SkillBonusMultiplier multiplier;
  private @Nullable LivingCondition playerCondition;
  private @Nullable LivingCondition targetCondition;
  private @Nullable DamageCondition damageCondition;

  public DamageBonus(float amount, AttributeModifier.Operation operation) {
    this.amount = amount;
    this.operation = operation;
  }

  public float getDamageBonus(
      AttributeModifier.Operation operation,
      DamageSource source,
      Player attacker,
      LivingEntity target) {
    if (this.operation != operation) return 0f;
    if (damageCondition != null && !damageCondition.met(source)) return 0f;
    if (playerCondition != null && !playerCondition.met(attacker)) return 0f;
    if (targetCondition != null && !targetCondition.met(target)) return 0f;
    if (multiplier != null) {
      return amount * multiplier.getValue(attacker);
    }
    return amount;
  }

  @Override
  public SkillBonus.Serializer getSerializer() {
    return PSTSkillBonuses.DAMAGE.get();
  }

  @Override
  public SkillBonus<DamageBonus> copy() {
    DamageBonus bonus = new DamageBonus(amount, operation);
    bonus.multiplier = this.multiplier;
    bonus.playerCondition = this.playerCondition;
    bonus.damageCondition = this.damageCondition;
    bonus.targetCondition = this.targetCondition;
    return bonus;
  }

  @Override
  public DamageBonus multiply(double multiplier) {
    amount *= (float) multiplier;
    return this;
  }

  @Override
  public boolean canMerge(SkillBonus<?> other) {
    if (!(other instanceof DamageBonus otherBonus)) return false;
    if (!Objects.equals(otherBonus.multiplier, this.multiplier)) return false;
    if (!Objects.equals(otherBonus.playerCondition, this.playerCondition)) return false;
    if (!Objects.equals(otherBonus.damageCondition, this.damageCondition)) return false;
    if (!Objects.equals(otherBonus.targetCondition, this.targetCondition)) return false;
    return otherBonus.operation == this.operation;
  }

  @Override
  public SkillBonus<DamageBonus> merge(SkillBonus<?> other) {
    if (!(other instanceof DamageBonus otherBonus)) {
      throw new IllegalArgumentException();
    }
    float mergedAmount = otherBonus.amount + this.amount;
    DamageBonus mergedBonus = new DamageBonus(mergedAmount, this.operation);
    mergedBonus.multiplier = this.multiplier;
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
    operationDescription = "attribute.modifier." + operationDescription + "." + operation.toValue();
    MutableComponent bonusDescription = Component.translatable(getDescriptionId());
    if (damageCondition != null) {
      bonusDescription = damageCondition.getTooltip(bonusDescription);
    }
    MutableComponent tooltip =
        Component.translatable(operationDescription, amountDescription, bonusDescription);
    if (multiplier != null) {
      tooltip = multiplier.getTooltip(tooltip);
    }
    if (playerCondition != null) {
      tooltip = playerCondition.getTooltip(tooltip, "you");
    }
    if (targetCondition != null) {
      tooltip = targetCondition.getTooltip(tooltip, "target");
    }
    return tooltip.withStyle(style);
  }

  @Override
  public void addEditorWidgets(SkillTreeEditor editor, int row) {
    // TODO: add widgets
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

  public SkillBonus<?> setMultiplier(SkillBonusMultiplier multiplier) {
    this.multiplier = multiplier;
    return this;
  }

  public static class Serializer implements SkillBonus.Serializer {
    @Override
    public DamageBonus deserialize(JsonObject json) throws JsonParseException {
      float amount = json.get("chance").getAsFloat();
      AttributeModifier.Operation operation = SerializationHelper.deserializeOperation(json);
      DamageBonus bonus = new DamageBonus(amount, operation);
      bonus.multiplier = SerializationHelper.deserializeBonusMultiplier(json);
      bonus.playerCondition =
          SerializationHelper.deserializeLivingCondition(json, "player_condition");
      bonus.damageCondition = SerializationHelper.deserializeDamageCondition(json);
      bonus.targetCondition =
          SerializationHelper.deserializeLivingCondition(json, "target_condition");
      return bonus;
    }

    @Override
    public void serialize(JsonObject json, SkillBonus<?> bonus) {
      if (!(bonus instanceof DamageBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      json.addProperty("chance", aBonus.amount);
      SerializationHelper.serializeOperation(json, aBonus.operation);
      SerializationHelper.serializeBonusMultiplier(json, aBonus.multiplier);
      SerializationHelper.serializeLivingCondition(
          json, aBonus.playerCondition, "player_condition");
      SerializationHelper.serializeDamageCondition(json, aBonus.damageCondition);
      SerializationHelper.serializeLivingCondition(
          json, aBonus.targetCondition, "target_condition");
    }

    @Override
    public DamageBonus deserialize(CompoundTag tag) {
      float amount = tag.getFloat("Amount");
      AttributeModifier.Operation operation = SerializationHelper.deserializeOperation(tag);
      DamageBonus bonus = new DamageBonus(amount, operation);
      bonus.multiplier = SerializationHelper.deserializeBonusMultiplier(tag);
      bonus.playerCondition =
          SerializationHelper.deserializeLivingCondition(tag, "player_condition");
      bonus.damageCondition = SerializationHelper.deserializeDamageCondition(tag);
      bonus.targetCondition =
          SerializationHelper.deserializeLivingCondition(tag, "target_condition");
      return bonus;
    }

    @Override
    public CompoundTag serialize(SkillBonus<?> bonus) {
      if (!(bonus instanceof DamageBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      tag.putFloat("Amount", aBonus.amount);
      SerializationHelper.serializeOperation(tag, aBonus.operation);
      SerializationHelper.serializeBonusMultiplier(tag, aBonus.multiplier);
      SerializationHelper.serializeLivingCondition(tag, aBonus.playerCondition, "player_condition");
      SerializationHelper.serializeDamageCondition(tag, aBonus.damageCondition);
      SerializationHelper.serializeLivingCondition(tag, aBonus.targetCondition, "target_condition");
      return tag;
    }

    @Override
    public DamageBonus deserialize(FriendlyByteBuf buf) {
      float amount = buf.readFloat();
      AttributeModifier.Operation operation = AttributeModifier.Operation.fromValue(buf.readInt());
      DamageBonus bonus = new DamageBonus(amount, operation);
      bonus.multiplier = NetworkHelper.readBonusMultiplier(buf);
      bonus.playerCondition = NetworkHelper.readLivingCondition(buf);
      bonus.damageCondition = NetworkHelper.readDamageCondition(buf);
      bonus.targetCondition = NetworkHelper.readLivingCondition(buf);
      return bonus;
    }

    @Override
    public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
      if (!(bonus instanceof DamageBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      buf.writeFloat(aBonus.amount);
      buf.writeInt(aBonus.operation.toValue());
      NetworkHelper.writeBonusMultiplier(buf, aBonus.multiplier);
      NetworkHelper.writeLivingCondition(buf, aBonus.playerCondition);
      NetworkHelper.writeDamageCondition(buf, aBonus.damageCondition);
      NetworkHelper.writeLivingCondition(buf, aBonus.targetCondition);
    }
  }
}
