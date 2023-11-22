package daripher.skilltree.data;

import com.google.gson.JsonObject;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.init.PSTRegistries;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.condition.damage.DamageCondition;
import daripher.skilltree.skill.bonus.condition.enchantment.EnchantmentCondition;
import daripher.skilltree.skill.bonus.condition.item.ItemCondition;
import daripher.skilltree.skill.bonus.condition.item.WeaponCondition;
import daripher.skilltree.skill.bonus.condition.living.LivingCondition;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import daripher.skilltree.skill.bonus.multiplier.SkillBonusMultiplier;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.common.CuriosHelper;

public class SerializationHelper {
  public static SkillBonus<?> deserializeSkillBonus(JsonObject json) {
    JsonObject bonusJson = json.getAsJsonObject("skill_bonus");
    String type = bonusJson.get("type").getAsString();
    ResourceLocation serializerId = new ResourceLocation(type);
    SkillBonus.Serializer serializer = PSTRegistries.SKILL_BONUSES.get().getValue(serializerId);
    return Objects.requireNonNull(serializer).deserialize(bonusJson);
  }

  public static void serializeSkillBonus(JsonObject json, SkillBonus<?> bonus) {
    ResourceLocation serializerId = PSTRegistries.SKILL_BONUSES.get().getKey(bonus.getSerializer());
    JsonObject bonusJson = new JsonObject();
    bonus.getSerializer().serialize(bonusJson, bonus);
    bonusJson.addProperty("type", Objects.requireNonNull(serializerId).toString());
    json.add("skill_bonus", bonusJson);
  }

  public static ItemBonus<?> deserializeItemBonus(JsonObject json) {
    JsonObject bonusJson = json.getAsJsonObject("item_bonus");
    String type = bonusJson.get("type").getAsString();
    ResourceLocation serializerId = new ResourceLocation(type);
    ItemBonus.Serializer serializer = PSTRegistries.ITEM_BONUSES.get().getValue(serializerId);
    return Objects.requireNonNull(serializer).deserialize(bonusJson);
  }

  public static void serializeItemBonus(JsonObject json, ItemBonus<?> bonus) {
    ResourceLocation serializerId = PSTRegistries.ITEM_BONUSES.get().getKey(bonus.getSerializer());
    JsonObject bonusJson = new JsonObject();
    bonus.getSerializer().serialize(bonusJson, bonus);
    bonusJson.addProperty("type", Objects.requireNonNull(serializerId).toString());
    json.add("item_bonus", bonusJson);
  }

  @NotNull
  public static Attribute deserializeAttribute(JsonObject json) {
    ResourceLocation attributeId = new ResourceLocation(json.get("attribute").getAsString());
    Attribute attribute;
    if (attributeId.getNamespace().equals("curios")) {
      attribute = CuriosHelper.getOrCreateSlotAttribute(attributeId.getPath());
    } else {
      attribute = ForgeRegistries.ATTRIBUTES.getValue(attributeId);
    }
    if (attribute == null) {
      throw new RuntimeException("Attribute " + attributeId + " doesn't exist!");
    }
    return attribute;
  }

  public static void serializeAttribute(JsonObject json, Attribute attribute) {
    ResourceLocation attributeId;
    if (attribute instanceof CuriosHelper.SlotAttributeWrapper wrapper) {
      attributeId = new ResourceLocation("curios", wrapper.identifier);
    } else {
      attributeId = ForgeRegistries.ATTRIBUTES.getKey(attribute);
    }
    assert attributeId != null;
    json.addProperty("attribute", attributeId.toString());
  }

  @NotNull
  public static AttributeModifier deserializeAttributeModifier(JsonObject json) {
    UUID id = UUID.fromString(json.get("id").getAsString());
    String name = json.get("name").getAsString();
    double amount = json.get("amount").getAsDouble();
    AttributeModifier.Operation operation = deserializeOperation(json);
    return new AttributeModifier(id, name, amount, operation);
  }

