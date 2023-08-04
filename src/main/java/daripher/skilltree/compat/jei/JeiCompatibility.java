package daripher.skilltree.compat.jei;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.item.gem.GemItem;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

@JeiPlugin
public class JeiCompatibility implements IModPlugin {

	@Override
	public ResourceLocation getPluginUid() {
		return new ResourceLocation(SkillTreeMod.MOD_ID, "jei_plugin");
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		// formatter:off
		ForgeRegistries.ITEMS.getValues().stream()
			.filter(GemItem.class::isInstance)
			.map(ItemStack::new)
			.forEach(stack -> addGemInfo(registration, stack));
		// formatter:on
	}

	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration) {
//		registration.
	}

	protected void addGemInfo(IRecipeRegistration registration, ItemStack itemStack) {
		registration.addIngredientInfo(itemStack, VanillaTypes.ITEM_STACK, Component.translatable("skilltree.jei.gem_info"));
	}
}
