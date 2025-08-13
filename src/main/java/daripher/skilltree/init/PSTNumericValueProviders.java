package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.skill.bonus.condition.living.numeric.provider.*;
import daripher.skilltree.skill.bonus.condition.living.numeric.NumericValueProvider;
import java.util.List;
import java.util.Objects;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.*;

public class PSTNumericValueProviders {
  public static final ResourceLocation REGISTRY_ID =
      new ResourceLocation(SkillTreeMod.MOD_ID, "numeric_value_providers");
  public static final DeferredRegister<NumericValueProvider.Serializer> REGISTRY =
      DeferredRegister.create(REGISTRY_ID, SkillTreeMod.MOD_ID);

  public static final RegistryObject<NumericValueProvider.Serializer> ATTRIBUTE_VALUE =
      REGISTRY.register("attribute_value", AttributeValueProvider.Serializer::new);
  public static final RegistryObject<NumericValueProvider.Serializer> EFFECT_AMOUNT =
      REGISTRY.register("effect_amount", EffectAmountProvider.Serializer::new);
  public static final RegistryObject<NumericValueProvider.Serializer> FOOD_LEVEL =
      REGISTRY.register("food_level", FoodLevelProvider.Serializer::new);
  public static final RegistryObject<NumericValueProvider.Serializer> HEALTH_LEVEL =
      REGISTRY.register("health_level", HealthLevelProvider.Serializer::new);
  public static final RegistryObject<NumericValueProvider.Serializer> EQUIPMENT_DURABILITY =
      REGISTRY.register("equipment_durability", EquipmentDurabilityProvider.Serializer::new);
  public static final RegistryObject<NumericValueProvider.Serializer> ENCHANTMENT_AMOUNT =
      REGISTRY.register("enchantment_amount", EnchantmentAmountProvider.Serializer::new);
  public static final RegistryObject<NumericValueProvider.Serializer> ENCHANTMENT_LEVELS =
      REGISTRY.register("enchantment_levels", EnchantmentLevelsProvider.Serializer::new);
  public static final RegistryObject<NumericValueProvider.Serializer> DISTANCE_TO_TARGET =
      REGISTRY.register("distance_to_target", DistanceToTargetProvider.Serializer::new);

  @SuppressWarnings("rawtypes")
  public static List<NumericValueProvider> providerList() {
    return PSTRegistries.NUMERIC_VALUE_PROVIDERS.get().getValues().stream()
        .map(NumericValueProvider.Serializer::createDefaultInstance)
        .map(NumericValueProvider.class::cast)
        .toList();
  }

  public static String getName(NumericValueProvider<?> provider) {
    ResourceLocation id =
        PSTRegistries.NUMERIC_VALUE_PROVIDERS.get().getKey(provider.getSerializer());
    return TooltipHelper.idToName(Objects.requireNonNull(id).getPath());
  }
}
