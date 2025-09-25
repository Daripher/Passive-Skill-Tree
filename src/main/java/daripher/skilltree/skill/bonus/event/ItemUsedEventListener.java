package daripher.skilltree.skill.bonus.event;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTEventListeners;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.EventListenerBonus;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.condition.item.ItemCondition;
import daripher.skilltree.skill.bonus.condition.item.PotionCondition;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ItemUsedEventListener implements SkillEventListener {
  private LivingCondition playerCondition = NoneLivingCondition.INSTANCE;
  private LivingMultiplier playerMultiplier = NoneLivingMultiplier.INSTANCE;
  private ItemCondition itemCondition;

  public ItemUsedEventListener(ItemCondition itemCondition) {
    this.itemCondition = itemCondition;
  }

  public void onEvent(
      @Nonnull Player player, @Nonnull ItemStack stack, @Nonnull EventListenerBonus<?> skill) {
    if (!playerCondition.isConditionMet(player)) return;
    if (!itemCondition.met(stack)) return;
    skill.multiply(playerMultiplier.getValue(player)).applyEffect(player);
  }

  @Override
  public MutableComponent getTooltip(Component bonusTooltip) {
    Component itemTooltip = itemCondition.getTooltip();
    MutableComponent eventTooltip =
        Component.translatable(getDescriptionId(), bonusTooltip, itemTooltip);
    eventTooltip = playerCondition.getTooltip(eventTooltip, SkillBonus.Target.PLAYER);
    eventTooltip = playerMultiplier.getTooltip(eventTooltip, SkillBonus.Target.PLAYER);
    return eventTooltip;
  }

  @Override
  public SkillEventListener.Serializer getSerializer() {
    return PSTEventListeners.ITEM_USED.get();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ItemUsedEventListener listener = (ItemUsedEventListener) o;
    return Objects.equals(playerCondition, listener.playerCondition)
        && Objects.equals(playerMultiplier, listener.playerMultiplier)
        && Objects.equals(itemCondition, listener.itemCondition);
  }

  @Override
  public int hashCode() {
    return Objects.hash(playerCondition, playerMultiplier, itemCondition);
  }

  @Override
  public void addEditorWidgets(SkillTreeEditor editor, Consumer<SkillEventListener> consumer) {
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
    editor.addLabel(0, 0, "Item Condition", ChatFormatting.GREEN);
    editor.increaseHeight(19);
    editor
        .addSelectionMenu(0, 0, 200, itemCondition)
        .setResponder(condition -> selectItemCondition(editor, consumer, condition))
        .setMenuInitFunc(() -> addItemConditionWidgets(editor, consumer));
    editor.increaseHeight(19);
  }

  private void addItemConditionWidgets(
      SkillTreeEditor editor, Consumer<SkillEventListener> consumer) {
    itemCondition.addEditorWidgets(
        editor,
        condition -> {
          setItemCondition(condition);
          consumer.accept(this);
        });
  }

  private void selectItemCondition(
      SkillTreeEditor editor, Consumer<SkillEventListener> consumer, ItemCondition condition) {
    setItemCondition(condition);
    consumer.accept(this);
    editor.rebuildWidgets();
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
      SkillTreeEditor editor, Consumer<SkillEventListener> consumer, LivingCondition condition) {
    setPlayerCondition(condition);
    consumer.accept(this);
    editor.rebuildWidgets();
  }

  @Override
  public SkillBonus.Target getTarget() {
    return SkillBonus.Target.PLAYER;
  }

  public void setPlayerCondition(LivingCondition playerCondition) {
    this.playerCondition = playerCondition;
  }

  public void setPlayerMultiplier(LivingMultiplier playerMultiplier) {
    this.playerMultiplier = playerMultiplier;
  }

  public void setItemCondition(ItemCondition itemCondition) {
    this.itemCondition = itemCondition;
  }

  public static class Serializer implements SkillEventListener.Serializer {
    @Override
    public SkillEventListener deserialize(JsonObject json) throws JsonParseException {
      ItemCondition itemCondition = SerializationHelper.deserializeItemCondition(json);
      ItemUsedEventListener listener = new ItemUsedEventListener(itemCondition);
      listener.setPlayerCondition(
          SerializationHelper.deserializeLivingCondition(json, "player_condition"));
      listener.setPlayerMultiplier(
          SerializationHelper.deserializeLivingMultiplier(json, "player_multiplier"));
      return listener;
    }

    @Override
    public void serialize(JsonObject json, SkillEventListener listener) {
      if (!(listener instanceof ItemUsedEventListener aListener)) {
        throw new IllegalArgumentException();
      }
      SerializationHelper.serializeItemCondition(json, aListener.itemCondition);
      SerializationHelper.serializeLivingCondition(
          json, aListener.playerCondition, "player_condition");
      SerializationHelper.serializeLivingMultiplier(
          json, aListener.playerMultiplier, "player_multiplier");
    }

    @Override
    public SkillEventListener deserialize(CompoundTag tag) {
      ItemCondition itemCondition = SerializationHelper.deserializeItemCondition(tag);
      ItemUsedEventListener listener = new ItemUsedEventListener(itemCondition);
      listener.setPlayerCondition(
          SerializationHelper.deserializeLivingCondition(tag, "player_condition"));
      listener.setPlayerMultiplier(
          SerializationHelper.deserializeLivingMultiplier(tag, "player_multiplier"));
      return listener;
    }

    @Override
    public CompoundTag serialize(SkillEventListener listener) {
      if (!(listener instanceof ItemUsedEventListener aListener)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      SerializationHelper.serializeItemCondition(tag, aListener.itemCondition);
      SerializationHelper.serializeLivingCondition(
          tag, aListener.playerCondition, "player_condition");
      SerializationHelper.serializeLivingMultiplier(
          tag, aListener.playerMultiplier, "player_multiplier");
      return tag;
    }

    @Override
    public SkillEventListener deserialize(FriendlyByteBuf buf) {
      ItemCondition itemCondition = NetworkHelper.readItemCondition(buf);
      ItemUsedEventListener listener = new ItemUsedEventListener(itemCondition);
      listener.setPlayerCondition(NetworkHelper.readLivingCondition(buf));
      listener.setPlayerMultiplier(NetworkHelper.readLivingMultiplier(buf));
      return listener;
    }

    @Override
    public void serialize(FriendlyByteBuf buf, SkillEventListener listener) {
      if (!(listener instanceof ItemUsedEventListener aListener)) {
        throw new IllegalArgumentException();
      }
      NetworkHelper.writeItemCondition(buf, aListener.itemCondition);
      NetworkHelper.writeLivingCondition(buf, aListener.playerCondition);
      NetworkHelper.writeLivingMultiplier(buf, aListener.playerMultiplier);
    }

    @Override
    public SkillEventListener createDefaultInstance() {
      return new ItemUsedEventListener(new PotionCondition(PotionCondition.Type.ANY));
    }
  }
}
