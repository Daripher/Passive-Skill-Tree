package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.skill.bonus.condition.living.*;
import java.util.List;
import java.util.Objects;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class PSTLivingConditions {
  public static final ResourceLocation REGISTRY_ID =
      new ResourceLocation(SkillTreeMod.MOD_ID, "living_conditions");
  public static final DeferredRegister<LivingCondition.Serializer> REGISTRY =
      DeferredRegister.create(REGISTRY_ID, SkillTreeMod.MOD_ID);

  public static final RegistryObject<LivingCondition.Serializer> NONE =
      REGISTRY.register("none", NoneLivingCondition.Serializer::new);
  public static final RegistryObject<LivingCondition.Serializer> EFFECT_AMOUNT =
      REGISTRY.register("effect_amount", EffectAmountCondition.Serializer::new);
  public static final RegistryObject<LivingCondition.Serializer> HEALTH_PERCENTAGE =
      REGISTRY.register("health_percentage", HealthPercentageCondition.Serializer::new);
  public static final RegistryObject<LivingCondition.Serializer> HAS_ENCHANTED_ITEM =
      REGISTRY.register("has_enchanted_item", HasEnchantedItemCondition.Serializer::new);
  public static final RegistryObject<LivingCondition.Serializer> HAS_ITEM_EQUIPPED =
      REGISTRY.register("has_item_equipped", HasItemEquippedCondition.Serializer::new);
  public static final RegistryObject<LivingCondition.Serializer> HAS_GEMS =
      REGISTRY.register("has_gems", HasGemsCondition.Serializer::new);
  public static final RegistryObject<LivingCondition.Serializer> HAS_EFFECT =
      REGISTRY.register("has_effect", HasEffectCondition.Serializer::new);
  public static final RegistryObject<LivingCondition.Serializer> BURNING =
      REGISTRY.register("burning", BurningCondition.Serializer::new);
  public static final RegistryObject<LivingCondition.Serializer> ATTRIBUTE_VALUE =
      REGISTRY.register("attribute_value", AttributeValueCondition.Serializer::new);
  public static final RegistryObject<LivingCondition.Serializer> FOOD_LEVEL =
      REGISTRY.register("food_level", FoodLevelCondition.Serializer::new);
  public static final RegistryObject<LivingCondition.Serializer> FISHING =
      REGISTRY.register("fishing", FishingCondition.Serializer::new);
  public static final RegistryObject<LivingCondition.Serializer> UNDERWATER =
      REGISTRY.register("underwater", UnderwaterCondition.Serializer::new);
  public static final RegistryObject<LivingCondition.Serializer> DUAL_WIELDING =
      REGISTRY.register("dual_wielding", DualWieldingCondition.Serializer::new);
  public static final RegistryObject<LivingCondition.Serializer> HAS_ITEM_IN_HAND =
      REGISTRY.register("has_item_in_hand", HasItemInHandCondition.Serializer::new);

  public static List<LivingCondition> conditionsList() {
    return PSTRegistries.LIVING_CONDITIONS.get().getValues().stream()
        .map(LivingCondition.Serializer::createDefaultInstance)
        .toList();
  }

  public static String getName(LivingCondition condition) {
    ResourceLocation id = PSTRegistries.LIVING_CONDITIONS.get().getKey(condition.getSerializer());
    return TooltipHelper.idToName(Objects.requireNonNull(id).getPath());
  }
}
