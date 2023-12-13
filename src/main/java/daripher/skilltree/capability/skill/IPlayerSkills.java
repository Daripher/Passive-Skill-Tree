package daripher.skilltree.capability.skill;

import daripher.skilltree.skill.PassiveSkill;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.INBTSerializable;

@AutoRegisterCapability
public interface IPlayerSkills extends INBTSerializable<CompoundTag> {
  List<PassiveSkill> getPlayerSkills();

  boolean learnSkill(PassiveSkill passiveSkill);

  int getSkillPoints();

  void setSkillPoints(int skillPoints);

  void grantSkillPoints(int skillPoints);

  boolean isTreeReset();

  void setTreeReset(boolean reset);

  void resetTree(ServerPlayer player);
}
