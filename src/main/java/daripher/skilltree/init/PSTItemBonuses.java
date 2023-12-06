package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.skill.bonus.item.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class PSTItemBonuses {
  public static final ResourceLocation REGISTRY_ID =
      new ResourceLocation(SkillTreeMod.MOD_ID, "item_bonuses");
  public static final DeferredRegister<ItemBonus.Serializer> REGISTRY =
      DeferredRegister.create(REGISTRY_ID, SkillTreeMod.MOD_ID);

  public static final RegistryObject<ItemBonus.Serializer> SKILL_BONUS =
      REGISTRY.register("skill_bonus", ItemSkillBonus.Serializer::new);
  public static final RegistryObject<ItemBonus.Serializer> SOCKETS =
      REGISTRY.register("sockets", ItemSocketsBonus.Serializer::new);
  public static final RegistryObject<ItemBonus.Serializer> DURABILITY =
      REGISTRY.register("durability", ItemDurabilityBonus.Serializer::new);
  public static final RegistryObject<ItemBonus.Serializer> QUIVER_CAPACITY =
      REGISTRY.register("quiver_capacity", QuiverCapacityBonus.Serializer::new);
  public static final RegistryObject<ItemBonus.Serializer> POTION_AMPLIFICATION =
      REGISTRY.register("potion_amplification", PotionAmplificationBonus.Serializer::new);
  public static final RegistryObject<ItemBonus.Serializer> POTION_DURATION =
      REGISTRY.register("potion_duration", PotionDurationBonus.Serializer::new);
  public static final RegistryObject<ItemBonus.Serializer> FOOD_EFFECT =
      REGISTRY.register("food_effect", FoodEffectBonus.Serializer::new);
  public static final RegistryObject<ItemBonus.Serializer> FOOD_SATURATION =
      REGISTRY.register("food_saturation", FoodSaturationBonus.Serializer::new);
  public static final RegistryObject<ItemBonus.Serializer> FOOD_HEALING =
      REGISTRY.register("food_healing", FoodHealingBonus.Serializer::new);

  @SuppressWarnings("rawtypes")
  public static List<ItemBonus> bonusList() {
    return PSTRegistries.ITEM_BONUSES.get().getValues().stream()
        .map(ItemBonus.Serializer::createDefaultInstance)
        .map(ItemBonus.class::cast)
        .toList();
  }

  public static String getName(ItemBonus<?> bonus) {
    ResourceLocation id = PSTRegistries.ITEM_BONUSES.get().getKey(bonus.getSerializer());
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
