package daripher.skilltree.skill.bonus.player;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.EventListenerBonus;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.event.AttackEventListener;
import daripher.skilltree.skill.bonus.event.SkillEventListener;
import daripher.skilltree.skill.bonus.event.TickingEventListener;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.Objects;
import java.util.function.Consumer;

public final class InflictEffectBonus implements EventListenerBonus<InflictEffectBonus> {
  private MobEffectInstance effectInstance;
  private SkillEventListener eventListener;
  private float chance;
  private int maxStacks;

  public InflictEffectBonus(
      float chance,
      MobEffectInstance effectInstance,
      SkillEventListener eventListener,
      int maxStacks) {
    this.chance = chance;
    this.effectInstance = effectInstance;
    this.eventListener = eventListener;
    this.maxStacks = maxStacks;
  }

  public InflictEffectBonus(float chance, MobEffectInstance effectInstance, int maxStacks) {
    this(chance, effectInstance, new AttackEventListener(), maxStacks);
  }

  @Override
  public void applyEffect(LivingEntity target) {
    RandomSource random = target.getRandom();
    if (!(random.nextFloat() < chance)) {
      return;
    }
    MobEffectInstance effectInstanceCopy = new MobEffectInstance(effectInstance);
    MobEffect effect = effectInstance.getEffect();
    if (maxStacks > 1) {
      effectInstanceCopy = getStackedEffectInstance(target, effect, effectInstanceCopy);
    }
    target.addEffect(effectInstanceCopy);
  }

  private MobEffectInstance getStackedEffectInstance(
      LivingEntity target, MobEffect effect, MobEffectInstance effectInstanceCopy) {
    MobEffectInstance activeEffectInstance = target.getEffect(effect);
    if (activeEffectInstance == null) {
      return effectInstanceCopy;
    }
    int amplifier = activeEffectInstance.getAmplifier();
    if (amplifier >= maxStacks - 1) {
      return effectInstanceCopy;
    }
    int duration = effectInstance.getDuration();
    effectInstanceCopy = new MobEffectInstance(effect, duration, amplifier + 1);
    return effectInstanceCopy;
  }

  @Override
  public SkillBonus.Serializer getSerializer() {
    return PSTSkillBonuses.INFLICT_EFFECT.get();
  }

  @Override
  public InflictEffectBonus copy() {
    return new InflictEffectBonus(chance, effectInstance, eventListener, maxStacks);
  }

  @Override
  public InflictEffectBonus multiply(double multiplier) {
    if (chance < 1) {
      chance *= (float) multiplier;
    } else {
      int newDuration = (int) (effectInstance.getDuration() * multiplier);
      effectInstance =
          new MobEffectInstance(
              effectInstance.getEffect(), newDuration, effectInstance.getAmplifier());
      return new InflictEffectBonus(chance, effectInstance, eventListener, maxStacks);
    }
    return this;
  }

  @Override
  public boolean canMerge(SkillBonus<?> other) {
    if (!(other instanceof InflictEffectBonus otherBonus)) return false;
    if (!Objects.equals(otherBonus.effectInstance.getEffect(), this.effectInstance.getEffect()))
      return false;
    return Objects.equals(otherBonus.eventListener, this.eventListener);
  }

  @Override
  public SkillBonus<EventListenerBonus<InflictEffectBonus>> merge(SkillBonus<?> other) {
    if (!(other instanceof InflictEffectBonus otherBonus)) {
      throw new IllegalArgumentException();
    }
    if (chance < 1) {
      return new InflictEffectBonus(
          otherBonus.chance + this.chance, effectInstance, eventListener, maxStacks);
    } else {
      int newDuration = effectInstance.getDuration() + otherBonus.effectInstance.getDuration();
      effectInstance =
          new MobEffectInstance(
              effectInstance.getEffect(), newDuration, effectInstance.getAmplifier());
      return new InflictEffectBonus(chance, effectInstance, eventListener, maxStacks);
    }
  }

