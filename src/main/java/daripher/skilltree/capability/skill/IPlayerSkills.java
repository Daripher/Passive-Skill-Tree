package daripher.skilltree.capability.skill;

import java.util.List;

import daripher.skilltree.skill.PassiveSkill;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.INBTSerializable;

@AutoRegisterCapability
public interface IPlayerSkills extends INBTSerializable<CompoundTag> {
	List<PassiveSkill> getPlayerSkills();

	boolean learnSkill(ServerPlayer player, PassiveSkill passiveSkill);

	boolean hasSkill(ResourceLocation skillId);

	int getSkillPoints();

	void setSkillPoints(int skillPoints);

	void grantSkillPoints(int skillPoints);

	boolean isTreeReset();

	void setTreeReset(boolean reset);

	void resetTree(ServerPlayer player);
}
