package daripher.skilltree.mixin.minecraft;

import daripher.skilltree.api.IrisciteSeedContainer;
import daripher.skilltree.skill.bonus.SkillBonusHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements IrisciteSeedContainer {
  private int rainbowJewelInsertionSeed;

  @SuppressWarnings("DataFlowIssue")
  protected PlayerMixin() {
    super(null, null);
  }

  @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
  private void readRainbowJewelInsertionSeed(CompoundTag tag, CallbackInfo callbackInfo) {
    rainbowJewelInsertionSeed = tag.getInt("RainbowJewelInsertionSeed");
  }

  @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
  private void writeRainbowJewelInsertionSeed(CompoundTag tag, CallbackInfo callbackInfo) {
    tag.putInt("RainbowJewelInsertionSeed", rainbowJewelInsertionSeed);
  }

  @SuppressWarnings("DataFlowIssue")
  @Inject(method = "onEnchantmentPerformed", at = @At("HEAD"))
  private void restoreEnchantmentExperience(
      ItemStack itemStack, int enchantmentCost, CallbackInfo callbackInfo) {
    Player player = (Player) (Object) this;
    float freeEnchantmentChance = SkillBonusHandler.getFreeEnchantmentChance(player);
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