  public static void serializeAttributeModifier(JsonObject json, AttributeModifier modifier) {
    json.addProperty("id", modifier.getId().toString());
    json.addProperty("name", modifier.getName());
    json.addProperty("amount", modifier.getAmount());
    serializeOperation(json, modifier.getOperation());
  }

  @NotNull
  public static AttributeModifier.Operation deserializeOperation(JsonObject json) {
    return AttributeModifier.Operation.fromValue(json.get("operation").getAsInt());
  }

  public static void serializeOperation(JsonObject json, AttributeModifier.Operation operation) {
    json.addProperty("operation", operation.toValue());
  }

  @Nullable
  public static SkillBonusMultiplier deserializeBonusMultiplier(JsonObject json) {
    if (!json.has("multiplier")) return null;
    JsonObject multiplierJson = json.getAsJsonObject("multiplier");
    ResourceLocation serializerId = new ResourceLocation(multiplierJson.get("type").getAsString());
    SkillBonusMultiplier.Serializer serializer =
        PSTRegistries.BONUS_MULTIPLIERS.get().getValue(serializerId);
    assert serializer != null;
    return serializer.deserialize(multiplierJson);
  }

  public static void serializeBonusMultiplier(JsonObject json, SkillBonusMultiplier multiplier) {
    if (multiplier == null) return;
    JsonObject multiplierJson = new JsonObject();
    SkillBonusMultiplier.Serializer serializer = multiplier.getSerializer();
    serializer.serialize(multiplierJson, multiplier);
    ResourceLocation serializerId = PSTRegistries.BONUS_MULTIPLIERS.get().getKey(serializer);
    assert serializerId != null;
    multiplierJson.addProperty("type", serializerId.toString());
    json.add("multiplier", multiplierJson);
  }

  @Nullable
  public static LivingCondition deserializeLivingCondition(JsonObject json, String name) {
    if (!json.has(name)) return null;
    JsonObject conditionJson = json.getAsJsonObject(name);
    ResourceLocation serializerId = new ResourceLocation(conditionJson.get("type").getAsString());
    LivingCondition.Serializer serializer =
        PSTRegistries.LIVING_CONDITIONS.get().getValue(serializerId);
    assert serializer != null;
    return serializer.deserialize(conditionJson);
  }

  public static void serializeLivingCondition(
      JsonObject json, LivingCondition condition, String name) {
    if (condition == null) return;
    JsonObject conditionJson = new JsonObject();
    LivingCondition.Serializer serializer = condition.getSerializer();
    serializer.serialize(conditionJson, condition);
    ResourceLocation serializerId = PSTRegistries.LIVING_CONDITIONS.get().getKey(serializer);
    assert serializerId != null;
    conditionJson.addProperty("type", serializerId.toString());
    json.add(name, conditionJson);
  }

  @Nullable
  public static DamageCondition deserializeDamageCondition(JsonObject json) {
    if (!json.has("damage_condition")) return null;
    JsonObject conditionJson = json.getAsJsonObject("damage_condition");
    ResourceLocation serializerId = new ResourceLocation(conditionJson.get("type").getAsString());
    DamageCondition.Serializer serializer =
        PSTRegistries.DAMAGE_CONDITIONS.get().getValue(serializerId);
    assert serializer != null;
    return serializer.deserialize(conditionJson);
  }

  public static void serializeDamageCondition(JsonObject json, DamageCondition condition) {
    if (condition == null) return;
    JsonObject conditionJson = new JsonObject();
    DamageCondition.Serializer serializer = condition.getSerializer();
    serializer.serialize(conditionJson, condition);
    ResourceLocation serializerId = PSTRegistries.DAMAGE_CONDITIONS.get().getKey(serializer);
    assert serializerId != null;
    conditionJson.addProperty("type", serializerId.toString());
    json.add("damage_condition", conditionJson);
  }

  @Nullable
  public static ItemCondition deserializeItemCondition(JsonObject json) {
    if (!json.has("item_condition")) return null;
    JsonObject conditionJson = json.getAsJsonObject("item_condition");
    ResourceLocation serializerId = new ResourceLocation(conditionJson.get("type").getAsString());
    ItemCondition.Serializer serializer =
        PSTRegistries.ITEM_CONDITIONS.get().getValue(serializerId);
    assert serializer != null;
    return serializer.deserialize(conditionJson);
  }

