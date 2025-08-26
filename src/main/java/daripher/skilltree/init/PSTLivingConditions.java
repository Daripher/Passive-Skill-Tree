package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.skill.bonus.condition.living.*;
import java.util.List;
import java.util.Objects;

import daripher.skilltree.skill.bonus.condition.living.numeric.NumericValueCondition;
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
  public static final RegistryObject<LivingCondition.Serializer> HAS_ITEM_EQUIPPED =
      REGISTRY.register("has_item_equipped", HasItemEquippedCondition.Serializer::new);
  public static final RegistryObject<LivingCondition.Serializer> HAS_EFFECT =
      REGISTRY.register("has_effect", HasEffectCondition.Serializer::new);
  public static final RegistryObject<LivingCondition.Serializer> BURNING =
      REGISTRY.register("burning", BurningCondition.Serializer::new);
  public static final RegistryObject<LivingCondition.Serializer> FISHING =
      REGISTRY.register("fishing", FishingCondition.Serializer::new);
  public static final RegistryObject<LivingCondition.Serializer> UNDERWATER =
      REGISTRY.register("underwater", UnderwaterCondition.Serializer::new);
  public static final RegistryObject<LivingCondition.Serializer> DUAL_WIELDING =
      REGISTRY.register("dual_wielding", DualWieldingCondition.Serializer::new);
  public static final RegistryObject<LivingCondition.Serializer> HAS_ITEM_IN_HAND =
      REGISTRY.register("has_item_in_hand", HasItemInHandCondition.Serializer::new);
  public static final RegistryObject<LivingCondition.Serializer> CROUCHING =
      REGISTRY.register("crouching", CrouchingCondition.Serializer::new);
  public static final RegistryObject<LivingCondition.Serializer> UNARMED =
      REGISTRY.register("unarmed", UnarmedCondition.Serializer::new);
  public static final RegistryObject<LivingCondition.Serializer> NUMERIC_VALUE =
      REGISTRY.register("numeric_value", NumericValueCondition.Serializer::new);
  public static final RegistryObject<LivingCondition.Serializer> ALL_ARMOR =
      REGISTRY.register("all_armor", AllArmorCondition.Serializer::new);

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
