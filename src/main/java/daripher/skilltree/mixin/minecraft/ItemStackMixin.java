package daripher.skilltree.mixin.minecraft;

import daripher.skilltree.skill.bonus.handler.ItemDurabilityLossPreventionBonusHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    public void preventDurabilityLoss(int amount, RandomSource random, @Nullable ServerPlayer playerUsingItem, CallbackInfoReturnable<Boolean> callbackInfo) {
        if (playerUsingItem == null) {
            return;
        }
        @SuppressWarnings("DataFlowIssue") ItemStack itemStack = (ItemStack) (Object) this;
        if (ItemDurabilityLossPreventionBonusHandler.shouldPreventItemDurabilityLoss(playerUsingItem, itemStack, random)) {
            callbackInfo.setReturnValue(false);
        }
    }
}
