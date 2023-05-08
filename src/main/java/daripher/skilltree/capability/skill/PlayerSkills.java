package daripher.skilltree.capability.skill;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.common.base.Predicates;

import daripher.skilltree.config.Config;
import daripher.skilltree.data.SkillsDataReloader;
import daripher.skilltree.skill.PassiveSkill;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class PlayerSkills implements IPlayerSkills {
	private static final UUID TREE_VERSION = UUID.fromString("bc9b1323-97e4-4152-8371-f21ae60d184d");
	private List<PassiveSkill> skills = new ArrayList<>();
	private int skillPoints;
	private int expirience;
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
	public int getExpirience() {
		return expirience;
	}

	@Override
	public void grantExpirience(int expirience) {
		this.expirience += expirience;
		var level = getSkillPoints() + getPlayerSkills().size();
		var levelUpCosts = Config.COMMON_CONFIG.getSkillPointCosts();

		if (level >= levelUpCosts.size()) {
			return;
		}

		var levelUpCost = levelUpCosts.get(level);

		while (this.expirience >= levelUpCost) {
			this.expirience -= levelUpCost;
			skillPoints++;
			level = getSkillPoints() + getPlayerSkills().size();

			if (level >= levelUpCosts.size()) {
				return;
			}

			levelUpCost = levelUpCosts.get(level);
		}
	}

	@Override
	public boolean learnSkill(ServerPlayer player, PassiveSkill passiveSkill) {
		if (skillPoints == 0) {
			return false;
		}
		if (skills.contains(passiveSkill)) {
			return false;
		}
		skillPoints--;
		return skills.add(passiveSkill);
	}

	@Override
	public boolean hasSkill(ResourceLocation skillId) {
		return skills.stream().map(PassiveSkill::getId).anyMatch(Predicates.equalTo(skillId));
	}

	@Override
	public boolean isTreeReset() {
		return treeReset;
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
		tag.putInt("Expirience", expirience);
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
		expirience = tag.getInt("Expirience");
		var skillTagsList = tag.getList("Skills", StringTag.valueOf("").getId());
		if (!treeVersion.equals(TREE_VERSION)) {
			skillPoints += skillTagsList.size();
			treeReset = true;
		} else {
			skillTagsList.forEach(skillTag -> {
				var skillId = new ResourceLocation(skillTag.getAsString());
				var passiveSkill = SkillsDataReloader.getSkillById(skillId);

				if (passiveSkill != null) {
					skills.add(passiveSkill);
				}
			});
		}
	}
}
