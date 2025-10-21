package daripher.skilltree.client.data;

import com.google.gson.JsonIOException;
import com.google.gson.stream.JsonReader;
import daripher.skilltree.data.reloader.SkillTreesReloader;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.PassiveSkillTree;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.loading.FMLPaths;

public class SkillTreeClientData {
  private static final Map<ResourceLocation, PassiveSkill> EDITOR_PASSIVE_SKILLS = new HashMap<>();
  private static final Map<ResourceLocation, PassiveSkillTree> EDITOR_TREES = new HashMap<>();
  private static final Set<ResourceLocation> EDITOR_TREES_IDS = new HashSet<>();
  private static boolean loadedIDs = false;

  public static PassiveSkill getEditorSkill(ResourceLocation id) {
    return EDITOR_PASSIVE_SKILLS.get(id);
  }

  public static @Nullable PassiveSkillTree getOrCreateEditorTree(ResourceLocation treeId) {
    try {
      File folder = getSkillTreeSavesFolder(treeId);
      if (!folder.exists()) {
        folder.mkdirs();
      }
      File mcmetaFile = new File(getEditorFolder(), "pack.mcmeta");
      if (!mcmetaFile.exists()) {
        generatePackMcmetaFile(mcmetaFile);
      }
      if (!getSkillTreeSaveFile(treeId).exists()) {
        PassiveSkillTree skillTree = SkillTreesReloader.getSkillTreeById(treeId);
        saveEditorSkillTree(skillTree);
      }
      if (!EDITOR_TREES.containsKey(treeId)) {
        loadEditorSkillTree(treeId);
      }
      if (!EDITOR_TREES.containsKey(treeId)) {
        EDITOR_TREES_IDS.add(treeId);
      }
      PassiveSkillTree skillTree = EDITOR_TREES.getOrDefault(treeId, new PassiveSkillTree(treeId));
      for (ResourceLocation skillId : skillTree.getSkillIds()) {
        try {
          loadOrCreateEditorSkill(skillId);
        } catch (Exception exception) {
          exception.printStackTrace();
          printMessage("Couldn't read passive skill " + skillId, ChatFormatting.DARK_RED);
          printMessage("");
          String errorMessage =
              exception.getMessage() == null ? "No error message" : exception.getMessage();
          printMessage(errorMessage, ChatFormatting.RED);
          return null;
        }
      }
      return skillTree;
    } catch (Exception exception) {
      EDITOR_TREES.clear();
      EDITOR_PASSIVE_SKILLS.clear();
      printMessage("Couldn't read skill tree " + treeId, ChatFormatting.DARK_RED);
      printMessage("");
      String errorMessage =
          exception.getMessage() == null ? "No error message" : exception.getMessage();
      printMessage(errorMessage, ChatFormatting.RED);
      printMessage("");
      printMessage("Try removing files from folder", ChatFormatting.DARK_RED);
      printMessage("");
      printMessage(getEditorDataFolder().getPath(), ChatFormatting.RED);
      exception.printStackTrace();
      return null;
    }
  }

  private static void generatePackMcmetaFile(File file) {
    try {
      BufferedWriter writer = new BufferedWriter(new FileWriter(file));
      String contents =
          """
          {
            "pack": {
              "description": {
                "text": "PST editor data"
              },
              "pack_format": 15
            }
          }
          """;
      writer.write(contents);
      writer.close();
    } catch (IOException exception) {
      exception.printStackTrace();
      throw new RuntimeException(exception);
    }
  }

  private static void loadOrCreateEditorSkill(ResourceLocation skillId) {
    File skillSavesFolder = getSkillSavesFolder(skillId);
    if (!skillSavesFolder.exists()) {
      skillSavesFolder.mkdirs();
    }
    if (!getSkillSaveFile(skillId).exists()) {
      PassiveSkill skill = SkillsReloader.getSkillById(skillId);
      if (skill != null) saveEditorSkill(skill);
    }
    if (!EDITOR_PASSIVE_SKILLS.containsKey(skillId)) {
      loadEditorSkill(skillId);
    }
  }

