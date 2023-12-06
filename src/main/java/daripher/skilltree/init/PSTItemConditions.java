package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.skill.bonus.condition.item.*;
import java.util.Arrays;
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
  public static final RegistryObject<ItemCondition.Serializer> ARMOR =
      REGISTRY.register("armor", ArmorCondition.Serializer::new);
  public static final RegistryObject<ItemCondition.Serializer> WEAPON =
      REGISTRY.register("weapon", WeaponCondition.Serializer::new);
  public static final RegistryObject<ItemCondition.Serializer> EQUIPMENT =
      REGISTRY.register("equipment", EquipmentCondition.Serializer::new);
  public static final RegistryObject<ItemCondition.Serializer> CURIO =
      REGISTRY.register("curio", CurioCondition.Serializer::new);
  public static final RegistryObject<ItemCondition.Serializer> POTIONS =
      REGISTRY.register("potion", PotionCondition.Serializer::new);
  public static final RegistryObject<ItemCondition.Serializer> FOOD =
      REGISTRY.register("food", FoodCondition.Serializer::new);
  public static final RegistryObject<ItemCondition.Serializer> JEWELRY =
      REGISTRY.register("jewelry", JewelryCondition.Serializer::new);
  public static final RegistryObject<ItemCondition.Serializer> PICKAXE =
      REGISTRY.register("pickaxe", PickaxeCondition.Serializer::new);

  public static List<ItemCondition> conditionsList() {
    return PSTRegistries.ITEM_CONDITIONS.get().getValues().stream()
        .map(ItemCondition.Serializer::createDefaultInstance)
        .toList();
  }

  public static String getName(ItemCondition condition) {
    ResourceLocation id = PSTRegistries.ITEM_CONDITIONS.get().getKey(condition.getSerializer());
    String[] words = Objects.requireNonNull(id).getPath().split("_");
    StringBuilder name = new StringBuilder();
    Arrays.stream(words)
        .map(w -> w.substring(0, 1).toUpperCase() + w.substring(1))
        .forEach(
            w -> {
              name.append(" ");
              name.append(w);
            });
    return name.toString();
  }
}
