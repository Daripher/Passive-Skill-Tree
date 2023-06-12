package daripher.skilltree;

import daripher.skilltree.compat.apotheosis.ApotheosisCompatibility;
import daripher.skilltree.compat.appleskin.AppleSkinCompatibility;
import daripher.skilltree.config.Config;
import daripher.skilltree.init.SkillTreeAttributes;
import daripher.skilltree.init.SkillTreeEffects;
import daripher.skilltree.init.SkillTreeItems;
import daripher.skilltree.init.SkillTreeRecipeSerializers;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(SkillTreeMod.MOD_ID)
public class SkillTreeMod {
	public static final String MOD_ID = "skilltree";

	public SkillTreeMod() {
		var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		SkillTreeItems.REGISTRY.register(modEventBus);
		SkillTreeAttributes.REGISTRY.register(modEventBus);
		SkillTreeRecipeSerializers.REGISTRY.register(modEventBus);
		SkillTreeEffects.REGISTRY.register(modEventBus);
		Config.register();
		addModCompatibilities();
	}

	protected void addModCompatibilities() {
		if (ModList.get().isLoaded("appleskin")) {
			AppleSkinCompatibility.ISNTANCE.addCompatibility();
		}
		if (ModList.get().isLoaded("apotheosis")) {
			ApotheosisCompatibility.ISNTANCE.addCompatibility();
		}
	}
}