  public static void serializeItemCondition(JsonObject json, ItemCondition condition) {
    if (condition == null) return;
    JsonObject conditionJson = new JsonObject();
    ItemCondition.Serializer serializer = condition.getSerializer();
    serializer.serialize(conditionJson, condition);
    ResourceLocation serializerId = PSTRegistries.ITEM_CONDITIONS.get().getKey(serializer);
    assert serializerId != null;
    conditionJson.addProperty("type", serializerId.toString());
    json.add("item_condition", conditionJson);
  }

  @Nullable
  public static MobEffect deserializeEffect(JsonObject json) {
    if (!json.has("effect")) return null;
    ResourceLocation effectId = new ResourceLocation(json.get("effect").getAsString());
    return ForgeRegistries.MOB_EFFECTS.getValue(effectId);
  }

  public static void serializeEffect(JsonObject json, MobEffect effect) {
    ResourceLocation effectId = ForgeRegistries.MOB_EFFECTS.getKey(effect);
    json.addProperty("effect", Objects.requireNonNull(effectId).toString());
  }

  @Nullable
  public static EquipmentSlot deserializeSlot(JsonObject json) {
    EquipmentSlot slot = null;
    if (json.has("slot")) {
      slot = EquipmentSlot.byName(json.get("slot").getAsString());
    }
    return slot;
  }

  public static void serializeSlot(JsonObject json, @Nullable EquipmentSlot slot) {
    if (slot != null) json.addProperty("slot", slot.getName());
  }

  @Nullable
  public static WeaponCondition.Type deserializeWeaponType(JsonObject json) {
    if (json.has("weapon_type")) {
      return WeaponCondition.Type.byName(json.get("weapon_type").getAsString());
    }
    return null;
  }

  public static void serializeWeaponType(JsonObject json, @Nullable WeaponCondition.Type type) {
    if (type != null) json.addProperty("weapon_type", type.getName());
  }

  @Nullable
  public static MobEffectCategory deserializePotionCategory(JsonObject json) {
    MobEffectCategory category = null;
    if (json.has("category")) {
      category = fromName(json.get("category").getAsString());
    }
    return category;
  }

  public static void serializePotionCategory(
      JsonObject json, @Nullable MobEffectCategory category) {
    if (category != null) json.addProperty("category", getName(category));
  }

  public static MobEffectInstance deserializeEffectInstance(JsonObject json) {
    MobEffect effect = deserializeEffect(json);
    int duration = json.get("duration").getAsInt();
    int amplifier = json.get("amplifier").getAsInt();
    return new MobEffectInstance(Objects.requireNonNull(effect), duration, amplifier);
  }

  public static void serializeEffectInstance(JsonObject json, MobEffectInstance effect) {
    serializeEffect(json, effect.getEffect());
    json.addProperty("duration", effect.getDuration());
    json.addProperty("amplifier", effect.getAmplifier());
  }

  @Nullable
  public static EnchantmentCondition deserializeEnchantmentCondition(JsonObject json) {
    if (!json.has("enchantment_condition")) return null;
    JsonObject conditionJson = json.getAsJsonObject("enchantment_condition");
    ResourceLocation serializerId = new ResourceLocation(conditionJson.get("type").getAsString());
    EnchantmentCondition.Serializer serializer =
        PSTRegistries.ENCHANTMENT_CONDITIONS.get().getValue(serializerId);
    assert serializer != null;
    return serializer.deserialize(conditionJson);
  }

  public static void serializeEnchantmentCondition(
      JsonObject json, EnchantmentCondition condition) {
    if (condition == null) return;
    JsonObject conditionJson = new JsonObject();
    EnchantmentCondition.Serializer serializer = condition.getSerializer();
    serializer.serialize(conditionJson, condition);
    ResourceLocation serializerId = PSTRegistries.ENCHANTMENT_CONDITIONS.get().getKey(serializer);
    assert serializerId != null;
    conditionJson.addProperty("type", serializerId.toString());
    json.add("enchantment_condition", conditionJson);
  }

