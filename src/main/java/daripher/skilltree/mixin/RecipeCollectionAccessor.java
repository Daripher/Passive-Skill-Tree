package daripher.skilltree.mixin;

import java.util.Set;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RecipeCollection.class)
public interface RecipeCollectionAccessor {
  @Accessor
  Set<Recipe<?>> getCraftable();
}
