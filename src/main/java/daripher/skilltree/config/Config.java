package daripher.skilltree.config;

import daripher.skilltree.SkillTreeMod;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID, bus = Bus.MOD)
public class Config {
  public static final ForgeConfigSpec SPEC;
  private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
  private static final ConfigValue<Integer> MAX_SKILL_POINTS;
  private static final ConfigValue<Integer> DEFAULT_HELMET_SOCKETS;
  private static final ConfigValue<Integer> DEFAULT_CHESTPLATE_SOCKETS;
  private static final ConfigValue<Integer> DEFAULT_LEGGINGS_SOCKETS;
  private static final ConfigValue<Integer> DEFAULT_BOOTS_SOCKETS;
  private static final ConfigValue<Integer> DEFAULT_WEAPON_SOCKETS;
  private static final ConfigValue<Integer> DEFAULT_SHIELD_SOCKETS;
  private static final ConfigValue<Integer> DEFAULT_NECKLACE_SOCKETS;
  private static final ConfigValue<Integer> DEFAULT_RING_SOCKETS;
  private static final ConfigValue<Integer> FIRST_SKILL_COST;
  private static final ConfigValue<Integer> LAST_SKILL_COST;
  private static final ConfigValue<Double> GEM_DROP_CHANCE;
  private static final ConfigValue<Double> AMNESIA_SCROLL_PENALTY;
  private static final ConfigValue<Double> GRINDSTONE_EXP_MULTIPLIER;
  private static final ConfigValue<Double> MIXTURE_EFFECTS_DURATION;
  private static final ConfigValue<Double> MIXTURE_EFFECTS_STRENGTH;
  private static final ConfigValue<Boolean> SHOW_CHAT_MESSAGES;
  private static final ConfigValue<Boolean> ENABLE_EXP_EXCHANGE;
  private static final ConfigValue<Boolean> DRAGON_DROPS_AMNESIA_SCROLL;
  private static final ConfigValue<Boolean> USE_POINTS_COSTS_ARRAY;
  private static final ConfigValue<List<? extends Integer>> SKILL_POINTS_COSTS;
  private static final ConfigValue<List<? extends String>> SOCKET_BLACKLIST;
  public static final int DEFAULT_MAX_SKILLS = 85;
  public static int max_skill_points;
  public static int first_skill_cost;
  public static int last_skill_cost;
  public static int default_helmet_sockets;
  public static int default_chestplate_sockets;
  public static int default_leggings_sockets;
  public static int default_boots_sockets;
  public static int default_weapon_sockets;
  public static int default_shield_sockets;
  public static int default_necklace_sockets;
  public static int default_ring_sockets;
  public static double gem_drop_chance;
  public static double amnesia_scroll_penalty;
  public static double grindstone_exp_multiplier;
  public static double mixture_effects_duration;
  public static double mixture_effects_strength;
  public static boolean show_chat_messages;
  public static boolean use_skill_points_array;
  public static boolean enable_exp_exchange;
  public static boolean dragon_drops_amnesia_scroll;
  public static List<? extends Integer> skill_points_costs;
  public static List<? extends String> socket_blacklist;

