package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.skill.bonus.multiplier.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class PSTSkillBonusMultipliers {
  public static final ResourceLocation REGISTRY_ID =
      new ResourceLocation(SkillTreeMod.MOD_ID, "skill_bonus_multipliers");
  public static final DeferredRegister<SkillBonusMultiplier.Serializer> REGISTRY =
      DeferredRegister.create(REGISTRY_ID, SkillTreeMod.MOD_ID);

  public static final RegistryObject<SkillBonusMultiplier.Serializer> EFFECT_AMOUNT =
      REGISTRY.register("effect_amount", EffectAmountMultiplier.Serializer::new);
  public static final RegistryObject<SkillBonusMultiplier.Serializer> ATTRIBUTE_VALUE =
      REGISTRY.register("attribute_value", AttributeValueMultiplier.Serializer::new);
  public static final RegistryObject<SkillBonusMultiplier.Serializer> ENCHANTS_AMOUNT =
      REGISTRY.register("enchants_amount", EnchantsAmountMultiplier.Serializer::new);
  public static final RegistryObject<SkillBonusMultiplier.Serializer> ENCHANTS_LEVELS =
      REGISTRY.register("enchants_levels", EnchantLevelsAmountMultiplier.Serializer::new);
  public static final RegistryObject<SkillBonusMultiplier.Serializer> GEMS_AMOUNT =
      REGISTRY.register("gems_amount", GemsAmountMultiplier.Serializer::new);
  public static final RegistryObject<SkillBonusMultiplier.Serializer> FOOD_LEVEL =
      REGISTRY.register("food_level", HungerLevelMultiplier.Serializer::new);
}
