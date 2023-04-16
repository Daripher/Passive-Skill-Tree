package daripher.skilltree.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.base.Predicates;

import daripher.skilltree.api.recipe.SkillRequiringRecipe;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.init.SkillTreeAttributes;
import daripher.skilltree.recipe.GemstoneInsertionRecipe;
import daripher.skilltree.util.GemstoneHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.UpgradeRecipe;

@Mixin(SmithingMenu.class)
public class MixinSmithingMenu {
	private @Shadow UpgradeRecipe selectedRecipe;
	private Player currentUser;

	@Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V", at = @At("TAIL"))
	private void init(int windowId, Inventory inventory, ContainerLevelAccess levelAccess, CallbackInfo callbackInfo) {
		this.currentUser = inventory.player;
	}

	@ModifyVariable(method = "createResult", at = @At("STORE"), ordinal = 0)
	private List<UpgradeRecipe> filterRecipesRequiringSkill(List<UpgradeRecipe> recipes) {
		recipes.removeIf(Predicates.not(this::canUseRecipe));
		return recipes;
	}

	@ModifyVariable(method = "createResult", at = @At("STORE"), ordinal = 0)
	private ItemStack setGemstoneBonus(ItemStack itemStack) {
		var gemstoneStrengthBonus = currentUser.getAttributeValue(SkillTreeAttributes.APPLIED_GEMSTONES_STRENGTH_BONUS.get());

		if (gemstoneStrengthBonus == 0) {
			return itemStack;
		}

		if (selectedRecipe instanceof GemstoneInsertionRecipe gemstoneInsertionRecipe) {
			GemstoneHelper.setGemstoneStrength(itemStack, gemstoneStrengthBonus, gemstoneInsertionRecipe.gemstoneSlot);
		}

		return itemStack;
	}

	private boolean canUseRecipe(UpgradeRecipe recipe) {
		if (recipe instanceof SkillRequiringRecipe skillRequiringRecipe) {
			var requiredSkillId = skillRequiringRecipe.getRequiredSkillId();

			if (requiredSkillId == null) {
				return true;
			}

			var playerSkills = PlayerSkillsProvider.get(currentUser);
			var hasSkill = playerSkills.hasSkill(requiredSkillId);
			return hasSkill;
		}

		return true;
	}
}
