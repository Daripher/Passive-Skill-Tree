package daripher.skilltree.config;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class Config {
	public static final CommonConfig COMMON_CONFIG;
	public static final ForgeConfigSpec COMMON_CONFIG_SPEC;

	public static class CommonConfig {
		private final ConfigValue<Integer> maximumSkillPoints;
		private final ConfigValue<List<? extends Integer>> skillPointsCosts;

		public CommonConfig(ForgeConfigSpec.Builder builder) {
			Predicate<Object> positiveOrZeroInteger = o -> o instanceof Integer && (Integer) o >= 0;
			maximumSkillPoints = builder.defineInRange("maximum_skill_points", 55, 1, 300);
			skillPointsCosts = builder.defineList("skill_points_costs", generateDefaultPointsCosts(55), positiveOrZeroInteger);
		}

		private List<Integer> generateDefaultPointsCosts(int maximumPoints) {
			var costs = new ArrayList<Integer>();
			costs.add(40);
			for (int i = 1; i < maximumPoints; i++) {
				var previousCost = costs.get(costs.size() - 1);
				var cost = previousCost + 4 + i;
				costs.add(cost);
			}
			return costs;
		}

		public int getMaximumSkillPoints() {
			return maximumSkillPoints.get();
		}

		public List<? extends Integer> getSkillPointCosts() {
			var maximumPoints = maximumSkillPoints.get();
			if (skillPointsCosts.get().size() < maximumPoints) {
				skillPointsCosts.set(generateDefaultPointsCosts(maximumPoints));
			}
			return skillPointsCosts.get();
		}
	}

	static {
		var configPair = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
		COMMON_CONFIG_SPEC = configPair.getRight();
		COMMON_CONFIG = configPair.getLeft();
	}
}