  @Nullable
  public static Attribute deserializeAttribute(CompoundTag tag) {
    ResourceLocation attributeId = new ResourceLocation(tag.getString("attribute"));
    Attribute attribute;
    if (attributeId.getNamespace().equals("curios")) {
      attribute = CuriosHelper.getOrCreateSlotAttribute(attributeId.getPath());
    } else {
      attribute = ForgeRegistries.ATTRIBUTES.getValue(attributeId);
    }
    if (attribute == null) {
      SkillTreeMod.LOGGER.error("Attribute {} doesn't exist!", attributeId);
    }
    return attribute;
  }

  public static void serializeAttribute(CompoundTag tag, Attribute attribute) {
    ResourceLocation attributeId;
    if (attribute instanceof CuriosHelper.SlotAttributeWrapper wrapper) {
      attributeId = new ResourceLocation("curios", wrapper.identifier);
    } else {
      attributeId = ForgeRegistries.ATTRIBUTES.getKey(attribute);
    }
    assert attributeId != null;
    tag.putString("attribute", attributeId.toString());
  }

  @NotNull
  public static AttributeModifier deserializeAttributeModifier(CompoundTag tag) {
    UUID modifierId = UUID.fromString(tag.getString("id"));
    String name = tag.getString("name");
    double amount = tag.getDouble("amount");
    AttributeModifier.Operation operation = deserializeOperation(tag);
    return new AttributeModifier(modifierId, name, amount, operation);
  }

  public static void serializeAttributeModifier(CompoundTag tag, AttributeModifier modifier) {
    tag.putString("id", modifier.getId().toString());
    tag.putString("name", modifier.getName());
    tag.putDouble("amount", modifier.getAmount());
    serializeOperation(tag, modifier.getOperation());
  }

  @NotNull
  public static AttributeModifier.Operation deserializeOperation(CompoundTag tag) {
    return AttributeModifier.Operation.fromValue(tag.getInt("operation"));
  }

  public static void serializeOperation(CompoundTag tag, AttributeModifier.Operation operation) {
    tag.putInt("operation", operation.toValue());
  }

  public static SkillBonusMultiplier deserializeBonusMultiplier(CompoundTag tag) {
    if (!tag.contains("multiplier")) return null;
    CompoundTag multiplierTag = tag.getCompound("multiplier");
    ResourceLocation serializerId = new ResourceLocation(multiplierTag.getString("Type"));
    SkillBonusMultiplier.Serializer serializer =
        PSTRegistries.BONUS_MULTIPLIERS.get().getValue(serializerId);
    assert serializer != null;
    return serializer.deserialize(multiplierTag);
  }

  public static void serializeBonusMultiplier(CompoundTag tag, SkillBonusMultiplier multiplier) {
    if (multiplier == null) return;
    SkillBonusMultiplier.Serializer serializer = multiplier.getSerializer();
    CompoundTag multiplierTag = serializer.serialize(multiplier);
    ResourceLocation serializerId = PSTRegistries.BONUS_MULTIPLIERS.get().getKey(serializer);
    assert serializerId != null;
    multiplierTag.putString("type", serializerId.toString());
    tag.put("multiplier", multiplierTag);
  }

  public static LivingCondition deserializeLivingCondition(CompoundTag tag, String name) {
    if (!tag.contains(name)) return null;
    CompoundTag conditionTag = tag.getCompound(name);
    ResourceLocation serializerId = new ResourceLocation(conditionTag.getString("type"));
    LivingCondition.Serializer serializer =
        PSTRegistries.LIVING_CONDITIONS.get().getValue(serializerId);
    assert serializer != null;
    return serializer.deserialize(conditionTag);
  }

