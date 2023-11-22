package daripher.skilltree.skill.bonus.player;

import com.google.gson.*;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.client.screen.SkillTreeEditor;
import daripher.skilltree.client.skill.SkillTreeClientData;
import daripher.skilltree.client.widget.*;
import daripher.skilltree.data.SerializationHelper;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.condition.living.LivingCondition;
import daripher.skilltree.skill.bonus.multiplier.SkillBonusMultiplier;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.common.CuriosHelper;

public final class AttributeBonus implements SkillBonus<AttributeBonus>, SkillBonus.Ticking {
  private final Attribute attribute;
  private AttributeModifier modifier;
  private @Nullable SkillBonusMultiplier multiplier;
  private @Nullable LivingCondition playerCondition;

  public AttributeBonus(Attribute attribute, AttributeModifier modifier) {
    this.attribute = attribute;
    this.modifier = modifier;
  }

  @SuppressWarnings("deprecation")
  @Override
  public void onSkillLearned(ServerPlayer player, boolean firstTime) {
    if (playerCondition != null || multiplier != null) return;
    if (attribute instanceof CuriosHelper.SlotAttributeWrapper wrapper) {
      if (firstTime)
        CuriosApi.getSlotHelper()
            .growSlotType(wrapper.identifier, (int) modifier.getAmount(), player);
      return;
    }
    AttributeInstance instance = player.getAttribute(attribute);
    if (instance == null) {
      SkillTreeMod.LOGGER.error(
          "Attempting to add attribute modifier to attribute {}, which is not present for player",
          attribute);
      return;
    }
    if (!instance.hasModifier(modifier)) instance.addTransientModifier(modifier);
  }

  @SuppressWarnings("deprecation")
  @Override
  public void onSkillRemoved(ServerPlayer player) {
    if (attribute instanceof CuriosHelper.SlotAttributeWrapper wrapper) {
      CuriosApi.getSlotHelper()
          .shrinkSlotType(wrapper.identifier, (int) modifier.getAmount(), player);
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
    if (playerCondition == null && multiplier == null) return;
    if (playerCondition != null) {
      if (!playerCondition.met(player)) {
        onSkillRemoved(player);
        return;
      }
    }
    if (multiplier != null && multiplier.getValue(player) == 0) {
      onSkillRemoved(player);
      return;
    }
    applyDynamicAttributeBonus(player);
  }

  private void applyDynamicAttributeBonus(ServerPlayer player) {
    AttributeInstance playerAttribute = player.getAttribute(attribute);
    if (playerAttribute == null) return;
    AttributeModifier oldModifier = playerAttribute.getModifier(modifier.getId());
    double value = modifier.getAmount();
    if (multiplier != null) {
      value *= multiplier.getValue(player);
    }
    if (oldModifier != null) {
      if (oldModifier.getAmount() == value) return;
      playerAttribute.removeModifier(modifier.getId());
    }
    playerAttribute.addPermanentModifier(
        new AttributeModifier(modifier.getId(), "DynamicBonus", value, modifier.getOperation()));
    if (attribute == Attributes.MAX_HEALTH) player.setHealth(player.getHealth());
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
    bonus.multiplier = this.multiplier;
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
    if (!Objects.equals(otherBonus.multiplier, this.multiplier)) return false;
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
            UUID.randomUUID(),
            "Merged",
            this.modifier.getAmount() + otherBonus.modifier.getAmount(),
            this.modifier.getOperation());
    AttributeBonus mergedBonus = new AttributeBonus(this.attribute, mergedModifier);
    mergedBonus.multiplier = this.multiplier;
    mergedBonus.playerCondition = this.playerCondition;
    return mergedBonus;
  }

  @Override
  public MutableComponent getTooltip() {
    AttributeModifier.Operation operation = modifier.getOperation();
    double amount = modifier.getAmount();
    double visibleAmount = amount;
    if (operation == AttributeModifier.Operation.ADDITION) {
      if (attribute.equals(Attributes.KNOCKBACK_RESISTANCE)) visibleAmount *= 10;
    } else {
      visibleAmount *= 100;
    }
    if (amount < 0) visibleAmount *= -1;
    String operationDescription = amount > 0 ? "plus" : "take";
    Style style = Style.EMPTY.withColor(0x7B7BE5);
    MutableComponent attributeDescription = Component.translatable(attribute.getDescriptionId());
    String amountDescription = ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(visibleAmount);
    operationDescription = "attribute.modifier." + operationDescription + "." + operation.toValue();
    MutableComponent tooltip =
        Component.translatable(operationDescription, amountDescription, attributeDescription);
    if (multiplier != null) {
      tooltip = multiplier.getTooltip(tooltip);
    }
    if (playerCondition != null) {
      tooltip = playerCondition.getTooltip(tooltip, "you");
    }
    return tooltip.withStyle(style);
  }

  @Override
  public void addEditorWidgets(SkillTreeEditor editor, int row) {
    TextField attributeEditor = editor.addTextField(0, 0, 220, 14, getAttributeId());
    attributeEditor.setSoftFilter(this::isAttributeId);
    attributeEditor.setSuggestionProvider(this::suggestAttribute);
    NumericTextField valueEditor = editor.addNumericTextField(225, 0, 30, 14, modifier.getAmount());
    EnumCycleButton<AttributeModifier.Operation> operationEditor =
        editor.addEnumCycleButton(260, 0, 95, 14, modifier.getOperation());
    attributeEditor.setResponder(
        s -> editorWidgetChanged(editor, attributeEditor, valueEditor, operationEditor, row));
    valueEditor.setResponder(
        s -> editorWidgetChanged(editor, attributeEditor, valueEditor, operationEditor, row));
    operationEditor.setPressFunc(
        b -> editorWidgetChanged(editor, attributeEditor, valueEditor, operationEditor, row));
    Button removeButton = editor.addRemoveButton(360, 0, 14, 14);
    removeButton.setPressFunc(b -> removeButtonPressed(editor, row));
    editor.shiftWidgets(0, 19);
  }

