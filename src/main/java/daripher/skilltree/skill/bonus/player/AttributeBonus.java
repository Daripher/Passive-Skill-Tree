package daripher.skilltree.skill.bonus.player;

import com.google.gson.*;
import daripher.skilltree.SkillTreeMod;
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
import daripher.skilltree.skill.bonus.multiplier.NoneMultiplier;
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
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotAttribute;

public final class AttributeBonus implements SkillBonus<AttributeBonus>, SkillBonus.Ticking {
  private Attribute attribute;
  private AttributeModifier modifier;
  private @Nonnull LivingMultiplier playerMultiplier = new NoneMultiplier();
  private @Nonnull LivingCondition playerCondition = new NoneLivingCondition();

  public AttributeBonus(Attribute attribute, AttributeModifier modifier) {
    this.attribute = attribute;
    this.modifier = modifier;
  }

  public AttributeBonus(
      Attribute attribute, String name, float amount, AttributeModifier.Operation operation) {
    this.attribute = attribute;
    this.modifier = new AttributeModifier(UUID.randomUUID(), name, amount, operation);
  }

  @Override
  public void onSkillLearned(ServerPlayer player, boolean firstTime) {
    if (!(playerCondition instanceof NoneLivingCondition)
        || !(playerMultiplier instanceof NoneMultiplier)) {
      return;
    }
    if (attribute instanceof SlotAttribute wrapper) {
      if (firstTime)
        CuriosApi.getSlotHelper()
            .growSlotType(wrapper.getIdentifier(), (int) modifier.getAmount(), player);
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
    if (attribute instanceof SlotAttribute wrapper) {
      CuriosApi.getSlotHelper()
          .shrinkSlotType(wrapper.getIdentifier(), (int) modifier.getAmount(), player);
      return;
    }
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
    if (playerCondition instanceof NoneLivingCondition
        && playerMultiplier instanceof NoneMultiplier) {
      return;
    }
    if (!(playerCondition instanceof NoneLivingCondition)) {
      if (!playerCondition.met(player)) {
        onSkillRemoved(player);
        return;
      }
    }
    if (!(playerMultiplier instanceof NoneMultiplier) && playerMultiplier.getValue(player) == 0) {
      onSkillRemoved(player);
      return;
    }
    applyDynamicAttributeBonus(player);
  }

  private void applyDynamicAttributeBonus(ServerPlayer player) {
    AttributeInstance instance = player.getAttribute(attribute);
    if (instance == null) return;
    AttributeModifier oldModifier = instance.getModifier(modifier.getId());
    double value = modifier.getAmount();
    value *= playerMultiplier.getValue(player);
    if (oldModifier != null) {
      if (oldModifier.getAmount() == value) return;
      instance.removeModifier(modifier.getId());
    }
    AttributeModifier dynamicModifier =
        new AttributeModifier(modifier.getId(), "DynamicBonus", value, modifier.getOperation());
    applyAttributeModifier(instance, dynamicModifier, player);
  }

  private void applyAttributeModifier(
      AttributeInstance instance, AttributeModifier modifier, Player player) {
    float healthPercentage = player.getHealth() / player.getMaxHealth();
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
    if (modifier.getOperation() == AttributeModifier.Operation.ADDITION
        && attribute.equals(Attributes.KNOCKBACK_RESISTANCE)) {
      visibleAmount *= 10;
    }
    MutableComponent tooltip =
        TooltipHelper.getSkillBonusTooltip(
            attribute.getDescriptionId(), visibleAmount, modifier.getOperation());
    tooltip = playerMultiplier.getTooltip(tooltip);
    tooltip = playerCondition.getTooltip(tooltip, "you");
    return tooltip.withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
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
      SkillTreeEditorScreen editor, int index, Consumer<AttributeBonus> consumer) {
    editor.addLabel(0, 0, "Attribute", ChatFormatting.GOLD);
    editor.shiftWidgets(0, 19);
    editor
        .addAttributePicker(0, 0, 200, 14, 10, attribute)
        .setResponder(
            a -> {
              setAttribute(a);
              consumer.accept(this.copy());
            });
    editor.shiftWidgets(0, 19);
    editor.addLabel(0, 0, "Amount", ChatFormatting.GOLD);
    editor.addLabel(55, 0, "Operation", ChatFormatting.GOLD);
    editor.shiftWidgets(0, 19);
    editor
        .addNumericTextField(0, 0, 50, 14, modifier.getAmount())
        .setNumericResponder(
            v -> {
              setAmount(v);
              consumer.accept(this.copy());
            });
    editor
        .addDropDownList(55, 0, 145, 14, 3, modifier.getOperation())
        .setToNameFunc(TooltipHelper::getOperationName)
        .setResponder(
            o -> {
              setOperation(o);
              consumer.accept(this.copy());
            });
    editor.shiftWidgets(0, 19);
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

  public void setUUID(UUID id) {
    this.modifier =
        new AttributeModifier(
            id, modifier.getName(), modifier.getAmount(), modifier.getOperation());
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

  public boolean hasCondition() {
    return !(playerCondition instanceof NoneLivingCondition);
  }

  public boolean hasMultiplier() {
    return !(playerMultiplier instanceof NoneMultiplier);
  }

  public static class Serializer implements SkillBonus.Serializer {
    @Override
    public AttributeBonus deserialize(JsonObject json) throws JsonParseException {
      Attribute attribute = SerializationHelper.deserializeAttribute(json);
      AttributeModifier modifier = SerializationHelper.deserializeAttributeModifier(json);
      AttributeBonus bonus = new AttributeBonus(attribute, modifier);
      bonus.playerMultiplier = SerializationHelper.deserializePlayerMultiplier(json);
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
      SerializationHelper.serializePlayerMultiplier(json, aBonus.playerMultiplier);
      SerializationHelper.serializeLivingCondition(
          json, aBonus.playerCondition, "player_condition");
    }

    @Override
    public AttributeBonus deserialize(CompoundTag tag) {
      Attribute attribute = SerializationHelper.deserializeAttribute(tag);
      AttributeModifier modifier = SerializationHelper.deserializeAttributeModifier(tag);
      AttributeBonus bonus = new AttributeBonus(attribute, modifier);
      bonus.playerMultiplier = SerializationHelper.deserializePlayerMultiplier(tag);
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
      SerializationHelper.serializePlayerMultiplier(tag, aBonus.playerMultiplier);
      SerializationHelper.serializeLivingCondition(tag, aBonus.playerCondition, "player_condition");
      return tag;
    }

    @Override
    public AttributeBonus deserialize(FriendlyByteBuf buf) {
      Attribute attribute = NetworkHelper.readAttribute(buf);
      AttributeModifier modifier = NetworkHelper.readAttributeModifier(buf);
      AttributeBonus bonus = new AttributeBonus(attribute, modifier);
      bonus.playerMultiplier = NetworkHelper.readBonusMultiplier(buf);
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
      NetworkHelper.writeBonusMultiplier(buf, aBonus.playerMultiplier);
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
