package daripher.skilltree.util;

import com.mojang.logging.LogUtils;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.PassiveSkillTree;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import top.theillusivec4.curios.common.CuriosHelper;
import top.theillusivec4.curios.common.CuriosHelper.SlotAttributeWrapper;

public class ByteBufHelper {
  private static final Logger LOGGER = LogUtils.getLogger();

  public static void writeOptionalResourceLocation(
      FriendlyByteBuf buf, Optional<ResourceLocation> location) {
    buf.writeBoolean(location.isPresent());
    if (location.isPresent()) buf.writeUtf(location.get().toString());
  }

  public static void writeAttributeModifier(
      FriendlyByteBuf buf, Pair<Attribute, AttributeModifier> pair) {
    writeAttribute(buf, pair.getLeft());
    AttributeModifier modifier = pair.getRight();
    double amount = modifier.getAmount();
    Operation operation = modifier.getOperation();
    buf.writeDouble(amount);
    buf.writeInt(operation.ordinal());
  }

  public static void writeAttribute(FriendlyByteBuf buf, Attribute attribute) {
    String attributeId;
    if (attribute instanceof SlotAttributeWrapper wrapper) {
      attributeId = "curios:" + wrapper.identifier;
    } else {
      attributeId = ForgeRegistries.ATTRIBUTES.getKey(attribute).toString();
    }
    buf.writeUtf(attributeId);
  }

  public static void writeResourceLocations(FriendlyByteBuf buf, List<ResourceLocation> locations) {
    buf.writeInt(locations.size());
    locations.forEach(location -> buf.writeUtf(location.toString()));
  }

  public static void writeAttributeModifiers(
      FriendlyByteBuf buf, List<Pair<Attribute, AttributeModifier>> modifiers) {
    buf.writeInt(modifiers.size());
    modifiers.forEach(modifier -> writeAttributeModifier(buf, modifier));
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
    writeResourceLocations(buf, skill.getConnectedSkills());
    writeOptionalResourceLocation(buf, skill.getConnectedTreeId());
    writeAttributeModifiers(buf, skill.getAttributeModifiers());
    writeResourceLocations(buf, skill.getConnectedAsGateways());
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

  public static Optional<ResourceLocation> readOptionalResourceLocation(FriendlyByteBuf buf) {
    return buf.readBoolean() ? Optional.of(new ResourceLocation(buf.readUtf())) : Optional.empty();
  }

  public static List<Pair<Attribute, AttributeModifier>> readAttributeModifiers(
      FriendlyByteBuf buf) {
    int count = buf.readInt();
    List<Pair<Attribute, AttributeModifier>> modifiers = new ArrayList<>();
    for (int i = 0; i < count; i++) modifiers.add(readAttributeModifier(buf));
    return modifiers;
  }

  public static Pair<Attribute, AttributeModifier> readAttributeModifier(FriendlyByteBuf buf) {
    Attribute attribute = readAttribute(buf);
    double amount = buf.readDouble();
    Operation operation = Operation.values()[buf.readInt()];
    AttributeModifier modifier = new AttributeModifier("Passive Skill Bonus", amount, operation);
    return Pair.of(attribute, modifier);
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

  public static PassiveSkill readPassiveSkill(FriendlyByteBuf buf) {
    ResourceLocation id = new ResourceLocation(buf.readUtf());
    int size = buf.readInt();
    ResourceLocation background = new ResourceLocation(buf.readUtf());
    ResourceLocation icon = new ResourceLocation(buf.readUtf());
    ResourceLocation border = new ResourceLocation(buf.readUtf());
    boolean startingPoint = buf.readBoolean();
    PassiveSkill skill = new PassiveSkill(id, size, background, icon, border, startingPoint);
    skill.setPosition(buf.readFloat(), buf.readFloat());
    readResourceLocations(buf).forEach(skill.getConnectedSkills()::add);
    skill.setConnectedTree(readOptionalResourceLocation(buf));
    readAttributeModifiers(buf).forEach(skill::addAttributeBonus);
    readResourceLocations(buf).forEach(skill.getConnectedAsGateways()::add);
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
}
