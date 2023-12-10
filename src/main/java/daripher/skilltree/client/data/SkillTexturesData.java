package daripher.skilltree.client.data;

import daripher.skilltree.SkillTreeMod;
import java.util.Set;
import java.util.TreeSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(
    modid = SkillTreeMod.MOD_ID,
    bus = Mod.EventBusSubscriber.Bus.MOD,
    value = Dist.CLIENT)
public class SkillTexturesData implements ResourceManagerReloadListener {
  public static Set<ResourceLocation> TOOLTIP_BACKGROUNDS = new TreeSet<>();
  public static Set<ResourceLocation> BORDERS = new TreeSet<>();
  public static Set<ResourceLocation> ICONS = new TreeSet<>();

  @SubscribeEvent
  public static void registerReloadListener(RegisterClientReloadListenersEvent event) {
    event.registerReloadListener(new SkillTexturesData());
  }

  @Override
  public void onResourceManagerReload(@NotNull ResourceManager resourceManager) {
    reloadTextures(TOOLTIP_BACKGROUNDS, "tooltip", resourceManager);
    reloadTextures(BORDERS, "icons/background", resourceManager);
    reloadTextures(ICONS, "icons", resourceManager);
    ICONS.removeAll(BORDERS);
  }

  private void reloadTextures(
      Set<ResourceLocation> storage, String folder, ResourceManager resourceManager) {
    storage.clear();
    storage.addAll(
        resourceManager
            .listResources("textures/" + folder, l -> l.getPath().endsWith(".png"))
            .keySet());
  }
}
