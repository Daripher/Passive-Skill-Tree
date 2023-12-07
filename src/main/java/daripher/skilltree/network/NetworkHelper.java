package daripher.skilltree.network;

import com.mojang.logging.LogUtils;
import daripher.skilltree.client.skill.SkillTreeClientData;
import daripher.skilltree.config.Config;
import daripher.skilltree.init.PSTRegistries;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.PassiveSkillTree;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.condition.damage.DamageCondition;
import daripher.skilltree.skill.bonus.condition.enchantment.EnchantmentCondition;
import daripher.skilltree.skill.bonus.condition.item.ItemCondition;
import daripher.skilltree.skill.bonus.condition.living.LivingCondition;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import daripher.skilltree.skill.bonus.multiplier.LivingMultiplier;
import java.util.*;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import top.theillusivec4.curios.common.CuriosHelper;
import top.theillusivec4.curios.common.CuriosHelper.SlotAttributeWrapper;

public class NetworkHelper {
  private static final Logger LOGGER = LogUtils.getLogger();

  public static void writeNullableResourceLocation(
      FriendlyByteBuf buf, @Nullable ResourceLocation location) {
    buf.writeBoolean(location != null);
    if (location != null) buf.writeUtf(location.toString());
  }

  public static void writeAttribute(FriendlyByteBuf buf, Attribute attribute) {
    String attributeId;
    if (attribute instanceof SlotAttributeWrapper wrapper) {
      attributeId = "curios:" + wrapper.identifier;
    } else {
      attributeId = Objects.requireNonNull(ForgeRegistries.ATTRIBUTES.getKey(attribute)).toString();
    }
    buf.writeUtf(attributeId);
  }

  public static void writeAttributeModifier(FriendlyByteBuf buf, AttributeModifier modifier) {
    buf.writeUtf(modifier.getName());
    buf.writeDouble(modifier.getAmount());
    writeOperation(buf, modifier.getOperation());
  }

  public static void writeResourceLocations(FriendlyByteBuf buf, List<ResourceLocation> locations) {
    buf.writeInt(locations.size());
    locations.forEach(location -> buf.writeUtf(location.toString()));
  }

  public static void writeSkillBonuses(FriendlyByteBuf buf, List<SkillBonus<?>> bonuses) {
    buf.writeInt(bonuses.size());
    bonuses.forEach(bonus -> writeSkillBonus(buf, bonus));
  }

  public static void writePassiveSkill(FriendlyByteBuf buf, PassiveSkill skill) {
    buf.writeUtf(skill.getId().toString());
    buf.writeInt(skill.getButtonSize());
    buf.writeUtf(skill.getBackgroundTexture().toString());
    buf.writeUtf(skill.getIconTexture().toString());
    buf.writeUtf(skill.getBorderTexture().toString());
    buf.writeBoolean(skill.isStartingPoint());
    buf.writeFloat(skill.getPositionX());
    buf.writeFloat(skill.getPositionY());
    writeResourceLocations(buf, skill.getConnections());
    writeNullableResourceLocation(buf, skill.getConnectedTreeId());
    writeSkillBonuses(buf, skill.getBonuses());
    writeResourceLocations(buf, skill.getGatewayConnections());
  }

  public static void writePassiveSkills(FriendlyByteBuf buf, Collection<PassiveSkill> skills) {
    buf.writeInt(skills.size());
    skills.forEach(skill -> writePassiveSkill(buf, skill));
  }

  public static List<ResourceLocation> readResourceLocations(FriendlyByteBuf buf) {
    int count = buf.readInt();
    List<ResourceLocation> locations = new ArrayList<>();
    for (int i = 0; i < count; i++) locations.add(new ResourceLocation(buf.readUtf()));
    return locations;
  }

  public static @Nullable ResourceLocation readNullableResourceLocation(FriendlyByteBuf buf) {
    return buf.readBoolean() ? new ResourceLocation(buf.readUtf()) : null;
  }

  public static List<SkillBonus<?>> readSkillBonuses(FriendlyByteBuf buf) {
    int count = buf.readInt();
    List<SkillBonus<?>> bonuses = new ArrayList<>();
    for (int i = 0; i < count; i++) bonuses.add(readSkillBonus(buf));
    return bonuses;
  }

  public static void writeSkillBonus(FriendlyByteBuf buf, SkillBonus<?> bonus) {
    SkillBonus.Serializer serializer = bonus.getSerializer();
    ResourceLocation serializerId = PSTRegistries.SKILL_BONUSES.get().getKey(serializer);
    assert serializerId != null;
    buf.writeUtf(serializerId.toString());
    serializer.serialize(buf, bonus);
  }

  public static SkillBonus<?> readSkillBonus(FriendlyByteBuf buf) {
    ResourceLocation serializerId = new ResourceLocation(buf.readUtf());
    SkillBonus.Serializer serializer = PSTRegistries.SKILL_BONUSES.get().getValue(serializerId);
    assert serializer != null;
    return serializer.deserialize(buf);
  }