  public static void serializeLivingCondition(
      CompoundTag tag, LivingCondition condition, String name) {
    if (condition == null) return;
    LivingCondition.Serializer serializer = condition.getSerializer();
    CompoundTag conditionTag = serializer.serialize(condition);
    ResourceLocation serializerId = PSTRegistries.LIVING_CONDITIONS.get().getKey(serializer);
    assert serializerId != null;
    conditionTag.putString("type", serializerId.toString());
    tag.put(name, conditionTag);
  }

  public static DamageCondition deserializeDamageCondition(CompoundTag tag) {
    if (!tag.contains("damage_condition")) return null;
    CompoundTag conditionTag = tag.getCompound("damage_condition");
    ResourceLocation serializerId = new ResourceLocation(conditionTag.getString("type"));
    DamageCondition.Serializer serializer =
        PSTRegistries.DAMAGE_CONDITIONS.get().getValue(serializerId);
    assert serializer != null;
    return serializer.deserialize(conditionTag);
  }

  public static void serializeDamageCondition(CompoundTag tag, DamageCondition condition) {
    if (condition == null) return;
    DamageCondition.Serializer serializer = condition.getSerializer();
    CompoundTag conditionTag = serializer.serialize(condition);
    ResourceLocation serializerId = PSTRegistries.DAMAGE_CONDITIONS.get().getKey(serializer);
    assert serializerId != null;
    conditionTag.putString("type", serializerId.toString());
    tag.put("damage_condition", conditionTag);
  }

  public static ItemCondition deserializeItemCondition(CompoundTag tag) {
    if (!tag.contains("item_condition")) return null;
    CompoundTag conditionTag = tag.getCompound("item_condition");
    ResourceLocation serializerId = new ResourceLocation(conditionTag.getString("type"));
    ItemCondition.Serializer serializer =
        PSTRegistries.ITEM_CONDITIONS.get().getValue(serializerId);
    assert serializer != null;
    return serializer.deserialize(conditionTag);
  }

  public static void serializeItemCondition(CompoundTag tag, ItemCondition condition) {
    if (condition == null) return;
    ItemCondition.Serializer serializer = condition.getSerializer();
    CompoundTag conditionTag = serializer.serialize(condition);
    ResourceLocation serializerId = PSTRegistries.ITEM_CONDITIONS.get().getKey(serializer);
    assert serializerId != null;
    conditionTag.putString("type", serializerId.toString());
    tag.put("item_condition", conditionTag);
  }

  @Nullable
  public static MobEffect deserializeEffect(CompoundTag tag) {
    if (!tag.contains("effect")) return null;
    ResourceLocation effectId = new ResourceLocation(tag.getString("effect"));
    return ForgeRegistries.MOB_EFFECTS.getValue(effectId);
  }

  public static void serializeEffect(CompoundTag tag, MobEffect effect) {
    ResourceLocation effectId = ForgeRegistries.MOB_EFFECTS.getKey(effect);
    tag.putString("effect", Objects.requireNonNull(effectId).toString());
  }

  public static SkillBonus<?> deserializeSkillBonus(CompoundTag tag) {
    CompoundTag bonusTag = tag.getCompound("skill_bonus");
    String type = bonusTag.getString("type");
    ResourceLocation serializerId = new ResourceLocation(type);
    SkillBonus.Serializer serializer = PSTRegistries.SKILL_BONUSES.get().getValue(serializerId);
    return Objects.requireNonNull(serializer).deserialize(bonusTag);
  }

  public static void serializeSkillBonus(CompoundTag tag, SkillBonus<?> bonus) {
    ResourceLocation serializerId = PSTRegistries.SKILL_BONUSES.get().getKey(bonus.getSerializer());
    CompoundTag bonusTag = bonus.getSerializer().serialize(bonus);
    bonusTag.putString("type", Objects.requireNonNull(serializerId).toString());
    tag.put("skill_bonus", bonusTag);
  }

  public static ItemBonus<?> deserializeItemBonus(CompoundTag tag) {
    CompoundTag bonusTag = tag.getCompound("item_bonus");
    String type = bonusTag.getString("type");
    ResourceLocation serializerId = new ResourceLocation(type);
    ItemBonus.Serializer serializer = PSTRegistries.ITEM_BONUSES.get().getValue(serializerId);
    return Objects.requireNonNull(serializer).deserialize(bonusTag);
  }

