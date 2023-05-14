package daripher.skilltree.integration;

import daripher.skilltree.util.FoodHelper;
import net.minecraftforge.common.MinecraftForge;
import squeek.appleskin.api.event.FoodValuesEvent;
import squeek.appleskin.api.food.FoodValues;

public enum AppleSkinIntegration {
	ISNTANCE;

	public void addCompatibility() {
		MinecraftForge.EVENT_BUS.addListener(this::adjustFoodValues);
	}

	private void adjustFoodValues(FoodValuesEvent event) {
		if (!FoodHelper.hasRestorationBonus(event.itemStack)) {
			return;
		}
		var restorationBonus = FoodHelper.getRestorationBonus(event.itemStack);
		var modifiedHunger = (int) (event.modifiedFoodValues.hunger * (1 + restorationBonus));
		var modifiedSaturation = event.modifiedFoodValues.saturationModifier / (1 + restorationBonus);
		event.modifiedFoodValues = new FoodValues(modifiedHunger, modifiedSaturation);
	}
}
