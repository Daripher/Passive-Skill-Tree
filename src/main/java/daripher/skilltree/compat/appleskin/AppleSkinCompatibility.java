package daripher.skilltree.compat.appleskin;

import daripher.skilltree.util.FoodHelper;
import net.minecraftforge.common.MinecraftForge;
import squeek.appleskin.api.event.FoodValuesEvent;
import squeek.appleskin.api.food.FoodValues;

public enum AppleSkinCompatibility {
	ISNTANCE;

	public void addCompatibility() {
		MinecraftForge.EVENT_BUS.addListener(this::adjustFoodValues);
	}

	private void adjustFoodValues(FoodValuesEvent event) {
		if (!FoodHelper.hasRestorationBonus(event.itemStack)) return;
		float restorationBonus = FoodHelper.getRestorationBonus(event.itemStack);
		int hunger = (int) (event.modifiedFoodValues.hunger * (1 + restorationBonus));
		float saturation = event.modifiedFoodValues.saturationModifier / (1 + restorationBonus);
		event.modifiedFoodValues = new FoodValues(hunger, saturation);
	}
}
