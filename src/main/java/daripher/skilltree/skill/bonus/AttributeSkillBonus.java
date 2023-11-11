package daripher.skilltree.skill.bonus;

import com.google.gson.*;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.client.screen.SkillTreeEditor;
import daripher.skilltree.client.skill.SkillTreeClientData;
import daripher.skilltree.client.widget.*;
import daripher.skilltree.init.PSTSkillBonusSerializers;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.PassiveSkill;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
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

public final class AttributeSkillBonus implements SkillBonus<AttributeSkillBonus> {
  private final Attribute attribute;
  private AttributeModifier modifier;

  public AttributeSkillBonus(Attribute attribute, AttributeModifier modifier) {
    this.attribute = attribute;
    this.modifier = modifier;
  }

  private static void setNewSkillBonus(
      TextField attributeEditor,
      NumericTextField valueEditor,
      EnumCycleButton<AttributeModifier.Operation> operationButton,
      int row,
      PassiveSkill skill) {

    String newAttributeId = attributeEditor.getValue();
    Attribute newAttribute = createAttribute(newAttributeId);
    AttributeSkillBonus oldBonus = (AttributeSkillBonus) skill.getBonuses().get(row);
    AttributeModifier oldModifier = oldBonus.modifier;
    double amount = valueEditor.getNumericValue();
    AttributeModifier.Operation operation = operationButton.getValue();
    AttributeModifier newModifier =
        new AttributeModifier(oldModifier.getId(), oldModifier.getName(), amount, operation);
    skill.getBonuses().set(row, new AttributeSkillBonus(newAttribute, newModifier));
    SkillTreeClientData.saveEditorSkill(skill);
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

  @SuppressWarnings("deprecation")
  @Override
  public void onSkillLearned(ServerPlayer player, boolean firstTime) {
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
    if (instance.hasModifier(modifier)) instance.removeModifier(modifier);
  }

  @Override
  public SkillBonus.Serializer<AttributeSkillBonus> getSerializer() {
    return PSTSkillBonusSerializers.ATTRIBUTE_BONUS.get();
  }

  @Override
  public SkillBonus<AttributeSkillBonus> copy() {
    return new AttributeSkillBonus(
        attribute,
        new AttributeModifier(
            UUID.randomUUID(), modifier.getName(), modifier.getAmount(), modifier.getOperation()));
  }

  @Override
  public AttributeSkillBonus multiply(double multiplier) {
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
    if (!(other instanceof AttributeSkillBonus otherBonus)) return false;
    if (otherBonus.attribute != this.attribute) return false;
    return otherBonus.modifier.getOperation() == this.modifier.getOperation();
  }

  @Override
  public SkillBonus<AttributeSkillBonus> merge(SkillBonus<?> other) {
    assert other instanceof AttributeSkillBonus;
    AttributeSkillBonus otherBonus = (AttributeSkillBonus) other;
    return new AttributeSkillBonus(this.attribute, mergeModifiers(otherBonus));
  }

  @NotNull
  private AttributeModifier mergeModifiers(AttributeSkillBonus otherBonus) {
    return new AttributeModifier(
        UUID.randomUUID(),
        "Fake",
        this.modifier.getAmount() + otherBonus.modifier.getAmount(),
        this.modifier.getOperation());
  }

  @Override
  public MutableComponent getTooltip() {
    AttributeModifier.Operation operation = modifier.getOperation();
    double amount = modifier.getAmount();
    double visibleAmount = amount;
    if (operation == AttributeModifier.Operation.ADDITION) {
      if (attribute.equals(Attributes.KNOCKBACK_RESISTANCE)) visibleAmount *= 10D;
    } else {
      visibleAmount *= 100D;
    }
    if (amount < 0D) visibleAmount *= -1D;
    String textOperation = amount > 0 ? "plus" : "take";
    ChatFormatting style = amount > 0 ? ChatFormatting.BLUE : ChatFormatting.RED;
    MutableComponent attributeDescription = Component.translatable(attribute.getDescriptionId());
    String textAmount = ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(visibleAmount);
    textOperation = "attribute.modifier." + textOperation + "." + operation.toValue();
    return Component.translatable(textOperation, textAmount, attributeDescription).withStyle(style);
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

  public static class Serializer implements SkillBonus.Serializer<AttributeSkillBonus> {
    @Override
    public AttributeSkillBonus deserialize(JsonObject json) throws JsonParseException {
      ResourceLocation attributeId = new ResourceLocation(json.get("attribute").getAsString());
      Attribute attribute;
      if (attributeId.getNamespace().equals("curios"))
        attribute = CuriosHelper.getOrCreateSlotAttribute(attributeId.getPath());
      else attribute = ForgeRegistries.ATTRIBUTES.getValue(attributeId);
      if (attribute == null) {
        throw new RuntimeException("Attribute " + attributeId + " doesn't exist!");
      }
      UUID id = UUID.fromString(json.get("id").getAsString());
      String name = json.get("name").getAsString();
      AttributeModifier.Operation operation =
          AttributeModifier.Operation.fromValue(json.get("operation").getAsInt());
      double amount = json.get("amount").getAsDouble();
      AttributeModifier modifier = new AttributeModifier(id, () -> name, amount, operation);
      return new AttributeSkillBonus(attribute, modifier);
    }

    @Override
    public void serialize(JsonObject json, SkillBonus<?> bonus) {
      if (!(bonus instanceof AttributeSkillBonus attributeBonus)) {
        throw new IllegalArgumentException();
      }
      ResourceLocation attributeId;
      if (attributeBonus.attribute instanceof CuriosHelper.SlotAttributeWrapper wrapper) {
        attributeId = new ResourceLocation("curios", wrapper.identifier);
      } else {
        attributeId = ForgeRegistries.ATTRIBUTES.getKey(attributeBonus.attribute);
      }
      assert attributeId != null;
      json.addProperty("attribute", attributeId.toString());
      json.addProperty("id", attributeBonus.modifier.getId().toString());
      json.addProperty("name", attributeBonus.modifier.getName());
      json.addProperty("operation", attributeBonus.modifier.getOperation().toValue());
      json.addProperty("amount", attributeBonus.modifier.getAmount());
    }

    @Override
    public AttributeSkillBonus deserialize(CompoundTag tag) {
      ResourceLocation attributeId = new ResourceLocation(tag.getString("Attribute"));
      Attribute attribute;
      if (attributeId.getNamespace().equals("curios")) {
        attribute = CuriosHelper.getOrCreateSlotAttribute(attributeId.getPath());
      } else {
        attribute = ForgeRegistries.ATTRIBUTES.getValue(attributeId);
      }
      if (attribute == null) {
        SkillTreeMod.LOGGER.error("Attribute {} doesn't exist!", attributeId);
        return null;
      }
      UUID modifierId = UUID.fromString(tag.getString("Id"));
      String name = tag.getString("Name");
      double amount = tag.getDouble("Amount");
      AttributeModifier.Operation operation =
          AttributeModifier.Operation.fromValue(tag.getInt("Operation"));
      AttributeModifier modifier = new AttributeModifier(modifierId, name, amount, operation);
      return new AttributeSkillBonus(attribute, modifier);
    }

    @Override
    public CompoundTag serialize(SkillBonus<?> bonus) {
      if (!(bonus instanceof AttributeSkillBonus attributeBonus)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      ResourceLocation attributeId;
      if (attributeBonus.attribute instanceof CuriosHelper.SlotAttributeWrapper wrapper) {
        attributeId = new ResourceLocation("curios", wrapper.identifier);
      } else {
        attributeId = ForgeRegistries.ATTRIBUTES.getKey(attributeBonus.attribute);
      }
      assert attributeId != null;
      tag.putString("Attribute", attributeId.toString());
      tag.putString("Id", attributeBonus.modifier.getId().toString());
      tag.putString("Name", attributeBonus.modifier.getName());
      tag.putDouble("Amount", attributeBonus.modifier.getAmount());
      tag.putInt("Operation", attributeBonus.modifier.getOperation().toValue());
      return tag;
    }

    @Override
    public AttributeSkillBonus deserialize(FriendlyByteBuf buf) {
      Attribute attribute = NetworkHelper.readAttribute(buf);
      String name = buf.readUtf();
      double amount = buf.readDouble();
      AttributeModifier.Operation operation = AttributeModifier.Operation.values()[buf.readInt()];
      AttributeModifier modifier = new AttributeModifier(name, amount, operation);
      return new AttributeSkillBonus(attribute, modifier);
    }

    @Override
    public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
      if (!(bonus instanceof AttributeSkillBonus attributeBonus)) {
        throw new IllegalArgumentException();
      }
      NetworkHelper.writeAttribute(buf, attributeBonus.attribute);
      buf.writeUtf(attributeBonus.modifier.getName());
      buf.writeDouble(attributeBonus.modifier.getAmount());
      buf.writeInt(attributeBonus.modifier.getOperation().toValue());
    }
  }
}
