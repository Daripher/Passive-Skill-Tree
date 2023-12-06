package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.skill.bonus.condition.damage.DamageCondition;
import daripher.skilltree.skill.bonus.condition.damage.MeleeDamageCondition;
import daripher.skilltree.skill.bonus.condition.damage.NoneDamageCondition;
import daripher.skilltree.skill.bonus.condition.damage.ProjectileDamageCondition;
import java.util.Arrays;
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
  public static final RegistryObject<DamageCondition.Serializer> IS_PROJECTILE =
      REGISTRY.register("projectile", ProjectileDamageCondition.Serializer::new);
  public static final RegistryObject<DamageCondition.Serializer> IS_MELEE =
      REGISTRY.register("melee", MeleeDamageCondition.Serializer::new);

  public static List<DamageCondition> conditionsList() {
    return PSTRegistries.DAMAGE_CONDITIONS.get().getValues().stream()
        .map(DamageCondition.Serializer::createDefaultInstance)
        .toList();
  }

  public static String getName(DamageCondition condition) {
    ResourceLocation id = PSTRegistries.DAMAGE_CONDITIONS.get().getKey(condition.getSerializer());
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
