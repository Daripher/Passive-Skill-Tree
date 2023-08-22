package daripher.skilltree.mixin.minecraft;

import java.util.List;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import daripher.skilltree.item.ItemHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.common.extensions.IForgeItemStack;
import net.minecraftforge.fml.ModList;

@Mixin(ItemStack.class)
public abstract class MixinItemStack implements IForgeItemStack {
	@Inject(method = "getMaxDamage", at = @At("RETURN"), cancellable = true)
	private void getMaxDamage(CallbackInfoReturnable<Integer> callback) {
		ItemStack stack = (ItemStack) (Object) this;
		if (!ItemHelper.hasBonus(stack, ItemHelper.DURABILITY)) return;
		double durabilityBonus = ItemHelper.getBonus(stack, ItemHelper.DURABILITY);
		callback.setReturnValue((int) (callback.getReturnValue() * (1 + durabilityBonus)));
	}

	// Slightly modified code from https://github.com/Shadows-of-Fire/Apotheosis

	// Injects just before ItemStack.TooltipPart.MODIFIERS is written to the tooltip to remember where to rewind to.
	@Inject(method = "getTooltipLines(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/TooltipFlag;)Ljava/util/List;", at = @At(value = "INVOKE", ordinal = 3, target = "net/minecraft/world/item/ItemStack.shouldShowInTooltip(ILnet/minecraft/world/item/ItemStack$TooltipPart;)Z"), locals = LocalCapture.CAPTURE_FAILSOFT)
	public void tooltipMarker(@Nullable Player player, TooltipFlag flag, CallbackInfoReturnable<List<Component>> components, List<Component> list) {
		if (ModList.get().isLoaded("apotheosis")) return;
		list.add(Component.literal("PST_REMOVE_MARKER"));
	}

	// Injects just after ItemStack.TooltipPart.MODIFIERS is written to the tooltip to remember where to rewind to.
	@Inject(method = "getTooltipLines(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/TooltipFlag;)Ljava/util/List;", at = @At(value = "INVOKE", ordinal = 1, target = "net/minecraft/world/item/ItemStack.hasTag()Z"), locals = LocalCapture.CAPTURE_FAILSOFT)
	public void tooltipMarker2(@Nullable Player player, TooltipFlag flag, CallbackInfoReturnable<List<Component>> components, List<Component> list) {
		if (ModList.get().isLoaded("apotheosis")) return;
		list.add(Component.literal("PST_REMOVE_MARKER_2"));
	}
}
