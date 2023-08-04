package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class PSTCreativeTabs {
	public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SkillTreeMod.MOD_ID);

	// formatter:off
	public static final RegistryObject<CreativeModeTab> TOOLS = REGISTRY.register("tools", () -> CreativeModeTab.builder()
			  .title(Component.translatable("itemGroup.skilltree"))
			  .icon(() -> new ItemStack(PSTItems.AMNESIA_SCROLL.get()))
			  .displayItems((params, output) -> {
				  PSTItems.REGISTRY.getEntries().stream().map(RegistryObject::get).forEach(output::accept);
			  })
			  .build());
	// formatter:on
}
