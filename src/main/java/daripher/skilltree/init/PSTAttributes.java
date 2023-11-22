package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID, bus = Bus.MOD)
public class PSTAttributes {
  public static final DeferredRegister<Attribute> REGISTRY =
      DeferredRegister.create(ForgeRegistries.ATTRIBUTES, SkillTreeMod.MOD_ID);

  // arsonist attributes
  public static final RegistryObject<Attribute> CHANCE_TO_IGNITE =
      create("chance_to_ignite", 1D, 1D);
  // scholar attributes
  public static final RegistryObject<Attribute> EXPERIENCE_PER_MINUTE =
      create("experience_per_minute", 0D, 0D);
  public static final RegistryObject<Attribute> EXPERIENCE_FROM_ORE =
      create("experience_from_ore", 1D, 1D);
  public static final RegistryObject<Attribute> EXPERIENCE_FROM_MOBS =
      create("experience_from_mobs", 1D, 1D);
  // adventurer attributes
  public static final RegistryObject<Attribute> DAMAGE_PER_DISTANCE_TO_SPAWN =
      create("damage_per_distance_to_spawn", 1D, 1D);
  // hunter attributes
  public static final RegistryObject<Attribute> DOUBLE_LOOT_CHANCE =
      create("double_loot_chance", 1D, 1D);
  public static final RegistryObject<Attribute> TRIPLE_LOOT_CHANCE =
      create("triple_loot_chance", 1D, 1D);
  public static final RegistryObject<Attribute> DAMAGE_PER_DISTANCE_TO_ENEMY =
      create("damage_per_distance_to_enemy", 1D, 1D);
  public static final RegistryObject<Attribute> CHANCE_TO_RETRIEVE_ARROWS =
      create("chance_to_retrieve_arrows", 1D, 1D);
  // ranger attributes
  public static final RegistryObject<Attribute> STEALTH = create("stealth", 1D, 1D);
  // fisherman attributes
  public static final RegistryObject<Attribute> DOUBLE_FISHING_LOOT_CHANCE =
      create("double_fishing_loot_chance", 1D, 1D);
  public static final RegistryObject<Attribute> EXPERIENCE_FROM_FISHING =
      create("experience_from_fishing", 1D, 1D);
  // shared attributes
  public static final RegistryObject<Attribute> EVASION = create("evasion", 0D, 0D);
  public static final RegistryObject<Attribute> LIFE_PER_HIT = create("life_per_hit", 0D, 0D);
  public static final RegistryObject<Attribute> LIFE_ON_BLOCK = create("life_on_block", 0D, 0D);
  public static final RegistryObject<Attribute> LIFE_REGENERATION =
      create("life_regeneration", 0D, 0D);
  public static final RegistryObject<Attribute> BLOCKING = create("blocking", 0D, 0D);
  public static final RegistryObject<Attribute> GEM_DROP_CHANCE =
      create("chance_to_find_gemstone", 1D, 1D);
  public static final RegistryObject<Attribute> INCOMING_HEALING =
      create("incoming_healing", 1D, 1D);
  public static final RegistryObject<Attribute> CHANCE_TO_EXPLODE_ENEMY =
      create("chance_to_explode_enemy", 1D, 1D);

  // TODO: make different max values
  private static RegistryObject<Attribute> create(
      String name, double defaultValue, double minValue) {
    String descriptionId = "attribute.name." + SkillTreeMod.MOD_ID + "." + name;
    return REGISTRY.register(
        name,
        () -> new RangedAttribute(descriptionId, defaultValue, minValue, 1024D).setSyncable(true));
  }

  @SubscribeEvent
  public static void attachAttributes(EntityAttributeModificationEvent event) {
    REGISTRY.getEntries().stream()
        .map(RegistryObject::get)
        .forEach(attribute -> event.add(EntityType.PLAYER, attribute));
  }
}
