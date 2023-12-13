package daripher.skilltree.skill;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;

public class PassiveSkillTree {
  private final List<ResourceLocation> skillIds = new ArrayList<>();
  private final ResourceLocation id;

  public PassiveSkillTree(ResourceLocation id) {
    this.id = id;
  }

  public ResourceLocation getId() {
    return id;
  }

  public List<ResourceLocation> getSkillIds() {
    return skillIds;
  }
}
