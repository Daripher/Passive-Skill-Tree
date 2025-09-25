package daripher.skilltree.skill.bonus.player;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
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
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class ItemDurabilityLossAvoidanceBonus
    implements SkillBonus<ItemDurabilityLossAvoidanceBonus> {
  private float chance;
  private @Nonnull LivingMultiplier playerMultiplier = NoneLivingMultiplier.INSTANCE;
  private @Nonnull LivingCondition playerCondition = NoneLivingCondition.INSTANCE;
  private @Nonnull ItemCondition itemCondition = NoneItemCondition.INSTANCE;

  public ItemDurabilityLossAvoidanceBonus(float chance) {
    this.chance = chance;
  }

  public float getChance(Player player, ItemStack itemStack) {
    if (!playerCondition.isConditionMet(player)) return 0f;
    if (!itemCondition.met(itemStack)) return 0f;
    return chance * playerMultiplier.getValue(player);
  }

  @Override
  public SkillBonus.Serializer getSerializer() {
    return PSTSkillBonuses.ITEM_DURABILITY_LOSS_AVOIDANCE.get();
  }

  @Override
  public ItemDurabilityLossAvoidanceBonus copy() {
    ItemDurabilityLossAvoidanceBonus bonus = new ItemDurabilityLossAvoidanceBonus(chance);
    bonus.playerMultiplier = this.playerMultiplier;
    bonus.playerCondition = this.playerCondition;
    bonus.itemCondition = this.itemCondition;
    return bonus;
  }

  @Override
  public ItemDurabilityLossAvoidanceBonus multiply(double multiplier) {
    chance *= (float) multiplier;
    return this;
  }

  @Override
  public boolean canMerge(SkillBonus<?> other) {
    if (!(other instanceof ItemDurabilityLossAvoidanceBonus otherBonus)) return false;
    if (!Objects.equals(otherBonus.playerMultiplier, this.playerMultiplier)) return false;
    if (!Objects.equals(otherBonus.itemCondition, this.itemCondition)) return false;
    return Objects.equals(otherBonus.playerCondition, this.playerCondition);
  }

  @Override
  public SkillBonus<ItemDurabilityLossAvoidanceBonus> merge(SkillBonus<?> other) {
    if (!(other instanceof ItemDurabilityLossAvoidanceBonus otherBonus)) {
      throw new IllegalArgumentException();
    }
    float mergedChance = otherBonus.chance + this.chance;
    ItemDurabilityLossAvoidanceBonus mergedBonus =
        new ItemDurabilityLossAvoidanceBonus(mergedChance);
    mergedBonus.playerMultiplier = this.playerMultiplier;
    mergedBonus.playerCondition = this.playerCondition;
    mergedBonus.itemCondition = this.itemCondition;
    return mergedBonus;
  }

  @Override
  public MutableComponent getTooltip() {
    MutableComponent tooltip;
    if (chance < 1f) {
      tooltip = Component.translatable(getDescriptionId() + ".chance", itemCondition.getTooltip());
      tooltip =
          TooltipHelper.getSkillBonusTooltip(
              tooltip, chance, AttributeModifier.Operation.MULTIPLY_BASE);
    } else {
      tooltip = Component.translatable(getDescriptionId(), itemCondition.getTooltip());
    }
    tooltip = playerMultiplier.getTooltip(tooltip, Target.PLAYER);
    tooltip = playerCondition.getTooltip(tooltip, Target.PLAYER);
    return tooltip.withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
  }

  @Override
  public boolean isPositive() {
    return chance > 0;
  }

  @Override
  public void addEditorWidgets(
      SkillTreeEditor editor, int row, Consumer<ItemDurabilityLossAvoidanceBonus> consumer) {
    editor.addLabel(0, 0, "Chance", ChatFormatting.GOLD);
    editor.increaseHeight(19);
    editor
        .addNumericTextField(0, 0, 50, 14, chance)
        .setNumericResponder(value -> selectChance(consumer, value));
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

  private void selectChance(Consumer<ItemDurabilityLossAvoidanceBonus> consumer, Double value) {
    setChance(value.floatValue());
    consumer.accept(this.copy());
  }

  private void addPlayerMultiplierWidgets(
      SkillTreeEditor editor, Consumer<ItemDurabilityLossAvoidanceBonus> consumer) {
    playerMultiplier.addEditorWidgets(
        editor,
        multiplier -> {
          setPlayerMultiplier(multiplier);
          consumer.accept(this.copy());
        });
  }

  private void selectPlayerMultiplier(
      SkillTreeEditor editor,
      Consumer<ItemDurabilityLossAvoidanceBonus> consumer,
      LivingMultiplier multiplier) {
    setPlayerMultiplier(multiplier);
    consumer.accept(this.copy());
    editor.rebuildWidgets();
  }

  private void addPlayerConditionWidgets(
      SkillTreeEditor editor, Consumer<ItemDurabilityLossAvoidanceBonus> consumer) {
    playerCondition.addEditorWidgets(
        editor,
        c -> {
          setPlayerCondition(c);
          consumer.accept(this.copy());
        });
  }

  private void selectPlayerCondition(
      SkillTreeEditor editor,
      Consumer<ItemDurabilityLossAvoidanceBonus> consumer,
      LivingCondition condition) {
    setPlayerCondition(condition);
    consumer.accept(this.copy());
    editor.rebuildWidgets();
  }

  private void addItemConditionWidgets(
      SkillTreeEditor editor, Consumer<ItemDurabilityLossAvoidanceBonus> consumer) {
    itemCondition.addEditorWidgets(
        editor,
        c -> {
          setItemCondition(c);
          consumer.accept(this.copy());
        });
  }

  private void selectItemCondition(
      SkillTreeEditor editor,
      Consumer<ItemDurabilityLossAvoidanceBonus> consumer,
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

  public void setChance(float chance) {
    this.chance = chance;
  }

  public static class Serializer implements SkillBonus.Serializer {
    @Override
    public ItemDurabilityLossAvoidanceBonus deserialize(JsonObject json) throws JsonParseException {
      float chance = SerializationHelper.getElement(json, "chance").getAsFloat();
      ItemDurabilityLossAvoidanceBonus bonus = new ItemDurabilityLossAvoidanceBonus(chance);
      bonus.playerMultiplier =
          SerializationHelper.deserializeLivingMultiplier(json, "player_multiplier");
      bonus.playerCondition =
          SerializationHelper.deserializeLivingCondition(json, "player_condition");
      bonus.itemCondition = SerializationHelper.deserializeItemCondition(json);
      return bonus;
    }

    @Override
    public void serialize(JsonObject json, SkillBonus<?> bonus) {
      if (!(bonus instanceof ItemDurabilityLossAvoidanceBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      json.addProperty("chance", aBonus.chance);
      SerializationHelper.serializeLivingMultiplier(
          json, aBonus.playerMultiplier, "player_multiplier");
      SerializationHelper.serializeLivingCondition(
          json, aBonus.playerCondition, "player_condition");
      SerializationHelper.serializeItemCondition(json, aBonus.itemCondition);
    }

    @Override
    public ItemDurabilityLossAvoidanceBonus deserialize(CompoundTag tag) {
      float chance = tag.getFloat("chance");
      ItemDurabilityLossAvoidanceBonus bonus = new ItemDurabilityLossAvoidanceBonus(chance);
      bonus.playerMultiplier =
          SerializationHelper.deserializeLivingMultiplier(tag, "player_multiplier");
      bonus.playerCondition =
          SerializationHelper.deserializeLivingCondition(tag, "player_condition");
      bonus.itemCondition = SerializationHelper.deserializeItemCondition(tag);
      return bonus;
    }

    @Override
    public CompoundTag serialize(SkillBonus<?> bonus) {
      if (!(bonus instanceof ItemDurabilityLossAvoidanceBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      tag.putFloat("chance", aBonus.chance);
      SerializationHelper.serializeLivingMultiplier(
          tag, aBonus.playerMultiplier, "player_multiplier");
      SerializationHelper.serializeLivingCondition(tag, aBonus.playerCondition, "player_condition");
      SerializationHelper.serializeItemCondition(tag, aBonus.itemCondition);
      return tag;
    }

    @Override
    public ItemDurabilityLossAvoidanceBonus deserialize(FriendlyByteBuf buf) {
      float chance = buf.readFloat();
      ItemDurabilityLossAvoidanceBonus bonus = new ItemDurabilityLossAvoidanceBonus(chance);
      bonus.playerMultiplier = NetworkHelper.readLivingMultiplier(buf);
      bonus.playerCondition = NetworkHelper.readLivingCondition(buf);
      bonus.itemCondition = NetworkHelper.readItemCondition(buf);
      return bonus;
    }

    @Override
    public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
      if (!(bonus instanceof ItemDurabilityLossAvoidanceBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      buf.writeFloat(aBonus.chance);
      NetworkHelper.writeLivingMultiplier(buf, aBonus.playerMultiplier);
      NetworkHelper.writeLivingCondition(buf, aBonus.playerCondition);
      NetworkHelper.writeItemCondition(buf, aBonus.itemCondition);
    }

    @Override
    public SkillBonus<?> createDefaultInstance() {
      return new ItemDurabilityLossAvoidanceBonus(0.1f);
    }
  }
}