  static {
    BUILDER.push("Skill Points");
    MAX_SKILL_POINTS = BUILDER.defineInRange("Maximum skill points", DEFAULT_MAX_SKILLS, 1, 1000);
    FIRST_SKILL_COST = BUILDER.defineInRange("First skill point cost", 15, 0, Integer.MAX_VALUE);
    LAST_SKILL_COST = BUILDER.defineInRange("Last skill point cost", 1400, 0, Integer.MAX_VALUE);
    BUILDER.comment("You can set cost for each skill point instead");
    USE_POINTS_COSTS_ARRAY = BUILDER.define("Use skill points costs array", false);
    BUILDER.comment("This list's size must be equal to maximum skill points.");
    SKILL_POINTS_COSTS =
        BUILDER.defineList(
            "Levelup costs", generateDefaultPointsCosts(), o -> o instanceof Integer i && i > 0);
    BUILDER.comment("Disabling this will remove chat messages when you gain a skill point.");
    SHOW_CHAT_MESSAGES = BUILDER.define("Show chat messages", true);
    BUILDER.comment(
        "Warning: If you disable this make sure you make alternative way of getting skill points.");
    ENABLE_EXP_EXCHANGE = BUILDER.define("Enable exprerience exchange for skill points", true);
    BUILDER.pop();

    BUILDER.push("Gems");
    GEM_DROP_CHANCE = BUILDER.defineInRange("Base drop chance", 0.15, 0, 1);
    BUILDER.pop();

    BUILDER.push("Equipment");
    DEFAULT_HELMET_SOCKETS = BUILDER.defineInRange("Default Helmets Sockets", 1, 0, 4);
    DEFAULT_CHESTPLATE_SOCKETS = BUILDER.defineInRange("Default Chestplates Sockets", 1, 0, 4);
    DEFAULT_LEGGINGS_SOCKETS = BUILDER.defineInRange("Default Leggings Sockets", 0, 0, 4);
    DEFAULT_BOOTS_SOCKETS = BUILDER.defineInRange("Default Boots Sockets", 1, 0, 4);
    DEFAULT_WEAPON_SOCKETS = BUILDER.defineInRange("Default Weapons Sockets", 1, 0, 4);
    DEFAULT_SHIELD_SOCKETS = BUILDER.defineInRange("Default Shields Sockets", 1, 0, 4);
    DEFAULT_RING_SOCKETS = BUILDER.defineInRange("Default Rings Sockets", 1, 0, 4);
    DEFAULT_NECKLACE_SOCKETS = BUILDER.defineInRange("Default Necklaces Sockets", 1, 0, 4);
    BUILDER.comment("You can remove sockets from items here");
    BUILDER.comment(
        "Example: Blacklisting specific items: [\"minecraft:diamond_hoe\", \"minecraft:golden_hoe\"]");
    BUILDER.comment("Example: Blacklisting whole mod: [\"<mod_id>:*\"]");
    BUILDER.comment("Example: Blacklisting all items: [\"*:*\"]");
    SOCKET_BLACKLIST =
        BUILDER.defineListAllowEmpty(
            List.of("IDs of items that shouldn't have sockets"),
            ArrayList::new,
            Config::validateItemName);
    BUILDER.comment("You can force items from other mods into equipmentType categories here");
    BUILDER.pop();

    BUILDER.push("Amnesia Scroll");
    BUILDER.comment("How much levels (percentage) player lose using amnesia scroll");
    AMNESIA_SCROLL_PENALTY = BUILDER.defineInRange("Amnesia scroll penalty", 0.2D, 0D, 1D);
    DRAGON_DROPS_AMNESIA_SCROLL =
        BUILDER.define("Drop amnesia scrolls from the Ender Dragon", true);
    BUILDER.pop();

    BUILDER.push("Experience");
    GRINDSTONE_EXP_MULTIPLIER =
        BUILDER.defineInRange("Grindstone experience multiplier", 0.1D, 0D, 1D);
    BUILDER.pop();

    BUILDER.push("Mixtures");
    MIXTURE_EFFECTS_DURATION = BUILDER.defineInRange("Effects duration multiplier", 1D, 0D, 2D);
    MIXTURE_EFFECTS_STRENGTH = BUILDER.defineInRange("Effects strength multiplier", 1D, 0D, 2D);
    BUILDER.pop();

    SPEC = BUILDER.build();
  }

  static List<Integer> generateDefaultPointsCosts() {
    List<Integer> costs = new ArrayList<>();
    costs.add(15);
    for (int i = 1; i < DEFAULT_MAX_SKILLS; i++) {
      int previousCost = costs.get(costs.size() - 1);
      int cost = previousCost + 3 + i;
      costs.add(cost);
    }
    return costs;
  }

  static boolean validateItemName(Object object) {
    return object instanceof String name
        && ForgeRegistries.ITEMS.containsKey(new ResourceLocation(name));
  }

  @SubscribeEvent
  static void load(ModConfigEvent event) {
    if (event.getConfig().getSpec() != SPEC) return;
    skill_points_costs = SKILL_POINTS_COSTS.get();
    use_skill_points_array = USE_POINTS_COSTS_ARRAY.get();
    max_skill_points = MAX_SKILL_POINTS.get();
    first_skill_cost = FIRST_SKILL_COST.get();
    last_skill_cost = LAST_SKILL_COST.get();
    default_helmet_sockets = DEFAULT_HELMET_SOCKETS.get();
    default_chestplate_sockets = DEFAULT_CHESTPLATE_SOCKETS.get();
    default_leggings_sockets = DEFAULT_LEGGINGS_SOCKETS.get();
    default_boots_sockets = DEFAULT_BOOTS_SOCKETS.get();
    default_weapon_sockets = DEFAULT_WEAPON_SOCKETS.get();
    default_shield_sockets = DEFAULT_SHIELD_SOCKETS.get();
    default_necklace_sockets = DEFAULT_NECKLACE_SOCKETS.get();
    default_ring_sockets = DEFAULT_RING_SOCKETS.get();
    gem_drop_chance = GEM_DROP_CHANCE.get();
    amnesia_scroll_penalty = AMNESIA_SCROLL_PENALTY.get();
    grindstone_exp_multiplier = GRINDSTONE_EXP_MULTIPLIER.get();
    show_chat_messages = SHOW_CHAT_MESSAGES.get();
    enable_exp_exchange = ENABLE_EXP_EXCHANGE.get();
    dragon_drops_amnesia_scroll = DRAGON_DROPS_AMNESIA_SCROLL.get();
    socket_blacklist = SOCKET_BLACKLIST.get();
    mixture_effects_duration = MIXTURE_EFFECTS_DURATION.get();
    mixture_effects_strength = MIXTURE_EFFECTS_STRENGTH.get();
  }

  public static int getSkillPointCost(int level) {
    if (USE_POINTS_COSTS_ARRAY.get()) {
      List<? extends Integer> costs = SKILL_POINTS_COSTS.get();
      if (level > costs.size()) {
        return costs.get(costs.size() - 1);
      }
      return costs.get(level);
    }
    return FIRST_SKILL_COST.get()
        + (LAST_SKILL_COST.get() - FIRST_SKILL_COST.get()) * level / max_skill_points;
  }
}
