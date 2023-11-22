package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.skill.bonus.condition.damage.DamageCondition;
import daripher.skilltree.skill.bonus.condition.damage.MeleeDamageCondition;
import daripher.skilltree.skill.bonus.condition.damage.ProjectileDamageCondition;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class PSTDamageConditions {
  public static final ResourceLocation REGISTRY_ID =
      new ResourceLocation(SkillTreeMod.MOD_ID, "damage_conditions");
  public static final DeferredRegister<DamageCondition.Serializer> REGISTRY =
      DeferredRegister.create(REGISTRY_ID, SkillTreeMod.MOD_ID);

  public static final RegistryObject<DamageCondition.Serializer> IS_PROJECTILE =
      REGISTRY.register("is_projectile", ProjectileDamageCondition.Serializer::new);
  public static final RegistryObject<DamageCondition.Serializer> IS_MELEE =
      REGISTRY.register("is_melee", MeleeDamageCondition.Serializer::new);
}
