package daripher.skilltree.compat.jei;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.compat.apotheosis.ApotheosisCompatibility;
import daripher.skilltree.item.gem.GemItem;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class JeiCompatibility implements IModPlugin {
  @Override
  public @NotNull ResourceLocation getPluginUid() {
    return new ResourceLocation(SkillTreeMod.MOD_ID, "jei_plugin");
  }

  @Override
  public void registerRecipes(@NotNull IRecipeRegistration registration) {
    if (ModList.get().isLoaded("apotheosis")) {
      if (ApotheosisCompatibility.INSTANCE.adventureModuleEnabled()) return;
    }
    ForgeRegistries.ITEMS.getValues().stream()
        .filter(GemItem.class::isInstance)
        .map(ItemStack::new)
        .forEach(itemStack -> addGemInfo(registration, itemStack));
  }

  @Override
  public void registerGuiHandlers(@NotNull IGuiHandlerRegistration registration) {
    //		registration.
  }

  protected void addGemInfo(IRecipeRegistration registration, ItemStack itemStack) {
    registration.addIngredientInfo(
        itemStack, VanillaTypes.ITEM_STACK, Component.translatable("skilltree.jei.gem_info"));
  }
}
