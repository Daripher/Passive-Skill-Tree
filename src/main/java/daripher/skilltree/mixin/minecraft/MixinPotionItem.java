package daripher.skilltree.mixin.minecraft;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import daripher.skilltree.util.PotionHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.Potion;
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
		if (PotionHelper.isMixture(itemStack)) return getMixtureName(itemStack);
		if (PotionHelper.isSuperiorPotion(itemStack)) return getSuperiorPotionName(itemStack);
		return super.getName(itemStack);
	}

	protected Component getMixtureName(ItemStack itemStack) {
		String mixtureId = getMixtureId(itemStack);
		MutableComponent translatedName = Component.translatable(mixtureId);
		// no special name
		if (translatedName.getString().equals(mixtureId)) {
			return Component.translatable(getDescriptionId() + ".mixture");
		}
		return translatedName;
	}

	protected Component getSuperiorPotionName(ItemStack itemStack) {
		ItemStack potionStack = PotionHelper.getActualPotionStack(itemStack);
		Component potionName = super.getName(potionStack);
		return Component.translatable("potion.superior").append(potionName);
	}

	@Inject(method = "appendHoverText", at = @At("TAIL"))
	public void appendAdvancedTooltip(ItemStack itemStack, Level level, List<Component> components, TooltipFlag tooltipFlag, CallbackInfo callbackInfo) {
		if (tooltipFlag != TooltipFlag.Default.ADVANCED) return;
		appendAdvancedTooltip(itemStack, level, components);
	}

	private void appendAdvancedTooltip(ItemStack itemStack, Level level, List<Component> components) {
		if (PotionHelper.isSuperiorPotion(itemStack)) {
			Potion potion = PotionHelper.getActualPotion(itemStack);
			String potionId = ForgeRegistries.POTIONS.getKey(potion).toString();
			components.add(Component.literal(potionId).withStyle(ChatFormatting.DARK_GRAY));
		}
		if (PotionHelper.isMixture(itemStack)) {
			PotionUtils.getMobEffects(itemStack).stream()
				.map(MobEffectInstance::getEffect)
				.map(MobEffect::getDescriptionId)
				.map(s -> s.replaceAll("effect.", ""))
				.map(Component::literal)
				.map(c -> c.withStyle(ChatFormatting.DARK_GRAY))
				.forEach(components::add);
		}
	}

	protected String getMixtureId(ItemStack itemStack) {
		var name = new StringBuilder(getDescriptionId() + ".mixture");
		PotionUtils.getMobEffects(itemStack).stream()
			.map(MobEffectInstance::getEffect)
			.map(MobEffect::getDescriptionId)
			.map(id -> id.replaceAll("effect.", ""))
			.forEach(id -> name.append("." + id));
		return name.toString();
	}
}
