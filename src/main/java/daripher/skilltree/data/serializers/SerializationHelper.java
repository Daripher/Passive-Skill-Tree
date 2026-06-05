package daripher.skilltree.data.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.init.PSTRegistries;
import daripher.skilltree.skill.bonus.event.SkillEventListener;
import daripher.skilltree.skill.bonus.function.FloatFunction;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import daripher.skilltree.skill.bonus.multiplier.LivingMultiplier;
import daripher.skilltree.skill.bonus.multiplier.NoneLivingMultiplier;
import daripher.skilltree.skill.bonus.predicate.damage.DamageCondition;
import daripher.skilltree.skill.bonus.predicate.damage.NoneDamageCondition;
import daripher.skilltree.skill.bonus.predicate.item.ItemStackPredicate;
import daripher.skilltree.skill.bonus.predicate.item.NoneItemStackPredicate;
import daripher.skilltree.skill.bonus.predicate.item.PotionStackPredicate;
import daripher.skilltree.skill.bonus.predicate.living.LivingEntityPredicate;
import daripher.skilltree.skill.bonus.predicate.living.NoneLivingEntityPredicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

public class SerializationHelper {
    @NotNull
    public static Attribute deserializeAttribute(JsonObject json) {
        ResourceLocation attributeId = ResourceLocation.parse(json.get("attribute").getAsString());
        Attribute attribute;
        attribute = ForgeRegistries.ATTRIBUTES.getValue(attributeId);
        if (attribute == null) {
            throw new RuntimeException("Attribute " + attributeId + " doesn't exist!");
        }
        return attribute;
    }

