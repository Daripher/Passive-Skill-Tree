package daripher.skilltree.config;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.skill.PassiveSkill;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID, bus = Bus.MOD)
public class ClientConfig {
  public static final ForgeConfigSpec SPEC;
  private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
  private static final ConfigValue<List<? extends String>> FAVORITE_SKILLS;
  private static final ConfigValue<? extends String> FAVORITE_COLOR_HEX;
  public static Set<ResourceLocation> favorite_skills;
  public static int favorite_color;
  public static boolean favorite_color_is_rainbow;

  static {
    FAVORITE_SKILLS =
        BUILDER.defineList("favorite_skills", new ArrayList<>(), ClientConfig::isValidSkillId);
    FAVORITE_COLOR_HEX =
        BUILDER.define("favorite_color_hex", "#42B0FF", ClientConfig::isValidHexColor);
    SPEC = BUILDER.build();
  }

  private static boolean isValidSkillId(Object o) {
    return o instanceof String s && ResourceLocation.isValidResourceLocation(s);
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  private static boolean isValidHexColor(Object o) {
    if (!(o instanceof String s)) return false;
    if (s.equals("rainbow")) return true;
    try {
      Integer.decode(s);
    } catch (NumberFormatException e) {
      return false;
    }
    return true;
  }

  @SubscribeEvent
  static void load(ModConfigEvent.Loading event) {
    if (event.getConfig().getSpec() != SPEC) return;
    favorite_skills =
        FAVORITE_SKILLS.get().stream().map(ResourceLocation::new).collect(Collectors.toSet());
    favorite_color_is_rainbow = FAVORITE_COLOR_HEX.get().equals("rainbow");
    if (!favorite_color_is_rainbow) {
      favorite_color = Integer.decode(FAVORITE_COLOR_HEX.get());
    }
  }

  public static void toggleFavoriteSkill(PassiveSkill skill) {
    if (favorite_skills.contains(skill.getId())) {
      favorite_skills.remove(skill.getId());
    } else {
      favorite_skills.add(skill.getId());
    }
    FAVORITE_SKILLS.set(
        favorite_skills.stream().map(ResourceLocation::toString).collect(Collectors.toList()));
  }
}
