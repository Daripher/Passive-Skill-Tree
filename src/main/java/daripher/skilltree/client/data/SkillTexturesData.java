package daripher.skilltree.client.data;

import daripher.skilltree.SkillTreeMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Mod.EventBusSubscriber(
    modid = SkillTreeMod.MOD_ID,
    bus = Mod.EventBusSubscriber.Bus.MOD,
    value = Dist.CLIENT)
public class SkillTexturesData implements ResourceManagerReloadListener {
  private static final Map<String, Set<ResourceLocation>> FOLDER_TO_TEXTURES = new HashMap<>();

  @SubscribeEvent
  public static void registerReloadListener(RegisterClientReloadListenersEvent event) {
    event.registerReloadListener(new SkillTexturesData());
  }

  @Override
  public void onResourceManagerReload(@NotNull ResourceManager resourceManager) {
    FOLDER_TO_TEXTURES.clear();
    Collection<String> namespaces = resourceManager.getNamespaces();
    for (String ignored : namespaces) {
      List<ResourceLocation> textureLocations =
          resourceManager
              .listResources("textures", SkillTexturesData::isTexturePath)
              .keySet()
              .stream()
              .toList();
      for (ResourceLocation textureLocation : textureLocations) {
        String folder = getTextureFolder(textureLocation);
        FOLDER_TO_TEXTURES.computeIfAbsent(folder, f -> new HashSet<>());
        FOLDER_TO_TEXTURES.get(folder).add(textureLocation);
      }
    }
  }

  @Nullable
  public static String autocompleteFolderName(String string) {
    Set<String> folders = FOLDER_TO_TEXTURES.keySet();
    Optional<String> autocomplete =
        folders.stream()
            .filter(s -> s.startsWith(string))
            .findAny()
            .map(s -> s.replaceFirst(string, ""));
    return autocomplete.orElse(null);
  }

  @NotNull
  public static String getTextureFolder(ResourceLocation textureLocation) {
    String location = textureLocation.toString();
    location = location.substring(0, location.lastIndexOf('/'));
    if (!location.contains("/")) {
      return "";
    }
    return location.substring(location.indexOf("/") + 1);
  }

  public static Set<ResourceLocation> getTexturesInFolder(String folder) {
    return FOLDER_TO_TEXTURES.getOrDefault(folder, Set.of());
  }

  private static boolean isTexturePath(ResourceLocation location) {
    return location.getPath().endsWith(".png");
  }

  public static boolean isTextureFolder(String string) {
    return FOLDER_TO_TEXTURES.containsKey(string);
  }
}
