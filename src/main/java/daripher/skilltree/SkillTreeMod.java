package daripher.skilltree;

import daripher.skilltree.compat.apotheosis.ApotheosisCompatibility;
import daripher.skilltree.compat.attributeslib.AttributesLibCompatibility;
import daripher.skilltree.compat.tetra.TetraCompatibility;
import daripher.skilltree.config.ClientConfig;
import daripher.skilltree.config.Config;
import daripher.skilltree.init.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    PSTCreativeTabs.REGISTRY.register(modEventBus);
    PSTSkillBonuses.REGISTRY.register(modEventBus);
    PSTLivingConditions.REGISTRY.register(modEventBus);
    PSTLivingMultipliers.REGISTRY.register(modEventBus);
    PSTDamageConditions.REGISTRY.register(modEventBus);
    PSTItemBonuses.REGISTRY.register(modEventBus);
    PSTItemConditions.REGISTRY.register(modEventBus);
    PSTEnchantmentConditions.REGISTRY.register(modEventBus);
    PSTLootPoolEntries.REGISTRY.register(modEventBus);
    PSTGemBonuses.REGISTRY.register(modEventBus);
    PSTEventListeners.REGISTRY.register(modEventBus);
    ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
    addCompatibilities();
  }

  public static boolean apotheosisEnabled() {
    return ModList.get().isLoaded("apotheosis")
        && ApotheosisCompatibility.INSTANCE.adventureModuleEnabled();
  }

  protected void addCompatibilities() {
    if (ModList.get().isLoaded("apotheosis")) ApotheosisCompatibility.INSTANCE.register();
    if (ModList.get().isLoaded("tetra")) TetraCompatibility.INSTANCE.register();
    if (ModList.get().isLoaded("attributeslib")) AttributesLibCompatibility.INSTANCE.register();
  }
}
