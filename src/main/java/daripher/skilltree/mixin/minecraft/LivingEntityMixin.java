package daripher.skilltree.mixin.minecraft;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import daripher.skilltree.entity.EquippedEntity;
import daripher.skilltree.skill.bonus.SkillBonusHandler;
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
public abstract class LivingEntityMixin implements EquippedEntity {
  private final List<ItemStack> equippedItems = new ArrayList<>();

  @SuppressWarnings("unused")
  @Inject(method = "dropAllDeathLoot", at = @At("HEAD"))
  private void storeEquipmentBeforeDeath(DamageSource damageSource, CallbackInfo callbackInfo) {
    for (EquipmentSlot slot : EquipmentSlot.values()) {
      ItemStack itemInSlot = getItemBySlot(slot);
      if (itemInSlot.isEmpty()) {
          continue;
      }
      equippedItems.add(itemInSlot);
    }
  }

  @SuppressWarnings({"ConstantValue", "unused"})
  @ModifyReturnValue(method = "getJumpPower", at = @At("RETURN"))
  private float applyJumpHeightBonus(float original) {
    boolean isPlayer = (Object) this instanceof Player;
    if (!isPlayer) {
        return original;
    }
    Player player = (Player) (Object) this;
    return original * SkillBonusHandler.getJumpHeightMultiplier(player);
  }

  @Override
  public boolean hasItemEquipped(ItemStack stack) {
    return equippedItems.stream()
        .anyMatch(equipped -> ItemStack.matches(stack, equipped));
  }

  public abstract @Shadow ItemStack getItemBySlot(EquipmentSlot slot);
}
