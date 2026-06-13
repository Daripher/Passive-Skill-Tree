package daripher.skilltree;

import daripher.skilltree.compat.attributeslib.AttributesLibCompatibility;
import daripher.skilltree.compat.curios.CuriosCompatibility;
import daripher.skilltree.compat.ironsspellbooks.IronsSpellbooksCompat;
import daripher.skilltree.config.ClientConfig;
import daripher.skilltree.config.ServerConfig;
import daripher.skilltree.init.*;
import daripher.skilltree.init.predicate.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(SkillTreeMod.MOD_ID)
public class SkillTreeMod {
    public static final String MOD_ID = "skilltree";
    public static final Logger LOGGER = LogManager.getLogger(SkillTreeMod.MOD_ID);

    public SkillTreeMod(FMLJavaModLoadingContext context) {
        registerModRegistries(context);
        registerConfigs(context);
        registerCompatibilities();
    }

    private static void registerModRegistries(FMLJavaModLoadingContext context) {
        IEventBus eventBus = context.getModEventBus();
        PSTItems.REGISTRY.register(eventBus);
        PSTMobEffects.REGISTRY.register(eventBus);
        PSTCreativeTabs.REGISTRY.register(eventBus);
        PSTSkillBonuses.REGISTRY.register(eventBus);
        PSTLivingEntityPredicates.REGISTRY.register(eventBus);
        PSTLivingMultipliers.REGISTRY.register(eventBus);
        PSTDamagePredicates.REGISTRY.register(eventBus);
        PSTItemPredicates.REGISTRY.register(eventBus);
        PSTEnchantmentPredicates.REGISTRY.register(eventBus);
        PSTEventListeners.REGISTRY.register(eventBus);
        PSTLootModifiers.REGISTRY.register(eventBus);
        PSTFloatFunctions.REGISTRY.register(eventBus);
        PSTPotions.REGISTRY.register(eventBus);
        PSTSkillRequirements.REGISTRY.register(eventBus);
        PSTBlocks.REGISTRY.register(eventBus);
        PSTMenuTypes.REGISTRY.register(eventBus);
        PSTRecipeSerializers.REGISTRY.register(eventBus);
        PSTItemBonuses.REGISTRY.register(eventBus);
        PSTRecipeTypes.REGISTRY.register(eventBus);
        PSTMobEffectPredicates.REGISTRY.register(eventBus);
    }

    private static void registerConfigs(FMLJavaModLoadingContext context) {
        context.registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);
        context.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
    }

    private static void registerCompatibilities() {
        if (ModList.get().isLoaded("attributeslib")) {
            AttributesLibCompatibility.INSTANCE.register();
        }
        if (ModList.get().isLoaded("curios")) {
            CuriosCompatibility.INSTANCE.register();
        }
        if (ModList.get().isLoaded("irons_spellbooks")) {
            IronsSpellbooksCompat.INSTANCE.register();
        }
    }
}
