package daripher.skilltree.skill.bonus.player;

import com.google.gson.*;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.TickingSkillBonus;
import daripher.skilltree.skill.bonus.predicate.living.LivingEntityPredicate;
import daripher.skilltree.skill.bonus.predicate.living.NoneLivingEntityPredicate;
import daripher.skilltree.skill.bonus.multiplier.LivingMultiplier;
import daripher.skilltree.skill.bonus.multiplier.NoneLivingMultiplier;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import daripher.skilltree.util.ForgeRegistries;

public final class AllAttributesBonus
    implements SkillBonus<AllAttributesBonus>, TickingSkillBonus {
  private static final Set<Holder<Attribute>> AFFECTED_ATTRIBUTES = new HashSet<>();
  private AttributeModifier modifier;
  private @Nonnull LivingMultiplier playerMultiplier = NoneLivingMultiplier.INSTANCE;
  private @Nonnull LivingEntityPredicate playerCondition = NoneLivingEntityPredicate.INSTANCE;

  public AllAttributesBonus(AttributeModifier modifier) {
    this.modifier = modifier;
  }

  @Override
  public void onSkillLearned(ServerPlayer player, boolean firstTime) {
    if (playerCondition != NoneLivingEntityPredicate.INSTANCE
        || playerMultiplier != NoneLivingMultiplier.INSTANCE) {
      return;
    }
    getAffectedAttributes().stream()
        .map(player::getAttribute)
        .filter(Objects::nonNull)
        .filter(a -> !a.hasModifier(modifier.id()))
        .forEach(a -> applyAttributeModifier(a, modifier, player));
  }

  @Override
  public void onSkillRemoved(ServerPlayer player) {
    getAffectedAttributes().stream()
        .map(player::getAttribute)
        .filter(Objects::nonNull)
        .filter(a -> !a.hasModifier(modifier.id()))
        .forEach(a -> a.removeModifier(modifier.id()));
  }

  @Override
  public void tick(ServerPlayer player) {
    if (playerCondition == NoneLivingEntityPredicate.INSTANCE
        && playerMultiplier == NoneLivingMultiplier.INSTANCE) {
      return;
    }
    if (playerCondition != NoneLivingEntityPredicate.INSTANCE) {
      if (!playerCondition.test(player)) {
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

  private void applyDynamicAttributeBonus(ServerPlayer player) {
    getAffectedAttributes().stream()
        .map(player::getAttribute)
        .filter(Objects::nonNull)
        .forEach(
            playerAttribute -> {
              AttributeModifier oldModifier = playerAttribute.getModifier(modifier.id());
              double value = modifier.amount();
              value *= playerMultiplier.getValue(player);
              if (oldModifier != null) {
                if (oldModifier.amount() == value) return;
                playerAttribute.removeModifier(modifier.id());
              }
              AttributeModifier dynamicModifier =
                  new AttributeModifier(
                      modifier.id(), value, modifier.operation());
              applyAttributeModifier(playerAttribute, dynamicModifier, player);
              if (playerAttribute.getAttribute().is(Attributes.MAX_HEALTH)) {
                player.setHealth(player.getHealth());
              }
            });
  }

  private void applyAttributeModifier(
      AttributeInstance instance, AttributeModifier modifier, Player player) {
    float healthPercentage = player.getHealth() / player.getMaxHealth();
    instance.addTransientModifier(modifier);
    if (getAffectedAttributes().contains(Attributes.MAX_HEALTH)) {
      player.setHealth(player.getMaxHealth() * healthPercentage);
    }
  }

  @Override
  public SkillBonus.Serializer getSerializer() {
    return PSTSkillBonuses.ALL_ATTRIBUTES.get();
  }

  @Override
  public AllAttributesBonus copy() {
    AttributeModifier modifier =
        new AttributeModifier(
            ResourceLocation.fromNamespaceAndPath(
                SkillTreeMod.MOD_ID, "all_attributes_bonus/" + UUID.randomUUID()),
            this.modifier.amount(),
            this.modifier.operation());
    AllAttributesBonus bonus = new AllAttributesBonus(modifier);
    bonus.playerMultiplier = this.playerMultiplier;
    bonus.playerCondition = this.playerCondition;
    return bonus;
  }

  @Override
  public AllAttributesBonus multiply(double multiplier) {
    modifier =
        new AttributeModifier(
            modifier.id(),
            modifier.amount() * multiplier,
            modifier.operation());
    return this;
  }

  @Override
  public boolean canMerge(SkillBonus<?> other) {
    if (!(other instanceof AllAttributesBonus otherBonus)) return false;
    if (!Objects.equals(otherBonus.playerMultiplier, this.playerMultiplier)) return false;
    if (!Objects.equals(otherBonus.playerCondition, this.playerCondition)) return false;
    return otherBonus.modifier.operation() == this.modifier.operation();
  }

  @Override
  public SkillBonus<AllAttributesBonus> merge(SkillBonus<?> other) {
    if (!(other instanceof AllAttributesBonus otherBonus)) {
      throw new IllegalArgumentException();
    }
    AttributeModifier mergedModifier =
        new AttributeModifier(
            this.modifier.id(),
            this.modifier.amount() + otherBonus.modifier.amount(),
            this.modifier.operation());
    AllAttributesBonus mergedBonus = new AllAttributesBonus(mergedModifier);
    mergedBonus.playerMultiplier = this.playerMultiplier;
    mergedBonus.playerCondition = this.playerCondition;
    return mergedBonus;
  }

  @Override
  public MutableComponent getTooltip() {
    MutableComponent tooltip =
        TooltipHelper.getSkillBonusTooltip(
            getDescriptionId(), modifier.amount(), modifier.operation());
    tooltip = playerMultiplier.getTooltip(tooltip, Target.PLAYER);
    tooltip = playerCondition.getTooltip(tooltip, Target.PLAYER);
    return tooltip.withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
  }

  @Override
  public boolean isPositive() {
    return modifier.amount() > 0;
  }

  @Override
  public void addEditorWidgets(
      SkillTreeEditor editor, int index, Consumer<AllAttributesBonus> consumer) {
    editor.addLabel(110, 0, "Amount", ChatFormatting.GOLD);
    editor.addLabel(0, 0, "Operation", ChatFormatting.GOLD);
    editor.increaseHeight(19);
    editor
        .addNumericTextField(110, 0, 50, 14, modifier.amount())
        .setNumericResponder(value -> selectAmount(consumer, value));
    editor
        .addOperationSelection(0, 0, 80, modifier.operation())
        .setResponder(operation -> selectOperation(consumer, operation));
    editor.increaseHeight(29);
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
      SkillTreeEditor editor, Consumer<AllAttributesBonus> consumer, LivingMultiplier multiplier) {
    setMultiplier(multiplier);
    consumer.accept(this.copy());
    editor.rebuildWidgets();
  }

  private void selectPlayerCondition(
      SkillTreeEditor editor, Consumer<AllAttributesBonus> consumer, LivingEntityPredicate condition) {
    setCondition(condition);
    consumer.accept(this.copy());
    editor.rebuildWidgets();
  }

  private void selectOperation(
      Consumer<AllAttributesBonus> consumer, AttributeModifier.Operation operation) {
    setOperation(operation);
    consumer.accept(this.copy());
  }

  private void selectAmount(Consumer<AllAttributesBonus> consumer, Double value) {
    setAmount(value);
    consumer.accept(this.copy());
  }

  private void addPlayerMultiplierWidgets(
      SkillTreeEditor editor, Consumer<AllAttributesBonus> consumer) {
    playerMultiplier.addEditorWidgets(
        editor,
        multiplier -> {
          setMultiplier(multiplier);
          consumer.accept(this.copy());
        });
  }

  private void addPlayerConditionWidgets(
      SkillTreeEditor editor, Consumer<AllAttributesBonus> consumer) {
    playerCondition.addEditorWidgets(
        editor,
        condition -> {
          setCondition(condition);
          consumer.accept(this.copy());
        });
  }

  public void setAmount(double amount) {
    this.modifier =
        new AttributeModifier(
            modifier.id(), amount, modifier.operation());
  }

  public void setOperation(AttributeModifier.Operation operation) {
    this.modifier =
        new AttributeModifier(
            modifier.id(), modifier.amount(), operation);
  }

  public SkillBonus<?> setCondition(LivingEntityPredicate condition) {
    this.playerCondition = condition;
    return this;
  }

  public SkillBonus<?> setMultiplier(LivingMultiplier multiplier) {
    this.playerMultiplier = multiplier;
    return this;
  }

  @SuppressWarnings("deprecation")
  private static Set<Holder<Attribute>> getAffectedAttributes() {
    if (AFFECTED_ATTRIBUTES.isEmpty()) {
      ForgeRegistries.ATTRIBUTES.getValues().stream()
          .map(BuiltInRegistries.ATTRIBUTE::wrapAsHolder)
          .filter(Player.createAttributes().build()::hasAttribute)
          .forEach(AFFECTED_ATTRIBUTES::add);
    }
    return AFFECTED_ATTRIBUTES;
  }

  public static class Serializer implements SkillBonus.Serializer {
    @Override
    public AllAttributesBonus deserialize(JsonObject json) throws JsonParseException {
      AttributeModifier modifier = SerializationHelper.deserializeAttributeModifier(json);
      AllAttributesBonus bonus = new AllAttributesBonus(modifier);
      bonus.playerMultiplier =
          SerializationHelper.deserializeLivingMultiplier(json, "player_multiplier");
      bonus.playerCondition =
          SerializationHelper.deserializeLivingCondition(json, "player_condition");
      return bonus;
    }

    @Override
    public void serialize(JsonObject json, SkillBonus<?> bonus) {
      if (!(bonus instanceof AllAttributesBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      SerializationHelper.serializeAttributeModifier(json, aBonus.modifier);
      SerializationHelper.serializeLivingMultiplier(
          json, aBonus.playerMultiplier, "player_multiplier");
      SerializationHelper.serializeLivingCondition(
          json, aBonus.playerCondition, "player_condition");
    }

    @Override
    public AllAttributesBonus deserialize(CompoundTag tag) {
      AttributeModifier modifier = SerializationHelper.deserializeAttributeModifier(tag);
      AllAttributesBonus bonus = new AllAttributesBonus(modifier);
      bonus.playerMultiplier =
          SerializationHelper.deserializeLivingMultiplier(tag, "player_multiplier");
      bonus.playerCondition =
          SerializationHelper.deserializeLivingCondition(tag, "player_condition");
      return bonus;
    }

    @Override
    public CompoundTag serialize(SkillBonus<?> bonus) {
      if (!(bonus instanceof AllAttributesBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      SerializationHelper.serializeAttributeModifier(tag, aBonus.modifier);
      SerializationHelper.serializeLivingMultiplier(
          tag, aBonus.playerMultiplier, "player_multiplier");
      SerializationHelper.serializeLivingCondition(tag, aBonus.playerCondition, "player_condition");
      return tag;
    }

    @Override
    public AllAttributesBonus deserialize(FriendlyByteBuf buf) {
      AttributeModifier modifier = NetworkHelper.readAttributeModifier(buf);
      AllAttributesBonus bonus = new AllAttributesBonus(modifier);
      bonus.playerMultiplier = NetworkHelper.readLivingMultiplier(buf);
      bonus.playerCondition = NetworkHelper.readLivingCondition(buf);
      return bonus;
    }

    @Override
    public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
      if (!(bonus instanceof AllAttributesBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      NetworkHelper.writeAttributeModifier(buf, aBonus.modifier);
      NetworkHelper.writeLivingMultiplier(buf, aBonus.playerMultiplier);
      NetworkHelper.writeLivingCondition(buf, aBonus.playerCondition);
    }

    @Override
    public SkillBonus<?> createDefaultInstance() {
      return new AllAttributesBonus(
          new AttributeModifier(
              ResourceLocation.fromNamespaceAndPath(
                  SkillTreeMod.MOD_ID, "all_attributes_bonus/" + UUID.randomUUID()),
              0.05,
              AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
    }
  }
}
