package daripher.skilltree.mixin.minecraft;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import daripher.skilltree.util.FoodHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;

@Mixin(ItemStack.class)
public class MixinItemStack {
	public @Nullable FoodProperties getFoodProperties(@Nullable LivingEntity entity) {
		var itemStack = (ItemStack) (Object) this;
		var foodProperties = itemStack.getItem().getFoodProperties(itemStack, entity);

		if (FoodHelper.hasRestorationBonus(itemStack)) {
			var restorationBonus = FoodHelper.getRestorationBonus(itemStack);
			var newPropertiesBuilder = new FoodProperties.Builder().nutrition((int) (foodProperties.getNutrition() * (1 + restorationBonus))).saturationMod(foodProperties.getSaturationModifier());

			if (foodProperties.canAlwaysEat()) {
				newPropertiesBuilder = newPropertiesBuilder.alwaysEat();
			}

			if (foodProperties.isFastFood()) {
				newPropertiesBuilder = newPropertiesBuilder.fast();
			}

			if (foodProperties.isMeat()) {
				newPropertiesBuilder = newPropertiesBuilder.meat();
			}

			for (var effectWithChance : foodProperties.getEffects()) {
				newPropertiesBuilder = newPropertiesBuilder.effect(effectWithChance::getFirst, effectWithChance.getSecond());
			}

			return newPropertiesBuilder.build();
		}

		return foodProperties;
	}
}
