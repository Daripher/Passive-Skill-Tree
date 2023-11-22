package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.skill.bonus.condition.enchantment.AnyEnchantmentCondition;
import daripher.skilltree.skill.bonus.condition.enchantment.ArmorEnchantmentCondition;
import daripher.skilltree.skill.bonus.condition.enchantment.EnchantmentCondition;
import daripher.skilltree.skill.bonus.condition.enchantment.WeaponEnchantmentCondition;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class PSTEnchantmentConditions {
  public static final ResourceLocation REGISTRY_ID =
      new ResourceLocation(SkillTreeMod.MOD_ID, "enchantment_conditions");
  public static final DeferredRegister<EnchantmentCondition.Serializer> REGISTRY =
      DeferredRegister.create(REGISTRY_ID, SkillTreeMod.MOD_ID);

  public static final RegistryObject<EnchantmentCondition.Serializer> ARMOR =
      REGISTRY.register("armor", ArmorEnchantmentCondition.Serializer::new);
  public static final RegistryObject<EnchantmentCondition.Serializer> WEAPON =
      REGISTRY.register("weapon", WeaponEnchantmentCondition.Serializer::new);
  public static final RegistryObject<EnchantmentCondition.Serializer> ANY =
      REGISTRY.register("any", AnyEnchantmentCondition.Serializer::new);
}
