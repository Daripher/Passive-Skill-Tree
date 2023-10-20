package daripher.skilltree.mixin.minecraft;

import daripher.skilltree.api.PlayerContainer;
import daripher.skilltree.init.PSTAttributes;
import daripher.skilltree.potion.PotionHelper;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BrewingStandBlockEntity.class)
public abstract class MixinBrewingStandBlockEntity extends BaseContainerBlockEntity
    implements PlayerContainer {
  private Optional<Player> player = Optional.empty();

  protected MixinBrewingStandBlockEntity() {
    super(null, null, null);
  }

  @Inject(method = "doBrew", at = @At("TAIL"))
  private static void enhanceBrewedPotions(
      Level level,
      BlockPos blockPos,
      NonNullList<ItemStack> itemStacks,
      CallbackInfo callbackInfo) {
    BlockEntity blockEntity = level.getBlockEntity(blockPos);
    if (!(blockEntity instanceof PlayerContainer playerContainer)) return;
    Optional<Player> player = playerContainer.getViewingPlayer();
    if (!player.isPresent()) return;
    for (int slot = 0; slot < 3; slot++) {
      ItemStack potionStack = itemStacks.get(slot);
      enhancePotion(potionStack, player.get());
    }
  }

  private static void enhancePotion(ItemStack potionStack, Player player) {
    float strengthBonus =
        (float) player.getAttributeValue(PSTAttributes.BREWED_POTIONS_STRENGTH.get()) - 1;
    float durationBonus =
        (float) player.getAttributeValue(PSTAttributes.BREWED_POTIONS_DURATION.get()) - 1;
    if (PotionHelper.isHarmfulPotion(potionStack)) {
      strengthBonus +=
          player.getAttributeValue(PSTAttributes.BREWED_HARMFUL_POTIONS_STRENGTH.get()) - 1;
      durationBonus +=
          player.getAttributeValue(PSTAttributes.BREWED_HARMFUL_POTIONS_DURATION.get()) - 1;
    }
    if (PotionHelper.isBeneficialPotion(potionStack)) {
      strengthBonus +=
          player.getAttributeValue(PSTAttributes.BREWED_BENEFICIAL_POTIONS_STRENGTH.get()) - 1;
      durationBonus +=
          player.getAttributeValue(PSTAttributes.BREWED_BENEFICIAL_POTIONS_DURATION.get()) - 1;
    }
    if (PotionHelper.isPoison(potionStack)) {
      strengthBonus += player.getAttributeValue(PSTAttributes.BREWED_POISONS_STRENGTH.get()) - 1;
    }
    if (PotionHelper.isHealingPotion(potionStack)) {
      strengthBonus +=
          player.getAttributeValue(PSTAttributes.BREWED_HEALING_POTIONS_STRENGTH.get()) - 1;
    }
    if (durationBonus == 0 && strengthBonus == 0) return;
    PotionHelper.enhancePotion(potionStack, strengthBonus, durationBonus);
  }

  @Override
  public AbstractContainerMenu createMenu(int window, Inventory inventory, Player player) {
    this.player = Optional.of(player);
    return super.createMenu(window, inventory, player);
  }

  @Override
  public Optional<Player> getViewingPlayer() {
    return player;
  }
}
