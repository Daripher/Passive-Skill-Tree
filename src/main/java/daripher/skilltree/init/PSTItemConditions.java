package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.skill.bonus.condition.item.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class PSTItemConditions {
  public static final ResourceLocation REGISTRY_ID =
      new ResourceLocation(SkillTreeMod.MOD_ID, "item_conditions");
  public static final DeferredRegister<ItemCondition.Serializer> REGISTRY =
      DeferredRegister.create(REGISTRY_ID, SkillTreeMod.MOD_ID);

  public static final RegistryObject<ItemCondition.Serializer> ARMOR =
      REGISTRY.register("armor", ArmorCondition.Serializer::new);
  public static final RegistryObject<ItemCondition.Serializer> WEAPON =
      REGISTRY.register("weapon", WeaponCondition.Serializer::new);
  public static final RegistryObject<ItemCondition.Serializer> EQUIPMENT =
      REGISTRY.register("equipment", EquipmentCondition.Serializer::new);
  public static final RegistryObject<ItemCondition.Serializer> CURIO =
      REGISTRY.register("curio", CurioCondition.Serializer::new);
  public static final RegistryObject<ItemCondition.Serializer> AXE =
      REGISTRY.register("axe", AxeCondition.Serializer::new);
  public static final RegistryObject<ItemCondition.Serializer> POTION =
      REGISTRY.register("potion", PotionCondition.Serializer::new);
  public static final RegistryObject<ItemCondition.Serializer> FOOD =
      REGISTRY.register("food", FoodCondition.Serializer::new);
  public static final RegistryObject<ItemCondition.Serializer> JEWELRY =
      REGISTRY.register("jewelry", JewelryCondition.Serializer::new);
  public static final RegistryObject<ItemCondition.Serializer> PICKAXE =
      REGISTRY.register("pickaxe", PickaxeCondition.Serializer::new);
}
