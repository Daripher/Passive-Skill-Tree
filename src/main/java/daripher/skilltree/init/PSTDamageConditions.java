package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.skill.bonus.condition.damage.*;

import java.util.List;
import java.util.Objects;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class PSTDamageConditions {
  public static final ResourceLocation REGISTRY_ID =
      new ResourceLocation(SkillTreeMod.MOD_ID, "damage_conditions");
  public static final DeferredRegister<DamageCondition.Serializer> REGISTRY =
      DeferredRegister.create(REGISTRY_ID, SkillTreeMod.MOD_ID);

  public static final RegistryObject<DamageCondition.Serializer> NONE =
      REGISTRY.register("none", NoneDamageCondition.Serializer::new);
  public static final RegistryObject<DamageCondition.Serializer> PROJECTILE =
      REGISTRY.register("projectile", ProjectileDamageCondition.Serializer::new);
  public static final RegistryObject<DamageCondition.Serializer> MELEE =
      REGISTRY.register("melee", MeleeDamageCondition.Serializer::new);
  public static final RegistryObject<DamageCondition.Serializer> MAGIC =
      REGISTRY.register("magic", MagicDamageCondition.Serializer::new);
  public static final RegistryObject<DamageCondition.Serializer> FALL =
      REGISTRY.register("fall", FallDamageCondition.Serializer::new);
  public static final RegistryObject<DamageCondition.Serializer> FIRE =
      REGISTRY.register("fire", FireDamageCondition.Serializer::new);
  public static final RegistryObject<DamageCondition.Serializer> POISON =
      REGISTRY.register("poison", PoisonDamageCondition.Serializer::new);
  public static final RegistryObject<DamageCondition.Serializer> THORNS =
      REGISTRY.register("thorns", ThornsDamageCondition.Serializer::new);

  public static List<DamageCondition> conditionsList() {
    return PSTRegistries.DAMAGE_CONDITIONS.get().getValues().stream()
        .map(DamageCondition.Serializer::createDefaultInstance)
        .toList();
  }

  public static String getName(DamageCondition condition) {
    ResourceLocation id = PSTRegistries.DAMAGE_CONDITIONS.get().getKey(condition.getSerializer());
    return TooltipHelper.idToName(Objects.requireNonNull(id).getPath());
  }
}
