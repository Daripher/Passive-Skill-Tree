package daripher.skilltree.mixin.minecraft;

import daripher.skilltree.api.EquipmentContainer;
import daripher.skilltree.init.PSTAttributes;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements EquipmentContainer {
  private final List<ItemStack> equippedItems = new ArrayList<>();

  @Inject(method = "dropAllDeathLoot", at = @At("HEAD"))
  private void storeEquipmentBeforeDeath(DamageSource damageSource, CallbackInfo callbackInfo) {
    for (EquipmentSlot slot : EquipmentSlot.values()) {
      ItemStack itemInSlot = getItemBySlot(slot);
      if (itemInSlot.isEmpty()) continue;
      equippedItems.add(itemInSlot);
    }
  }

  @SuppressWarnings("ConstantValue")
  @Inject(method = "getJumpPower", at = @At("RETURN"), cancellable = true)
  private void applyJumpHeightBonus(CallbackInfoReturnable<Float> callback) {
    if ((Object) this instanceof Player) {
      double jumpHeight = getAttributeValue(PSTAttributes.JUMP_HEIGHT.get());
      callback.setReturnValue((float) (callback.getReturnValue() * jumpHeight));
    }
  }

  @Override
  public boolean equipped(ItemStack stack) {
    return equippedItems.stream().anyMatch(equipped -> ItemStack.matches(stack, equipped));
  }

  public abstract @Shadow ItemStack getItemBySlot(EquipmentSlot slot);

  public abstract @Shadow double getAttributeValue(Attribute attribute);
}
