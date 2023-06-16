package daripher.skilltree.compat.jei;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.compat.apotheosis.ApotheosisCompatibility;
import daripher.skilltree.item.gem.GemItem;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

@JeiPlugin
public class JeiCompatibility implements IModPlugin {

	@Override
	public ResourceLocation getPluginUid() {
		return new ResourceLocation(SkillTreeMod.MOD_ID, "jei_plugin");
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		if (ModList.get().isLoaded("apotheosis")) {
			if (ApotheosisCompatibility.ISNTANCE.adventureModuleEnabled()) return;
		}
		ForgeRegistries.ITEMS.getValues().stream().filter(GemItem.class::isInstance).map(ItemStack::new).forEach(itemStack -> addGemInfo(registration, itemStack));
	}

	protected void addGemInfo(IRecipeRegistration registration, ItemStack itemStack) {
		registration.addIngredientInfo(itemStack, VanillaTypes.ITEM_STACK, Component.translatable("skilltree.jei.gem_info"));
	}
}