  @Override
  public MutableComponent getTooltip() {
    Component effectDescription = TooltipHelper.getEffectTooltip(effectInstance);
    int duration = effectInstance.getDuration();
    Target target = eventListener.getTarget();
    String targetDescription = target.getName();
    String bonusDescription = getDescriptionId() + "." + targetDescription;
    if (chance < 1) {
      bonusDescription += ".chance";
    }
    MutableComponent tooltip;
    boolean isInstantEffect = duration == 0;
    boolean showDuration = !isInstantEffect && !(getEventListener() instanceof TickingEventListener && duration <= 20);
    if (showDuration) {
      Component durationDescription = getDurationDescription();
      tooltip = Component.translatable(bonusDescription, effectDescription, durationDescription);
    } else {
      tooltip = Component.translatable(bonusDescription, effectDescription, "");
    }
    if (chance < 1) {
      tooltip =
          TooltipHelper.getSkillBonusTooltip(
              tooltip, chance, AttributeModifier.Operation.MULTIPLY_BASE);
    }
    tooltip = eventListener.getTooltip(tooltip);
    if (maxStacks > 1) {
      tooltip = Component.translatable(getDescriptionId() + ".stacks", tooltip, maxStacks);
    }
    return tooltip.withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
  }

  private Component getDurationDescription() {
    boolean measureInSeconds = effectInstance.getDuration() < 1200;
    String measurement = measureInSeconds ? "seconds" : "minutes";
    float duration =
        measureInSeconds
            ? effectInstance.getDuration() / 20f
            : effectInstance.getDuration() / 1200f;
    String formattedDuration = TooltipHelper.formatNumber(duration);
    return Component.translatable(getDescriptionId() + "." + measurement, formattedDuration);
  }

  @Override
  public void gatherInfo(Consumer<MutableComponent> consumer) {
    TooltipHelper.consumeTranslated(effectInstance.getDescriptionId() + ".info", consumer);
  }

  @Override
  public boolean isPositive() {
    return chance > 0
        ^ eventListener.getTarget() == Target.PLAYER
        ^ effectInstance.getEffect().getCategory() != MobEffectCategory.HARMFUL;
  }

  @Override
  public SkillEventListener getEventListener() {
    return eventListener;
  }

  @Override
  public void addEditorWidgets(
      SkillTreeEditor editor, int row, Consumer<EventListenerBonus<InflictEffectBonus>> consumer) {
    editor.addLabel(0, 0, "Effect", ChatFormatting.GOLD);
    editor.addLabel(150, 0, "Chance", ChatFormatting.GOLD);
    editor.increaseHeight(19);
    editor
        .addSelectionMenu(0, 0, 145, effectInstance.getEffect())
        .setResponder(effect -> selectEffect(consumer, effect));
    editor
        .addNumericTextField(150, 0, 50, 14, chance)
        .setNumericResponder(value -> selectChance(consumer, value));
    editor.increaseHeight(19);
    editor.addLabel(0, 0, "Duration", ChatFormatting.GOLD);
    editor.addLabel(55, 0, "Amplifier", ChatFormatting.GOLD);
    editor.addLabel(110, 0, "Stacks", ChatFormatting.GOLD);
    editor.increaseHeight(19);
    editor
        .addNumericTextField(0, 0, 50, 14, effectInstance.getDuration())
        .setNumericFilter(value -> value >= -1)
        .setNumericResponder(value -> selectDuration(consumer, value));
    editor
        .addNumericTextField(55, 0, 50, 14, effectInstance.getAmplifier())
        .setNumericFilter(value -> value >= 0)
        .setNumericResponder(value -> selectAmplifier(consumer, value));
    editor
        .addNumericTextField(110, 0, 50, 14, maxStacks)
        .setNumericFilter(value -> value >= 1)
        .setNumericResponder(value -> selectMaxStacks(consumer, value));
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
      SkillTreeEditor editor, Consumer<EventListenerBonus<InflictEffectBonus>> consumer) {
    eventListener.addEditorWidgets(
        editor,
        eventListener -> {
          setEventListener(eventListener);
          consumer.accept(this.copy());
        });
  }

  private void selectEventListener(
      SkillTreeEditor editor,
      Consumer<EventListenerBonus<InflictEffectBonus>> consumer,
      SkillEventListener eventListener) {
    setEventListener(eventListener);
    consumer.accept(this.copy());
    editor.rebuildWidgets();
  }

  private void selectAmplifier(
      Consumer<EventListenerBonus<InflictEffectBonus>> consumer, Double value) {
    setAmplifier(value.intValue());
    consumer.accept(this.copy());
  }

  private void selectMaxStacks(
      Consumer<EventListenerBonus<InflictEffectBonus>> consumer, Double value) {
    setMaxStacks(value.intValue());
    consumer.accept(this.copy());
  }

