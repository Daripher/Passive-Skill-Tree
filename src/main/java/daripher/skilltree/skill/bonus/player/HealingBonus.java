package daripher.skilltree.skill.bonus.player;

import com.google.gson.*;
import daripher.skilltree.client.screen.SkillTreeEditorScreen;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.data.SerializationHelper;
import daripher.skilltree.init.PSTEventListeners;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.EventListenerBonus;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.event.AttackEventListener;
import daripher.skilltree.skill.bonus.event.SkillEventListener;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

public final class HealingBonus implements EventListenerBonus<HealingBonus> {
  private float chance;
  private float amount;
  private SkillEventListener eventListener;

  public HealingBonus(float chance, float amount, SkillEventListener eventListener) {
    this.chance = chance;
    this.amount = amount;
    this.eventListener = eventListener;
  }

  public HealingBonus(float chance, float amount) {
    this(chance, amount, new AttackEventListener().setTarget(Target.PLAYER));
  }

  @Override
  public void applyEffect(LivingEntity target) {
    if (target.getRandom().nextFloat() < chance) {
      if (target.getHealth() < target.getMaxHealth() && target instanceof Player player) {
        player.getFoodData().addExhaustion(amount / 2);
      }
      target.heal(amount);
    }
  }

  @Override
  public SkillBonus.Serializer getSerializer() {
    return PSTSkillBonuses.HEALING.get();
  }

  @Override
  public HealingBonus copy() {
    return new HealingBonus(chance, amount, eventListener);
  }

  @Override
  public HealingBonus multiply(double multiplier) {
    if (chance == 1) {
      amount *= (float) multiplier;
    } else {
      chance *= (float) multiplier;
    }
    return this;
  }

  @Override
  public boolean canMerge(SkillBonus<?> other) {
    if (!(other instanceof HealingBonus otherBonus)) return false;
    if (otherBonus.amount != this.amount) return false;
    return Objects.equals(otherBonus.eventListener, this.eventListener);
  }

  @Override
  public HealingBonus merge(SkillBonus<?> other) {
    if (!(other instanceof HealingBonus otherBonus)) {
      throw new IllegalArgumentException();
    }
    if (otherBonus.chance == 1 && this.chance == 1) {
      return new HealingBonus(chance, otherBonus.amount + this.amount, eventListener);
    }
    return new HealingBonus(otherBonus.chance + this.chance, amount, eventListener);
  }

  @Override
  public MutableComponent getTooltip() {
    String targetDescription = eventListener.getTarget().name().toLowerCase();
    String bonusDescription = getDescriptionId() + "." + targetDescription;
    if (chance < 1) {
      bonusDescription += ".chance";
    }
    String amountDescription = TooltipHelper.formatNumber(amount);
    MutableComponent tooltip = Component.translatable(bonusDescription, amountDescription);
    if (chance < 1) {
      tooltip =
          TooltipHelper.getSkillBonusTooltip(
              tooltip, chance, AttributeModifier.Operation.MULTIPLY_BASE);
    }
    tooltip = eventListener.getTooltip(tooltip);
    return tooltip.withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
  }

  @Override
  public boolean isPositive() {
    return chance > 0 ^ eventListener.getTarget() == Target.ENEMY;
  }

  @Override
  public SkillEventListener getEventListener() {
    return eventListener;
  }

  @Override
  public void addEditorWidgets(
      SkillTreeEditorScreen editor, int row, Consumer<EventListenerBonus<HealingBonus>> consumer) {
    editor.addLabel(0, 0, "Chance", ChatFormatting.GOLD);
    editor.addLabel(110, 0, "Amount", ChatFormatting.GOLD);
    editor.shiftWidgets(0, 19);
    editor
        .addNumericTextField(0, 0, 90, 14, chance)
        .setNumericResponder(
            v -> {
              setChance(v.floatValue());
              consumer.accept(this.copy());
            });
    editor
        .addNumericTextField(110, 0, 90, 14, amount)
        .setNumericResponder(
            v -> {
              setAmount(v.intValue());
              consumer.accept(this.copy());
            });
    editor.shiftWidgets(0, 19);
    editor.addLabel(0, 0, "Event", ChatFormatting.GOLD);
    editor.shiftWidgets(0, 19);
    editor
        .addDropDownList(0, 0, 200, 14, 10, eventListener, PSTEventListeners.eventsList())
        .setToNameFunc(e -> Component.literal(PSTEventListeners.getName(e)))
        .setResponder(
            e -> {
              setEventListener(e);
              consumer.accept(this.copy());
              editor.rebuildWidgets();
            });
    editor.shiftWidgets(0, 19);
    eventListener.addEditorWidgets(
        editor,
        e -> {
          setEventListener(e);
          consumer.accept(this.copy());
        });
  }

  public void setEventListener(SkillEventListener eventListener) {
    this.eventListener = eventListener;
  }

  public void setChance(float chance) {
    this.chance = chance;
  }

  public void setAmount(int amount) {
    this.amount = amount;
  }

  public static class Serializer implements SkillBonus.Serializer {
    @Override
    public HealingBonus deserialize(JsonObject json) throws JsonParseException {
      float chance = json.get("chance").getAsFloat();
      float amount = json.get("amount").getAsFloat();
      HealingBonus bonus = new HealingBonus(chance, amount);
      bonus.eventListener = SerializationHelper.deserializeEventListener(json);
      return bonus;
    }

    @Override
    public void serialize(JsonObject json, SkillBonus<?> bonus) {
      if (!(bonus instanceof HealingBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      json.addProperty("chance", aBonus.chance);
      json.addProperty("amount", aBonus.amount);
      SerializationHelper.serializeEventListener(json, aBonus.eventListener);
    }

    @Override
    public HealingBonus deserialize(CompoundTag tag) {
      float chance = tag.getFloat("chance");
      float amount = tag.getFloat("amount");
      HealingBonus bonus = new HealingBonus(chance, amount);
      bonus.eventListener = SerializationHelper.deserializeEventListener(tag);
      return bonus;
    }

    @Override
    public CompoundTag serialize(SkillBonus<?> bonus) {
      if (!(bonus instanceof HealingBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      tag.putFloat("chance", aBonus.chance);
      tag.putFloat("amount", aBonus.amount);
      SerializationHelper.serializeEventListener(tag, aBonus.eventListener);
      return tag;
    }

    @Override
    public HealingBonus deserialize(FriendlyByteBuf buf) {
      float amount = buf.readFloat();
      float duration = buf.readFloat();
      HealingBonus bonus = new HealingBonus(amount, duration);
      bonus.eventListener = NetworkHelper.readEventListener(buf);
      return bonus;
    }

    @Override
    public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
      if (!(bonus instanceof HealingBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      buf.writeFloat(aBonus.chance);
      buf.writeFloat(aBonus.amount);
      NetworkHelper.writeEventListener(buf, aBonus.eventListener);
    }

    @Override
    public SkillBonus<?> createDefaultInstance() {
      return new HealingBonus(0.05f, 5);
    }
  }
}
