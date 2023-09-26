package daripher.skilltree.client;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import daripher.skilltree.data.SkillsReloader;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.util.ByteBufHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.loading.FMLPaths;

public class SkillTreeClientData {
	private static final Map<ResourceLocation, PassiveSkill> ALL_ASSIVE_SKILLS = new HashMap<>();
	private static final Map<ResourceLocation, Map<ResourceLocation, PassiveSkill>> SKILL_TREES = new HashMap<>();
	private static final Map<ResourceLocation, Map<ResourceLocation, PassiveSkill>> EDITOR_TREES = new HashMap<>();

	public static void loadFromByteBuf(FriendlyByteBuf buf) {
		ALL_ASSIVE_SKILLS.clear();
		SKILL_TREES.clear();
		List<PassiveSkill> skills = ByteBufHelper.readPassiveSkills(buf);
		skills.forEach(SkillTreeClientData::storeSkill);
	}

	private static void storeSkill(PassiveSkill skill) {
		ALL_ASSIVE_SKILLS.put(skill.getId(), skill);
		// TODO: remove when tree system is implemented
		ResourceLocation treeId = new ResourceLocation("skilltree", "tree");
		if (SKILL_TREES.get(treeId) == null) SKILL_TREES.put(treeId, new HashMap<>());
		SKILL_TREES.get(treeId).put(skill.getId(), skill);
	}

	public static Set<ResourceLocation> getTreeIds() {
		return SKILL_TREES.keySet();
	}

	public static Map<ResourceLocation, PassiveSkill> getSkillsForTree(ResourceLocation skillTreeId) {
		return SKILL_TREES.getOrDefault(skillTreeId, new HashMap<>());
	}

	public static Map<ResourceLocation, PassiveSkill> getOrCreateEditorTree(ResourceLocation treeId) {
		File treeSavesFolder = new File(getSavesFolder(), treeId.toString().replace(":", "/"));
		if (!treeSavesFolder.exists()) {
			treeSavesFolder.mkdirs();
			saveEditorTree(treeId);
		}
		if (!EDITOR_TREES.containsKey(treeId)) {
			return loadEditorTree(treeSavesFolder, treeId);
		} else {
			return EDITOR_TREES.get(treeId);
		}
	}

	private static File getSavesFolder() {
		return new File(FMLPaths.GAMEDIR.get().toFile(), "skilltree/editor");
	}

	private static void saveEditorTree(ResourceLocation treeId) {
		Map<ResourceLocation, PassiveSkill> skills = getSkillsForTree(treeId);
		skills.forEach((id, skill) -> saveEditorSkill(treeId, id, skill));
		EDITOR_TREES.put(treeId, skills);
	}

	public static void saveEditorSkill(ResourceLocation treeId, ResourceLocation id, PassiveSkill skill) {
		File folder = new File(getSavesFolder(), treeId.toString().replace(":", "/"));
		JsonElement json = SkillsReloader.GSON.toJsonTree(skill);
		try {
			PrintWriter out = new PrintWriter(new FileWriter(new File(folder, id.getPath() + ".json")));
			out.write(json.toString());
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Map<ResourceLocation, PassiveSkill> loadEditorTree(File folder, ResourceLocation treeId) {
		Map<ResourceLocation, PassiveSkill> skills = new HashMap<>();
		// formatter:off
		Stream.of(folder.listFiles())
			.filter(file -> !file.isDirectory())
			.filter(file -> file.getPath().endsWith(".json"))
			.forEach(file -> {
				try {
					JsonReader reader = new JsonReader(new FileReader(file));
					JsonElement json = JsonParser.parseReader(reader);
					ResourceLocation skillId = new ResourceLocation("skilltree:" + FilenameUtils.removeExtension(file.getName()));
					PassiveSkill skill = SkillsReloader.GSON.fromJson(json, PassiveSkill.class);
					skills.put(skillId, skill);
					reader.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		// formatter:on
		EDITOR_TREES.put(treeId, skills);
		return skills;
	}
}
