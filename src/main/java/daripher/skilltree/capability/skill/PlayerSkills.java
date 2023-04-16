package daripher.skilltree.capability.skill;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Predicates;

import daripher.skilltree.config.Config;
import daripher.skilltree.data.SkillsDataReloader;
import daripher.skilltree.skill.PassiveSkill;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class PlayerSkills implements IPlayerSkills {
	private List<PassiveSkill> skills = new ArrayList<>();
	private int skillPoints;
	private int expirience;

	@Override
	public List<PassiveSkill> getPlayerSkills() {
		return skills;
	}

	@Override
	public int getSkillPoints() {
		return skillPoints;
	}

	@Override
	public int getExpirience() {
		return expirience;
	}

	@Override
	public void grantExpirience(int expirience) {
		this.expirience += expirience;
		var level = getSkillPoints() + getPlayerSkills().size();
		var levelUpCosts = Config.COMMON_CONFIG.skillPointsCosts.get();

		if (level >= levelUpCosts.size()) {
			return;
		}

		var levelUpCost = levelUpCosts.get(level);

		if (this.expirience >= levelUpCost) {
			this.expirience -= levelUpCost;
			skillPoints++;
		}
	}

	@Override
	public void learnSkill(ServerPlayer player, PassiveSkill passiveSkill) {
		if (skillPoints == 0) {
			return;
		}

		skillPoints--;
		skills.add(passiveSkill);
		var attributeBonus = passiveSkill.getAttributeBonus();

		if (attributeBonus != null) {
			var modifiedAttribute = player.getAttribute(attributeBonus.getLeft());
			var attributeModifier = new AttributeModifier(passiveSkill.getUniqueId(), passiveSkill.getId().toString(), attributeBonus.getMiddle(), attributeBonus.getRight());
			modifiedAttribute.addPermanentModifier(attributeModifier);
		}
	}

	@Override
	public boolean hasSkill(ResourceLocation skillId) {
		return skills.stream().map(PassiveSkill::getId).anyMatch(Predicates.equalTo(skillId));
	}

	@Override
	public CompoundTag serializeNBT() {
		var tag = new CompoundTag();
		var skillTagsList = new ListTag();

		skills.forEach(skill -> {
			skillTagsList.add(StringTag.valueOf(skill.getId().toString()));
		});

		tag.put("Skills", skillTagsList);
		tag.putInt("Points", skillPoints);
		tag.putInt("Expirience", expirience);
		return tag;
	}

	@Override
	public void deserializeNBT(CompoundTag tag) {
		skills.clear();
		var skillTagsList = tag.getList("Skills", StringTag.valueOf("").getId());

		skillTagsList.forEach(skillTag -> {
			var skillId = new ResourceLocation(skillTag.getAsString());
			var passiveSkill = SkillsDataReloader.getSkillById(skillId);
			skills.add(passiveSkill);
		});

		skillPoints = tag.getInt("Points");
		expirience = tag.getInt("Expirience");
	}
}
