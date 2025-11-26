package daripher.skilltree.skill.bonus.event;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTEventListeners;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.EventListenerBonus;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.predicate.living.LivingEntityPredicate;
import daripher.skilltree.skill.bonus.predicate.living.NoneLivingEntityPredicate;
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
import net.minecraft.world.entity.player.Player;

public class TickingEventListener implements SkillEventListener {
  private LivingEntityPredicate playerCondition = NoneLivingEntityPredicate.INSTANCE;
  private LivingMultiplier playerMultiplier = NoneLivingMultiplier.INSTANCE;
  private int cooldown;

  public TickingEventListener(int cooldown) {
    this.cooldown = cooldown;
  }

  public TickingEventListener() {
    this(100);
  }

  public void onEvent(@Nonnull Player player, @Nonnull EventListenerBonus<?> skill) {
    if (!playerCondition.test(player)) {
      return;
    }
    skill.multiply(playerMultiplier.getValue(player)).applyEffect(player);
  }

  @Override
  public MutableComponent getTooltip(Component bonusTooltip) {
    String descriptionId = getDescriptionId();
    MutableComponent eventTooltip;
    if (cooldown <= 20) {
      descriptionId += ".second";
      eventTooltip = Component.translatable(descriptionId, bonusTooltip);
    } else {
      String timeDescription;
      if (cooldown < 1200) {
        descriptionId += ".seconds";
        timeDescription = TooltipHelper.formatNumber(cooldown / 20f);
        eventTooltip = Component.translatable(descriptionId, bonusTooltip, timeDescription);
      }
      else {
        if (cooldown == 1200) {
          descriptionId += ".minute";
          eventTooltip = Component.translatable(descriptionId, bonusTooltip);
        } else {
          descriptionId += ".minutes";
          timeDescription = TooltipHelper.formatNumber(cooldown / 20f / 60f);
          eventTooltip = Component.translatable(descriptionId, bonusTooltip, timeDescription);
        }
      }
    }
    eventTooltip = playerCondition.getTooltip(eventTooltip, SkillBonus.Target.PLAYER);
    eventTooltip = playerMultiplier.getTooltip(eventTooltip, SkillBonus.Target.PLAYER);
    return eventTooltip;
  }

  @Override
  public SkillEventListener.Serializer getSerializer() {
    return PSTEventListeners.TICKING.get();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TickingEventListener that = (TickingEventListener) o;
    return cooldown == that.cooldown &&
           Objects.equals(playerCondition, that.playerCondition) &&
           Objects.equals(playerMultiplier, that.playerMultiplier);
  }

  @Override
  public int hashCode() {
    return Objects.hash(playerCondition, playerMultiplier, cooldown);
  }

  @Override
  public void addEditorWidgets(SkillTreeEditor editor, Consumer<SkillEventListener> consumer) {
    editor.addLabel(0, 0, "Cooldown", ChatFormatting.GREEN);
    editor.increaseHeight(19);
    editor.addNumericTextField(0,0,90,14,cooldown)
        .setNumericFilter(value -> value.intValue() >= 1 && value.intValue() == value)
        .setNumericResponder(value -> selectCooldown(consumer, value));
    editor.increaseHeight(19);
    editor.addLabel(0, 0, "Player Condition", ChatFormatting.GREEN);
    editor.increaseHeight(19);
    editor
        .addSelectionMenu(0, 0, 200, playerCondition)
        .setResponder(condition -> selectPlayerCondition(editor, consumer, condition))
        .setMenuInitFunc(() -> addPlayerConditionWidgets(editor, consumer));
    editor.increaseHeight(19);
    editor.addLabel(0, 0, "Player Multiplier", ChatFormatting.GREEN);
    editor.increaseHeight(19);
    editor
        .addSelectionMenu(0, 0, 200, playerMultiplier)
        .setResponder(multiplier -> selectPlayerMultiplier(editor, consumer, multiplier))
        .setMenuInitFunc(() -> addPlayerMultiplierWidgets(editor, consumer));
    editor.increaseHeight(19);
  }

  private void selectCooldown(
      Consumer<SkillEventListener> consumer, Double value) {
    setCooldown(value.intValue());
    consumer.accept(this);
  }

  private void addPlayerMultiplierWidgets(
      SkillTreeEditor editor, Consumer<SkillEventListener> consumer) {
    playerMultiplier.addEditorWidgets(
        editor,
        multiplier -> {
          setPlayerMultiplier(multiplier);
          consumer.accept(this);
        });
  }

