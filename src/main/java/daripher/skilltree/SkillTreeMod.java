package daripher.skilltree;

import daripher.skilltree.compat.AppleSkinCompatibility;
import daripher.skilltree.config.Config;
import daripher.skilltree.init.SkillTreeAttributes;
import daripher.skilltree.init.SkillTreeEffects;
import daripher.skilltree.init.SkillTreeItems;
import daripher.skilltree.init.SkillTreeRecipeSerializers;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(SkillTreeMod.MOD_ID)
public class SkillTreeMod {
	public static final String MOD_ID = "skilltree";

	public SkillTreeMod() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG_SPEC);
		var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		SkillTreeItems.REGISTRY.register(modEventBus);
		SkillTreeAttributes.REGISTRY.register(modEventBus);
		SkillTreeRecipeSerializers.REGISTRY.register(modEventBus);
		SkillTreeEffects.REGISTRY.register(modEventBus);

		if (ModList.get().isLoaded("appleskin")) {
			AppleSkinCompatibility.ISNTANCE.addCompatibility();
		}
	}
}
