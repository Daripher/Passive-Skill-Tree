package daripher.skilltree.data.reloader;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.data.serializers.AttributeModifierSerializer;
import daripher.skilltree.data.serializers.AttributeSerializer;
import daripher.skilltree.data.serializers.PairSerializer;
import daripher.skilltree.skill.PassiveSkill;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class SkillsReloader extends SimpleJsonResourceReloadListener {
  public static final Gson GSON =
      new GsonBuilder()
          .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
          .registerTypeAdapter(Attribute.class, new AttributeSerializer())
          .registerTypeAdapter(AttributeModifier.class, new AttributeModifierSerializer())
          .registerTypeAdapter(
              new TypeToken<Pair<Attribute, AttributeModifier>>() {}.getType(),
              new PairSerializer<>(
                  "attribute", "modifier", Attribute.class, AttributeModifier.class))
          .setPrettyPrinting()
          .create();
  private static final Logger LOGGER = LogUtils.getLogger();
  private static final Map<ResourceLocation, PassiveSkill> SKILLS = new HashMap<>();

  public SkillsReloader() {
    super(GSON, "skills");
  }

  @SubscribeEvent
  public static void reloadSkills(AddReloadListenerEvent event) {
    event.addListener(new SkillsReloader());
  }

  public static Map<ResourceLocation, PassiveSkill> getSkills() {
    return SKILLS;
  }

  public static @Nullable PassiveSkill getSkillById(ResourceLocation id) {
    return SKILLS.get(id);
  }

  @Override
  protected void apply(
      Map<ResourceLocation, JsonElement> map,
      @NotNull ResourceManager resourceManager,
      @NotNull ProfilerFiller profilerFiller) {
    SKILLS.clear();
    map.forEach(this::readSkill);
  }

  protected void readSkill(ResourceLocation id, JsonElement json) {
    try {
      PassiveSkill skill = GSON.fromJson(json, PassiveSkill.class);
      SKILLS.put(skill.getId(), skill);
    } catch (Exception exception) {
      LOGGER.error("Couldn't load passive skill {}", id);
      exception.printStackTrace();
    }
  }
}
