package daripher.skilltree.skill.bonus.event;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.screen.SkillTreeEditorScreen;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.data.SerializationHelper;
import daripher.skilltree.init.PSTDamageConditions;
import daripher.skilltree.init.PSTEventListeners;
import daripher.skilltree.init.PSTLivingConditions;
import daripher.skilltree.init.PSTLivingMultipliers;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.condition.damage.DamageCondition;
import daripher.skilltree.skill.bonus.condition.damage.NoneDamageCondition;
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
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class AttackEventListener implements SkillEventListener {
  private LivingCondition playerCondition = NoneLivingCondition.INSTANCE;
  private LivingCondition enemyCondition = NoneLivingCondition.INSTANCE;
  private DamageCondition damageCondition = NoneDamageCondition.INSTANCE;
  private LivingMultiplier playerMultiplier = NoneLivingMultiplier.INSTANCE;
  private LivingMultiplier enemyMultiplier = NoneLivingMultiplier.INSTANCE;
  private SkillBonus.Target target = SkillBonus.Target.ENEMY;

  public void onAttack(
      @Nonnull Player player,
      @Nonnull LivingEntity enemy,
      @Nonnull DamageSource damage,
      @Nonnull Consumer<Float> multiplierConsumer,
      @Nonnull SkillBonus.EventListener skill) {
    if (!playerCondition.met(player)) return;
    if (!enemyCondition.met(enemy)) return;
    if (!damageCondition.met(damage)) return;
    LivingEntity target = this.target == SkillBonus.Target.PLAYER ? player : enemy;
    multiplierConsumer.accept(playerMultiplier.getValue(player) * enemyMultiplier.getValue(enemy));
    skill.applyEffect(target);
  }

  @Override
  public MutableComponent getTooltip(Component bonusTooltip) {
    Component damageDescription = damageCondition.getTooltip();
    MutableComponent eventTooltip =
        Component.translatable(getDescriptionId(), bonusTooltip, damageDescription);
    eventTooltip = playerCondition.getTooltip(eventTooltip, "you");
    eventTooltip = enemyCondition.getTooltip(eventTooltip, "target");
    //    eventTooltip = playerMultiplier.getTooltip(eventTooltip, "you");
    //    eventTooltip = enemyMultiplier.getTooltip(eventTooltip, "target");
    return eventTooltip;
  }

  @Override
  public SkillEventListener.Serializer getSerializer() {
    return PSTEventListeners.ATTACK.get();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AttackEventListener listener = (AttackEventListener) o;
    return Objects.equals(playerCondition, listener.playerCondition)
        && Objects.equals(enemyCondition, listener.enemyCondition)
        && Objects.equals(damageCondition, listener.damageCondition)
        && Objects.equals(playerMultiplier, listener.playerMultiplier)
        && Objects.equals(enemyMultiplier, listener.enemyMultiplier)
        && target == listener.target;
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        playerCondition,
        enemyCondition,
        damageCondition,
        playerMultiplier,
        enemyMultiplier,
        target);
  }

  @Override
  public void addEditorWidgets(
      SkillTreeEditorScreen editor, Consumer<SkillEventListener> consumer) {
    editor.addLabel(0, 0, "Player Condition", ChatFormatting.GREEN);
    editor.shiftWidgets(0, 19);
    editor
        .addDropDownList(0, 0, 200, 14, 10, playerCondition, PSTLivingConditions.conditionsList())
        .setToNameFunc(c -> Component.literal(PSTLivingConditions.getName(c)))
        .setResponder(
            c -> {
              setPlayerCondition(c);
              consumer.accept(this);
              editor.rebuildWidgets();
            });
    editor.shiftWidgets(0, 19);
    playerCondition.addEditorWidgets(
        editor,
        c -> {
          setPlayerCondition(c);
          consumer.accept(this);
        });
    editor.addLabel(0, 0, "Enemy Condition", ChatFormatting.GREEN);
    editor.shiftWidgets(0, 19);
    editor
        .addDropDownList(0, 0, 200, 14, 10, enemyCondition, PSTLivingConditions.conditionsList())
        .setToNameFunc(c -> Component.literal(PSTLivingConditions.getName(c)))
        .setResponder(
            c -> {
              setEnemyCondition(c);
              consumer.accept(this);
              editor.rebuildWidgets();
            });
    editor.shiftWidgets(0, 19);
    enemyCondition.addEditorWidgets(
        editor,
        c -> {
          setEnemyCondition(c);
          consumer.accept(this);
        });
    editor.addLabel(0, 0, "Player Multiplier", ChatFormatting.GREEN);
    editor.shiftWidgets(0, 19);
    editor
        .addDropDownList(0, 0, 200, 14, 10, playerMultiplier, PSTLivingMultipliers.multiplierList())
        .setToNameFunc(m -> Component.literal(PSTLivingMultipliers.getName(m)))
        .setResponder(
            m -> {
              setPlayerMultiplier(m);
              consumer.accept(this);
              editor.rebuildWidgets();
            });
    editor.shiftWidgets(0, 19);
    playerMultiplier.addEditorWidgets(
        editor,
        m -> {
          setPlayerMultiplier(m);
          consumer.accept(this);
        });
    editor.addLabel(0, 0, "Enemy Multiplier", ChatFormatting.GREEN);
    editor.shiftWidgets(0, 19);
    editor
        .addDropDownList(0, 0, 200, 14, 10, enemyMultiplier, PSTLivingMultipliers.multiplierList())
        .setToNameFunc(m -> Component.literal(PSTLivingMultipliers.getName(m)))
        .setResponder(
            m -> {
              setEnemyMultiplier(m);
              consumer.accept(this);
              editor.rebuildWidgets();
            });
    editor.shiftWidgets(0, 19);
    enemyMultiplier.addEditorWidgets(
        editor,
        m -> {
          setPlayerMultiplier(m);
          consumer.accept(this);
        });
    editor.addLabel(0, 0, "Damage", ChatFormatting.GREEN);
    editor.addLabel(105, 0, "Target", ChatFormatting.GREEN);
    editor.shiftWidgets(0, 19);
    editor
        .addDropDownList(0, 0, 95, 14, 10, damageCondition, PSTDamageConditions.conditionsList())
        .setToNameFunc(c -> Component.literal(PSTDamageConditions.getName(c)))
        .setResponder(
            c -> {
              setDamageCondition(c);
              consumer.accept(this);
            });
    editor
        .addDropDownList(105, 0, 95, 14, 10, target)
        .setToNameFunc(t -> Component.literal(TooltipHelper.idToName(t.name().toLowerCase())))
        .setResponder(
            t -> {
              setTarget(t);
              consumer.accept(this);
            });
    editor.shiftWidgets(0, 19);
  }

  @Override
  public SkillBonus.Target getTarget() {
    return target;
  }

  public void setDamageCondition(DamageCondition damageCondition) {
    this.damageCondition = damageCondition;
  }

  public void setEnemyCondition(LivingCondition enemyCondition) {
    this.enemyCondition = enemyCondition;
  }

  public void setPlayerCondition(LivingCondition playerCondition) {
    this.playerCondition = playerCondition;
  }

  public void setEnemyMultiplier(LivingMultiplier enemyMultiplier) {
    this.enemyMultiplier = enemyMultiplier;
  }

  public void setPlayerMultiplier(LivingMultiplier playerMultiplier) {
    this.playerMultiplier = playerMultiplier;
  }

  public void setTarget(SkillBonus.Target target) {
    this.target = target;
  }

  public static class Serializer implements SkillEventListener.Serializer {
    @Override
    public SkillEventListener deserialize(JsonObject json) throws JsonParseException {
      AttackEventListener listener = new AttackEventListener();
      listener.setDamageCondition(SerializationHelper.deserializeDamageCondition(json));
      listener.setEnemyCondition(
          SerializationHelper.deserializeLivingCondition(json, "enemy_condition"));
      listener.setPlayerCondition(
          SerializationHelper.deserializeLivingCondition(json, "player_condition"));
      listener.setEnemyMultiplier(
          SerializationHelper.deserializeLivingMultiplier(json, "enemy_multiplier"));
      listener.setPlayerMultiplier(
          SerializationHelper.deserializeLivingMultiplier(json, "player_multiplier"));
      listener.setTarget(SkillBonus.Target.valueOf(json.get("target").getAsString().toUpperCase()));
      return listener;
    }

    @Override
    public void serialize(JsonObject json, SkillEventListener listener) {
      if (!(listener instanceof AttackEventListener aListener)) {
        throw new IllegalArgumentException();
      }
      SerializationHelper.serializeDamageCondition(json, aListener.damageCondition);
      SerializationHelper.serializeLivingCondition(
          json, aListener.enemyCondition, "enemy_condition");
      SerializationHelper.serializeLivingCondition(
          json, aListener.playerCondition, "player_condition");
      SerializationHelper.serializeLivingMultiplier(
          json, aListener.enemyMultiplier, "enemy_multiplier");
      SerializationHelper.serializeLivingMultiplier(
          json, aListener.playerMultiplier, "player_multiplier");
      json.addProperty("target", aListener.target.name().toLowerCase());
    }

    @Override
    public SkillEventListener deserialize(CompoundTag tag) {
      AttackEventListener listener = new AttackEventListener();
      listener.setDamageCondition(SerializationHelper.deserializeDamageCondition(tag));
      listener.setEnemyCondition(
          SerializationHelper.deserializeLivingCondition(tag, "enemy_condition"));
      listener.setPlayerCondition(
          SerializationHelper.deserializeLivingCondition(tag, "player_condition"));
      listener.setEnemyMultiplier(
          SerializationHelper.deserializeLivingMultiplier(tag, "enemy_multiplier"));
      listener.setPlayerMultiplier(
          SerializationHelper.deserializeLivingMultiplier(tag, "player_multiplier"));
      listener.setTarget(SkillBonus.Target.valueOf(tag.getString("target").toUpperCase()));
      return listener;
    }

    @Override
    public CompoundTag serialize(SkillEventListener listener) {
      if (!(listener instanceof AttackEventListener aListener)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      SerializationHelper.serializeDamageCondition(tag, aListener.damageCondition);
      SerializationHelper.serializeLivingCondition(
          tag, aListener.enemyCondition, "enemy_condition");
      SerializationHelper.serializeLivingCondition(
          tag, aListener.playerCondition, "player_condition");
      SerializationHelper.serializeLivingMultiplier(
          tag, aListener.enemyMultiplier, "enemy_multiplier");
      SerializationHelper.serializeLivingMultiplier(
          tag, aListener.playerMultiplier, "player_multiplier");
      tag.putString("target", aListener.target.name().toLowerCase());
      return tag;
    }

    @Override
    public SkillEventListener deserialize(FriendlyByteBuf buf) {
      AttackEventListener listener = new AttackEventListener();
      listener.setDamageCondition(NetworkHelper.readDamageCondition(buf));
      listener.setEnemyCondition(NetworkHelper.readLivingCondition(buf));
      listener.setPlayerCondition(NetworkHelper.readLivingCondition(buf));
      listener.setEnemyMultiplier(NetworkHelper.readLivingMultiplier(buf));
      listener.setPlayerMultiplier(NetworkHelper.readLivingMultiplier(buf));
      listener.setTarget(SkillBonus.Target.values()[buf.readInt()]);
      return listener;
    }

    @Override
    public void serialize(FriendlyByteBuf buf, SkillEventListener listener) {
      if (!(listener instanceof AttackEventListener aListener)) {
        throw new IllegalArgumentException();
      }
      NetworkHelper.writeDamageCondition(buf, aListener.damageCondition);
      NetworkHelper.writeLivingCondition(buf, aListener.enemyCondition);
      NetworkHelper.writeLivingCondition(buf, aListener.playerCondition);
      NetworkHelper.writeLivingMultiplier(buf, aListener.enemyMultiplier);
      NetworkHelper.writeLivingMultiplier(buf, aListener.playerMultiplier);
      buf.writeInt(aListener.target.ordinal());
    }

    @Override
    public SkillEventListener createDefaultInstance() {
      return new AttackEventListener();
    }
  }
}
