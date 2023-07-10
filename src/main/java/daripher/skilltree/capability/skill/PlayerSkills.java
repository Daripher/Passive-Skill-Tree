package daripher.skilltree.capability.skill;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import daripher.skilltree.data.SkillsReloader;
import daripher.skilltree.skill.PassiveSkill;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class PlayerSkills implements IPlayerSkills {
	private static final UUID TREE_VERSION = UUID.fromString("77fee779-4042-4312-8aca-5776af55238e");
	private List<PassiveSkill> skills = new ArrayList<>();
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
		getPlayerSkills().forEach(skill -> {
			skill.getAttributeModifiers().forEach(pair -> {
				var attribute = pair.getLeft();
				var modifier = pair.getRight();
				var playerAttribute = player.getAttribute(attribute);
				if (playerAttribute.hasModifier(modifier)) {
					playerAttribute.removeModifier(modifier);
				}
			});
		});
		getPlayerSkills().clear();
	}

	@Override
	public CompoundTag serializeNBT() {
		var tag = new CompoundTag();
		tag.putUUID("TreeVersion", TREE_VERSION);
		tag.putInt("Points", skillPoints);
		tag.putBoolean("TreeReset", treeReset);
		var skillTagsList = new ListTag();

		skills.forEach(skill -> {
			skillTagsList.add(StringTag.valueOf(skill.getId().toString()));
		});

		tag.put("Skills", skillTagsList);
		return tag;
	}

	@Override
	public void deserializeNBT(CompoundTag tag) {
		skills.clear();
		var treeVersion = tag.hasUUID("TreeVersion") ? tag.getUUID("TreeVersion") : null;
		skillPoints = tag.getInt("Points");
		var skillTagsList = tag.getList("Skills", StringTag.valueOf("").getId());
		if (!treeVersion.equals(TREE_VERSION)) {
			skillPoints += skillTagsList.size();
			treeReset = true;
		} else {
			skillTagsList.forEach(skillTag -> {
				var skillId = new ResourceLocation(skillTag.getAsString());
				var passiveSkill = SkillsReloader.getSkillById(skillId);

				if (passiveSkill != null) {
					skills.add(passiveSkill);
				}
			});
		}
	}
}
