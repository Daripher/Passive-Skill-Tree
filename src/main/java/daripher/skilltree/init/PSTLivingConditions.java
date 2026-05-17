package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.client.tooltip.TooltipHelper;
import java.util.List;
import java.util.Objects;

import daripher.skilltree.skill.bonus.predicate.living.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class PSTLivingConditions {
  public static final ResourceLocation REGISTRY_ID =
      ResourceLocation.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "living_conditions");
  public static final DeferredRegister<LivingEntityPredicate.Serializer> REGISTRY =
      DeferredRegister.create(REGISTRY_ID, SkillTreeMod.MOD_ID);

  public static final DeferredHolder<LivingEntityPredicate.Serializer, ? extends LivingEntityPredicate.Serializer> NONE =
      REGISTRY.register("none", NoneLivingEntityPredicate.Serializer::new);
  public static final DeferredHolder<LivingEntityPredicate.Serializer, ? extends LivingEntityPredicate.Serializer> HAS_ITEM_EQUIPPED =
      REGISTRY.register("has_item_equipped", HasItemEquippedEntityPredicate.Serializer::new);
  public static final DeferredHolder<LivingEntityPredicate.Serializer, ? extends LivingEntityPredicate.Serializer> HAS_EFFECT =
      REGISTRY.register("has_effect", HasEffectEntityPredicate.Serializer::new);
  public static final DeferredHolder<LivingEntityPredicate.Serializer, ? extends LivingEntityPredicate.Serializer> BURNING =
      REGISTRY.register("burning", BurningEntityPredicate.Serializer::new);
  public static final DeferredHolder<LivingEntityPredicate.Serializer, ? extends LivingEntityPredicate.Serializer> FISHING =
      REGISTRY.register("fishing", FishingEntityPredicate.Serializer::new);
  public static final DeferredHolder<LivingEntityPredicate.Serializer, ? extends LivingEntityPredicate.Serializer> UNDERWATER =
      REGISTRY.register("underwater", UnderwaterEntityPredicate.Serializer::new);
  public static final DeferredHolder<LivingEntityPredicate.Serializer, ? extends LivingEntityPredicate.Serializer> DUAL_WIELDING =
      REGISTRY.register("dual_wielding", DualWieldingEntityPredicate.Serializer::new);
  public static final DeferredHolder<LivingEntityPredicate.Serializer, ? extends LivingEntityPredicate.Serializer> HAS_ITEM_IN_HAND =
      REGISTRY.register("has_item_in_hand", HasItemInHandEntityPredicate.Serializer::new);
  public static final DeferredHolder<LivingEntityPredicate.Serializer, ? extends LivingEntityPredicate.Serializer> CROUCHING =
      REGISTRY.register("crouching", CrouchingEntityPredicate.Serializer::new);
  public static final DeferredHolder<LivingEntityPredicate.Serializer, ? extends LivingEntityPredicate.Serializer> UNARMED =
      REGISTRY.register("unarmed", UnarmedEntityPredicate.Serializer::new);
  public static final DeferredHolder<LivingEntityPredicate.Serializer, ? extends LivingEntityPredicate.Serializer> NUMERIC_VALUE =
      REGISTRY.register("numeric_value", FloatFunctionEntityPredicate.Serializer::new);
  public static final DeferredHolder<LivingEntityPredicate.Serializer, ? extends LivingEntityPredicate.Serializer> ALL_ARMOR =
      REGISTRY.register("all_armor", AllArmorEntityPredicate.Serializer::new);

  public static List<LivingEntityPredicate> conditionsList() {
    return PSTRegistries.LIVING_CONDITIONS.get().getValues().stream()
        .map(LivingEntityPredicate.Serializer::createDefaultInstance)
        .toList();
  }

  public static String getName(LivingEntityPredicate condition) {
    ResourceLocation id = PSTRegistries.LIVING_CONDITIONS.get().getKey(condition.getSerializer());
    return TooltipHelper.idToName(Objects.requireNonNull(id).getPath());
  }
}