  public static void serializeItemBonus(CompoundTag tag, ItemBonus<?> bonus) {
    ResourceLocation serializerId = PSTRegistries.ITEM_BONUSES.get().getKey(bonus.getSerializer());
    CompoundTag bonusTag = bonus.getSerializer().serialize(bonus);
    bonusTag.putString("type", Objects.requireNonNull(serializerId).toString());
    tag.put("item_bonus", bonusTag);
  }

  @Nullable
  public static EquipmentSlot deserializeSlot(CompoundTag tag) {
    EquipmentSlot slot = null;
    if (tag.contains("category")) {
      slot = EquipmentSlot.byName(tag.getString("category"));
    }
    return slot;
  }

  public static void serializeSlot(CompoundTag tag, @Nullable EquipmentSlot slot) {
    if (slot != null) tag.putString("category", slot.getName());
  }

  @Nullable
  public static WeaponCondition.Type deserializeWeaponType(CompoundTag tag) {
    WeaponCondition.Type type = null;
    if (tag.contains("weapon_type")) {
      type = WeaponCondition.Type.byName(tag.getString("weapon_type"));
    }
    return type;
  }

  public static void serializeWeaponType(CompoundTag tag, @Nullable WeaponCondition.Type type) {
    if (type != null) tag.putString("weapon_type", type.getName());
  }

  public static @Nullable MobEffectCategory deserializePotionCategory(CompoundTag tag) {
    MobEffectCategory category = null;
    if (tag.contains("category")) {
      category = fromName(tag.getString("category"));
    }
    return category;
  }

  public static void serializePotionCategory(
      CompoundTag tag, @Nullable MobEffectCategory category) {
    if (category != null) tag.putString("category", getName(category));
  }

  public static MobEffectInstance deserializeEffectInstance(CompoundTag tag) {
    MobEffect effect = deserializeEffect(tag);
    int duration = tag.getInt("duration");
    int amplifier = tag.getInt("amplifier");
    return new MobEffectInstance(effect, duration, amplifier);
  }

  public static void serializeEffectInstance(CompoundTag tag, MobEffectInstance effect) {
    serializeEffect(tag, effect.getEffect());
    tag.putInt("duration", effect.getDuration());
    tag.putInt("amplifier", effect.getAmplifier());
  }

  public static EnchantmentCondition deserializeEnchantmentCondition(CompoundTag tag) {
    if (!tag.contains("enchantment_condition")) return null;
    CompoundTag conditionTag = tag.getCompound("enchantment_condition");
    ResourceLocation serializerId = new ResourceLocation(conditionTag.getString("type"));
    EnchantmentCondition.Serializer serializer =
        PSTRegistries.ENCHANTMENT_CONDITIONS.get().getValue(serializerId);
    assert serializer != null;
    return serializer.deserialize(conditionTag);
  }

  public static void serializeEnchantmentCondition(
      CompoundTag tag, EnchantmentCondition condition) {
    if (condition == null) return;
    EnchantmentCondition.Serializer serializer = condition.getSerializer();
    CompoundTag conditionTag = serializer.serialize(condition);
    ResourceLocation serializerId = PSTRegistries.ENCHANTMENT_CONDITIONS.get().getKey(serializer);
    assert serializerId != null;
    conditionTag.putString("type", serializerId.toString());
    tag.put("enchantment_condition", conditionTag);
  }

  public static String getName(@Nullable MobEffectCategory category) {
    if (category == null) return "any";
    return switch (category) {
      case BENEFICIAL -> "beneficial";
      case HARMFUL -> "harmful";
      case NEUTRAL -> "neutral";
    };
  }

  public static @Nullable MobEffectCategory fromName(String string) {
    return switch (string) {
      case "beneficial" -> MobEffectCategory.BENEFICIAL;
      case "harmful" -> MobEffectCategory.HARMFUL;
      case "neutral" -> MobEffectCategory.NEUTRAL;
      default -> null;
    };
  }
}