  @Nullable
  private static Attribute createAttribute(String id) {
    Attribute newAttribute;
    if (id.startsWith("curios:")) {
      newAttribute = CuriosHelper.getOrCreateSlotAttribute(id.replace("curios:", ""));
    } else {
      newAttribute = ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(id));
    }
    return newAttribute;
  }

  private static void removeButtonPressed(SkillTreeEditor editor, int row) {
    editor
        .getSelectedSkills()
        .forEach(
            skill -> {
              skill.getBonuses().remove(row);
              SkillTreeClientData.saveEditorSkill(skill);
              editor.rebuildWidgets();
            });
  }

  private void editorWidgetChanged(
      SkillTreeEditor editor,
      TextField attributeEditor,
      NumericTextField valueEditor,
      EnumCycleButton<AttributeModifier.Operation> operationEditor,
      final int row) {

    if (!attributeEditor.isValueValid()) return;
    editor
        .getSelectedSkills()
        .forEach(
            skill -> setNewSkillBonus(attributeEditor, valueEditor, operationEditor, row, skill));
  }

  private static void setNewSkillBonus(
      TextField attributeEditor,
      NumericTextField valueEditor,
      EnumCycleButton<AttributeModifier.Operation> operationButton,
      int row,
      PassiveSkill skill) {

    String newAttributeId = attributeEditor.getValue();
    Attribute newAttribute = createAttribute(newAttributeId);
    AttributeBonus oldBonus = (AttributeBonus) skill.getBonuses().get(row);
    AttributeModifier oldModifier = oldBonus.modifier;
    double amount = valueEditor.getNumericValue();
    AttributeModifier.Operation operation = operationButton.getValue();
    AttributeModifier newModifier =
        new AttributeModifier(oldModifier.getId(), oldModifier.getName(), amount, operation);
    skill.getBonuses().set(row, new AttributeBonus(newAttribute, newModifier));
    SkillTreeClientData.saveEditorSkill(skill);
  }

  @NotNull
  private String getAttributeId() {
    String attributeId;
    if (attribute instanceof CuriosHelper.SlotAttributeWrapper wrapper) {
      attributeId = "curios:" + wrapper.identifier;
    } else {
      attributeId = Objects.requireNonNull(ForgeRegistries.ATTRIBUTES.getKey(attribute)).toString();
    }
    return attributeId;
  }

  private boolean isAttributeId(String value) {
    if (value.startsWith("curios:")) return true;
    if (!ResourceLocation.isValidResourceLocation(value)) return false;
    return ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(value)) != null;
  }

  private @Nullable String suggestAttribute(String value) {
    Optional<String> attribute =
        ForgeRegistries.ATTRIBUTES.getKeys().stream()
            .map(ResourceLocation::toString)
            .filter(s -> s.startsWith(value))
            .findFirst();
    return attribute.map(s -> s.replace(value, "")).orElse(null);
  }

  public Attribute getAttribute() {
    return attribute;
  }

  public AttributeModifier getModifier() {
    return modifier;
  }

  public SkillBonus<?> setCondition(LivingCondition condition) {
    this.playerCondition = condition;
    return this;
  }

  public SkillBonus<?> setMultiplier(SkillBonusMultiplier multiplier) {
    this.multiplier = multiplier;
    return this;
  }

  public static class Serializer implements SkillBonus.Serializer {
    @Override
    public AttributeBonus deserialize(JsonObject json) throws JsonParseException {
      Attribute attribute = SerializationHelper.deserializeAttribute(json);
      AttributeModifier modifier = SerializationHelper.deserializeAttributeModifier(json);
      AttributeBonus bonus = new AttributeBonus(attribute, modifier);
      bonus.multiplier = SerializationHelper.deserializeBonusMultiplier(json);
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
      SerializationHelper.serializeBonusMultiplier(json, aBonus.multiplier);
      SerializationHelper.serializeLivingCondition(
          json, aBonus.playerCondition, "player_condition");
    }

    @Override
    public AttributeBonus deserialize(CompoundTag tag) {
      Attribute attribute = SerializationHelper.deserializeAttribute(tag);
      AttributeModifier modifier = SerializationHelper.deserializeAttributeModifier(tag);
      AttributeBonus bonus = new AttributeBonus(attribute, modifier);
      bonus.multiplier = SerializationHelper.deserializeBonusMultiplier(tag);
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
      SerializationHelper.serializeBonusMultiplier(tag, aBonus.multiplier);
      SerializationHelper.serializeLivingCondition(tag, aBonus.playerCondition, "player_condition");
      return tag;
    }

    @Override
    public AttributeBonus deserialize(FriendlyByteBuf buf) {
      Attribute attribute = NetworkHelper.readAttribute(buf);
      AttributeModifier modifier = NetworkHelper.readAttributeModifier(buf);
      AttributeBonus bonus = new AttributeBonus(attribute, modifier);
      bonus.multiplier = NetworkHelper.readBonusMultiplier(buf);
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
      NetworkHelper.writeBonusMultiplier(buf, aBonus.multiplier);
      NetworkHelper.writeLivingCondition(buf, aBonus.playerCondition);
    }
  }
}
