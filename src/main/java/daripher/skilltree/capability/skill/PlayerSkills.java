package daripher.skilltree.capability.skill;

import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.skill.PassiveSkill;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class PlayerSkills implements IPlayerSkills {
  private static final UUID TREE_VERSION = UUID.fromString("2d7ac360-63be-480e-aa6b-3eaff41a44f8");
  private final List<PassiveSkill> skills = new ArrayList<>();
  private int skillPoints;
  private boolean treeReset;

  @Override
  public List<PassiveSkill> getPlayerSkills() {
    return skills;
  }

  @Override
  public int getSkillPoints() {
    return skillPoints;
  }

  @Override
  public void setSkillPoints(int skillPoints) {
    this.skillPoints = skillPoints;
  }

  @Override
  public void grantSkillPoints(int skillPoints) {
    this.skillPoints += skillPoints;
  }

  @Override
  public boolean learnSkill(ServerPlayer player, PassiveSkill passiveSkill) {
    if (skillPoints == 0) return false;
    if (skills.contains(passiveSkill)) return false;
    skillPoints--;
    return skills.add(passiveSkill);
  }

  @Override
  public boolean hasSkill(ResourceLocation skillId) {
    return skills.stream().map(PassiveSkill::getId).anyMatch(skillId::equals);
  }

  @Override
  public boolean isTreeReset() {
    return treeReset;
  }

  @Override
  public void setTreeReset(boolean reset) {
    treeReset = reset;
  }

  @Override
  public void resetTree(ServerPlayer player) {
    skillPoints += getPlayerSkills().size();
    getPlayerSkills().forEach(skill -> skill.remove(player));
    getPlayerSkills().clear();
  }

  @Override
  public CompoundTag serializeNBT() {
    CompoundTag tag = new CompoundTag();
    tag.putUUID("TreeVersion", TREE_VERSION);
    tag.putInt("Points", skillPoints);
    tag.putBoolean("TreeReset", treeReset);
    ListTag skillsTag = new ListTag();
    skills.forEach(skill -> skillsTag.add(StringTag.valueOf(skill.getId().toString())));
    tag.put("Skills", skillsTag);
    return tag;
  }

  @Override
  public void deserializeNBT(CompoundTag tag) {
    skills.clear();
    UUID treeVersion = tag.hasUUID("TreeVersion") ? tag.getUUID("TreeVersion") : null;
    skillPoints = tag.getInt("Points");
    ListTag skillsTag = tag.getList("Skills", Tag.TAG_STRING);
    if (!TREE_VERSION.equals(treeVersion)) {
      skillPoints += skillsTag.size();
      treeReset = true;
      return;
    }
    for (Tag skillTag : skillsTag) {
      ResourceLocation skillId = new ResourceLocation(skillTag.getAsString());
      PassiveSkill passiveSkill = SkillsReloader.getSkillById(skillId);
      if (passiveSkill == null) {
        skills.clear();
        treeReset = true;
        skillPoints += skillsTag.size();
        return;
      }
      skills.add(passiveSkill);
    }
  }
}
