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
import net.minecraftforge.registries.ForgeRegistries;

@Mixin(PotionItem.class)
public class MixinPotionItem extends Item {
	private MixinPotionItem() {
		super(null);
	}

	@Override
	public Component getName(ItemStack itemStack) {
		if (PotionHelper.isPotionMix(itemStack)) {
			return getMixtureName(itemStack);
		}
		if (PotionHelper.isSuperiorPotion(itemStack)) {
			return getSuperiorPotionName(itemStack);
		}
		return super.getName(itemStack);
	}

	protected Component getMixtureName(ItemStack itemStack) {
		var mixtureId = getMixtureId(itemStack);
		var translatedName = Component.translatable(mixtureId);
		if (translatedName.getString().equals(mixtureId)) {
			var baseMixtureName = Component.translatable(getDescriptionId() + ".mixture");
			return baseMixtureName;
		}
		return translatedName;
	}

	protected Component getSuperiorPotionName(ItemStack itemStack) {
		var actualPotionStack = PotionHelper.getActualPotionStack(itemStack);
		var actualPotionName = super.getName(actualPotionStack);
		return Component.translatable("potion.superior").append(actualPotionName);
	}

	@Inject(method = "appendHoverText", at = @At("TAIL"))
	public void appendAdvancedTooltip(ItemStack itemStack, Level level, List<Component> components, TooltipFlag tooltipFlag, CallbackInfo callbackInfo) {
		if (tooltipFlag == TooltipFlag.Default.ADVANCED) {
			appendAdvancedTooltip(itemStack, level, components);
		}
	}

	private void appendAdvancedTooltip(ItemStack itemStack, Level level, List<Component> components) {
		if (PotionHelper.isSuperiorPotion(itemStack)) {
			var actualPotion = PotionHelper.getActualPotion(itemStack);
			var actualPotionId = ForgeRegistries.POTIONS.getKey(actualPotion).toString();
			var actualPotionTooltip = Component.literal(actualPotionId).withStyle(ChatFormatting.DARK_GRAY);
			components.add(actualPotionTooltip);
		}
		if (PotionHelper.isPotionMix(itemStack)) {
			var mixtureId = getMixtureId(itemStack);
			var mixtureIdTooltip = Component.literal(mixtureId).withStyle(ChatFormatting.DARK_GRAY);
			components.add(mixtureIdTooltip);
		}
	}

	protected String getMixtureId(ItemStack itemStack) {
		var effects = PotionUtils.getMobEffects(itemStack);
		var name = new StringBuilder(getDescriptionId() + ".mixture");
		effects.stream()
			.map(MobEffectInstance::getEffect)
			.map(MobEffect::getDescriptionId)
			.map(id -> id.replaceAll("effect.", ""))
			.forEach(id -> name.append("." + id));
		var mixtureId = name.toString();
		return mixtureId;
	}
}
