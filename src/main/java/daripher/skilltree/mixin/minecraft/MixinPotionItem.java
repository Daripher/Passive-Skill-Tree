package daripher.skilltree.mixin.minecraft;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import daripher.skilltree.util.PotionHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;

@Mixin(PotionItem.class)
public class MixinPotionItem extends Item {
	private MixinPotionItem() {
		super(null);
	}

	@Override
	public Component getName(ItemStack itemStack) {
		if (PotionHelper.isPotionMix(itemStack)) {
			var mixtureId = getMixtureId(itemStack);
			var translatedName = Component.translatable(mixtureId);
			if (translatedName.getString().equals(mixtureId)) {
				return Component.translatable(getDescriptionId() + ".mixture");
			}
			return translatedName;
		}
		if (PotionHelper.isSuperiorPotion(itemStack)) {
			var actualPotionStack = PotionHelper.getActualPotionStack(itemStack);
			var actualPotionName = super.getName(actualPotionStack);
			return Component.translatable("potion.superior").append(actualPotionName);
		}
		return super.getName(itemStack);
	}

	@Inject(method = "appendHoverText", at = @At("TAIL"))
	public void appendMixtureId(ItemStack itemStack, Level level, List<Component> components, TooltipFlag tooltipFlag, CallbackInfo callbackInfo) {
		if (!PotionHelper.isPotionMix(itemStack)) {
			return;
		}
		if (tooltipFlag != TooltipFlag.Default.ADVANCED) {
			return;
		}
		components.add(Component.literal(getMixtureId(itemStack)).withStyle(ChatFormatting.DARK_GRAY));
	}

	protected String getMixtureId(ItemStack itemStack) {
		var effects = PotionUtils.getMobEffects(itemStack);
		var name = new StringBuilder(getDescriptionId() + ".mixture");
		effects.stream().map(MobEffectInstance::getEffect).map(MobEffect::getDescriptionId).map(id -> id.replaceAll("effect.", "")).forEach(id -> name.append("." + id));
		var mixtureId = name.toString();
		return mixtureId;
	}
}
