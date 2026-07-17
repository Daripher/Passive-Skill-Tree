package daripher.skilltree.mixin.minecraft;

import daripher.skilltree.entity.player.PlayerExtension;
import daripher.skilltree.skill.bonus.handler.EnchantingExperienceRefundBonusHandler;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements PlayerExtension {
    @SuppressWarnings("DataFlowIssue")
    protected PlayerMixin() {
        super(null, null);
    }

    @SuppressWarnings("DataFlowIssue")
    @Inject(
            method = "onEnchantmentPerformed(Lnet/minecraft/world/item/ItemStack;I)V",
            at = @At("TAIL"),
            remap = false)
    private void restoreEnchantmentExperience(ItemStack itemStack, int enchantmentCost, CallbackInfo callbackInfo) {
        Player player = (Player) (Object) this;
        if (EnchantingExperienceRefundBonusHandler.shouldRefundEnchantingExperience(player, itemStack)) {
            player.giveExperienceLevels(enchantmentCost);
        }
    }
}
