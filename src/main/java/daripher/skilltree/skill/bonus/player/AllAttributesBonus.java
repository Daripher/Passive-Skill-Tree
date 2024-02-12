package daripher.skilltree.skill.bonus.player;

import com.google.gson.*;
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
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.registries.ForgeRegistries;

public final class AllAttributesBonus
    implements SkillBonus<AllAttributesBonus>, SkillBonus.Ticking {
  private static final Set<Attribute> AFFECTED_ATTRIBUTES = new HashSet<>();
  private AttributeModifier modifier;
  private @Nonnull LivingMultiplier playerMultiplier = NoneMultiplier.INSTANCE;
  private @Nonnull LivingCondition playerCondition = new NoneLivingCondition();

  public AllAttributesBonus(AttributeModifier modifier) {
    this.modifier = modifier;
  }

  @Override
  public void onSkillLearned(ServerPlayer player, boolean firstTime) {
    if (!(playerCondition instanceof NoneLivingCondition)
        || playerMultiplier != NoneMultiplier.INSTANCE) {
      return;
    }
    getAffectedAttributes().stream()
        .map(player::getAttribute)
        .filter(Objects::nonNull)
        .filter(a -> !a.hasModifier(modifier))
        .forEach(a -> applyAttributeModifier(a, modifier, player));
  }

  @Override
  public void onSkillRemoved(ServerPlayer player) {
    getAffectedAttributes().stream()
        .map(player::getAttribute)
        .filter(Objects::nonNull)
        .filter(a -> !a.hasModifier(modifier))
        .forEach(a -> a.removeModifier(modifier.getId()));
  }

  @Override
  public void tick(ServerPlayer player) {
    if (playerCondition instanceof NoneLivingCondition
        && playerMultiplier == NoneMultiplier.INSTANCE) {
      return;
    }
    if (!(playerCondition instanceof NoneLivingCondition)) {
      if (!playerCondition.met(player)) {
        onSkillRemoved(player);
        return;
      }
    }
    if (playerMultiplier != NoneMultiplier.INSTANCE && playerMultiplier.getValue(player) == 0) {
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
              AttributeModifier oldModifier = playerAttribute.getModifier(modifier.getId());
              double value = modifier.getAmount();
              value *= playerMultiplier.getValue(player);
              if (oldModifier != null) {
                if (oldModifier.getAmount() == value) return;
                playerAttribute.removeModifier(modifier.getId());
              }
              AttributeModifier dynamicModifier =
                  new AttributeModifier(
                      modifier.getId(), "Dynamic", value, modifier.getOperation());
              applyAttributeModifier(playerAttribute, dynamicModifier, player);
              if (playerAttribute.getAttribute() == Attributes.MAX_HEALTH) {
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
            UUID.randomUUID(),
            this.modifier.getName(),
            this.modifier.getAmount(),
            this.modifier.getOperation());
    AllAttributesBonus bonus = new AllAttributesBonus(modifier);
    bonus.playerMultiplier = this.playerMultiplier;
    bonus.playerCondition = this.playerCondition;
    return bonus;
  }

  @Override
  public AllAttributesBonus multiply(double multiplier) {
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
    if (!(other instanceof AllAttributesBonus otherBonus)) return false;
    if (!Objects.equals(otherBonus.playerMultiplier, this.playerMultiplier)) return false;
    if (!Objects.equals(otherBonus.playerCondition, this.playerCondition)) return false;
    return otherBonus.modifier.getOperation() == this.modifier.getOperation();
  }

  @Override
  public SkillBonus<AllAttributesBonus> merge(SkillBonus<?> other) {
    if (!(other instanceof AllAttributesBonus otherBonus)) {
      throw new IllegalArgumentException();
    }
    AttributeModifier mergedModifier =
        new AttributeModifier(
            this.modifier.getId(),
            "Merged",
            this.modifier.getAmount() + otherBonus.modifier.getAmount(),
            this.modifier.getOperation());
    AllAttributesBonus mergedBonus = new AllAttributesBonus(mergedModifier);
    mergedBonus.playerMultiplier = this.playerMultiplier;
    mergedBonus.playerCondition = this.playerCondition;
    return mergedBonus;
  }

  @Override
  public MutableComponent getTooltip() {
    MutableComponent tooltip =
        TooltipHelper.getSkillBonusTooltip(
            getDescriptionId(), modifier.getAmount(), modifier.getOperation());
    tooltip = playerMultiplier.getTooltip(tooltip);
    tooltip = playerCondition.getTooltip(tooltip, "you");
    return tooltip.withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
  }

  @Override
  public boolean isPositive() {
    return modifier.getAmount() > 0;
  }

  @Override
  public void addEditorWidgets(
      SkillTreeEditorScreen editor, int index, Consumer<AllAttributesBonus> consumer) {
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

  @SuppressWarnings("deprecation")
  private static Set<Attribute> getAffectedAttributes() {
    if (AFFECTED_ATTRIBUTES.isEmpty()) {
      ForgeRegistries.ATTRIBUTES.getValues().stream()
          .filter(ForgeHooks.getAttributesView().get(EntityType.PLAYER)::hasAttribute)
          .forEach(AFFECTED_ATTRIBUTES::add);
    }
    return AFFECTED_ATTRIBUTES;
  }

  public static class Serializer implements SkillBonus.Serializer {
    @Override
    public AllAttributesBonus deserialize(JsonObject json) throws JsonParseException {
      AttributeModifier modifier = SerializationHelper.deserializeAttributeModifier(json);
      AllAttributesBonus bonus = new AllAttributesBonus(modifier);
      bonus.playerMultiplier = SerializationHelper.deserializePlayerMultiplier(json);
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
      SerializationHelper.serializePlayerMultiplier(json, aBonus.playerMultiplier);
      SerializationHelper.serializeLivingCondition(
          json, aBonus.playerCondition, "player_condition");
    }

    @Override
    public AllAttributesBonus deserialize(CompoundTag tag) {
      AttributeModifier modifier = SerializationHelper.deserializeAttributeModifier(tag);
      AllAttributesBonus bonus = new AllAttributesBonus(modifier);
      bonus.playerMultiplier = SerializationHelper.deserializePlayerMultiplier(tag);
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
      SerializationHelper.serializePlayerMultiplier(tag, aBonus.playerMultiplier);
      SerializationHelper.serializeLivingCondition(tag, aBonus.playerCondition, "player_condition");
      return tag;
    }

    @Override
    public AllAttributesBonus deserialize(FriendlyByteBuf buf) {
      AttributeModifier modifier = NetworkHelper.readAttributeModifier(buf);
      AllAttributesBonus bonus = new AllAttributesBonus(modifier);
      bonus.playerMultiplier = NetworkHelper.readBonusMultiplier(buf);
      bonus.playerCondition = NetworkHelper.readLivingCondition(buf);
      return bonus;
    }

    @Override
    public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
      if (!(bonus instanceof AllAttributesBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      NetworkHelper.writeAttributeModifier(buf, aBonus.modifier);
      NetworkHelper.writeBonusMultiplier(buf, aBonus.playerMultiplier);
      NetworkHelper.writeLivingCondition(buf, aBonus.playerCondition);
    }

    @Override
    public SkillBonus<?> createDefaultInstance() {
      return new AllAttributesBonus(
          new AttributeModifier(
              UUID.randomUUID(), "Skill", 0.05, AttributeModifier.Operation.MULTIPLY_BASE));
    }
  }
}
