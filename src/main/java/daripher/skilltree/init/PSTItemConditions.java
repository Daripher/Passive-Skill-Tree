package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.client.tooltip.TooltipHelper;
import java.util.List;
import java.util.Objects;

import daripher.skilltree.skill.bonus.predicate.item.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class PSTItemConditions {
  public static final ResourceLocation REGISTRY_ID =
      new ResourceLocation(SkillTreeMod.MOD_ID, "item_conditions");
  public static final DeferredRegister<ItemStackPredicate.Serializer> REGISTRY =
      DeferredRegister.create(REGISTRY_ID, SkillTreeMod.MOD_ID);

  public static final RegistryObject<ItemStackPredicate.Serializer> NONE =
      REGISTRY.register("none", NoneItemStackPredicate.Serializer::new);
  public static final RegistryObject<ItemStackPredicate.Serializer> POTIONS =
      REGISTRY.register("potion", PotionStackPredicate.Serializer::new);
  public static final RegistryObject<ItemStackPredicate.Serializer> FOOD =
      REGISTRY.register("food", FoodStackPredicate.Serializer::new);
  public static final RegistryObject<ItemStackPredicate.Serializer> ITEM_ID =
      REGISTRY.register("item_id", ItemIdPredicate.Serializer::new);
  public static final RegistryObject<ItemStackPredicate.Serializer> ENCHANTED =
      REGISTRY.register("enchanted", EnchantedStackPredicate.Serializer::new);
  public static final RegistryObject<ItemStackPredicate.Serializer> TAG =
      REGISTRY.register("tag", ItemTagPredicate.Serializer::new);
  public static final RegistryObject<ItemStackPredicate.Serializer> EQUIPMENT_TYPE =
      REGISTRY.register("equipment_type", EquipmentPredicate.Serializer::new);

  public static List<ItemStackPredicate> conditionsList() {
    return PSTRegistries.ITEM_CONDITIONS.get().getValues().stream()
        .map(ItemStackPredicate.Serializer::createDefaultInstance)
        .toList();
  }

  public static String getName(ItemStackPredicate condition) {
    ResourceLocation id = PSTRegistries.ITEM_CONDITIONS.get().getKey(condition.getSerializer());
    return TooltipHelper.idToName(Objects.requireNonNull(id).getPath());
  }
}