    public static void serializeAttribute(JsonObject json, Attribute attribute) {
        ResourceLocation attributeId;
        attributeId = ForgeRegistries.ATTRIBUTES.getKey(attribute);
        Objects.requireNonNull(attributeId);
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

    public static @Nonnull LivingMultiplier deserializeLivingMultiplier(JsonObject json, String name) {
        if (!json.has(name)) {
            return NoneLivingMultiplier.INSTANCE;
        }
        JsonObject multiplierJson = json.getAsJsonObject(name);
        ResourceLocation serializerId = ResourceLocation.parse(multiplierJson.get("type").getAsString());
        LivingMultiplier.Serializer serializer = PSTRegistries.LIVING_MULTIPLIERS.get().getValue(serializerId);
        String errorMessage = "Unknown living multiplier: " + serializerId;
        return deserializeObject(serializer, multiplierJson, errorMessage);
    }

    public static void serializeLivingMultiplier(JsonObject json, @Nonnull LivingMultiplier multiplier, String name) {
        if (multiplier == NoneLivingMultiplier.INSTANCE) {
            return;
        }
        JsonObject multiplierJson = new JsonObject();
        LivingMultiplier.Serializer serializer = multiplier.getSerializer();
        serializer.serialize(multiplierJson, multiplier);
        ResourceLocation serializerId = PSTRegistries.LIVING_MULTIPLIERS.get().getKey(serializer);
        Objects.requireNonNull(serializerId);
        multiplierJson.addProperty("type", serializerId.toString());
        json.add(name, multiplierJson);
    }

    public static @Nonnull LivingEntityPredicate deserializeLivingCondition(JsonObject json, String name) {
        if (!json.has(name)) {
            return NoneLivingEntityPredicate.INSTANCE;
        }
        JsonObject conditionJson = json.getAsJsonObject(name);
        ResourceLocation serializerId = ResourceLocation.parse(conditionJson.get("type").getAsString());
        LivingEntityPredicate.Serializer serializer = PSTRegistries.LIVING_CONDITIONS.get().getValue(serializerId);
        String errorMessage = "Unknown living condition: " + serializerId;
        return deserializeObject(serializer, conditionJson, errorMessage);
    }

    public static void serializeLivingCondition(JsonObject json, @Nonnull LivingEntityPredicate condition, String name) {
        if (condition == NoneLivingEntityPredicate.INSTANCE) {
            return;
        }
        JsonObject conditionJson = new JsonObject();
        LivingEntityPredicate.Serializer serializer = condition.getSerializer();
        serializer.serialize(conditionJson, condition);
        ResourceLocation serializerId = PSTRegistries.LIVING_CONDITIONS.get().getKey(serializer);
        Objects.requireNonNull(serializerId);
        conditionJson.addProperty("type", serializerId.toString());
        json.add(name, conditionJson);
    }

    @Nonnull
    public static DamageCondition deserializeDamageCondition(JsonObject json) {
        return deserializeDamageCondition(json, "damage_condition");
    }

    @Nonnull
    public static DamageCondition deserializeDamageCondition(JsonObject json, String name) {
        if (!json.has(name)) {
            return NoneDamageCondition.INSTANCE;
        }
        JsonObject conditionJson = json.getAsJsonObject(name);
        ResourceLocation serializerId = ResourceLocation.parse(conditionJson.get("type").getAsString());
        DamageCondition.Serializer serializer = PSTRegistries.DAMAGE_CONDITIONS.get().getValue(serializerId);
        String errorMessage = "Unknown damage condition: " + serializerId;
        return deserializeObject(serializer, conditionJson, errorMessage);
    }

    public static void serializeDamageCondition(JsonObject json, @Nonnull DamageCondition condition) {
        serializeDamageCondition(json, condition, "damage_condition");
    }

    public static void serializeDamageCondition(JsonObject json, @Nonnull DamageCondition condition, String name) {
        JsonObject conditionJson = new JsonObject();
        DamageCondition.Serializer serializer = condition.getSerializer();
        serializer.serialize(conditionJson, condition);
        ResourceLocation serializerId = PSTRegistries.DAMAGE_CONDITIONS.get().getKey(serializer);
        conditionJson.addProperty("type", Objects.requireNonNull(serializerId).toString());
        json.add(name, conditionJson);
    }

    public static @Nonnull ItemStackPredicate deserializeItemCondition(JsonObject json) {
        return deserializeItemCondition(json, "item_condition");
    }

    public static @Nonnull ItemStackPredicate deserializeItemCondition(JsonObject json, String name) {
        if (!json.has(name)) {
            return NoneItemStackPredicate.INSTANCE;
        }
        JsonObject conditionJson = json.getAsJsonObject(name);
        ResourceLocation serializerId = ResourceLocation.parse(conditionJson.get("type").getAsString());
        ItemStackPredicate.Serializer serializer = PSTRegistries.ITEM_CONDITIONS.get().getValue(serializerId);
        String errorMessage = "Unknown item condition: " + serializerId;
        return deserializeObject(serializer, conditionJson, errorMessage);
    }

    public static void serializeItemCondition(JsonObject json, @Nonnull ItemStackPredicate condition) {
        serializeItemCondition(json, condition, "item_condition");
    }

    public static void serializeItemCondition(JsonObject json, @Nonnull ItemStackPredicate condition, String name) {
        if (condition == NoneItemStackPredicate.INSTANCE) {
            return;
        }
        JsonObject conditionJson = new JsonObject();
        ItemStackPredicate.Serializer serializer = condition.getSerializer();
        serializer.serialize(conditionJson, condition);
        ResourceLocation serializerId = PSTRegistries.ITEM_CONDITIONS.get().getKey(serializer);
        conditionJson.addProperty("type", Objects.requireNonNull(serializerId).toString());
        json.add(name, conditionJson);
    }

    public static @Nonnull SkillEventListener deserializeEventListener(JsonObject json) {
        JsonObject eventJson = json.getAsJsonObject("event_listener");
        ResourceLocation serializerId = ResourceLocation.parse(eventJson.get("type").getAsString());
        SkillEventListener.Serializer serializer = PSTRegistries.EVENT_LISTENERS.get().getValue(serializerId);
        String errorMessage = "Unknown event listener: " + serializerId;
        return deserializeObject(serializer, eventJson, errorMessage);
    }

    public static void serializeEventListener(JsonObject json, @Nonnull SkillEventListener condition) {
        JsonObject conditionJson = new JsonObject();
        SkillEventListener.Serializer serializer = condition.getSerializer();
        serializer.serialize(conditionJson, condition);
        ResourceLocation serializerId = PSTRegistries.EVENT_LISTENERS.get().getKey(serializer);
        conditionJson.addProperty("type", Objects.requireNonNull(serializerId).toString());
        json.add("event_listener", conditionJson);
    }

    public static @Nullable MobEffect deserializeEffect(JsonObject json) {
        if (!json.has("effect")) {
            return null;
        }
        ResourceLocation effectId = ResourceLocation.parse(json.get("effect").getAsString());
        return ForgeRegistries.MOB_EFFECTS.getValue(effectId);
    }

    public static void serializeEffect(JsonObject json, MobEffect effect) {
        ResourceLocation effectId = ForgeRegistries.MOB_EFFECTS.getKey(effect);
        json.addProperty("effect", Objects.requireNonNull(effectId).toString());
    }

    public static @Nullable PotionStackPredicate.Type deserializePotionType(JsonObject json) {
        return PotionStackPredicate.Type.byName(json.get("potion_type").getAsString());
    }

    public static void serializePotionType(JsonObject json, PotionStackPredicate.Type type) {
        json.addProperty("potion_type", type.getName());
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

    public static FloatFunction<?> deserializeValueProvider(JsonObject json) {
        JsonObject providerJson = json.getAsJsonObject("value_provider");
        String type = providerJson.get("type").getAsString();
        ResourceLocation serializerId = ResourceLocation.parse(type);
        FloatFunction.Serializer serializer = PSTRegistries.FLOAT_FUNCTIONS.get().getValue(serializerId);
        String errorMessage = "Unknown value provider: " + serializerId;
        return deserializeObject(serializer, providerJson, errorMessage);
    }

    public static void serializeValueProvider(JsonObject json, FloatFunction<?> provider) {
        ResourceLocation serializerId = PSTRegistries.FLOAT_FUNCTIONS.get().getKey(provider.getSerializer());
        JsonObject bonusJson = new JsonObject();
        provider.getSerializer().serialize(bonusJson, provider);
        bonusJson.addProperty("type", Objects.requireNonNull(serializerId).toString());
        json.add("value_provider", bonusJson);
    }

    @Nullable
    public static Attribute deserializeAttribute(CompoundTag tag) {
        ResourceLocation attributeId = ResourceLocation.parse(tag.getString("attribute"));
        Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(attributeId);
        if (attribute == null) {
            SkillTreeMod.LOGGER.error("Attribute {} doesn't exist!", attributeId);
        }
        return attribute;
    }

    public static void serializeAttribute(CompoundTag tag, Attribute attribute) {
        ResourceLocation attributeId = ForgeRegistries.ATTRIBUTES.getKey(attribute);
        Objects.requireNonNull(attributeId);
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

    public static @Nonnull LivingMultiplier deserializeLivingMultiplier(CompoundTag tag, String name) {
        if (!tag.contains(name)) {
            return NoneLivingMultiplier.INSTANCE;
        }
        CompoundTag multiplierTag = tag.getCompound(name);
        ResourceLocation serializerId = ResourceLocation.parse(multiplierTag.getString("type"));
        LivingMultiplier.Serializer serializer = PSTRegistries.LIVING_MULTIPLIERS.get().getValue(serializerId);
        return Objects.requireNonNull(serializer).deserialize(multiplierTag);
    }

    public static void serializeLivingMultiplier(CompoundTag tag, @Nonnull LivingMultiplier multiplier, String name) {
        LivingMultiplier.Serializer serializer = multiplier.getSerializer();
        CompoundTag multiplierTag = serializer.serialize(multiplier);
        ResourceLocation serializerId = PSTRegistries.LIVING_MULTIPLIERS.get().getKey(serializer);
        multiplierTag.putString("type", Objects.requireNonNull(serializerId).toString());
        tag.put(name, multiplierTag);
    }

    public static @Nonnull LivingEntityPredicate deserializeLivingCondition(CompoundTag tag, String name) {
        CompoundTag conditionTag = tag.getCompound(name);
        ResourceLocation serializerId = ResourceLocation.parse(conditionTag.getString("type"));
        LivingEntityPredicate.Serializer serializer = PSTRegistries.LIVING_CONDITIONS.get().getValue(serializerId);
        return Objects.requireNonNull(serializer).deserialize(conditionTag);
    }

    public static void serializeLivingCondition(CompoundTag tag, @Nonnull LivingEntityPredicate condition, String name) {
        LivingEntityPredicate.Serializer serializer = condition.getSerializer();
        CompoundTag conditionTag = serializer.serialize(condition);
        ResourceLocation serializerId = PSTRegistries.LIVING_CONDITIONS.get().getKey(serializer);
        Objects.requireNonNull(serializerId);
        conditionTag.putString("type", serializerId.toString());
        tag.put(name, conditionTag);
    }

    public static @Nonnull DamageCondition deserializeDamageCondition(CompoundTag tag) {
        return deserializeDamageCondition(tag, "damage_condition");
    }

    public static @Nonnull DamageCondition deserializeDamageCondition(CompoundTag tag, String name) {
        CompoundTag conditionTag = tag.getCompound(name);
        ResourceLocation serializerId = ResourceLocation.parse(conditionTag.getString("type"));
        DamageCondition.Serializer serializer = PSTRegistries.DAMAGE_CONDITIONS.get().getValue(serializerId);
        return Objects.requireNonNull(serializer).deserialize(conditionTag);
    }

    public static void serializeDamageCondition(CompoundTag tag, @Nonnull DamageCondition condition) {
        serializeDamageCondition(tag, condition, "damage_condition");
    }

    public static void serializeDamageCondition(CompoundTag tag, @Nonnull DamageCondition condition, String name) {
        DamageCondition.Serializer serializer = condition.getSerializer();
        CompoundTag conditionTag = serializer.serialize(condition);
        ResourceLocation serializerId = PSTRegistries.DAMAGE_CONDITIONS.get().getKey(serializer);
        conditionTag.putString("type", Objects.requireNonNull(serializerId).toString());
        tag.put(name, conditionTag);
    }

    public static @Nonnull ItemStackPredicate deserializeItemCondition(CompoundTag tag) {
        CompoundTag conditionTag = tag.getCompound("item_condition");
        ResourceLocation serializerId = ResourceLocation.parse(conditionTag.getString("type"));
        ItemStackPredicate.Serializer serializer = PSTRegistries.ITEM_CONDITIONS.get().getValue(serializerId);
        return Objects.requireNonNull(serializer).deserialize(conditionTag);
    }

    public static void serializeItemCondition(CompoundTag tag, @Nonnull ItemStackPredicate condition) {
        ItemStackPredicate.Serializer serializer = condition.getSerializer();
        CompoundTag conditionTag = serializer.serialize(condition);
        ResourceLocation serializerId = PSTRegistries.ITEM_CONDITIONS.get().getKey(serializer);
        conditionTag.putString("type", Objects.requireNonNull(serializerId).toString());
        tag.put("item_condition", conditionTag);
    }

    public static @Nonnull SkillEventListener deserializeEventListener(CompoundTag tag) {
        CompoundTag conditionTag = tag.getCompound("event_listener");
        ResourceLocation serializerId = ResourceLocation.parse(conditionTag.getString("type"));
        SkillEventListener.Serializer serializer = PSTRegistries.EVENT_LISTENERS.get().getValue(serializerId);
        return Objects.requireNonNull(serializer).deserialize(conditionTag);
    }

    public static void serializeEventListener(CompoundTag tag, @Nonnull SkillEventListener condition) {
        SkillEventListener.Serializer serializer = condition.getSerializer();
        CompoundTag conditionTag = serializer.serialize(condition);
        ResourceLocation serializerId = PSTRegistries.EVENT_LISTENERS.get().getKey(serializer);
        conditionTag.putString("type", Objects.requireNonNull(serializerId).toString());
        tag.put("event_listener", conditionTag);
    }

    @Nullable
    public static MobEffect deserializeEffect(CompoundTag tag) {
        if (!tag.contains("effect")) {
            return null;
        }
        ResourceLocation effectId = ResourceLocation.parse(tag.getString("effect"));
        return ForgeRegistries.MOB_EFFECTS.getValue(effectId);
    }

    public static void serializeEffect(CompoundTag tag, MobEffect effect) {
        ResourceLocation effectId = ForgeRegistries.MOB_EFFECTS.getKey(effect);
        tag.putString("effect", Objects.requireNonNull(effectId).toString());
    }

    public static PotionStackPredicate.Type deserializePotionType(CompoundTag tag) {
        return PotionStackPredicate.Type.byName(tag.getString("potion_type"));
    }

    public static void serializePotionType(CompoundTag tag, PotionStackPredicate.Type type) {
        tag.putString("category", type.getName());
    }

    public static MobEffectInstance deserializeEffectInstance(CompoundTag tag) {
        MobEffect effect = Objects.requireNonNull(deserializeEffect(tag));
        int duration = tag.getInt("duration");
        int amplifier = tag.getInt("amplifier");
        return new MobEffectInstance(effect, duration, amplifier);
    }

    public static void serializeEffectInstance(CompoundTag tag, MobEffectInstance effect) {
        serializeEffect(tag, effect.getEffect());
        tag.putInt("duration", effect.getDuration());
        tag.putInt("amplifier", effect.getAmplifier());
    }

    public static FloatFunction<?> deserializeValueProvider(CompoundTag tag) {
        CompoundTag providerTag = tag.getCompound("value_provider");
        String type = providerTag.getString("type");
        ResourceLocation serializerId = ResourceLocation.parse(type);
        FloatFunction.Serializer serializer = PSTRegistries.FLOAT_FUNCTIONS.get().getValue(serializerId);
        return Objects.requireNonNull(serializer).deserialize(providerTag);
    }

    public static void serializeValueProvider(CompoundTag tag, FloatFunction<?> provider) {
        ResourceLocation serializerId = PSTRegistries.FLOAT_FUNCTIONS.get().getKey(provider.getSerializer());
        CompoundTag providerTag = provider.getSerializer().serialize(provider);
        providerTag.putString("type", Objects.requireNonNull(serializerId).toString());
        tag.put("value_provider", providerTag);
    }

    private static <T> T deserializeObject(Serializer<T> serializer, JsonObject jsonObject, String errorMessage) {
        return Objects.requireNonNull(serializer, errorMessage).deserialize(jsonObject);
    }

    public static JsonElement getElement(JsonObject json, String name) {
        JsonElement element = json.get(name);
        return Objects.requireNonNull(element, "Element not found: " + name);
    }

    public static void serializeItemBonus(JsonObject jsonObject, ItemBonus<?> itemBonus) {
        JsonObject itemBonusJson = new JsonObject();
        ItemBonus.Serializer itemBonusSerializer = itemBonus.getSerializer();
        ResourceLocation itemBonusId = PSTRegistries.ITEM_BONUSES.get().getKey(itemBonusSerializer);
        Objects.requireNonNull(itemBonusId);
        itemBonusJson.addProperty("type", itemBonusId.toString());
        itemBonusSerializer.serialize(itemBonusJson, itemBonus);
        jsonObject.add("item_bonus", itemBonusJson);
    }

    public static ItemBonus<?> deserializeItemBonus(JsonObject jsonObject) {
        JsonObject itemBonusJson = jsonObject.get("item_bonus").getAsJsonObject();
        ResourceLocation serializerId = ResourceLocation.parse(itemBonusJson.get("type").getAsString());
        ItemBonus.Serializer serializer = PSTRegistries.ITEM_BONUSES.get().getValue(serializerId);
        Objects.requireNonNull(serializer);
        return serializer.deserialize(itemBonusJson);
    }
}
