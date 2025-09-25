package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.client.tooltip.TooltipHelper;

import java.util.List;
import java.util.Objects;

import daripher.skilltree.skill.requirement.NumericValueRequirement;
import daripher.skilltree.skill.requirement.SkillRequirement;
import daripher.skilltree.skill.requirement.StatRequirement;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class PSTSkillRequirements {
  public static final ResourceLocation REGISTRY_ID =
      new ResourceLocation(SkillTreeMod.MOD_ID, "skill_requirements");
  public static final DeferredRegister<SkillRequirement.Serializer> REGISTRY =
      DeferredRegister.create(REGISTRY_ID, SkillTreeMod.MOD_ID);

  public static final RegistryObject<SkillRequirement.Serializer> STAT_VALUE =
      REGISTRY.register("stat_value", StatRequirement.Serializer::new);
  public static final RegistryObject<SkillRequirement.Serializer> NUMERIC_VALUE =
      REGISTRY.register("numeric_value", NumericValueRequirement.Serializer::new);

  @SuppressWarnings("rawtypes")
  public static List<SkillRequirement> requirementList() {
    return PSTRegistries.SKILL_REQUIREMENTS.get().getValues().stream()
        .map(SkillRequirement.Serializer::createDefaultInstance)
        .map(SkillRequirement.class::cast)
        .toList();
  }

  public static String getName(SkillRequirement<?> bonus) {
    ResourceLocation id = PSTRegistries.SKILL_REQUIREMENTS.get().getKey(bonus.getSerializer());
    return TooltipHelper.idToName(Objects.requireNonNull(id).getPath());
  }
}
