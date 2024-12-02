package daripher.skilltree.mixin.minecraft;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import daripher.skilltree.init.PSTEnchantments;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ThrowablePotionItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ThrowablePotionItem.class)
public class ThrowablePotionItemMixin extends Item {
  @SuppressWarnings("DataFlowIssue")
  private ThrowablePotionItemMixin() {
    super(null);
  }

  @Inject(method = "use", at = @At("HEAD"))
  private void setBottomlessFlaskCooldown(Level level,
      Player player,
      InteractionHand hand,
      CallbackInfoReturnable<InteractionResultHolder<ItemStack>> callbackInfo) {
    ItemStack itemStack = player.getItemInHand(hand);
    if (itemStack.getEnchantmentLevel(PSTEnchantments.BOTTOMLESS_FLASK.get()) > 0) {
      ItemCooldowns playerCooldowns = player.getCooldowns();
      playerCooldowns.addCooldown(this, 60);
    }
  }

  @WrapWithCondition(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V"))
  private boolean shouldConsumePotion(ItemStack itemStack, int decrement) {
    return itemStack.getEnchantmentLevel(PSTEnchantments.BOTTOMLESS_FLASK.get()) == 0;
  }
}
