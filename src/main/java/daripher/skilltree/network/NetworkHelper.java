package daripher.skilltree.network;

import com.mojang.logging.LogUtils;
import daripher.skilltree.init.PSTRegistries;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.PassiveSkillTree;
import daripher.skilltree.skill.bonus.SkillBonus;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
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

  public static void writeSkillBonus(FriendlyByteBuf buf, SkillBonus<?> bonus) {
    SkillBonus.Serializer<?> serializer = bonus.getSerializer();
    ResourceLocation serializerId = PSTRegistries.SKILL_BONUS_SERIALIZERS.get().getKey(serializer);
    assert serializerId != null;
    buf.writeUtf(serializerId.toString());
    serializer.serialize(buf, bonus);
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
    writeResourceLocations(buf, skill.getConnectedSkills());
    writeNullableResourceLocation(buf, skill.getConnectedTreeId());
    writeSkillBonuses(buf, skill.getBonuses());
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

  public static @Nullable ResourceLocation readNullableResourceLocation(FriendlyByteBuf buf) {
    return buf.readBoolean() ? new ResourceLocation(buf.readUtf()) : null;
  }

  public static List<SkillBonus<?>> readSkillBonuses(FriendlyByteBuf buf) {
    int count = buf.readInt();
    List<SkillBonus<?>> bonuses = new ArrayList<>();
    for (int i = 0; i < count; i++) bonuses.add(readSkillBonus(buf));
    return bonuses;
  }

  public static SkillBonus<?> readSkillBonus(FriendlyByteBuf buf) {
    ResourceLocation serializerId = new ResourceLocation(buf.readUtf());
    SkillBonus.Serializer<?> serializer =
        PSTRegistries.SKILL_BONUS_SERIALIZERS.get().getValue(serializerId);
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
    skill.setConnectedTree(readNullableResourceLocation(buf));
    readSkillBonuses(buf).forEach(skill::addSkillBonus);
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
