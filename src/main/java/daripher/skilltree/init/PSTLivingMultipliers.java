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
      new ResourceLocation(SkillTreeMod.MOD_ID, "skill_bonus_multipliers");
  public static final DeferredRegister<LivingMultiplier.Serializer> REGISTRY =
      DeferredRegister.create(REGISTRY_ID, SkillTreeMod.MOD_ID);

  public static final RegistryObject<LivingMultiplier.Serializer> NONE =
      REGISTRY.register("none", NoneMultiplier.Serializer::new);
  public static final RegistryObject<LivingMultiplier.Serializer> EFFECT_AMOUNT =
      REGISTRY.register("effect_amount", EffectAmountMultiplier.Serializer::new);
  public static final RegistryObject<LivingMultiplier.Serializer> ATTRIBUTE_VALUE =
      REGISTRY.register("attribute_value", AttributeValueMultiplier.Serializer::new);
  public static final RegistryObject<LivingMultiplier.Serializer> ENCHANTS_AMOUNT =
      REGISTRY.register("enchants_amount", EnchantsAmountMultiplier.Serializer::new);
  public static final RegistryObject<LivingMultiplier.Serializer> ENCHANTS_LEVELS =
      REGISTRY.register("enchants_levels", EnchantLevelsAmountMultiplier.Serializer::new);
  public static final RegistryObject<LivingMultiplier.Serializer> GEMS_AMOUNT =
      REGISTRY.register("gems_amount", GemsAmountMultiplier.Serializer::new);
  public static final RegistryObject<LivingMultiplier.Serializer> FOOD_LEVEL =
      REGISTRY.register("food_level", HungerLevelMultiplier.Serializer::new);
  public static final RegistryObject<LivingMultiplier.Serializer> DISTANCE_TO_TARGET =
      REGISTRY.register("distance_to_target", DistanceToTargetMultiplier.Serializer::new);
  public static final RegistryObject<LivingMultiplier.Serializer> MISSING_HEALTH_POINTS =
      REGISTRY.register("missing_health_points", MissingHealthPointsMultiplier.Serializer::new);
  public static final RegistryObject<LivingMultiplier.Serializer> MISSING_HEALTH_PERCENTAGE =
      REGISTRY.register(
          "missing_health_percentage", MissingHealthPercentageMultiplier.Serializer::new);

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