  private void selectDuration(
      Consumer<EventListenerBonus<InflictEffectBonus>> consumer, Double value) {
    setDuration(value.intValue());
    consumer.accept(this.copy());
  }

  private void selectChance(
      Consumer<EventListenerBonus<InflictEffectBonus>> consumer, Double value) {
    setChance(value.floatValue());
    consumer.accept(this.copy());
  }

  private void selectEffect(
      Consumer<EventListenerBonus<InflictEffectBonus>> consumer, MobEffect effect) {
    setEffectInstance(effect);
    consumer.accept(this);
  }

  public void setChance(float chance) {
    this.chance = chance;
  }

  public void setEffectInstance(MobEffect effectInstance) {
    this.effectInstance =
        new MobEffectInstance(
            effectInstance, this.effectInstance.getDuration(), this.effectInstance.getAmplifier());
  }

  public void setDuration(int duration) {
    this.effectInstance =
        new MobEffectInstance(
            this.effectInstance.getEffect(), duration, this.effectInstance.getAmplifier());
  }

  public void setAmplifier(int amplifier) {
    this.effectInstance =
        new MobEffectInstance(
            this.effectInstance.getEffect(), this.effectInstance.getDuration(), amplifier);
  }

  public void setMaxStacks(int maxStacks) {
    this.maxStacks = maxStacks;
  }

  public void setEventListener(SkillEventListener eventListener) {
    this.eventListener = eventListener;
  }

  public static class Serializer implements SkillBonus.Serializer {
    @Override
    public InflictEffectBonus deserialize(JsonObject json) throws JsonParseException {
      float chance = SerializationHelper.getElement(json, "chance").getAsFloat();
      MobEffectInstance effect = SerializationHelper.deserializeEffectInstance(json);
      int maxStacks = json.has("max_stacks") ? json.get("max_stacks").getAsInt() : 0;
      InflictEffectBonus bonus = new InflictEffectBonus(chance, effect, maxStacks);
      bonus.eventListener = SerializationHelper.deserializeEventListener(json);
      return bonus;
    }

    @Override
    public void serialize(JsonObject json, SkillBonus<?> bonus) {
      if (!(bonus instanceof InflictEffectBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      json.addProperty("chance", aBonus.chance);
      json.addProperty("max_stacks", aBonus.maxStacks);
      SerializationHelper.serializeEffectInstance(json, aBonus.effectInstance);
      SerializationHelper.serializeEventListener(json, aBonus.eventListener);
    }

    @Override
    public InflictEffectBonus deserialize(CompoundTag tag) {
      float chance = tag.getFloat("chance");
      MobEffectInstance effect = SerializationHelper.deserializeEffectInstance(tag);
      int maxStacks = tag.getInt("max_stacks");
      InflictEffectBonus bonus = new InflictEffectBonus(chance, effect, maxStacks);
      bonus.eventListener = SerializationHelper.deserializeEventListener(tag);
      return bonus;
    }

    @Override
    public CompoundTag serialize(SkillBonus<?> bonus) {
      if (!(bonus instanceof InflictEffectBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      tag.putFloat("chance", aBonus.chance);
      tag.putInt("max_stacks", aBonus.maxStacks);
      SerializationHelper.serializeEffectInstance(tag, aBonus.effectInstance);
      SerializationHelper.serializeEventListener(tag, aBonus.eventListener);
      return tag;
    }

    @Override
    public InflictEffectBonus deserialize(FriendlyByteBuf buf) {
      float amount = buf.readFloat();
      int maxStacks = buf.readInt();
      MobEffectInstance effect = NetworkHelper.readEffectInstance(buf);
      InflictEffectBonus bonus = new InflictEffectBonus(amount, effect, maxStacks);
      bonus.eventListener = NetworkHelper.readEventListener(buf);
      return bonus;
    }

    @Override
    public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
      if (!(bonus instanceof InflictEffectBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      buf.writeFloat(aBonus.chance);
      buf.writeInt(aBonus.maxStacks);
      NetworkHelper.writeEffectInstance(buf, aBonus.effectInstance);
      NetworkHelper.writeEventListener(buf, aBonus.eventListener);
    }

    @Override
    public SkillBonus<?> createDefaultInstance() {
      return new InflictEffectBonus(0.05f, new MobEffectInstance(MobEffects.POISON, 100), 1);
    }
  }
}
