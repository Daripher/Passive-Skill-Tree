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
			Predicate<Object> positiveInteger = o -> o instanceof Integer && (Integer) o > 0;
			Predicate<Object> positiveOrZeroInteger = o -> o instanceof Integer && (Integer) o >= 0;
			maximumSkillPoints = builder.define("maximum_skill_points", 55, positiveInteger);
			skillPointsCosts = builder.defineList("skill_points_costs", generateDefaultPointsCosts(), positiveOrZeroInteger);
		}

		private List<Integer> generateDefaultPointsCosts() {
			var costs = new ArrayList<Integer>();
			costs.add(9);
			costs.add(14);

			while (costs.size() < 55) {
				var cost = costs.get(costs.size() - 1) + costs.get(costs.size() - 2);
				cost /= 1.8;

				if (cost <= costs.get(costs.size() - 1)) {
					cost = costs.get(costs.size() - 1) + 1;
				}

				costs.add(cost);
			}

			return costs;
		}

		public int getMaximumSkillPoints() {
			return maximumSkillPoints.get();
		}

		public List<? extends Integer> getSkillPointCosts() {
			if (skillPointsCosts.get().size() < maximumSkillPoints.get()) {
				skillPointsCosts.set(generateDefaultPointsCosts());
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
