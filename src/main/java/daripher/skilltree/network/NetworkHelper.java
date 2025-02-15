package daripher.skilltree.network;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.init.PSTRegistries;
import daripher.skilltree.item.gem.GemType;
import daripher.skilltree.item.gem.bonus.GemBonusProvider;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.PassiveSkillTree;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.condition.damage.DamageCondition;
import daripher.skilltree.skill.bonus.condition.enchantment.EnchantmentCondition;
import daripher.skilltree.skill.bonus.condition.item.ItemCondition;
import daripher.skilltree.skill.bonus.condition.living.LivingCondition;
import daripher.skilltree.skill.bonus.condition.living.numeric.NumericValueProvider;
import daripher.skilltree.skill.bonus.event.SkillEventListener;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import daripher.skilltree.skill.bonus.multiplier.LivingMultiplier;
import java.util.*;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public class NetworkHelper {
  public static void writePassiveSkill(FriendlyByteBuf buf, PassiveSkill skill) {
    buf.writeUtf(skill.getId().toString());
    buf.writeInt(skill.getSkillSize());
    buf.writeUtf(skill.getFrameTexture().toString());
    buf.writeUtf(skill.getIconTexture().toString());
    buf.writeUtf(skill.getTooltipFrameTexture().toString());
    buf.writeBoolean(skill.isStartingPoint());
    buf.writeFloat(skill.getPositionX());
    buf.writeFloat(skill.getPositionY());
    buf.writeUtf(skill.getTitle());
    buf.writeUtf(skill.getTitleColor());
    writeResourceLocations(buf, skill.getDirectConnections());
    writeNullableResourceLocation(buf, skill.getConnectedTreeId());
    writeSkillBonuses(buf, skill.getBonuses());
    writeResourceLocations(buf, skill.getLongConnections());
    writeResourceLocations(buf, skill.getOneWayConnections());
    writeTags(buf, skill.getTags());
    writeDescription(buf, skill.getDescription());
  }

  public static PassiveSkill readPassiveSkill(FriendlyByteBuf buf) {
    ResourceLocation id = new ResourceLocation(buf.readUtf());
    int size = buf.readInt();
    ResourceLocation background = new ResourceLocation(buf.readUtf());
    ResourceLocation icon = new ResourceLocation(buf.readUtf());
    ResourceLocation border = new ResourceLocation(buf.readUtf());
    boolean startingPoint = buf.readBoolean();
    PassiveSkill skill = new PassiveSkill(id, size, background, icon, border, startingPoint);
    skill.setPosition(buf.readFloat(), buf.readFloat());
    skill.setTitle(buf.readUtf());
    skill.setTitleColor(buf.readUtf());
    skill.getDirectConnections().addAll(readResourceLocations(buf));
    skill.setConnectedTree(readNullableResourceLocation(buf));
    skill.getBonuses().addAll(readSkillBonuses(buf));
    skill.getLongConnections().addAll(readResourceLocations(buf));
    skill.getOneWayConnections().addAll(readResourceLocations(buf));
    skill.getTags().addAll(readTags(buf));
    skill.setDescription(readDescription(buf));
    return skill;
  }

  public static void writeAttribute(FriendlyByteBuf buf, Attribute attribute) {
    String attributeId =
        Objects.requireNonNull(ForgeRegistries.ATTRIBUTES.getKey(attribute)).toString();
    buf.writeUtf(attributeId);
  }

  public static @Nullable Attribute readAttribute(FriendlyByteBuf buf) {
    String attributeId = buf.readUtf();
    Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(attributeId));
    if (attribute == null) {
      SkillTreeMod.LOGGER.error("Attribute {} does not exist", attributeId);
    }
    return attribute;
  }

  public static void writeAttributeModifier(FriendlyByteBuf buf, AttributeModifier modifier) {
    buf.writeLong(modifier.getId().getMostSignificantBits());
    buf.writeLong(modifier.getId().getLeastSignificantBits());
    buf.writeUtf(modifier.getName());
    buf.writeDouble(modifier.getAmount());
    writeOperation(buf, modifier.getOperation());
  }

  @Nonnull
  public static AttributeModifier readAttributeModifier(FriendlyByteBuf buf) {
    UUID id = new UUID(buf.readLong(), buf.readLong());
    String name = buf.readUtf();
    double amount = buf.readDouble();
    AttributeModifier.Operation operation = readOperation(buf);
    return new AttributeModifier(id, name, amount, operation);
  }

  public static void writeNullableResourceLocation(
      FriendlyByteBuf buf, @Nullable ResourceLocation location) {
    buf.writeBoolean(location != null);
    if (location != null) buf.writeUtf(location.toString());
  }

  public static @Nullable ResourceLocation readNullableResourceLocation(FriendlyByteBuf buf) {
    return buf.readBoolean() ? new ResourceLocation(buf.readUtf()) : null;
  }

  public static void writeResourceLocations(FriendlyByteBuf buf, List<ResourceLocation> locations) {
    buf.writeInt(locations.size());
    locations.forEach(location -> buf.writeUtf(location.toString()));
  }

  public static List<ResourceLocation> readResourceLocations(FriendlyByteBuf buf) {
    int count = buf.readInt();
    List<ResourceLocation> locations = new ArrayList<>();
    for (int i = 0; i < count; i++) locations.add(new ResourceLocation(buf.readUtf()));
    return locations;
  }

  private static void writeTags(FriendlyByteBuf buf, List<String> tags) {
    buf.writeInt(tags.size());
    for (String tag : tags) {
      buf.writeUtf(tag);
    }
  }

  private static List<String> readTags(FriendlyByteBuf buf) {
    List<String> tags = new ArrayList<>();
    int size = buf.readInt();
    for (int i = 0; i < size; i++) {
      tags.add(buf.readUtf());
    }
    return tags;
  }

  public static void writeSkillBonuses(FriendlyByteBuf buf, List<SkillBonus<?>> bonuses) {
    buf.writeInt(bonuses.size());
    bonuses.forEach(bonus -> writeSkillBonus(buf, bonus));
  }

  public static List<SkillBonus<?>> readSkillBonuses(FriendlyByteBuf buf) {
    int count = buf.readInt();
    List<SkillBonus<?>> bonuses = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      bonuses.add(readSkillBonus(buf));
    }
    return bonuses;
  }

  public static void writePassiveSkills(FriendlyByteBuf buf, Collection<PassiveSkill> skills) {
    buf.writeInt(skills.size());
    skills.forEach(skill -> writePassiveSkill(buf, skill));
  }

  public static List<PassiveSkill> readPassiveSkills(FriendlyByteBuf buf) {
    int count = buf.readInt();
    List<PassiveSkill> skills = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      skills.add(readPassiveSkill(buf));
    }
    return skills;
  }

  public static void writeSkillBonus(FriendlyByteBuf buf, SkillBonus<?> bonus) {
    SkillBonus.Serializer serializer = bonus.getSerializer();
    ResourceLocation serializerId = PSTRegistries.SKILL_BONUSES.get().getKey(serializer);
    Objects.requireNonNull(serializerId);
    buf.writeUtf(serializerId.toString());
    serializer.serialize(buf, bonus);
  }

  public static SkillBonus<?> readSkillBonus(FriendlyByteBuf buf) {
    ResourceLocation serializerId = new ResourceLocation(buf.readUtf());
    SkillBonus.Serializer serializer = PSTRegistries.SKILL_BONUSES.get().getValue(serializerId);
    Objects.requireNonNull(serializer);
    return serializer.deserialize(buf);
  }

  public static void writeDescription(
      FriendlyByteBuf buf, @Nullable List<MutableComponent> description) {
    buf.writeBoolean(description != null);
    if (description == null) return;
    buf.writeInt(description.size());
    for (MutableComponent component : description) {
      writeChatComponent(buf, component);
    }
  }

  public static @Nullable List<MutableComponent> readDescription(FriendlyByteBuf buf) {
    if (!buf.readBoolean()) return null;
    int size = buf.readInt();
    List<MutableComponent> description = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      description.add(readChatComponent(buf));
    }
    return description;
  }

  public static void writeChatComponent(FriendlyByteBuf buf, MutableComponent component) {
    buf.writeUtf(component.getString());
    Style style = component.getStyle();
    buf.writeBoolean(style.isBold());
    buf.writeBoolean(style.isItalic());
    buf.writeBoolean(style.isUnderlined());
    buf.writeBoolean(style.isStrikethrough());
    buf.writeBoolean(style.isObfuscated());
    TextColor textColor = style.getColor();
    buf.writeInt(textColor == null ? -1 : textColor.getValue());
  }

  public static MutableComponent readChatComponent(FriendlyByteBuf buf) {
    String text = buf.readUtf();
    Style style =
        Style.EMPTY
            .withBold(buf.readBoolean())
            .withItalic(buf.readBoolean())
            .withUnderlined(buf.readBoolean())
            .withStrikethrough(buf.readBoolean())
            .withObfuscated(buf.readBoolean());
    int color = buf.readInt();
    if (color != -1) {
      style = style.withColor(color);
    }
    return Component.literal(text).withStyle(style);
  }

  public static void writePassiveSkillTrees(
      FriendlyByteBuf buf, Collection<PassiveSkillTree> skillTrees) {
    buf.writeInt(skillTrees.size());
    skillTrees.forEach(skillTree -> writePassiveSkillTree(buf, skillTree));
  }

  public static List<PassiveSkillTree> readPassiveSkillTrees(FriendlyByteBuf buf) {
    int count = buf.readInt();
    List<PassiveSkillTree> skillTrees = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      skillTrees.add(readPassiveSkillTree(buf));
    }
    return skillTrees;
  }

  public static void writePassiveSkillTree(FriendlyByteBuf buf, PassiveSkillTree skillTree) {
    buf.writeUtf(skillTree.getId().toString());
    writeResourceLocations(buf, skillTree.getSkillIds());
    writeTagLimits(buf, skillTree.getSkillLimitations());
  }

  public static PassiveSkillTree readPassiveSkillTree(FriendlyByteBuf buf) {
    ResourceLocation id = new ResourceLocation(buf.readUtf());
    PassiveSkillTree skillTree = new PassiveSkillTree(id);
    readResourceLocations(buf).forEach(skillTree.getSkillIds()::add);
    readTagLimits(buf).forEach(skillTree.getSkillLimitations()::put);
    return skillTree;
  }

  private static void writeTagLimits(FriendlyByteBuf buf, Map<String, Integer> limits) {
    buf.writeInt(limits.size());
    for (Map.Entry<String, Integer> entry : limits.entrySet()) {
      buf.writeUtf(entry.getKey());
      buf.writeInt(entry.getValue());
    }
  }

  private static Map<String, Integer> readTagLimits(FriendlyByteBuf buf) {
    Map<String, Integer> limits = new HashMap<>();
    int size = buf.readInt();
    for (int i = 0; i < size; i++) {
      limits.put(buf.readUtf(), buf.readInt());
    }
    return limits;
  }

  public static void writeLivingMultiplier(
      FriendlyByteBuf buf, @Nonnull LivingMultiplier multiplier) {
    LivingMultiplier.Serializer serializer = multiplier.getSerializer();
    ResourceLocation serializerId = PSTRegistries.LIVING_MULTIPLIERS.get().getKey(serializer);
    buf.writeUtf(Objects.requireNonNull(serializerId).toString());
    serializer.serialize(buf, multiplier);
  }

  public static @Nonnull LivingMultiplier readLivingMultiplier(FriendlyByteBuf buf) {
    ResourceLocation serializerId = new ResourceLocation(buf.readUtf());
    LivingMultiplier.Serializer serializer =
        PSTRegistries.LIVING_MULTIPLIERS.get().getValue(serializerId);
    return Objects.requireNonNull(serializer).deserialize(buf);
  }

  public static void writeLivingCondition(FriendlyByteBuf buf, @Nonnull LivingCondition condition) {
    LivingCondition.Serializer serializer = condition.getSerializer();
    ResourceLocation serializerId = PSTRegistries.LIVING_CONDITIONS.get().getKey(serializer);
    buf.writeUtf(Objects.requireNonNull(serializerId).toString());
    serializer.serialize(buf, condition);
  }

  public static @Nonnull LivingCondition readLivingCondition(FriendlyByteBuf buf) {
    ResourceLocation serializerId = new ResourceLocation(buf.readUtf());
    LivingCondition.Serializer serializer =
        PSTRegistries.LIVING_CONDITIONS.get().getValue(serializerId);
    return Objects.requireNonNull(serializer).deserialize(buf);
  }

  public static void writeDamageCondition(FriendlyByteBuf buf, @Nonnull DamageCondition condition) {
    DamageCondition.Serializer serializer = condition.getSerializer();
    ResourceLocation serializerId = PSTRegistries.DAMAGE_CONDITIONS.get().getKey(serializer);
    Objects.requireNonNull(serializerId);
    buf.writeUtf(serializerId.toString());
    serializer.serialize(buf, condition);
  }

  public static @Nonnull DamageCondition readDamageCondition(FriendlyByteBuf buf) {
    ResourceLocation serializerId = new ResourceLocation(buf.readUtf());
    DamageCondition.Serializer serializer =
        PSTRegistries.DAMAGE_CONDITIONS.get().getValue(serializerId);
    return Objects.requireNonNull(serializer).deserialize(buf);
  }

  public static void writeItemCondition(FriendlyByteBuf buf, @Nonnull ItemCondition condition) {
    ItemCondition.Serializer serializer = condition.getSerializer();
    ResourceLocation serializerId = PSTRegistries.ITEM_CONDITIONS.get().getKey(serializer);
    buf.writeUtf(Objects.requireNonNull(serializerId).toString());
    serializer.serialize(buf, condition);
  }

  public static @Nonnull ItemCondition readItemCondition(FriendlyByteBuf buf) {
    ResourceLocation serializerId = new ResourceLocation(buf.readUtf());
    ItemCondition.Serializer serializer =
        PSTRegistries.ITEM_CONDITIONS.get().getValue(serializerId);
    return Objects.requireNonNull(serializer).deserialize(buf);
  }

  public static void writeEventListener(
      FriendlyByteBuf buf, @Nonnull SkillEventListener condition) {
    SkillEventListener.Serializer serializer = condition.getSerializer();
    ResourceLocation serializerId = PSTRegistries.EVENT_LISTENERS.get().getKey(serializer);
    buf.writeUtf(Objects.requireNonNull(serializerId).toString());
    serializer.serialize(buf, condition);
  }

  public static @Nonnull SkillEventListener readEventListener(FriendlyByteBuf buf) {
    ResourceLocation serializerId = new ResourceLocation(buf.readUtf());
    SkillEventListener.Serializer serializer =
        PSTRegistries.EVENT_LISTENERS.get().getValue(serializerId);
    return Objects.requireNonNull(serializer).deserialize(buf);
  }

  public static void writeEnchantmentCondition(
      FriendlyByteBuf buf, @Nonnull EnchantmentCondition condition) {
    EnchantmentCondition.Serializer serializer = condition.getSerializer();
    ResourceLocation serializerId = PSTRegistries.ENCHANTMENT_CONDITIONS.get().getKey(serializer);
    buf.writeUtf(Objects.requireNonNull(serializerId).toString());
    serializer.serialize(buf, condition);
  }

  public static @Nonnull EnchantmentCondition readEnchantmentCondition(FriendlyByteBuf buf) {
    ResourceLocation serializerId = new ResourceLocation(buf.readUtf());
    EnchantmentCondition.Serializer serializer =
        PSTRegistries.ENCHANTMENT_CONDITIONS.get().getValue(serializerId);
    return Objects.requireNonNull(serializer).deserialize(buf);
  }

  public static void writeEffect(FriendlyByteBuf buf, MobEffect effect) {
    ResourceLocation effectId = ForgeRegistries.MOB_EFFECTS.getKey(effect);
    buf.writeUtf(Objects.requireNonNull(effectId).toString());
  }

  public static @Nullable MobEffect readEffect(FriendlyByteBuf buf) {
    ResourceLocation effectId = new ResourceLocation(buf.readUtf());
    return ForgeRegistries.MOB_EFFECTS.getValue(effectId);
  }

  public static <T extends Enum<T>> void writeEnum(FriendlyByteBuf buf, T anEnum) {
    buf.writeInt(anEnum.ordinal());
  }

  public static <T extends Enum<T>> @Nullable T readEnum(FriendlyByteBuf buf, Class<T> type) {
    return type.getEnumConstants()[(buf.readInt())];
  }

  public static void writeItemBonus(FriendlyByteBuf buf, ItemBonus<?> bonus) {
    ItemBonus.Serializer serializer = bonus.getSerializer();
    ResourceLocation serializerId = PSTRegistries.ITEM_BONUSES.get().getKey(serializer);
    Objects.requireNonNull(serializerId);
    buf.writeUtf(serializerId.toString());
    serializer.serialize(buf, bonus);
  }

  public static ItemBonus<?> readItemBonus(FriendlyByteBuf buf) {
    ResourceLocation serializerId = new ResourceLocation(buf.readUtf());
    ItemBonus.Serializer serializer = PSTRegistries.ITEM_BONUSES.get().getValue(serializerId);
    Objects.requireNonNull(serializer);
    return serializer.deserialize(buf);
  }

  public static void writeOperation(FriendlyByteBuf buf, AttributeModifier.Operation operation) {
    buf.writeInt(operation.toValue());
  }

  @NotNull
  public static AttributeModifier.Operation readOperation(FriendlyByteBuf buf) {
    return AttributeModifier.Operation.fromValue(buf.readInt());
  }

  public static void writeEffectInstance(FriendlyByteBuf buf, MobEffectInstance effect) {
    writeEffect(buf, effect.getEffect());
    buf.writeInt(effect.getDuration());
    buf.writeInt(effect.getAmplifier());
  }

  @NotNull
  public static MobEffectInstance readEffectInstance(FriendlyByteBuf buf) {
    MobEffect effect = readEffect(buf);
    Objects.requireNonNull(effect);
    return new MobEffectInstance(effect, buf.readInt(), buf.readInt());
  }

  public static void writeGemBonusProvider(FriendlyByteBuf buf, GemBonusProvider provider) {
    GemBonusProvider.Serializer serializer = provider.getSerializer();
    ResourceLocation serializerId = PSTRegistries.GEM_BONUSES.get().getKey(serializer);
    Objects.requireNonNull(serializerId);
    buf.writeUtf(serializerId.toString());
    serializer.serialize(buf, provider);
  }

  public static GemBonusProvider readGemBonusProvider(FriendlyByteBuf buf) {
    ResourceLocation serializerId = new ResourceLocation(buf.readUtf());
    GemBonusProvider.Serializer serializer = PSTRegistries.GEM_BONUSES.get().getValue(serializerId);
    Objects.requireNonNull(serializer);
    return serializer.deserialize(buf);
  }

  public static void writeValueProvider(FriendlyByteBuf buf, NumericValueProvider<?> provider) {
    NumericValueProvider.Serializer serializer = provider.getSerializer();
    ResourceLocation serializerId = PSTRegistries.NUMERIC_VALUE_PROVIDERS.get().getKey(serializer);
    Objects.requireNonNull(serializerId);
    buf.writeUtf(serializerId.toString());
    serializer.serialize(buf, provider);
  }

  public static NumericValueProvider<?> readValueProvider(FriendlyByteBuf buf) {
    ResourceLocation serializerId = new ResourceLocation(buf.readUtf());
    NumericValueProvider.Serializer serializer =
        PSTRegistries.NUMERIC_VALUE_PROVIDERS.get().getValue(serializerId);
    Objects.requireNonNull(serializer);
    return serializer.deserialize(buf);
  }

  public static void writeGemTypes(FriendlyByteBuf buf, Collection<GemType> types) {
    buf.writeInt(types.size());
    types.forEach(t -> writeGemType(buf, t));
  }

  public static List<GemType> readGemTypes(FriendlyByteBuf buf) {
    int size = buf.readInt();
    List<GemType> list = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      list.add(readGemType(buf));
    }
    return list;
  }

  private static void writeGemType(FriendlyByteBuf buf, GemType type) {
    buf.writeInt(type.bonuses().size());
    type.bonuses()
        .forEach(
            (c, p) -> {
              writeItemCondition(buf, c);
              writeGemBonusProvider(buf, p);
            });
    buf.writeUtf(type.id().toString());
  }

  public static GemType readGemType(FriendlyByteBuf buf) {
    int bonuses = buf.readInt();
    Map<ItemCondition, GemBonusProvider> bonusProviders = new HashMap<>();
    for (int i = 0; i < bonuses; i++) {
      bonusProviders.put(readItemCondition(buf), readGemBonusProvider(buf));
    }
    ResourceLocation id = new ResourceLocation(buf.readUtf());
    return new GemType(id, bonusProviders);
  }
}
