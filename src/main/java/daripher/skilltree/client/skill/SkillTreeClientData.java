package daripher.skilltree.client.skill;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonIOException;
import com.google.gson.stream.JsonReader;

import daripher.skilltree.data.reloader.SkillTreesReloader;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.PassiveSkillTree;
import daripher.skilltree.util.ByteBufHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.loading.FMLPaths;

public class SkillTreeClientData {
	private static final Map<ResourceLocation, PassiveSkill> PASSIVE_SKILLS = new HashMap<>();
	private static final Map<ResourceLocation, PassiveSkillTree> SKILL_TREES = new HashMap<>();
	private static final Map<ResourceLocation, PassiveSkill> EDITOR_PASSIVE_SKILLS = new HashMap<>();
	private static final Map<ResourceLocation, PassiveSkillTree> EDITOR_TREES = new HashMap<>();

	public static void loadFromByteBuf(FriendlyByteBuf buf) {
		PASSIVE_SKILLS.clear();
		SKILL_TREES.clear();
		List<PassiveSkill> skills = ByteBufHelper.readPassiveSkills(buf);
		skills.forEach(SkillTreeClientData::storeSkill);
		List<PassiveSkillTree> skillTrees = ByteBufHelper.readPassiveSkillTrees(buf);
		skillTrees.forEach(SkillTreeClientData::storeSkillTree);
	}

	private static void storeSkill(PassiveSkill skill) {
		PASSIVE_SKILLS.put(skill.getId(), skill);
	}

	private static void storeSkillTree(PassiveSkillTree skillTree) {
		SKILL_TREES.put(skillTree.getId(), skillTree);
	}

	public static List<ResourceLocation> getAllTreesIds() {
		ArrayList<ResourceLocation> ids = new ArrayList<>(SKILL_TREES.keySet());
		ids.addAll(EDITOR_TREES.keySet());
		return ids;
	}

	public static PassiveSkill getSkill(ResourceLocation id) {
		return PASSIVE_SKILLS.get(id);
	}

	public static PassiveSkill getEditorSkill(ResourceLocation id) {
		return EDITOR_PASSIVE_SKILLS.get(id);
	}

	public static PassiveSkillTree getSkillTree(ResourceLocation id) {
		return SKILL_TREES.get(id);
	}

	public static PassiveSkillTree getOrCreateEditorTree(ResourceLocation treeId) {
		File skillTreeSavesFolder = getSkillTreeSavesFolder(treeId);
		if (!skillTreeSavesFolder.exists()) {
			skillTreeSavesFolder.mkdirs();
		}
		if (!getSkillTreeSaveFile(treeId).exists() && SKILL_TREES.containsKey(treeId)) {
			saveEditorSkillTree(SKILL_TREES.get(treeId));
		}
		if (!EDITOR_TREES.containsKey(treeId)) {
			loadEditorSkillTree(treeId);
		}
		PassiveSkillTree skillTree = EDITOR_TREES.getOrDefault(treeId, new PassiveSkillTree(treeId));
		skillTree.getSkillIds().forEach(skillId -> {
			File skillSavesFolder = getSkillSavesFolder(skillId);
			if (!skillSavesFolder.exists()) {
				skillSavesFolder.mkdirs();
			}
			if (!getSkillSaveFile(skillId).exists() && PASSIVE_SKILLS.containsKey(skillId)) {
				saveEditorSkill(PASSIVE_SKILLS.get(skillId));
			}
			if (!EDITOR_PASSIVE_SKILLS.containsKey(skillId)) {
				loadEditorSkill(skillId);
			}
		});
		return skillTree;
	}

	public static void saveEditorSkillTree(PassiveSkillTree skillTree) {
		try (FileWriter writer = new FileWriter(getSkillTreeSaveFile(skillTree.getId()))) {
			SkillTreesReloader.GSON.toJson(skillTree, writer);
			writer.close();
		} catch (JsonIOException | IOException e) {
			e.printStackTrace();
		}
	}

	public static void loadEditorSkillTree(ResourceLocation treeId) {
		PassiveSkillTree skillTree = readFromFile(PassiveSkillTree.class, getSkillTreeSaveFile(treeId));
		if (skillTree == null) {
			skillTree = new PassiveSkillTree(treeId);
			saveEditorSkillTree(skillTree);
		}
		EDITOR_TREES.put(treeId, skillTree);
	}

	public static void saveEditorSkill(PassiveSkill skill) {
		try (FileWriter writer = new FileWriter(getSkillSaveFile(skill.getId()))) {
			SkillsReloader.GSON.toJson(skill, writer);
			writer.close();
		} catch (JsonIOException | IOException e) {
			e.printStackTrace();
		}
	}

	public static void loadEditorSkill(ResourceLocation skillId) {
		PassiveSkill skill = readFromFile(PassiveSkill.class, getSkillSaveFile(skillId));
		EDITOR_PASSIVE_SKILLS.put(skillId, skill);
	}

	public static void deleteEditorSkill(PassiveSkill skill) {
		getSkillSaveFile(skill.getId()).delete();
		EDITOR_PASSIVE_SKILLS.remove(skill.getId());
	}

	private static File getSavesFolder() {
		return new File(FMLPaths.GAMEDIR.get().toFile(), "skilltree/editor/data");
	}

	private static File getSkillSavesFolder(ResourceLocation skillId) {
		return new File(getSavesFolder(), skillId.getNamespace() + "/skills");
	}

	private static File getSkillTreeSavesFolder(ResourceLocation skillTreeId) {
		return new File(getSavesFolder(), skillTreeId.getNamespace() + "/skill_trees");
	}

	private static File getSkillSaveFile(ResourceLocation skillId) {
		return new File(getSkillSavesFolder(skillId), skillId.getPath() + ".json");
	}

	private static File getSkillTreeSaveFile(ResourceLocation skillTreeId) {
		return new File(getSkillTreeSavesFolder(skillTreeId), skillTreeId.getPath() + ".json");
	}

	private static <T> T readFromFile(Class<T> objectType, File file) {
		T object = null;
		try (JsonReader reader = new JsonReader(new FileReader(file))) {
			object = SkillsReloader.GSON.fromJson(reader, objectType);
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return object;
	}
}
