package daripher.skilltree.data.client;

import daripher.skilltree.SkillTreeMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class SkillTexturesData implements ResourceManagerReloadListener {
    private static final Map<String, Set<ResourceLocation>> FOLDER_TO_TEXTURES = new HashMap<>();

    @SubscribeEvent
    public static void registerReloadListener(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new SkillTexturesData());
    }

    @Override
    public void onResourceManagerReload(@NotNull ResourceManager resourceManager) {
        FOLDER_TO_TEXTURES.clear();
        Map<ResourceLocation, Resource> textures = resourceManager.listResources("textures", SkillTexturesData::isTexturePath);
        List<ResourceLocation> textureLocations = textures.keySet().stream().toList();
        for (ResourceLocation textureLocation : textureLocations) {
            String folder = getTextureFolder(textureLocation);
            if (folder.isEmpty()) {
                continue;
            }
            FOLDER_TO_TEXTURES.computeIfAbsent(folder, f -> new HashSet<>()).add(textureLocation);
        }
    }

    @NotNull
    public static String getTextureFolder(ResourceLocation textureLocation) {
        String path = textureLocation.getPath();
        int lastSlash = path.lastIndexOf('/');
        if (lastSlash <= 0) {
            return "";
        }
        return path.substring(0, lastSlash);    }

    private static boolean isTexturePath(ResourceLocation location) {
        return location.getPath().endsWith(".png");
    }

    public static Set<ResourceLocation> getTexturesInFolder(String folder) {
        return FOLDER_TO_TEXTURES.getOrDefault(folder, Set.of());
    }

    public static boolean isTextureFolder(String string) {
        return FOLDER_TO_TEXTURES.containsKey(string);
    }

    @Nullable
    public static String autocompleteFolderName(String string) {
        Set<String> folders = FOLDER_TO_TEXTURES.keySet();
        Optional<String> autocomplete = folders.stream().filter(s -> s.startsWith(string)).findAny().map(s -> s.replaceFirst(string, ""));
        return autocomplete.orElse(null);
    }
}
