package daripher.skilltree.client;

import java.util.HashMap;
import java.util.Map;

import daripher.skilltree.skill.PassiveSkill;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class SkillTreeClientData {
	private static final Map<ResourceLocation, PassiveSkill> ALL_ASSIVE_SKILLS = new HashMap<>();
	private static final Map<ResourceLocation, Map<ResourceLocation, PassiveSkill>> SKILL_TREES = new HashMap<>();

	public static void loadFromByteBuf(FriendlyByteBuf buf) {
		ALL_ASSIVE_SKILLS.clear();
		SKILL_TREES.clear();
		var skillsCount = buf.readInt();

		for (var i = 0; i < skillsCount; i++) {
			var passiveSkill = PassiveSkill.loadFromByteBuf(buf);
			ALL_ASSIVE_SKILLS.put(passiveSkill.getId(), passiveSkill);
			var skillTreeId = passiveSkill.getTreeId();

			if (SKILL_TREES.get(skillTreeId) == null) {
				SKILL_TREES.put(skillTreeId, new HashMap<>());
			}

			SKILL_TREES.get(skillTreeId).put(passiveSkill.getId(), passiveSkill);
		}
	}

	public static Map<ResourceLocation, PassiveSkill> getSkillsForTree(ResourceLocation skillTreeId) {
		return SKILL_TREES.get(skillTreeId);
	}
}
