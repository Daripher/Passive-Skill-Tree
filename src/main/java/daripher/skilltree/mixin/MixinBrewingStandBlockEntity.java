package daripher.skilltree.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import daripher.skilltree.api.block.ExtendedBrewingStand;
import daripher.skilltree.init.SkillTreeAttributes;
import daripher.skilltree.util.PotionHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;

@Mixin(BrewingStandBlockEntity.class)
public abstract class MixinBrewingStandBlockEntity extends BaseContainerBlockEntity implements ExtendedBrewingStand {
	private Player currentUser;

	protected MixinBrewingStandBlockEntity() {
		super(null, null, null);
	}

	@Inject(method = "doBrew", at = @At("TAIL"))
	private static void enhanceBrewedPotions(Level level, BlockPos blockPos, NonNullList<ItemStack> itemStacks, CallbackInfo callbackInfo) {
		var blockEntity = level.getBlockEntity(blockPos);
		if (!(blockEntity instanceof ExtendedBrewingStand)) {
			return;
		}
		var brewingStand = (ExtendedBrewingStand) blockEntity;
		var player = brewingStand.getCurrentUser();
		var durationBonus = (float) player.getAttributeValue(SkillTreeAttributes.BREWED_POTIONS_DURATION_MULTIPLIER.get()) - 1;
		var potionAmplificationChance = (float) player.getAttributeValue(SkillTreeAttributes.BREWED_POTIONS_STRENGTH_MULTIPLIER.get()) - 1;
		var harmfulPotionAmplificationChance = (float) player.getAttributeValue(SkillTreeAttributes.BREWED_HARMFUL_POTIONS_STRENGTH_MULTIPLIER.get()) - 1;
		var beneficialPotionAmplificationChance = (float) player.getAttributeValue(SkillTreeAttributes.BREWED_BENEFICIAL_POTIONS_STRENGTH_MULTIPLIER.get()) - 1;
		for (var slot = 0; slot < 3; slot++) {
			var amplificationChance = potionAmplificationChance;
			var potionStack = itemStacks.get(slot);
			if (PotionHelper.isHarmfulPotion(potionStack)) {
				amplificationChance += harmfulPotionAmplificationChance;
			}
			if (PotionHelper.isBeneficialPotion(potionStack)) {
				amplificationChance += beneficialPotionAmplificationChance;
			}
			PotionHelper.enhancePotion(potionStack, amplificationChance, durationBonus);
		}
	}

	@Override
	public AbstractContainerMenu createMenu(int window, Inventory inventory, Player player) {
		currentUser = player;
		return super.createMenu(window, inventory, player);
	}

	@Override
	public Player getCurrentUser() {
		return currentUser;
	}
}
