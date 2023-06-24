package daripher.skilltree.mixin.minecraft;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import daripher.skilltree.api.PSTLivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity implements PSTLivingEntity {
	private final List<ItemStack> equippedItems = new ArrayList<>();

	@Inject(method = "dropAllDeathLoot", at = @At("HEAD"))
	private void storeEquipmentBeforeDeath(DamageSource damageSource, CallbackInfo callbackInfo) {
		for (var slot : EquipmentSlot.values()) {
			ItemStack itemInSlot = getItemBySlot(slot);
			if (itemInSlot.isEmpty()) continue;
			equippedItems.add(itemInSlot);
		}
	}
	
	@Override
	public boolean hadEquipped(ItemStack stack) {
		return equippedItems.stream().anyMatch(equipped -> ItemStack.matches(stack, equipped));
	}
	
	public @Shadow abstract ItemStack getItemBySlot(EquipmentSlot slot);
}
