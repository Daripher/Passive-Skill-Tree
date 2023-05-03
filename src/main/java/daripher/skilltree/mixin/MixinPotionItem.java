package daripher.skilltree.mixin;

import org.spongepowered.asm.mixin.Mixin;

import daripher.skilltree.util.PotionHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;

@Mixin(PotionItem.class)
public class MixinPotionItem extends Item {
	private MixinPotionItem() {
		super(null);
	}

	@Override
	public Component getName(ItemStack itemStack) {
		if (PotionHelper.isSuperiorPotion(itemStack)) {
			var actualPotionStack = PotionHelper.getActualPotionStack(itemStack);
			var actualPotionName = super.getName(actualPotionStack);
			return Component.translatable("potion.superior").append(actualPotionName);
		}

		return super.getName(itemStack);
	}
}
