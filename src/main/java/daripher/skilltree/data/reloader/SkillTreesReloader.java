package daripher.skilltree.data.reloader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.PassiveSkillTree;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class SkillTreesReloader extends SimpleJsonResourceReloadListener {
  public static final Gson GSON =
      new GsonBuilder()
          .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
          .setPrettyPrinting()
          .create();
  private static final Map<ResourceLocation, PassiveSkillTree> SKILL_TREES = new HashMap<>();

  public SkillTreesReloader() {
    super(GSON, "skill_trees");
  }

  @SubscribeEvent
  public static void reloadSkillTrees(AddReloadListenerEvent event) {
    event.addListener(new SkillTreesReloader());
  }

  public static Map<ResourceLocation, PassiveSkillTree> getSkillTrees() {
    return SKILL_TREES;
  }

  public static PassiveSkillTree getSkillTreeById(ResourceLocation id) {
    return SKILL_TREES.getOrDefault(id, new PassiveSkillTree(id));
  }

  public static void loadFromByteBuf(FriendlyByteBuf buf) {
    SKILL_TREES.clear();
    NetworkHelper.readPassiveSkillTrees(buf).forEach(t -> SKILL_TREES.put(t.getId(), t));
  }

  @Override
  protected void apply(
      Map<ResourceLocation, JsonElement> map,
      @NotNull ResourceManager resourceManager,
      @NotNull ProfilerFiller profilerFiller) {
    SKILL_TREES.clear();
    map.forEach(this::readSkillTree);
  }

  protected void readSkillTree(ResourceLocation id, JsonElement json) {
    try {
      PassiveSkillTree tree = GSON.fromJson(json, PassiveSkillTree.class);
      SKILL_TREES.put(tree.getId(), tree);
    } catch (Exception exception) {
      SkillTreeMod.LOGGER.error("Couldn't load passive skill tree {}", id);
      exception.printStackTrace();
    }
  }
}
