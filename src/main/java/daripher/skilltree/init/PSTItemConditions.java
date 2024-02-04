package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.skill.bonus.condition.item.*;
import java.util.List;
import java.util.Objects;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class PSTItemConditions {
  public static final ResourceLocation REGISTRY_ID =
      new ResourceLocation(SkillTreeMod.MOD_ID, "item_conditions");
  public static final DeferredRegister<ItemCondition.Serializer> REGISTRY =
      DeferredRegister.create(REGISTRY_ID, SkillTreeMod.MOD_ID);

  public static final RegistryObject<ItemCondition.Serializer> NONE =
      REGISTRY.register("none", NoneItemCondition.Serializer::new);
  public static final RegistryObject<ItemCondition.Serializer> POTIONS =
      REGISTRY.register("potion", PotionCondition.Serializer::new);
  public static final RegistryObject<ItemCondition.Serializer> FOOD =
      REGISTRY.register("food", FoodCondition.Serializer::new);
  public static final RegistryObject<ItemCondition.Serializer> ITEM_ID =
      REGISTRY.register("item_id", ItemIdCondition.Serializer::new);
  public static final RegistryObject<ItemCondition.Serializer> ENCHANTED =
      REGISTRY.register("enchanted", EnchantedCondition.Serializer::new);
  public static final RegistryObject<ItemCondition.Serializer> TAG =
      REGISTRY.register("tag", ItemTagCondition.Serializer::new);

  public static List<ItemCondition> conditionsList() {
    return PSTRegistries.ITEM_CONDITIONS.get().getValues().stream()
        .map(ItemCondition.Serializer::createDefaultInstance)
        .toList();
  }

  public static String getName(ItemCondition condition) {
    ResourceLocation id = PSTRegistries.ITEM_CONDITIONS.get().getKey(condition.getSerializer());
    return TooltipHelper.idToName(Objects.requireNonNull(id).getPath());
  }
}
