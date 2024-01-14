package daripher.skilltree.data.reloader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.data.serializers.GemTypeSerializer;
import daripher.skilltree.item.gem.GemType;
import daripher.skilltree.network.NetworkHelper;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.Nonnull;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class GemTypesReloader extends SimpleJsonResourceReloadListener {
  public static final Gson GSON =
      new GsonBuilder()
          .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
          .registerTypeAdapter(GemType.class, new GemTypeSerializer())
          .setPrettyPrinting()
          .create();

  private static final Map<ResourceLocation, GemType> GEM_TYPES = new TreeMap<>();
  public static final GemType NO_TYPE =
      new GemType(new ResourceLocation(SkillTreeMod.MOD_ID, "none"), Map.of());

  public GemTypesReloader() {
    super(GSON, "gem_types");
  }

  @SubscribeEvent
  public static void reloadSkills(AddReloadListenerEvent event) {
    event.addListener(new GemTypesReloader());
  }

  public static Map<ResourceLocation, GemType> getGemTypes() {
    return GEM_TYPES;
  }

  public static @Nonnull GemType getGemTypeById(ResourceLocation id) {
    return GEM_TYPES.getOrDefault(id, NO_TYPE);
  }

  public static void loadFromByteBuf(FriendlyByteBuf buf) {
    GEM_TYPES.clear();
    NetworkHelper.readGemTypes(buf).forEach(t -> GEM_TYPES.put(t.id(), t));
  }

  @Override
  protected void apply(
      Map<ResourceLocation, JsonElement> map,
      @NotNull ResourceManager resourceManager,
      @NotNull ProfilerFiller profilerFiller) {
    GEM_TYPES.clear();
    map.forEach(this::readGemType);
  }

  protected void readGemType(ResourceLocation id, JsonElement json) {
    try {
      GemType gemType = GSON.fromJson(json, GemType.class);
      GEM_TYPES.put(gemType.id(), gemType);
    } catch (Exception exception) {
      SkillTreeMod.LOGGER.error("Couldn't load gem type {}", id);
      exception.printStackTrace();
    }
  }
}
