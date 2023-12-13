package daripher.skilltree.data.generation.skills;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.data.reloader.SkillTreesReloader;
import daripher.skilltree.skill.PassiveSkillTree;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class PSTSkillTreesProvider implements DataProvider {
  private final PackOutput packOutput;
  private final PSTSkillsProvider skillsProvider;

  public PSTSkillTreesProvider(DataGenerator dataGenerator, PSTSkillsProvider skillsProvider) {
    this.packOutput = dataGenerator.getPackOutput();
    this.skillsProvider = skillsProvider;
  }

  @Override
  public @NotNull CompletableFuture<?> run(@NotNull CachedOutput output) {
    ImmutableList.Builder<CompletableFuture<?>> futuresBuilder = new ImmutableList.Builder<>();
    PassiveSkillTree skillTree =
        new PassiveSkillTree(new ResourceLocation(SkillTreeMod.MOD_ID, "main_tree"));
    skillsProvider.getSkills().keySet().forEach(skillTree.getSkillIds()::add);
    Path path = packOutput.getOutputFolder().resolve(getSkillTreePath(skillTree));
    JsonElement json = SkillTreesReloader.GSON.toJsonTree(skillTree);
    futuresBuilder.add(DataProvider.saveStable(output, json, path));
    return CompletableFuture.allOf(futuresBuilder.build().toArray(CompletableFuture[]::new));
  }

  public String getSkillTreePath(PassiveSkillTree skillTree) {
    ResourceLocation id = skillTree.getId();
    return "data/" + id.getNamespace() + "/skill_trees/" + id.getPath() + ".json";
  }

  @Override
  public @NotNull String getName() {
    return "Skill Trees Provider";
  }
}
