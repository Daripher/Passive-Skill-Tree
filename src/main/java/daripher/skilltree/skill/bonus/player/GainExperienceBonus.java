package daripher.skilltree.skill.bonus.player;

import com.google.gson.*;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
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

public final class GainExperienceBonus implements EventListenerBonus<GainExperienceBonus> {
  private float chance;
  private int amount;
  private SkillEventListener eventListener;

  public GainExperienceBonus(float chance, int amount, SkillEventListener eventListener) {
    this.chance = chance;
    this.amount = amount;
    this.eventListener = eventListener;
  }

  public GainExperienceBonus(float chance, int amount) {
    this(chance, amount, new AttackEventListener().setTarget(Target.PLAYER));
  }

  @Override
  public void applyEffect(LivingEntity target) {
    if (!(target instanceof Player player)) {
      return;
    }
    if (target.getRandom().nextFloat() < chance) {
      player.giveExperiencePoints(amount);
    }
  }

  @Override
  public SkillBonus.Serializer getSerializer() {
    return PSTSkillBonuses.GAIN_EXPERIENCE.get();
  }

  @Override
  public GainExperienceBonus copy() {
    return new GainExperienceBonus(chance, amount, eventListener);
  }

  @Override
  public GainExperienceBonus multiply(double multiplier) {
    if (chance == 1) {
      amount = (int) (amount * multiplier);
    } else {
      chance *= (float) multiplier;
    }
    return this;
  }

  @Override
  public boolean canMerge(SkillBonus<?> other) {
    if (!(other instanceof GainExperienceBonus otherBonus)) return false;
    if (otherBonus.amount != this.amount) return false;
    return Objects.equals(otherBonus.eventListener, this.eventListener);
  }

  @Override
  public GainExperienceBonus merge(SkillBonus<?> other) {
    if (!(other instanceof GainExperienceBonus otherBonus)) {
      throw new IllegalArgumentException();
    }
    if (otherBonus.chance == 1 && this.chance == 1) {
      return new GainExperienceBonus(chance, otherBonus.amount + this.amount, eventListener);
    }
    return new GainExperienceBonus(otherBonus.chance + this.chance, amount, eventListener);
  }

  @Override
  public MutableComponent getTooltip() {
    String bonusDescription = getDescriptionId();
    if (chance < 1) {
      bonusDescription += ".chance";
    }
    MutableComponent tooltip = Component.translatable(bonusDescription, amount);
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
    return chance > 0;
  }

  @Override
  public SkillEventListener getEventListener() {
    return eventListener;
  }

  @Override
  public void addEditorWidgets(
      SkillTreeEditor editor, int row, Consumer<EventListenerBonus<GainExperienceBonus>> consumer) {
    editor.addLabel(0, 0, "Chance", ChatFormatting.GOLD);
    editor.addLabel(110, 0, "Amount", ChatFormatting.GOLD);
    editor.increaseHeight(19);
    editor
        .addNumericTextField(0, 0, 90, 14, chance)
        .setNumericResponder(value -> selectChance(consumer, value));
    editor
        .addNumericTextField(110, 0, 90, 14, amount)
        .setNumericFilter(value -> value.intValue() == value)
        .setNumericResponder(value -> selectAmount(consumer, value));
    editor.increaseHeight(19);
    editor.addLabel(0, 0, "Event", ChatFormatting.GOLD);
    editor.increaseHeight(19);
    editor
        .addSelectionMenu(0, 0, 200, eventListener)
        .setResponder(eventListener -> selectEventListener(editor, consumer, eventListener))
        .setMenuInitFunc(() -> addEventListenerWidgets(editor, consumer));
    editor.increaseHeight(19);
  }

  private void addEventListenerWidgets(
      SkillTreeEditor editor, Consumer<EventListenerBonus<GainExperienceBonus>> consumer) {
    eventListener.addEditorWidgets(
        editor,
        eventListener -> {
          setEventListener(eventListener);
          consumer.accept(this.copy());
        });
  }

  private void selectEventListener(
      SkillTreeEditor editor,
      Consumer<EventListenerBonus<GainExperienceBonus>> consumer,
      SkillEventListener eventListener) {
    setEventListener(eventListener);
    consumer.accept(this.copy());
    editor.rebuildWidgets();
  }

  private void selectAmount(Consumer<EventListenerBonus<GainExperienceBonus>> consumer, Double value) {
    setAmount(value.intValue());
    consumer.accept(this.copy());
  }

  private void selectChance(Consumer<EventListenerBonus<GainExperienceBonus>> consumer, Double value) {
    setChance(value.floatValue());
    consumer.accept(this.copy());
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
    public GainExperienceBonus deserialize(JsonObject json) throws JsonParseException {
      float chance = SerializationHelper.getElement(json, "chance").getAsFloat();
      int amount = SerializationHelper.getElement(json, "amount").getAsInt();
      GainExperienceBonus bonus = new GainExperienceBonus(chance, amount);
      bonus.eventListener = SerializationHelper.deserializeEventListener(json);
      return bonus;
    }

    @Override
    public void serialize(JsonObject json, SkillBonus<?> bonus) {
      if (!(bonus instanceof GainExperienceBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      json.addProperty("chance", aBonus.chance);
      json.addProperty("amount", aBonus.amount);
      SerializationHelper.serializeEventListener(json, aBonus.eventListener);
    }

    @Override
    public GainExperienceBonus deserialize(CompoundTag tag) {
      float chance = tag.getFloat("chance");
      int amount = tag.getInt("amount");
      GainExperienceBonus bonus = new GainExperienceBonus(chance, amount);
      bonus.eventListener = SerializationHelper.deserializeEventListener(tag);
      return bonus;
    }

    @Override
    public CompoundTag serialize(SkillBonus<?> bonus) {
      if (!(bonus instanceof GainExperienceBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      tag.putFloat("chance", aBonus.chance);
      tag.putInt("amount", aBonus.amount);
      SerializationHelper.serializeEventListener(tag, aBonus.eventListener);
      return tag;
    }

    @Override
    public GainExperienceBonus deserialize(FriendlyByteBuf buf) {
      float chance = buf.readFloat();
      int amount = buf.readInt();
      GainExperienceBonus bonus = new GainExperienceBonus(chance, amount);
      bonus.eventListener = NetworkHelper.readEventListener(buf);
      return bonus;
    }

    @Override
    public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
      if (!(bonus instanceof GainExperienceBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      buf.writeFloat(aBonus.chance);
      buf.writeInt(aBonus.amount);
      NetworkHelper.writeEventListener(buf, aBonus.eventListener);
    }

    @Override
    public SkillBonus<?> createDefaultInstance() {
      return new GainExperienceBonus(0.05f, 5);
    }
  }
}
