package daripher.skilltree.mixin.minecraft;

import daripher.skilltree.api.IrisciteSeedContainer;
import daripher.skilltree.init.PSTAttributes;
import daripher.skilltree.item.ItemHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class MixinPlayer extends LivingEntity implements IrisciteSeedContainer {
  private int rainbowJewelInsertionSeed;

  protected MixinPlayer() {
    super(null, null);
  }

  @Override
  public int getUseItemRemainingTicks() {
    if (!ItemHelper.isRangedWeapon(getUseItem())) {
      return super.getUseItemRemainingTicks();
    }
    double attackSpeedBonus = getAttributeValue(Attributes.ATTACK_SPEED) - 1;
    if (attackSpeedBonus <= 0) {
      return super.getUseItemRemainingTicks();
    }
    int useDuration = getUseItem().getUseDuration() - useItemRemaining;
    return (int) (useItemRemaining - useDuration * attackSpeedBonus);
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
  private void restoreEnchantmentExperience(
      ItemStack itemStack, int enchantmentCost, CallbackInfo callbackInfo) {
    var player = (Player) (Object) this;
    double freeEnchantmentChance =
        player.getAttributeValue(PSTAttributes.FREE_ENCHANTMENT_CHANCE.get()) - 1;
    if (player.getRandom().nextFloat() < freeEnchantmentChance) {
      player.giveExperienceLevels(enchantmentCost);
    }
  }

  @Override
  public int getIrisciteSeed() {
    return rainbowJewelInsertionSeed;
  }

  @Override
  public void updateIrisciteSeed() {
    rainbowJewelInsertionSeed = random.nextInt();
  }
}
