package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.skill.bonus.function.*;

import java.util.List;
import java.util.Objects;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.*;

public class PSTFloatFunctions {
  public static final ResourceLocation REGISTRY_ID =
      new ResourceLocation(SkillTreeMod.MOD_ID, "numeric_value_providers");
  public static final DeferredRegister<FloatFunction.Serializer> REGISTRY =
      DeferredRegister.create(REGISTRY_ID, SkillTreeMod.MOD_ID);

  public static final RegistryObject<FloatFunction.Serializer> ATTRIBUTE_VALUE =
      REGISTRY.register("attribute_value", AttributeValueFunction.Serializer::new);
  public static final RegistryObject<FloatFunction.Serializer> EFFECT_AMOUNT =
      REGISTRY.register("effect_amount", EffectAmountFunction.Serializer::new);
  public static final RegistryObject<FloatFunction.Serializer> FOOD_LEVEL =
      REGISTRY.register("food_level", FoodLevelFunction.Serializer::new);
  public static final RegistryObject<FloatFunction.Serializer> HEALTH_LEVEL =
      REGISTRY.register("health_level", HealthLevelFunction.Serializer::new);
  public static final RegistryObject<FloatFunction.Serializer> EQUIPMENT_DURABILITY =
      REGISTRY.register("equipment_durability", EquipmentDurabilityFunction.Serializer::new);
  public static final RegistryObject<FloatFunction.Serializer> ENCHANTMENT_AMOUNT =
      REGISTRY.register("enchantment_amount", EnchantmentAmountFunction.Serializer::new);
  public static final RegistryObject<FloatFunction.Serializer> ENCHANTMENT_LEVELS =
      REGISTRY.register("enchantment_levels", EnchantmentLevelsFunction.Serializer::new);
  public static final RegistryObject<FloatFunction.Serializer> DISTANCE_TO_TARGET =
      REGISTRY.register("distance_to_target", DistanceToTargetFunction.Serializer::new);

  @SuppressWarnings("rawtypes")
  public static List<FloatFunction> providerList() {
    return PSTRegistries.FLOAT_FUNCTIONS.get().getValues().stream()
        .map(FloatFunction.Serializer::createDefaultInstance)
        .map(FloatFunction.class::cast)
        .toList();
  }

  public static String getName(FloatFunction<?> provider) {
    ResourceLocation id =
        PSTRegistries.FLOAT_FUNCTIONS.get().getKey(provider.getSerializer());
    return TooltipHelper.idToName(Objects.requireNonNull(id).getPath());
  }
}
