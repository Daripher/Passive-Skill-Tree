package daripher.skilltree.skill.bonus.player;

import com.google.gson.*;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTAttributes;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.TickingSkillBonus;
import daripher.skilltree.skill.bonus.condition.living.LivingCondition;
import daripher.skilltree.skill.bonus.condition.living.NoneLivingCondition;
import daripher.skilltree.skill.bonus.condition.living.numeric.provider.AttributeValueProvider;
import daripher.skilltree.skill.bonus.multiplier.LivingMultiplier;
import daripher.skilltree.skill.bonus.multiplier.NoneLivingMultiplier;
import daripher.skilltree.skill.bonus.multiplier.NumericValueMultiplier;
import java.util.*;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public final class AttributeBonus implements SkillBonus<AttributeBonus>, TickingSkillBonus {
  private Attribute attribute;
  private AttributeModifier modifier;
  private @Nonnull LivingMultiplier playerMultiplier = NoneLivingMultiplier.INSTANCE;
  private @Nonnull LivingCondition playerCondition = NoneLivingCondition.INSTANCE;

  public AttributeBonus(Attribute attribute, AttributeModifier modifier) {
    this.attribute = attribute;
    this.modifier = modifier;
  }

  @Override
  public void onSkillLearned(ServerPlayer player, boolean firstTime) {
    if (playerCondition != NoneLivingCondition.INSTANCE
        || playerMultiplier != NoneLivingMultiplier.INSTANCE) {
      return;
    }
    AttributeInstance instance = player.getAttribute(attribute);
    if (instance == null) {
      SkillTreeMod.LOGGER.error(
          "Attempting to add attribute modifier to attribute {}, which is not present for player",
          attribute);
      return;
    }
    if (!instance.hasModifier(modifier)) {
      applyAttributeModifier(instance, modifier, player);
    }
  }

  @Override
  public void onSkillRemoved(ServerPlayer player) {
    AttributeInstance instance = player.getAttribute(attribute);
    if (instance == null) {
      SkillTreeMod.LOGGER.error(
          "Attempting to remove attribute modifier from attribute {}, which is not present for player",
          attribute);
      return;
    }
    instance.removeModifier(modifier.getId());
  }

  @Override
  public void tick(ServerPlayer player) {
    if (!isDynamic()) {
      return;
    }
    if (playerCondition != NoneLivingCondition.INSTANCE) {
      if (!playerCondition.isConditionMet(player)) {
        onSkillRemoved(player);
        return;
      }
    }
    if (playerMultiplier != NoneLivingMultiplier.INSTANCE
        && playerMultiplier.getValue(player) == 0) {
      onSkillRemoved(player);
      return;
    }
    applyDynamicAttributeBonus(player);
  }

  public boolean isDynamic() {
    return playerCondition != NoneLivingCondition.INSTANCE
        || playerMultiplier != NoneLivingMultiplier.INSTANCE;
  }

  private void applyDynamicAttributeBonus(ServerPlayer player) {
    AttributeInstance instance = player.getAttribute(attribute);
    if (instance == null) return;
    AttributeModifier oldModifier = instance.getModifier(modifier.getId());
    double value = modifier.getAmount();
    value *= playerMultiplier.getValue(player);
    if (oldModifier != null) {
      if (oldModifier.getAmount() == value) return;
    }
    AttributeModifier dynamicModifier =
        new AttributeModifier(modifier.getId(), "DynamicBonus", value, modifier.getOperation());
    applyAttributeModifier(instance, dynamicModifier, player);
  }

  private void applyAttributeModifier(
      AttributeInstance instance, AttributeModifier modifier, Player player) {
    float healthPercentage = player.getHealth() / player.getMaxHealth();
    if (instance.getModifier(modifier.getId()) != null) {
      instance.removeModifier(modifier.getId());
    }
    instance.addTransientModifier(modifier);
    if (attribute == Attributes.MAX_HEALTH) {
      player.setHealth(player.getMaxHealth() * healthPercentage);
    }
  }

  @Override
  public SkillBonus.Serializer getSerializer() {
    return PSTSkillBonuses.ATTRIBUTE.get();
  }

  @Override
  public AttributeBonus copy() {
    AttributeModifier modifier =
        new AttributeModifier(
            UUID.randomUUID(),
            this.modifier.getName(),
            this.modifier.getAmount(),
            this.modifier.getOperation());
    AttributeBonus bonus = new AttributeBonus(attribute, modifier);
    bonus.playerMultiplier = this.playerMultiplier;
    bonus.playerCondition = this.playerCondition;
    return bonus;
  }

  @Override
  public AttributeBonus multiply(double multiplier) {
    modifier =
        new AttributeModifier(
            modifier.getId(),
            modifier.getName(),
            modifier.getAmount() * multiplier,
            modifier.getOperation());
    return this;
  }

  @Override
  public boolean canMerge(SkillBonus<?> other) {
    if (!(other instanceof AttributeBonus otherBonus)) return false;
    if (otherBonus.attribute != this.attribute) return false;
    if (!Objects.equals(otherBonus.playerMultiplier, this.playerMultiplier)) return false;
    if (!Objects.equals(otherBonus.playerCondition, this.playerCondition)) return false;
    return otherBonus.modifier.getOperation() == this.modifier.getOperation();
  }

  @Override
  public SkillBonus<AttributeBonus> merge(SkillBonus<?> other) {
    if (!(other instanceof AttributeBonus otherBonus)) {
      throw new IllegalArgumentException();
    }
    AttributeModifier mergedModifier =
        new AttributeModifier(
            this.modifier.getId(),
            "Merged",
            this.modifier.getAmount() + otherBonus.modifier.getAmount(),
            this.modifier.getOperation());
    AttributeBonus mergedBonus = new AttributeBonus(this.attribute, mergedModifier);
    mergedBonus.playerMultiplier = this.playerMultiplier;
    mergedBonus.playerCondition = this.playerCondition;
    return mergedBonus;
  }

  @Override
  public MutableComponent getTooltip() {
    float visibleAmount = (float) modifier.getAmount();
    String descriptionId = attribute.getDescriptionId();
    MutableComponent tooltip;
    if (isPercentageRegeneration()) {
      visibleAmount *= 100;
      String amountDescription = TooltipHelper.formatNumber(visibleAmount);
      descriptionId = getDescriptionId() + ".percentage_regeneration";
      tooltip = Component.translatable(descriptionId, amountDescription);
    } else {
      if (isKnockbackResistanceAddition()) {
        visibleAmount *= 10;
      }
      AttributeModifier.Operation operation = modifier.getOperation();
      tooltip = TooltipHelper.getSkillBonusTooltip(descriptionId, visibleAmount, operation);
      tooltip = playerMultiplier.getTooltip(tooltip, Target.PLAYER);
    }
    tooltip = playerCondition.getTooltip(tooltip, Target.PLAYER);
    return tooltip.withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
  }

  private boolean isKnockbackResistanceAddition() {
    return modifier.getOperation() == AttributeModifier.Operation.ADDITION
        && attribute.equals(Attributes.KNOCKBACK_RESISTANCE);
  }

  private boolean isPercentageRegeneration() {
    return modifier.getOperation() == AttributeModifier.Operation.ADDITION
        && attribute == PSTAttributes.REGENERATION.get()
        && playerMultiplier instanceof NumericValueMultiplier numericValueMultiplier
        && numericValueMultiplier.getValueProvider()
            instanceof AttributeValueProvider attributeValueProvider
        && attributeValueProvider.getAttribute() == Attributes.MAX_HEALTH
        && numericValueMultiplier.getDivisor() == 1;
  }

  @Override
  public void gatherInfo(Consumer<MutableComponent> consumer) {
    SkillBonus.super.gatherInfo(consumer);
    TooltipHelper.consumeTranslated(attribute.getDescriptionId() + ".info", consumer);
  }

  @Override
  public boolean isPositive() {
    return modifier.getAmount() > 0;
  }

  @Override
  public void addEditorWidgets(
      SkillTreeEditor editor, int index, Consumer<AttributeBonus> consumer) {
    editor.addLabel(0, 0, "Attribute", ChatFormatting.GOLD);
    editor.increaseHeight(19);
    editor
        .addSelectionMenu(0, 0, 200, attribute)
        .setResponder(attribute -> selectAttribute(consumer, attribute));
    editor.increaseHeight(19);
    editor.addLabel(0, 0, "Amount", ChatFormatting.GOLD);
    editor.addLabel(55, 0, "Operation", ChatFormatting.GOLD);
    editor.increaseHeight(19);
    editor
        .addNumericTextField(0, 0, 50, 14, modifier.getAmount())
        .setNumericResponder(value -> selectAmount(consumer, value));
    editor
        .addOperationSelection(55, 0, 145, modifier.getOperation())
        .setResponder(operation -> selectOperation(consumer, operation));
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

  private void selectPlayerMultiplier(
      SkillTreeEditor editor, Consumer<AttributeBonus> consumer, LivingMultiplier multiplier) {
    setMultiplier(multiplier);
    consumer.accept(this.copy());
    editor.rebuildWidgets();
  }

  private void selectPlayerCondition(
      SkillTreeEditor editor, Consumer<AttributeBonus> consumer, LivingCondition condition) {
    setCondition(condition);
    consumer.accept(this.copy());
    editor.rebuildWidgets();
  }

  private void selectOperation(
      Consumer<AttributeBonus> consumer, AttributeModifier.Operation operation) {
    setOperation(operation);
    consumer.accept(this.copy());
  }

  private void selectAmount(Consumer<AttributeBonus> consumer, Double value) {
    setAmount(value);
    consumer.accept(this.copy());
  }

  private void selectAttribute(Consumer<AttributeBonus> consumer, Attribute attribute) {
    setAttribute(attribute);
    consumer.accept(this.copy());
  }

  private void addPlayerConditionWidgets(
      SkillTreeEditor editor, Consumer<AttributeBonus> consumer) {
    playerCondition.addEditorWidgets(
        editor,
        c -> {
          setCondition(c);
          consumer.accept(this.copy());
        });
  }

  private void addPlayerMultiplierWidgets(
      SkillTreeEditor editor, Consumer<AttributeBonus> consumer) {
    playerMultiplier.addEditorWidgets(
        editor,
        m -> {
          setMultiplier(m);
          consumer.accept(this.copy());
        });
  }

  public Attribute getAttribute() {
    return attribute;
  }

  public AttributeModifier getModifier() {
    return modifier;
  }

  public void setAttribute(Attribute attribute) {
    this.attribute = attribute;
  }

  public void setAmount(double amount) {
    this.modifier =
        new AttributeModifier(
            modifier.getId(), modifier.getName(), amount, modifier.getOperation());
  }

  public void setOperation(AttributeModifier.Operation operation) {
    this.modifier =
        new AttributeModifier(
            modifier.getId(), modifier.getName(), modifier.getAmount(), operation);
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
    public AttributeBonus deserialize(JsonObject json) throws JsonParseException {
      Attribute attribute = SerializationHelper.deserializeAttribute(json);
      AttributeModifier modifier = SerializationHelper.deserializeAttributeModifier(json);
      AttributeBonus bonus = new AttributeBonus(attribute, modifier);
      bonus.playerMultiplier =
          SerializationHelper.deserializeLivingMultiplier(json, "player_multiplier");
      bonus.playerCondition =
          SerializationHelper.deserializeLivingCondition(json, "player_condition");
      return bonus;
    }

    @Override
    public void serialize(JsonObject json, SkillBonus<?> bonus) {
      if (!(bonus instanceof AttributeBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      SerializationHelper.serializeAttribute(json, aBonus.attribute);
      SerializationHelper.serializeAttributeModifier(json, aBonus.modifier);
      SerializationHelper.serializeLivingMultiplier(
          json, aBonus.playerMultiplier, "player_multiplier");
      SerializationHelper.serializeLivingCondition(
          json, aBonus.playerCondition, "player_condition");
    }

    @Override
    public AttributeBonus deserialize(CompoundTag tag) {
      Attribute attribute = SerializationHelper.deserializeAttribute(tag);
      AttributeModifier modifier = SerializationHelper.deserializeAttributeModifier(tag);
      AttributeBonus bonus = new AttributeBonus(attribute, modifier);
      bonus.playerMultiplier =
          SerializationHelper.deserializeLivingMultiplier(tag, "player_multiplier");
      bonus.playerCondition =
          SerializationHelper.deserializeLivingCondition(tag, "player_condition");
      return bonus;
    }

    @Override
    public CompoundTag serialize(SkillBonus<?> bonus) {
      if (!(bonus instanceof AttributeBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      SerializationHelper.serializeAttribute(tag, aBonus.attribute);
      SerializationHelper.serializeAttributeModifier(tag, aBonus.modifier);
      SerializationHelper.serializeLivingMultiplier(
          tag, aBonus.playerMultiplier, "player_multiplier");
      SerializationHelper.serializeLivingCondition(tag, aBonus.playerCondition, "player_condition");
      return tag;
    }

    @Override
    public AttributeBonus deserialize(FriendlyByteBuf buf) {
      Attribute attribute = NetworkHelper.readAttribute(buf);
      AttributeModifier modifier = NetworkHelper.readAttributeModifier(buf);
      AttributeBonus bonus = new AttributeBonus(attribute, modifier);
      bonus.playerMultiplier = NetworkHelper.readLivingMultiplier(buf);
      bonus.playerCondition = NetworkHelper.readLivingCondition(buf);
      return bonus;
    }

    @Override
    public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
      if (!(bonus instanceof AttributeBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      NetworkHelper.writeAttribute(buf, aBonus.attribute);
      NetworkHelper.writeAttributeModifier(buf, aBonus.modifier);
      NetworkHelper.writeLivingMultiplier(buf, aBonus.playerMultiplier);
      NetworkHelper.writeLivingCondition(buf, aBonus.playerCondition);
    }

    @Override
    public SkillBonus<?> createDefaultInstance() {
      return new AttributeBonus(
          Attributes.ARMOR,
          new AttributeModifier(
              UUID.randomUUID(), "Skill", 1, AttributeModifier.Operation.ADDITION));
    }
  }
}
