package daripher.skilltree.mixin.minecraft;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import daripher.skilltree.entity.EquipmentContainer;
import daripher.skilltree.skill.bonus.handler.JumpHeightBonusHandler;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements EquipmentContainer {
    private final List<ItemStack> equipment = new ArrayList<>();

    @SuppressWarnings({"ConstantValue", "unused"})
    @ModifyReturnValue(method = "getJumpPower", at = @At("RETURN"))
    private float applyJumpHeightBonus(float original) {
        if ((Object) this instanceof Player player) {
            return original * JumpHeightBonusHandler.getJumpHeightMultiplier(player);
        }
        return original;
    }

    @SuppressWarnings("unused")
    @Inject(method = "dropAllDeathLoot", at = @At("HEAD"))
    private void storeEquipmentBeforeDeath(DamageSource damageSource, CallbackInfo callbackInfo) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack itemInSlot = getItemBySlot(slot);
            if (itemInSlot.isEmpty()) {
                continue;
            }
            equipment.add(itemInSlot);
        }
    }

    @Override
    public boolean hasItemEquipped(ItemStack stack) {
        return equipment.stream().anyMatch(equipped -> ItemStack.matches(stack, equipped));
    }

    public abstract @Shadow ItemStack getItemBySlot(EquipmentSlot slot);
}
