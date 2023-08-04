package daripher.skilltree.mixin.farmersdelight;

import java.util.Optional;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import daripher.skilltree.api.PlayerContainer;
import daripher.skilltree.util.FoodHelper;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import vectorwing.farmersdelight.common.block.entity.StoveBlockEntity;

@Mixin(value = StoveBlockEntity.class)
public class MixinStoveBlockEntity implements PlayerContainer {
	private @Nullable Player player;

	@Redirect(method = "cookAndOutputItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/CampfireCookingRecipe;getResultItem(Lnet/minecraft/core/RegistryAccess;)Lnet/minecraft/world/item/ItemStack;", remap = true), remap = false)
	private ItemStack setCookedFoodBonuses(CampfireCookingRecipe recipe, RegistryAccess access) {
		ItemStack result = recipe.getResultItem(access);
		if (player == null) return result;
		FoodHelper.setCraftedFoodBonuses(result, player);
		return result;
	}

	@Override
	public Optional<Player> getPlayer() {
		return Optional.ofNullable(player);
	}

	@Override
	public void setPlayer(Player player) {
		this.player = player;
	}
}
