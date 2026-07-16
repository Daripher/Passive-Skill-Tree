package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.client.screen.menu.WorkbenchScreen;
import daripher.skilltree.inventory.menu.WorkbenchMenu;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import daripher.skilltree.util.ForgeRegistries;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class PSTMenuTypes {
    public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.MENU_TYPES, SkillTreeMod.MOD_ID);

    public static final DeferredHolder<MenuType<?>, ? extends MenuType<WorkbenchMenu>> ARTISAN_WORKBENCH = REGISTRY.register("artisan_workbench", () -> new MenuType<>(WorkbenchMenu::new, FeatureFlags.DEFAULT_FLAGS));

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ARTISAN_WORKBENCH.get(), WorkbenchScreen::new);
    }
}
