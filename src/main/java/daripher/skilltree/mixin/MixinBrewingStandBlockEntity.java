package daripher.skilltree.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import daripher.skilltree.api.player.PlayerContainer;
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
public abstract class MixinBrewingStandBlockEntity extends BaseContainerBlockEntity implements PlayerContainer {
	private Optional<Player> player = Optional.empty();

	protected MixinBrewingStandBlockEntity() {
		super(null, null, null);
	}

	@Inject(method = "doBrew", at = @At("TAIL"))
	private static void enhanceBrewedPotions(Level level, BlockPos blockPos, NonNullList<ItemStack> itemStacks, CallbackInfo callbackInfo) {
		var blockEntity = level.getBlockEntity(blockPos);
		if (!(blockEntity instanceof PlayerContainer)) {
			return;
		}
		var playerContainer = (PlayerContainer) blockEntity;
		var player = playerContainer.getPlayer().get();
		for (var slot = 0; slot < 3; slot++) {
			var potionStack = itemStacks.get(slot);
			enhancePotion(potionStack, player);
		}
	}

	private static void enhancePotion(ItemStack potionStack, Player player) {
		var amplificationChance = (float) player.getAttributeValue(SkillTreeAttributes.BREWED_POTIONS_STRENGTH_MULTIPLIER.get()) - 1;
		if (PotionHelper.isHarmfulPotion(potionStack)) {
			var harmfulPotionAmplificationChance = (float) player.getAttributeValue(SkillTreeAttributes.BREWED_HARMFUL_POTIONS_STRENGTH_MULTIPLIER.get()) - 1;
			amplificationChance += harmfulPotionAmplificationChance;
		}
		if (PotionHelper.isBeneficialPotion(potionStack)) {
			var beneficialPotionAmplificationChance = (float) player.getAttributeValue(SkillTreeAttributes.BREWED_BENEFICIAL_POTIONS_STRENGTH_MULTIPLIER.get()) - 1;
			amplificationChance += beneficialPotionAmplificationChance;
		}
		var durationBonus = (float) player.getAttributeValue(SkillTreeAttributes.BREWED_POTIONS_DURATION_MULTIPLIER.get()) - 1;
		PotionHelper.enhancePotion(potionStack, amplificationChance, durationBonus);
	}

	@Override
	public AbstractContainerMenu createMenu(int window, Inventory inventory, Player player) {
		setPlayer(player);
		return super.createMenu(window, inventory, player);
	}

	@Override
	public Optional<Player> getPlayer() {
		return player;
	}

	@Override
	public void setPlayer(Player player) {
		this.player = Optional.of(player);
	}
}
