package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.skill.bonus.condition.enchantment.ArmorEnchantmentCondition;
import daripher.skilltree.skill.bonus.condition.enchantment.EnchantmentCondition;
import daripher.skilltree.skill.bonus.condition.enchantment.NoneEnchantmentCondition;
import daripher.skilltree.skill.bonus.condition.enchantment.WeaponEnchantmentCondition;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class PSTEnchantmentConditions {
  public static final ResourceLocation REGISTRY_ID =
      new ResourceLocation(SkillTreeMod.MOD_ID, "enchantment_conditions");
  public static final DeferredRegister<EnchantmentCondition.Serializer> REGISTRY =
      DeferredRegister.create(REGISTRY_ID, SkillTreeMod.MOD_ID);

  public static final RegistryObject<EnchantmentCondition.Serializer> NONE =
      REGISTRY.register("none", NoneEnchantmentCondition.Serializer::new);
  public static final RegistryObject<EnchantmentCondition.Serializer> ARMOR =
      REGISTRY.register("armor", ArmorEnchantmentCondition.Serializer::new);
  public static final RegistryObject<EnchantmentCondition.Serializer> WEAPON =
      REGISTRY.register("weapon", WeaponEnchantmentCondition.Serializer::new);

  public static List<EnchantmentCondition> conditionsList() {
    return PSTRegistries.ENCHANTMENT_CONDITIONS.get().getValues().stream()
        .map(EnchantmentCondition.Serializer::createDefaultInstance)
        .toList();
  }

  public static String getName(EnchantmentCondition condition) {
    ResourceLocation id =
        PSTRegistries.ENCHANTMENT_CONDITIONS.get().getKey(condition.getSerializer());
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
