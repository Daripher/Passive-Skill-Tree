package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.player.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.*;

public class PSTSkillBonuses {
  public static final ResourceLocation REGISTRY_ID =
      new ResourceLocation(SkillTreeMod.MOD_ID, "skill_bonuses");
  public static final DeferredRegister<SkillBonus.Serializer> REGISTRY =
      DeferredRegister.create(REGISTRY_ID, SkillTreeMod.MOD_ID);

  public static final RegistryObject<SkillBonus.Serializer> ATTRIBUTE =
      REGISTRY.register("attribute", AttributeBonus.Serializer::new);
  public static final RegistryObject<SkillBonus.Serializer> COMMAND =
      REGISTRY.register("command", CommandBonus.Serializer::new);
  public static final RegistryObject<SkillBonus.Serializer> DAMAGE =
      REGISTRY.register("damage", DamageBonus.Serializer::new);
  public static final RegistryObject<SkillBonus.Serializer> CRIT_DAMAGE =
      REGISTRY.register("crit_damage", CritDamageBonus.Serializer::new);
  public static final RegistryObject<SkillBonus.Serializer> CRIT_CHANCE =
      REGISTRY.register("crit_chance", CritChanceBonus.Serializer::new);
  public static final RegistryObject<SkillBonus.Serializer> CRAFTED_ITEM_BONUS =
      REGISTRY.register("crafted_item_bonus", CraftedItemBonus.Serializer::new);
  public static final RegistryObject<SkillBonus.Serializer> GEM_POWER =
      REGISTRY.register("gem_power", GemPowerBonus.Serializer::new);
  public static final RegistryObject<SkillBonus.Serializer> PLAYER_SOCKETS =
      REGISTRY.register("player_sockets", PlayerSocketsBonus.Serializer::new);
  public static final RegistryObject<SkillBonus.Serializer> REPAIR_EFFICIENCY =
      REGISTRY.register("repair_efficiency", RepairEfficiencyBonus.Serializer::new);
  public static final RegistryObject<SkillBonus.Serializer> BLOCK_BREAK_SPEED =
      REGISTRY.register("block_break_speed", BlockBreakSpeedBonus.Serializer::new);
  public static final RegistryObject<SkillBonus.Serializer> RECIPE_UNLOCK =
      REGISTRY.register("recipe_unlock", RecipeUnlockBonus.Serializer::new);
  public static final RegistryObject<SkillBonus.Serializer> ENCHANTMENT_AMPLIFICATION =
      REGISTRY.register("enchantment_amplification", EnchantmentAmplificationBonus.Serializer::new);
  public static final RegistryObject<SkillBonus.Serializer> ENCHANTMENT_REQUIREMENT =
      REGISTRY.register("enchantment_requirement", EnchantmentRequirementBonus.Serializer::new);
  public static final RegistryObject<SkillBonus.Serializer> FREE_ENCHANTMENT =
      REGISTRY.register("free_enchantment", FreeEnchantmentBonus.Serializer::new);
  public static final RegistryObject<SkillBonus.Serializer> JUMP_HEIGHT =
      REGISTRY.register("jump_height", JumpHeightBonus.Serializer::new);
}
