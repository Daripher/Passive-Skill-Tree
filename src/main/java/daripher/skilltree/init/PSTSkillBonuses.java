package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.player.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Objects;

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
  public static final RegistryObject<SkillBonus.Serializer> REPAIR_EFFICIENCY =
      REGISTRY.register("repair_efficiency", RepairEfficiencyBonus.Serializer::new);
  public static final RegistryObject<SkillBonus.Serializer> BLOCK_BREAK_SPEED =
      REGISTRY.register("block_break_speed", BlockBreakSpeedBonus.Serializer::new);
  public static final RegistryObject<SkillBonus.Serializer> RECIPE_UNLOCK =
      REGISTRY.register("recipe_unlock", RecipeUnlockBonus.Serializer::new);
  public static final RegistryObject<SkillBonus.Serializer> FREE_ENCHANTMENT =
      REGISTRY.register("free_enchantment", FreeEnchantmentBonus.Serializer::new);
  public static final RegistryObject<SkillBonus.Serializer> JUMP_HEIGHT =
      REGISTRY.register("jump_height", JumpHeightBonus.Serializer::new);
  public static final RegistryObject<SkillBonus.Serializer> INCOMING_HEALING =
      REGISTRY.register("incoming_healing", IncomingHealingBonus.Serializer::new);
  public static final RegistryObject<SkillBonus.Serializer> LOOT_DUPLICATION =
      REGISTRY.register("loot_duplication", LootDuplicationBonus.Serializer::new);
  public static final RegistryObject<SkillBonus.Serializer> GAINED_EXPERIENCE =
      REGISTRY.register("gained_experience", GainedExperienceBonus.Serializer::new);
  public static final RegistryObject<SkillBonus.Serializer> INFLICT_IGNITE =
      REGISTRY.register("inflict_ignite", InflictIgniteBonus.Serializer::new);
  public static final RegistryObject<SkillBonus.Serializer> ARROW_RETRIEVAL =
      REGISTRY.register("arrow_retrieval", ArrowRetrievalBonus.Serializer::new);
  public static final RegistryObject<SkillBonus.Serializer> HEALTH_RESERVATION =
      REGISTRY.register("health_reservation", HealthReservationBonus.Serializer::new);
  public static final RegistryObject<SkillBonus.Serializer> ALL_ATTRIBUTES =
      REGISTRY.register("all_attributes", AllAttributesBonus.Serializer::new);
  public static final RegistryObject<SkillBonus.Serializer> INFLICT_EFFECT =
      REGISTRY.register("inflict_effect", InflictEffectBonus.Serializer::new);
  public static final RegistryObject<SkillBonus.Serializer> CANT_USE_ITEM =
      REGISTRY.register("cant_use_item", CantUseItemBonus.Serializer::new);
  public static final RegistryObject<SkillBonus.Serializer> HEALING =
      REGISTRY.register("healing", HealingBonus.Serializer::new);
  public static final RegistryObject<SkillBonus.Serializer> INFLICT_DAMAGE =
      REGISTRY.register("inflict_damage", InflictDamageBonus.Serializer::new);
  public static final RegistryObject<SkillBonus.Serializer> CAN_POISON_ANYONE =
      REGISTRY.register("can_poison_anyone", CanPoisonAnyoneBonus.Serializer::new);
  public static final RegistryObject<SkillBonus.Serializer> LETHAL_POISON =
      REGISTRY.register("lethal_poison", LethalPoisonBonus.Serializer::new);
  public static final RegistryObject<SkillBonus.Serializer> CURIO_SLOTS =
      REGISTRY.register("curio_slots", CurioSlotsBonus.Serializer::new);
  public static final RegistryObject<SkillBonus.Serializer> DAMAGE_TAKEN =
      REGISTRY.register("damage_taken", DamageTakenBonus.Serializer::new);
  public static final RegistryObject<SkillBonus.Serializer> DAMAGE_AVOIDANCE =
      REGISTRY.register("damage_avoidance", DamageAvoidanceBonus.Serializer::new);
  public static final RegistryObject<SkillBonus.Serializer> DAMAGE_CONVERSION =
      REGISTRY.register("damage_conversion", DamageConversionBonus.Serializer::new);
  public static final RegistryObject<SkillBonus.Serializer> GRANT_ITEM =
      REGISTRY.register("grant_item", GrantItemBonus.Serializer::new);
  public static final RegistryObject<SkillBonus.Serializer> EFFECT_DURATION =
      REGISTRY.register("effect_duration", EffectDurationBonus.Serializer::new);
  public static final RegistryObject<SkillBonus.Serializer> PROJECTILE_DUPLICATION =
      REGISTRY.register("projectile_duplication", ProjectileDuplicationBonus.Serializer::new);
  public static final RegistryObject<SkillBonus.Serializer> SELF_SPLASH_IMMUNE =
      REGISTRY.register("self_splash_immune", SelfSplashImmuneBonus.Serializer::new);
  public static final RegistryObject<SkillBonus.Serializer> PROJECTILE_SPEED =
      REGISTRY.register("projectile_speed", ProjectileSpeedBonus.Serializer::new);

  @SuppressWarnings("rawtypes")
  public static List<SkillBonus> bonusList() {
    return PSTRegistries.SKILL_BONUSES.get().getValues().stream()
        .map(SkillBonus.Serializer::createDefaultInstance)
        .map(SkillBonus.class::cast)
        .toList();
  }

  public static String getName(SkillBonus<?> bonus) {
    ResourceLocation id = PSTRegistries.SKILL_BONUSES.get().getKey(bonus.getSerializer());
    return TooltipHelper.idToName(Objects.requireNonNull(id).getPath());
  }
}
