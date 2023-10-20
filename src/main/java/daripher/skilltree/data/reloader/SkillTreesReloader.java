package daripher.skilltree.data.reloader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.skill.PassiveSkillTree;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.slf4j.Logger;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class SkillTreesReloader extends SimpleJsonResourceReloadListener {
  public static final Gson GSON =
      new GsonBuilder()
          .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
          .setPrettyPrinting()
          .create();
  private static final Logger LOGGER = LogUtils.getLogger();
  private static final List<PassiveSkillTree> SKILL_TREES = new ArrayList<>();

  public SkillTreesReloader() {
    super(GSON, "skill_trees");
  }

  @SubscribeEvent
  public static void reloadSkills(AddReloadListenerEvent event) {
    event.addListener(new SkillTreesReloader());
  }

  public static List<PassiveSkillTree> getSkillTrees() {
    return SKILL_TREES;
  }

  @Override
  protected void apply(
      Map<ResourceLocation, JsonElement> map,
      ResourceManager resourceManager,
      ProfilerFiller profilerFiller) {
    SKILL_TREES.clear();
    map.forEach(this::readSkillTree);
  }

  protected void readSkillTree(ResourceLocation id, JsonElement json) {
    try {
      PassiveSkillTree tree = GSON.fromJson(json, PassiveSkillTree.class);
      SKILL_TREES.add(tree);
    } catch (Exception exception) {
      LOGGER.error("Couldn't load passive skill tree {}", id);
      exception.printStackTrace();
    }
  }
}
