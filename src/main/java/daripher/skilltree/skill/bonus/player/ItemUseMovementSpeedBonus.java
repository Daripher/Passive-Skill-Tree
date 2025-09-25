package daripher.skilltree.skill.bonus.player;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.condition.item.EquipmentCondition;
import daripher.skilltree.skill.bonus.condition.item.ItemCondition;
import daripher.skilltree.skill.bonus.condition.item.NoneItemCondition;
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
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class ItemUseMovementSpeedBonus implements SkillBonus<ItemUseMovementSpeedBonus> {
  private float multiplier;
  private @Nonnull LivingMultiplier playerMultiplier = NoneLivingMultiplier.INSTANCE;
  private @Nonnull LivingCondition playerCondition = NoneLivingCondition.INSTANCE;
  private @Nonnull ItemCondition itemCondition = NoneItemCondition.INSTANCE;

  public ItemUseMovementSpeedBonus(float multiplier) {
    this.multiplier = multiplier;
  }

  public float getMultiplier(Player player, ItemStack itemStack) {
    if (!playerCondition.isConditionMet(player)) return 0f;
    if (!itemCondition.met(itemStack)) return 0f;
    return multiplier * playerMultiplier.getValue(player);
  }

  @Override
  public SkillBonus.Serializer getSerializer() {
    return PSTSkillBonuses.ITEM_USE_MOVEMENT_SPEED.get();
  }

  @Override
  public ItemUseMovementSpeedBonus copy() {
    ItemUseMovementSpeedBonus bonus = new ItemUseMovementSpeedBonus(multiplier);
    bonus.playerMultiplier = this.playerMultiplier;
    bonus.playerCondition = this.playerCondition;
    bonus.itemCondition = this.itemCondition;
    return bonus;
  }

  @Override
  public ItemUseMovementSpeedBonus multiply(double multiplier) {
    this.multiplier *= (float) multiplier;
    return this;
  }

  @Override
  public boolean canMerge(SkillBonus<?> other) {
    if (!(other instanceof ItemUseMovementSpeedBonus otherBonus)) return false;
    if (!Objects.equals(otherBonus.playerMultiplier, this.playerMultiplier)) return false;
    if (!Objects.equals(otherBonus.itemCondition, this.itemCondition)) return false;
    return Objects.equals(otherBonus.playerCondition, this.playerCondition);
  }

  @Override
  public SkillBonus<ItemUseMovementSpeedBonus> merge(SkillBonus<?> other) {
    if (!(other instanceof ItemUseMovementSpeedBonus otherBonus)) {
      throw new IllegalArgumentException();
    }
    float mergedMultiplier = otherBonus.multiplier + this.multiplier;
    ItemUseMovementSpeedBonus mergedBonus = new ItemUseMovementSpeedBonus(mergedMultiplier);
    mergedBonus.playerMultiplier = this.playerMultiplier;
    mergedBonus.playerCondition = this.playerCondition;
    mergedBonus.itemCondition = this.itemCondition;
    return mergedBonus;
  }

  @Override
  public MutableComponent getTooltip() {
    MutableComponent tooltip;
    String keySuffix = isPositive() ? "positive" : "negative";
    String multiplierString = TooltipHelper.formatNumber(Mth.abs(multiplier) * 100);
    String descriptionKey = getDescriptionId() + "." + keySuffix;
    Component itemConditionTooltip = itemCondition.getTooltip("plural");
    if (isPositive() && multiplier == -1) {
      descriptionKey = getDescriptionId() + ".remove";
      tooltip = Component.translatable(descriptionKey, itemConditionTooltip);
    } else {
      tooltip = Component.translatable(descriptionKey, itemConditionTooltip, multiplierString);
    }
    tooltip = playerMultiplier.getTooltip(tooltip, Target.PLAYER);
    tooltip = playerCondition.getTooltip(tooltip, Target.PLAYER);
    return tooltip.withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
  }

  @Override
  public boolean isPositive() {
    return multiplier < 0;
  }

  @Override
  public void addEditorWidgets(
      SkillTreeEditor editor, int row, Consumer<ItemUseMovementSpeedBonus> consumer) {
    editor.addLabel(0, 0, "Multiplier", ChatFormatting.GOLD);
    editor.increaseHeight(19);
    editor
        .addNumericTextField(0, 0, 50, 14, multiplier)
        .setNumericResponder(value -> selectMultiplier(consumer, value));
    editor.increaseHeight(19);
    editor.addLabel(0, 0, "Item Condition", ChatFormatting.GOLD);
    editor.increaseHeight(19);
    editor
        .addSelectionMenu(0, 0, 200, itemCondition)
        .setResponder(condition -> selectItemCondition(editor, consumer, condition))
        .setMenuInitFunc(() -> addItemConditionWidgets(editor, consumer));
    editor.increaseHeight(19);
    editor.addLabel(0, 0, "Player Condition", ChatFormatting.GOLD);
    editor.increaseHeight(19);
    editor
        .addSelectionMenu(0, 0, 200, playerCondition)
        .setResponder(condition -> selectPlayerCondition(editor, consumer, condition))
        .setMenuInitFunc(() -> addPlayerConditionWidgets(editor, consumer));
    editor.increaseHeight(19);
    editor.addLabel(0, 0, "Player Multiplier", ChatFormatting.GOLD);
    editor.increaseHeight(19);
    editor
        .addSelectionMenu(0, 0, 200, playerMultiplier)
        .setResponder(multiplier -> selectPlayerMultiplier(editor, consumer, multiplier))
        .setMenuInitFunc(() -> addPlayerMultiplierWidgets(editor, consumer));
    editor.increaseHeight(19);
  }

  private void selectMultiplier(Consumer<ItemUseMovementSpeedBonus> consumer, Double value) {
    setMultiplier(value.floatValue());
    consumer.accept(this.copy());
  }

  private void addPlayerMultiplierWidgets(
      SkillTreeEditor editor, Consumer<ItemUseMovementSpeedBonus> consumer) {
    playerMultiplier.addEditorWidgets(
        editor,
        multiplier -> {
          setPlayerMultiplier(multiplier);
          consumer.accept(this.copy());
        });
  }

  private void selectPlayerMultiplier(
      SkillTreeEditor editor,
      Consumer<ItemUseMovementSpeedBonus> consumer,
      LivingMultiplier multiplier) {
    setPlayerMultiplier(multiplier);
    consumer.accept(this.copy());
    editor.rebuildWidgets();
  }

  private void addPlayerConditionWidgets(
      SkillTreeEditor editor, Consumer<ItemUseMovementSpeedBonus> consumer) {
    playerCondition.addEditorWidgets(
        editor,
        c -> {
          setPlayerCondition(c);
          consumer.accept(this.copy());
        });
  }

  private void selectPlayerCondition(
      SkillTreeEditor editor,
      Consumer<ItemUseMovementSpeedBonus> consumer,
      LivingCondition condition) {
    setPlayerCondition(condition);
    consumer.accept(this.copy());
    editor.rebuildWidgets();
  }

  private void addItemConditionWidgets(
      SkillTreeEditor editor, Consumer<ItemUseMovementSpeedBonus> consumer) {
    itemCondition.addEditorWidgets(
        editor,
        c -> {
          setItemCondition(c);
          consumer.accept(this.copy());
        });
  }

  private void selectItemCondition(
      SkillTreeEditor editor,
      Consumer<ItemUseMovementSpeedBonus> consumer,
      ItemCondition condition) {
    setItemCondition(condition);
    consumer.accept(this.copy());
    editor.rebuildWidgets();
  }

  public SkillBonus<?> setPlayerCondition(LivingCondition condition) {
    this.playerCondition = condition;
    return this;
  }

  public SkillBonus<?> setItemCondition(ItemCondition condition) {
    this.itemCondition = condition;
    return this;
  }

  public SkillBonus<?> setPlayerMultiplier(LivingMultiplier multiplier) {
    this.playerMultiplier = multiplier;
    return this;
  }

  public void setMultiplier(float multiplier) {
    this.multiplier = multiplier;
  }

  public static class Serializer implements SkillBonus.Serializer {
    @Override
    public ItemUseMovementSpeedBonus deserialize(JsonObject json) throws JsonParseException {
      float multiplier = SerializationHelper.getElement(json, "multiplier").getAsFloat();
      ItemUseMovementSpeedBonus bonus = new ItemUseMovementSpeedBonus(multiplier);
      bonus.playerMultiplier =
          SerializationHelper.deserializeLivingMultiplier(json, "player_multiplier");
      bonus.playerCondition =
          SerializationHelper.deserializeLivingCondition(json, "player_condition");
      bonus.itemCondition = SerializationHelper.deserializeItemCondition(json);
      return bonus;
    }

    @Override
    public void serialize(JsonObject json, SkillBonus<?> bonus) {
      if (!(bonus instanceof ItemUseMovementSpeedBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      json.addProperty("multiplier", aBonus.multiplier);
      SerializationHelper.serializeLivingMultiplier(
          json, aBonus.playerMultiplier, "player_multiplier");
      SerializationHelper.serializeLivingCondition(
          json, aBonus.playerCondition, "player_condition");
      SerializationHelper.serializeItemCondition(json, aBonus.itemCondition);
    }

    @Override
    public ItemUseMovementSpeedBonus deserialize(CompoundTag tag) {
      float multiplier = tag.getFloat("multiplier");
      ItemUseMovementSpeedBonus bonus = new ItemUseMovementSpeedBonus(multiplier);
      bonus.playerMultiplier =
          SerializationHelper.deserializeLivingMultiplier(tag, "player_multiplier");
      bonus.playerCondition =
          SerializationHelper.deserializeLivingCondition(tag, "player_condition");
      bonus.itemCondition = SerializationHelper.deserializeItemCondition(tag);
      return bonus;
    }

    @Override
    public CompoundTag serialize(SkillBonus<?> bonus) {
      if (!(bonus instanceof ItemUseMovementSpeedBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      tag.putFloat("multiplier", aBonus.multiplier);
      SerializationHelper.serializeLivingMultiplier(
          tag, aBonus.playerMultiplier, "player_multiplier");
      SerializationHelper.serializeLivingCondition(tag, aBonus.playerCondition, "player_condition");
      SerializationHelper.serializeItemCondition(tag, aBonus.itemCondition);
      return tag;
    }

    @Override
    public ItemUseMovementSpeedBonus deserialize(FriendlyByteBuf buf) {
      float multiplier = buf.readFloat();
      ItemUseMovementSpeedBonus bonus = new ItemUseMovementSpeedBonus(multiplier);
      bonus.playerMultiplier = NetworkHelper.readLivingMultiplier(buf);
      bonus.playerCondition = NetworkHelper.readLivingCondition(buf);
      bonus.itemCondition = NetworkHelper.readItemCondition(buf);
      return bonus;
    }

    @Override
    public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
      if (!(bonus instanceof ItemUseMovementSpeedBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      buf.writeFloat(aBonus.multiplier);
      NetworkHelper.writeLivingMultiplier(buf, aBonus.playerMultiplier);
      NetworkHelper.writeLivingCondition(buf, aBonus.playerCondition);
      NetworkHelper.writeItemCondition(buf, aBonus.itemCondition);
    }

    @Override
    public SkillBonus<?> createDefaultInstance() {
      return new ItemUseMovementSpeedBonus(-0.1f)
          .setItemCondition(new EquipmentCondition(EquipmentCondition.Type.SHIELD));
    }
  }
}
