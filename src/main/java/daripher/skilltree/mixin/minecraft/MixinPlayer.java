package daripher.skilltree.mixin.minecraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import daripher.skilltree.api.SkillTreePlayer;
import daripher.skilltree.init.SkillTreeAttributes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;

@Mixin(Player.class)
public abstract class MixinPlayer extends LivingEntity implements SkillTreePlayer {
	private int rainbowJewelInsertionSeed;

	protected MixinPlayer() {
		super(null, null);
	}

	@Override
	public int getUseItemRemainingTicks() {
		if (getUseItem().getItem() instanceof BowItem) {
			var bowChargeSpeed = this.getAttributeValue(Attributes.ATTACK_SPEED) - 1;

			if (bowChargeSpeed > 1) {
				var bowChargeSpeedBonus = bowChargeSpeed - 1;
				var maxUseDuration = getUseItem().getUseDuration();
				var useDuration = maxUseDuration - useItemRemaining;
				return (int) (useItemRemaining - useDuration * bowChargeSpeedBonus);
			}
		}

		return super.getUseItemRemainingTicks();
	}

	@Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
	private void readRainbowJewelInsertionSeed(CompoundTag tag, CallbackInfo callbackInfo) {
		rainbowJewelInsertionSeed = tag.getInt("RainbowJewelInsertionSeed");
	}

	@Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
	private void writeRainbowJewelInsertionSeed(CompoundTag tag, CallbackInfo callbackInfo) {
		tag.putInt("RainbowJewelInsertionSeed", rainbowJewelInsertionSeed);
	}

	@Inject(method = "onEnchantmentPerformed", at = @At("HEAD"))
	private void restoreEnchantmentExperience(ItemStack itemStack, int enchantmentCost, CallbackInfo callbackInfo) {
		var player = (Player) (Object) this;
		var freeEnchantmentChance = player.getAttributeValue(SkillTreeAttributes.FREE_ENCHANTMENT_CHANCE.get()) - 1;
		if (player.getRandom().nextFloat() < freeEnchantmentChance) {
			player.giveExperienceLevels(enchantmentCost);
		}
	}

	@Override
	public int getRainbowGemstoneRandomSeed() {
		return rainbowJewelInsertionSeed;
	}

	@Override
	public void updateRainbowGemstoneRandomSeed() {
		rainbowJewelInsertionSeed = random.nextInt();
	}
}