  private void selectPlayerMultiplier(
      SkillTreeEditor editor, Consumer<SkillEventListener> consumer, LivingMultiplier multiplier) {
    setPlayerMultiplier(multiplier);
    consumer.accept(this);
    editor.rebuildWidgets();
  }

  private void addPlayerConditionWidgets(
      SkillTreeEditor editor, Consumer<SkillEventListener> consumer) {
    playerCondition.addEditorWidgets(
        editor,
        condition -> {
          setPlayerCondition(condition);
          consumer.accept(this);
        });
  }

  private void selectPlayerCondition(
      SkillTreeEditor editor, Consumer<SkillEventListener> consumer, LivingEntityPredicate condition) {
    setPlayerCondition(condition);
    consumer.accept(this);
    editor.rebuildWidgets();
  }

  @Override
  public SkillBonus.Target getTarget() {
    return SkillBonus.Target.PLAYER;
  }

  public int getCooldown() {
    return cooldown;
  }

  public void setCooldown(int cooldown) {
    this.cooldown = cooldown;
  }

  public void setPlayerCondition(LivingEntityPredicate playerCondition) {
    this.playerCondition = playerCondition;
  }

  public void setPlayerMultiplier(LivingMultiplier playerMultiplier) {
    this.playerMultiplier = playerMultiplier;
  }

  public static class Serializer implements SkillEventListener.Serializer {
    @Override
    public SkillEventListener deserialize(JsonObject json) throws JsonParseException {
      TickingEventListener listener = new TickingEventListener();
      listener.setPlayerCondition(
          SerializationHelper.deserializeLivingCondition(json, "player_condition"));
      listener.setPlayerMultiplier(
          SerializationHelper.deserializeLivingMultiplier(json, "player_multiplier"));
      if (!json.has("cooldown")) {
        listener.setCooldown(10);
      } else {
        listener.setCooldown(json.get("cooldown").getAsInt());
      }
      return listener;
    }

    @Override
    public void serialize(JsonObject json, SkillEventListener listener) {
      if (!(listener instanceof TickingEventListener aListener)) {
        throw new IllegalArgumentException();
      }
      SerializationHelper.serializeLivingCondition(
          json, aListener.playerCondition, "player_condition");
      SerializationHelper.serializeLivingMultiplier(
          json, aListener.playerMultiplier, "player_multiplier");
      json.addProperty("cooldown", aListener.cooldown);
    }

    @Override
    public SkillEventListener deserialize(CompoundTag tag) {
      TickingEventListener listener = new TickingEventListener();
      listener.setPlayerCondition(
          SerializationHelper.deserializeLivingCondition(tag, "player_condition"));
      listener.setPlayerMultiplier(
          SerializationHelper.deserializeLivingMultiplier(tag, "player_multiplier"));
      if (!tag.contains("cooldown")) {
        listener.setCooldown(10);
      } else {
        listener.setCooldown(tag.getInt("cooldown"));
      }
      return listener;
    }

    @Override
    public CompoundTag serialize(SkillEventListener listener) {
      if (!(listener instanceof TickingEventListener aListener)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      SerializationHelper.serializeLivingCondition(
          tag, aListener.playerCondition, "player_condition");
      SerializationHelper.serializeLivingMultiplier(
          tag, aListener.playerMultiplier, "player_multiplier");
      tag.putInt("cooldown", aListener.cooldown);
      return tag;
    }

    @Override
    public SkillEventListener deserialize(FriendlyByteBuf buf) {
      TickingEventListener listener = new TickingEventListener();
      listener.setPlayerCondition(NetworkHelper.readLivingCondition(buf));
      listener.setPlayerMultiplier(NetworkHelper.readLivingMultiplier(buf));
      listener.setCooldown(buf.readInt());
      return listener;
    }

    @Override
    public void serialize(FriendlyByteBuf buf, SkillEventListener listener) {
      if (!(listener instanceof TickingEventListener aListener)) {
        throw new IllegalArgumentException();
      }
      NetworkHelper.writeLivingCondition(buf, aListener.playerCondition);
      NetworkHelper.writeLivingMultiplier(buf, aListener.playerMultiplier);
      buf.writeInt(aListener.cooldown);
    }

    @Override
    public SkillEventListener createDefaultInstance() {
      return new TickingEventListener();
    }
  }
}
