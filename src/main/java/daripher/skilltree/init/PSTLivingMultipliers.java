package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.skill.bonus.multiplier.*;
import java.util.List;
import java.util.Objects;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class PSTLivingMultipliers {
  public static final ResourceLocation REGISTRY_ID =
      ResourceLocation.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "skill_bonus_multipliers");
  public static final DeferredRegister<LivingMultiplier.Serializer> REGISTRY =
      DeferredRegister.create(REGISTRY_ID, SkillTreeMod.MOD_ID);

  public static final RegistryObject<LivingMultiplier.Serializer> NONE =
      REGISTRY.register("none", NoneLivingMultiplier.Serializer::new);
  public static final RegistryObject<LivingMultiplier.Serializer> NUMERIC_VALUE =
      REGISTRY.register("numeric_value", FloatFunctionMultiplier.Serializer::new);

  public static List<LivingMultiplier> multiplierList() {
    return PSTRegistries.LIVING_MULTIPLIERS.get().getValues().stream()
        .map(LivingMultiplier.Serializer::createDefaultInstance)
        .toList();
  }

  public static String getName(LivingMultiplier condition) {
    ResourceLocation id = PSTRegistries.LIVING_MULTIPLIERS.get().getKey(condition.getSerializer());
    return TooltipHelper.idToName(Objects.requireNonNull(id).getPath());
  }
}
