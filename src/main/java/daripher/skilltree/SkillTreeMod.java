package daripher.skilltree;

import daripher.skilltree.compat.apotheosis.ApotheosisCompatibility;
import daripher.skilltree.compat.appleskin.AppleSkinCompatibility;
import daripher.skilltree.config.Config;
import daripher.skilltree.init.PSTAttributes;
import daripher.skilltree.init.PSTEffects;
import daripher.skilltree.init.PSTItems;
import daripher.skilltree.init.PSTRecipeSerializers;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import top.theillusivec4.curios.api.SlotTypeMessage;

@Mod(SkillTreeMod.MOD_ID)
public class SkillTreeMod {
	public static final String MOD_ID = "skilltree";

	public SkillTreeMod() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		PSTItems.REGISTRY.register(modEventBus);
		PSTAttributes.REGISTRY.register(modEventBus);
		PSTRecipeSerializers.REGISTRY.register(modEventBus);
		PSTEffects.REGISTRY.register(modEventBus);
		modEventBus.addListener(this::registerCurioSlots);
		Config.register();
		addCompatibilities();
	}

	private void registerCurioSlots(InterModEnqueueEvent event) {
		InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder("ring").size(2).build());
		InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder("necklace").build());
		InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE,
				() -> new SlotTypeMessage.Builder("quiver").icon(new ResourceLocation("curios", "slot/empty_quiver_slot")).build());
	}

	protected void addCompatibilities() {
		if (ModList.get().isLoaded("appleskin")) AppleSkinCompatibility.ISNTANCE.addCompatibility();
		if (ModList.get().isLoaded("apotheosis")) ApotheosisCompatibility.ISNTANCE.addCompatibility();
	}
}
