package daripher.skilltree.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.util.ByteBufHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class SkillTreeClientData {
	private static final Map<ResourceLocation, PassiveSkill> ALL_ASSIVE_SKILLS = new HashMap<>();
	private static final Map<ResourceLocation, Map<ResourceLocation, PassiveSkill>> SKILL_TREES = new HashMap<>();

	public static void loadFromByteBuf(FriendlyByteBuf buf) {
		ALL_ASSIVE_SKILLS.clear();
		SKILL_TREES.clear();
		List<PassiveSkill> skills = ByteBufHelper.readPassiveSkills(buf);
		skills.forEach(SkillTreeClientData::storeSkill);
	}

	private static void storeSkill(PassiveSkill skill) {
		ALL_ASSIVE_SKILLS.put(skill.getId(), skill);
		ResourceLocation treeId = skill.getTreeId();
		if (SKILL_TREES.get(treeId) == null) SKILL_TREES.put(treeId, new HashMap<>());
		SKILL_TREES.get(treeId).put(skill.getId(), skill);
	}

	public static Map<ResourceLocation, PassiveSkill> getSkillsForTree(ResourceLocation skillTreeId) {
		return SKILL_TREES.get(skillTreeId);
	}
}
