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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class CritDamageBonus implements SkillBonus<CritDamageBonus> {
  private float amount;
  private @Nullable SkillBonusMultiplier multiplier;
  private @Nullable LivingCondition playerCondition;
  private @Nullable LivingCondition targetCondition;
  private @Nullable DamageCondition damageCondition;

  public CritDamageBonus(float amount) {
    this.amount = amount;
  }

  public float getDamageBonus(DamageSource source, Player attacker, LivingEntity target) {
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
    return PSTSkillBonuses.CRIT_DAMAGE.get();
  }

  @Override
  public SkillBonus<CritDamageBonus> copy() {
    CritDamageBonus bonus = new CritDamageBonus(amount);
    bonus.multiplier = this.multiplier;
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
    if (!Objects.equals(otherBonus.multiplier, this.multiplier)) return false;
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
    operationDescription = "attribute.modifier." + operationDescription + ".1";
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
    public CritDamageBonus deserialize(JsonObject json) throws JsonParseException {
      float amount = json.get("chance").getAsFloat();
      CritDamageBonus bonus = new CritDamageBonus(amount);
      bonus.multiplier = SerializationHelper.deserializeBonusMultiplier(json);
      bonus.playerCondition = SerializationHelper.deserializeLivingCondition(json, "player_condition");
      bonus.damageCondition = SerializationHelper.deserializeDamageCondition(json);
      bonus.targetCondition = SerializationHelper.deserializeLivingCondition(json, "target_condition");
      return bonus;
    }

    @Override
    public void serialize(JsonObject json, SkillBonus<?> bonus) {
      if (!(bonus instanceof CritDamageBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      json.addProperty("chance", aBonus.amount);
      SerializationHelper.serializeBonusMultiplier(json, aBonus.multiplier);
      SerializationHelper.serializeLivingCondition(json, aBonus.playerCondition, "player_condition");
      SerializationHelper.serializeDamageCondition(json, aBonus.damageCondition);
      SerializationHelper.serializeLivingCondition(json, aBonus.targetCondition, "target_condition");
    }

    @Override
    public CritDamageBonus deserialize(CompoundTag tag) {
      float amount = tag.getFloat("Amount");
      CritDamageBonus bonus = new CritDamageBonus(amount);
      bonus.multiplier = SerializationHelper.deserializeBonusMultiplier(tag);
      bonus.playerCondition = SerializationHelper.deserializeLivingCondition(tag, "player_condition");
      bonus.damageCondition = SerializationHelper.deserializeDamageCondition(tag);
      bonus.targetCondition = SerializationHelper.deserializeLivingCondition(tag, "target_condition");
      return bonus;
    }

    @Override
    public CompoundTag serialize(SkillBonus<?> bonus) {
      if (!(bonus instanceof CritDamageBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      tag.putFloat("Amount", aBonus.amount);
      SerializationHelper.serializeBonusMultiplier(tag, aBonus.multiplier);
      SerializationHelper.serializeLivingCondition(tag, aBonus.playerCondition, "player_condition");
      SerializationHelper.serializeDamageCondition(tag, aBonus.damageCondition);
      SerializationHelper.serializeLivingCondition(tag, aBonus.targetCondition, "target_condition");
      return tag;
    }

    @Override
    public CritDamageBonus deserialize(FriendlyByteBuf buf) {
      float amount = buf.readFloat();
      CritDamageBonus bonus = new CritDamageBonus(amount);
      bonus.multiplier = NetworkHelper.readBonusMultiplier(buf);
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
      NetworkHelper.writeBonusMultiplier(buf, aBonus.multiplier);
      NetworkHelper.writeLivingCondition(buf, aBonus.playerCondition);
      NetworkHelper.writeDamageCondition(buf, aBonus.damageCondition);
      NetworkHelper.writeLivingCondition(buf, aBonus.targetCondition);
    }
  }
}
