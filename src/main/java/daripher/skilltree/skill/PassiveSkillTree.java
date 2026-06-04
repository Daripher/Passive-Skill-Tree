package daripher.skilltree.skill;

import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PassiveSkillTree {
  private final List<ResourceLocation> skillIds = new ArrayList<>();
  private final ResourceLocation id;
  private @Nullable Map<String, Integer> skillLimitations;

  public PassiveSkillTree(ResourceLocation id) {
    this.id = id;
  }

  public ResourceLocation getId() {
    return id;
  }

  public List<ResourceLocation> getSkillIds() {
    return skillIds;
  }

  public Map<String, Integer> getSkillLimitations() {
    if (skillLimitations == null) return skillLimitations = new LinkedHashMap<>();
    return skillLimitations;
  }
}
