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
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

public final class IncomingHealingBonus implements SkillBonus<IncomingHealingBonus> {
  private float multiplier;
  private @Nonnull LivingMultiplier playerMultiplier = NoneLivingMultiplier.INSTANCE;
  private @Nonnull LivingCondition playerCondition = NoneLivingCondition.INSTANCE;

  public IncomingHealingBonus(float multiplier) {
    this.multiplier = multiplier;
  }

  public float getHealingMultiplier(Player player) {
    if (!playerCondition.met(player)) return 0f;
    return this.multiplier * playerMultiplier.getValue(player);
  }

  @Override
  public SkillBonus.Serializer getSerializer() {
    return PSTSkillBonuses.INCOMING_HEALING.get();
  }

  @Override
  public IncomingHealingBonus copy() {
    IncomingHealingBonus bonus = new IncomingHealingBonus(multiplier);
    bonus.playerMultiplier = this.playerMultiplier;
    bonus.playerCondition = this.playerCondition;
    return bonus;
  }

  @Override
  public IncomingHealingBonus multiply(double multiplier) {
    this.multiplier = (float) (this.multiplier * multiplier);
    return this;
  }

  @Override
  public boolean canMerge(SkillBonus<?> other) {
    if (!(other instanceof IncomingHealingBonus otherBonus)) return false;
    if (!Objects.equals(otherBonus.playerMultiplier, this.playerMultiplier)) return false;
    if (!Objects.equals(otherBonus.playerCondition, this.playerCondition)) return false;
    return otherBonus.multiplier == this.multiplier;
  }

  @Override
  public SkillBonus<IncomingHealingBonus> merge(SkillBonus<?> other) {
    if (!(other instanceof IncomingHealingBonus otherBonus)) {
      throw new IllegalArgumentException();
    }
    IncomingHealingBonus mergedBonus =
        new IncomingHealingBonus(this.multiplier + otherBonus.multiplier);
    mergedBonus.playerMultiplier = this.playerMultiplier;
    mergedBonus.playerCondition = this.playerCondition;
    return mergedBonus;
  }

  @Override
  public MutableComponent getTooltip() {
    MutableComponent tooltip =
        TooltipHelper.getSkillBonusTooltip(
            getDescriptionId(), multiplier, AttributeModifier.Operation.MULTIPLY_BASE);
    tooltip = playerMultiplier.getTooltip(tooltip);
    tooltip = playerCondition.getTooltip(tooltip, "you");
    return tooltip.withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
  }

  @Override
  public boolean isPositive() {
    return multiplier > 0;
  }

  @Override
  public void addEditorWidgets(
      SkillTreeEditorScreen editor, int index, Consumer<IncomingHealingBonus> consumer) {
    editor.addLabel(0, 0, "Multiplier", ChatFormatting.GOLD);
    editor.shiftWidgets(0, 19);
    editor
        .addNumericTextField(0, 0, 50, 14, multiplier)
        .setNumericResponder(
            v -> {
              setMultiplier(v.floatValue());
              consumer.accept(this.copy());
            });
    editor.addLabel(0, 0, "Player Condition", ChatFormatting.GOLD);
    editor.shiftWidgets(0, 19);
    editor
        .addDropDownList(0, 0, 200, 14, 10, playerCondition, PSTLivingConditions.conditionsList())
        .setToNameFunc(c -> Component.literal(PSTLivingConditions.getName(c)))
        .setResponder(
            c -> {
              setCondition(c);
              consumer.accept(this.copy());
              editor.rebuildWidgets();
            });
    editor.shiftWidgets(0, 19);
    playerCondition.addEditorWidgets(
        editor,
        c -> {
          setCondition(c);
          consumer.accept(this.copy());
        });
    editor.addLabel(0, 0, "Player Multiplier", ChatFormatting.GOLD);
    editor.shiftWidgets(0, 19);
    editor
        .addDropDownList(0, 0, 200, 14, 10, playerMultiplier, PSTLivingMultipliers.multiplierList())
        .setToNameFunc(m -> Component.literal(PSTLivingMultipliers.getName(m)))
        .setResponder(
            m -> {
              setMultiplier(m);
              consumer.accept(this.copy());
              editor.rebuildWidgets();
            });
    editor.shiftWidgets(0, 19);
    playerMultiplier.addEditorWidgets(
        editor,
        m -> {
          setMultiplier(m);
          consumer.accept(this.copy());
        });
  }

  public void setMultiplier(float multiplier) {
    this.multiplier = multiplier;
  }

  public SkillBonus<?> setCondition(LivingCondition condition) {
    this.playerCondition = condition;
    return this;
  }

  public SkillBonus<?> setMultiplier(LivingMultiplier multiplier) {
    this.playerMultiplier = multiplier;
    return this;
  }

  public static class Serializer implements SkillBonus.Serializer {
    @Override
    public IncomingHealingBonus deserialize(JsonObject json) throws JsonParseException {
      float multiplier = json.get("multiplier").getAsFloat();
      IncomingHealingBonus bonus = new IncomingHealingBonus(multiplier);
      bonus.playerMultiplier = SerializationHelper.deserializePlayerMultiplier(json);
      bonus.playerCondition =
          SerializationHelper.deserializeLivingCondition(json, "player_condition");
      return bonus;
    }

    @Override
    public void serialize(JsonObject json, SkillBonus<?> bonus) {
      if (!(bonus instanceof IncomingHealingBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      json.addProperty("multiplier", aBonus.multiplier);
      SerializationHelper.serializePlayerMultiplier(json, aBonus.playerMultiplier);
      SerializationHelper.serializeLivingCondition(
          json, aBonus.playerCondition, "player_condition");
    }

    @Override
    public IncomingHealingBonus deserialize(CompoundTag tag) {
      float multiplier = tag.getFloat("multiplier");
      IncomingHealingBonus bonus = new IncomingHealingBonus(multiplier);
      bonus.playerMultiplier = SerializationHelper.deserializePlayerMultiplier(tag);
      bonus.playerCondition =
          SerializationHelper.deserializeLivingCondition(tag, "player_condition");
      return bonus;
    }

    @Override
    public CompoundTag serialize(SkillBonus<?> bonus) {
      if (!(bonus instanceof IncomingHealingBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      tag.putFloat("multiplier", aBonus.multiplier);
      SerializationHelper.serializePlayerMultiplier(tag, aBonus.playerMultiplier);
      SerializationHelper.serializeLivingCondition(tag, aBonus.playerCondition, "player_condition");
      return tag;
    }

    @Override
    public IncomingHealingBonus deserialize(FriendlyByteBuf buf) {
      IncomingHealingBonus bonus = new IncomingHealingBonus(buf.readFloat());
      bonus.playerMultiplier = NetworkHelper.readBonusMultiplier(buf);
      bonus.playerCondition = NetworkHelper.readLivingCondition(buf);
      return bonus;
    }

    @Override
    public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
      if (!(bonus instanceof IncomingHealingBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      buf.writeFloat(aBonus.multiplier);
      NetworkHelper.writeBonusMultiplier(buf, aBonus.playerMultiplier);
      NetworkHelper.writeLivingCondition(buf, aBonus.playerCondition);
    }

    @Override
    public SkillBonus<?> createDefaultInstance() {
      return new IncomingHealingBonus(0.15f);
    }
  }
}