  public static @Nullable Attribute readAttribute(FriendlyByteBuf buf) {
    String attributeId = buf.readUtf();
    Attribute attribute;
    if (attributeId.startsWith("curios:")) {
      attributeId = attributeId.replace("curios:", "");
      attribute = CuriosHelper.getOrCreateSlotAttribute(attributeId);
    } else {
      attribute = ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(attributeId));
    }
    if (attribute == null) LOGGER.error("Attribute {} does not exist", attributeId);
    return attribute;
  }

  @Nonnull
  public static AttributeModifier readAttributeModifier(FriendlyByteBuf buf) {
    String name = buf.readUtf();
    double amount = buf.readDouble();
    AttributeModifier.Operation operation = readOperation(buf);
    return new AttributeModifier(name, amount, operation);
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
    readResourceLocations(buf).forEach(skill.getConnections()::add);
    skill.setConnectedTree(readNullableResourceLocation(buf));
    readSkillBonuses(buf).forEach(skill::addSkillBonus);
    readResourceLocations(buf).forEach(skill.getGatewayConnections()::add);
    return skill;
  }

  public static List<PassiveSkill> readPassiveSkills(FriendlyByteBuf buf) {
    int count = buf.readInt();
    List<PassiveSkill> skills = new ArrayList<>();
    for (int i = 0; i < count; i++) skills.add(readPassiveSkill(buf));
    return skills;
  }

  public static void writePassiveSkillTrees(
      FriendlyByteBuf buf, Collection<PassiveSkillTree> skillTrees) {
    buf.writeInt(skillTrees.size());
    skillTrees.forEach(skillTree -> writePassiveSkillTree(buf, skillTree));
  }

  public static void writePassiveSkillTree(FriendlyByteBuf buf, PassiveSkillTree skillTree) {
    buf.writeUtf(skillTree.getId().toString());
    writeResourceLocations(buf, skillTree.getSkillIds());
  }

  public static List<PassiveSkillTree> readPassiveSkillTrees(FriendlyByteBuf buf) {
    int count = buf.readInt();
    List<PassiveSkillTree> skillTrees = new ArrayList<>();
    for (int i = 0; i < count; i++) skillTrees.add(readPassiveSkillTree(buf));
    return skillTrees;
  }

  public static PassiveSkillTree readPassiveSkillTree(FriendlyByteBuf buf) {
    ResourceLocation id = new ResourceLocation(buf.readUtf());
    PassiveSkillTree skillTree = new PassiveSkillTree(id);
    readResourceLocations(buf).forEach(skillTree.getSkillIds()::add);
    return skillTree;
  }

  public static void writeBonusMultiplier(
      FriendlyByteBuf buf, @Nonnull LivingMultiplier multiplier) {
    LivingMultiplier.Serializer serializer = multiplier.getSerializer();
    ResourceLocation serializerId = PSTRegistries.LIVING_MULTIPLIERS.get().getKey(serializer);
    buf.writeUtf(Objects.requireNonNull(serializerId).toString());
    serializer.serialize(buf, multiplier);
  }

  public static @Nonnull LivingMultiplier readBonusMultiplier(FriendlyByteBuf buf) {
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
    assert serializerId != null;
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

  public static MobEffect readEffect(FriendlyByteBuf buf) {
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
    assert serializerId != null;
    buf.writeUtf(serializerId.toString());
    serializer.serialize(buf, bonus);
  }

  public static ItemBonus<?> readItemBonus(FriendlyByteBuf buf) {
    ResourceLocation serializerId = new ResourceLocation(buf.readUtf());
    ItemBonus.Serializer serializer = PSTRegistries.ITEM_BONUSES.get().getValue(serializerId);
    assert serializer != null;
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
    return new MobEffectInstance(readEffect(buf), buf.readInt(), buf.readInt());
  }

  public static void writeSkillTreeConfig(FriendlyByteBuf buf) {
    buf.writeBoolean(Config.use_skill_points_array);
    if (Config.use_skill_points_array) {
      buf.writeInt(Config.skill_points_costs.size());
      Config.skill_points_costs.forEach(buf::writeInt);
    }
    buf.writeInt(Config.max_skill_points);
    buf.writeInt(Config.first_skill_cost);
    buf.writeInt(Config.last_skill_cost);
    buf.writeBoolean(Config.enable_exp_exchange);
  }

  public static void loadSkillTreeConfig(FriendlyByteBuf buf) {
    SkillTreeClientData.use_skill_cost_array = buf.readBoolean();
    if (SkillTreeClientData.use_skill_cost_array) {
      SkillTreeClientData.skill_points_costs = new int[buf.readInt()];
      for (int i = 0; i < SkillTreeClientData.skill_points_costs.length; i++) {
        SkillTreeClientData.skill_points_costs[i] = buf.readInt();
      }
    }
    SkillTreeClientData.max_skill_points = buf.readInt();
    SkillTreeClientData.first_skill_cost = buf.readInt();
    SkillTreeClientData.last_skill_cost = buf.readInt();
    SkillTreeClientData.enable_exp_exchange = buf.readBoolean();
  }
}