  public static void saveEditorSkillTree(PassiveSkillTree skillTree) {
    File file = getSkillTreeSaveFile(skillTree.getId());
    try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
      SkillTreesReloader.GSON.toJson(skillTree, writer);
    } catch (JsonIOException | IOException exception) {
      exception.printStackTrace();
      throw new RuntimeException("Can't save editor skill tree " + skillTree.getId());
    }
  }

  public static void loadEditorSkillTree(ResourceLocation treeId) throws IOException {
    File file = getSkillTreeSaveFile(treeId);
    PassiveSkillTree skillTree;
    try {
      skillTree = readFromFile(PassiveSkillTree.class, file);
    } catch (Exception exception) {
      skillTree = new PassiveSkillTree(treeId);
      saveEditorSkillTree(skillTree);
      EDITOR_TREES.put(treeId, skillTree);
      throw exception;
    }
    EDITOR_TREES.put(treeId, skillTree);
  }

  public static void saveEditorSkill(PassiveSkill skill) {
    File file = getSkillSaveFile(skill.getId());
    try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
      SkillsReloader.GSON.toJson(skill, writer);
    } catch (JsonIOException | IOException exception) {
      exception.printStackTrace();
      throw new RuntimeException("Can't save editor skill " + skill.getId());
    }
  }

  public static void loadEditorSkill(ResourceLocation skillId) {
    PassiveSkill skill;
    try {
      skill = readFromFile(PassiveSkill.class, getSkillSaveFile(skillId));
    } catch (IOException exception) {
      exception.printStackTrace();
      printMessage("Can't load editor skill " + skillId, ChatFormatting.DARK_RED);
      throw new RuntimeException("Can't load editor skill " + skillId);
    }
    EDITOR_PASSIVE_SKILLS.put(skillId, skill);
  }

  public static void deleteEditorSkill(PassiveSkill skill) {
    getSkillSaveFile(skill.getId()).delete();
    EDITOR_PASSIVE_SKILLS.remove(skill.getId());
  }

  private static File getEditorDataFolder() {
    return new File(getEditorFolder(), "data");
  }

  private static File getEditorFolder() {
    return new File(FMLPaths.GAMEDIR.get().toFile(), "skilltree/editor");
  }

  private static File getSkillSavesFolder(ResourceLocation skillId) {
    return new File(getEditorDataFolder(), skillId.getNamespace() + "/skills");
  }

  private static File getSkillTreeSavesFolder(ResourceLocation skillTreeId) {
    return new File(getEditorDataFolder(), skillTreeId.getNamespace() + "/skill_trees");
  }

  private static File getSkillSaveFile(ResourceLocation skillId) {
    return new File(getSkillSavesFolder(skillId), skillId.getPath() + ".json");
  }

  private static File getSkillTreeSaveFile(ResourceLocation skillTreeId) {
    return new File(getSkillTreeSavesFolder(skillTreeId), skillTreeId.getPath() + ".json");
  }

  private static <T> T readFromFile(Class<T> objectType, File file) throws IOException {
    try (JsonReader reader = new JsonReader(new FileReader(file, StandardCharsets.UTF_8))) {
      return SkillsReloader.GSON.fromJson(reader, objectType);
    }
  }

  public static void printMessage(String text, ChatFormatting... styles) {
    LocalPlayer player = Minecraft.getInstance().player;
    if (player != null) {
      MutableComponent component = Component.literal(text);
      for (ChatFormatting style : styles) {
        component.withStyle(style);
      }
      player.sendSystemMessage(component);
    }
  }

  public static Set<ResourceLocation> getEditorTreesIDs() {
    if(loadedIDs) {
      return EDITOR_TREES_IDS;
    }
    File dataFolder = getEditorDataFolder();
    File[] dataFiles = dataFolder.listFiles();
    if (!dataFolder.exists() || dataFiles == null) {
      return EDITOR_TREES_IDS;
    }
    for(File namespaceDirectory : dataFiles) {
      if (!namespaceDirectory.isDirectory()) continue;
      File skillTreesDirectory = new File(namespaceDirectory, "skill_trees");
      if (!skillTreesDirectory.exists()) continue;
      File[] skillTreeFiles = skillTreesDirectory.listFiles();
      if (skillTreeFiles == null) continue;
      String namespace = namespaceDirectory.getName();
      for (File skillTreeFile : skillTreeFiles) {
        String skillTreeFileName = skillTreeFile.getName();
        if (!skillTreeFileName.endsWith(".json")) continue;
        String skillTreeName = skillTreeFileName.substring(0, skillTreeFileName.lastIndexOf('.'));
        EDITOR_TREES_IDS.add(new ResourceLocation(namespace, skillTreeName));
      }
    }
    loadedIDs = true;
    return EDITOR_TREES_IDS;
  }
}
