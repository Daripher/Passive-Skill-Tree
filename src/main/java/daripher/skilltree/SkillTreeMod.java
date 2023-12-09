package daripher.skilltree;

import daripher.skilltree.compat.apotheosis.ApotheosisCompatibility;
import daripher.skilltree.compat.tetra.TetraCompatibility;
import daripher.skilltree.config.ClientConfig;
import daripher.skilltree.config.Config;
import daripher.skilltree.init.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.curios.api.SlotTypeMessage;

@Mod(SkillTreeMod.MOD_ID)
public class SkillTreeMod {
  public static final String MOD_ID = "skilltree";
  public static final Logger LOGGER = LogManager.getLogger(SkillTreeMod.MOD_ID);

  public SkillTreeMod() {
    IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
    PSTItems.REGISTRY.register(modEventBus);
    PSTAttributes.REGISTRY.register(modEventBus);
    PSTRecipeSerializers.REGISTRY.register(modEventBus);
    PSTEffects.REGISTRY.register(modEventBus);
    PSTSkillBonuses.REGISTRY.register(modEventBus);
    PSTLivingConditions.REGISTRY.register(modEventBus);
    PSTLivingMultipliers.REGISTRY.register(modEventBus);
    PSTDamageConditions.REGISTRY.register(modEventBus);
    PSTItemBonuses.REGISTRY.register(modEventBus);
    PSTItemConditions.REGISTRY.register(modEventBus);
    PSTEnchantmentConditions.REGISTRY.register(modEventBus);
    modEventBus.addListener(this::registerCurioSlots);
    ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
    addCompatibilities();
  }

  public static boolean apotheosisEnabled() {
    return ModList.get().isLoaded("apotheosis")
        && ApotheosisCompatibility.INSTANCE.adventureModuleEnabled();
  }

  private void registerCurioSlots(InterModEnqueueEvent event) {
    InterModComms.sendTo(
        "curios",
        SlotTypeMessage.REGISTER_TYPE,
        () -> new SlotTypeMessage.Builder("ring").size(2).build());
    InterModComms.sendTo(
        "curios",
        SlotTypeMessage.REGISTER_TYPE,
        () -> new SlotTypeMessage.Builder("necklace").build());
    InterModComms.sendTo(
        "curios",
        SlotTypeMessage.REGISTER_TYPE,
        () ->
            new SlotTypeMessage.Builder("quiver")
                .icon(new ResourceLocation("curios", "category/empty_quiver_slot"))
                .build());
  }

  protected void addCompatibilities() {
    if (ModList.get().isLoaded("apotheosis")) ApotheosisCompatibility.INSTANCE.addCompatibility();
    if (ModList.get().isLoaded("tetra")) TetraCompatibility.INSTANCE.addCompatibility();
  }
}
