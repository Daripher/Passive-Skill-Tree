package daripher.skilltree.data.generation.skills;

import java.io.IOException;
import java.nio.file.Path;

import com.google.gson.JsonElement;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.data.reloader.SkillTreesReloader;
import daripher.skilltree.skill.PassiveSkillTree;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;

public class PSTSkillTreesProvider implements DataProvider {
    private final DataGenerator dataGenerator;
    private final PSTSkillsProvider skillsProvider;

    public PSTSkillTreesProvider(DataGenerator dataGenerator, PSTSkillsProvider skillsProvider) {
        this.dataGenerator = dataGenerator;
        this.skillsProvider = skillsProvider;
    }

    @Override
    public void run(CachedOutput output) throws IOException {
        PassiveSkillTree skillTree = new PassiveSkillTree(new ResourceLocation(SkillTreeMod.MOD_ID, "main_tree"));
        skillsProvider.getSkills().keySet().forEach(skillTree.getSkillIds()::add);
        Path path = dataGenerator.getOutputFolder().resolve(getSkillTreePath(skillTree));
        JsonElement json = SkillTreesReloader.GSON.toJsonTree(skillTree);
        try {
            DataProvider.saveStable(output, json, path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getSkillTreePath(PassiveSkillTree skillTree) {
        ResourceLocation id = skillTree.getId();
        return "data/" + id.getNamespace() + "/skill_trees/" + id.getPath() + ".json";
    }

    @Override
    public String getName() {
        return "Skill Trees Provider";
    }
}