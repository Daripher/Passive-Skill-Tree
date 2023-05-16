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
		private final ConfigValue<Double> gemstoneDropChance;
		private final ConfigValue<Boolean> showChatMessages;
		private final ConfigValue<List<? extends Integer>> skillPointsCosts;
		private final ConfigValue<List<? extends String>> blacklistedGemstoneContainers;

		public CommonConfig(ForgeConfigSpec.Builder builder) {
			Predicate<Object> positiveOrZeroInteger = o -> o instanceof Integer i && i >= 0;
			Predicate<Object> potentialItemId = o -> o instanceof String s && s.contains(":");
			builder.push("Skill points");
			maximumSkillPoints = builder.defineInRange("Maximum skill points", 50, 1, 500);
			builder.comment("This list's size must be equal to maximum skill points.");
			skillPointsCosts = builder.defineList("Levelup costs", generateDefaultPointsCosts(50), positiveOrZeroInteger);
			builder.comment("Disabling this will remove chat messages when you gain a skill point.");
			showChatMessages = builder.define("Show chat messages", true);
			builder.pop();
			builder.push("Gemstones");
			gemstoneDropChance = builder.defineInRange("Base drop chance", 0.05, 0, 1);
			builder.comment("This is how to blacklist specific items: [\"minecraft:diamond_hoe\", \"minecraft:golden_hoe\"]");
			builder.comment("You can also blacklist whole namespace like this: [\"<mod_id>:*\"]");
			builder.comment("You can also blacklist all items like this: [\"*:*\"]");
			blacklistedGemstoneContainers = builder.defineList("IDs of items that shouldn't have gemstone slots", new ArrayList<String>(), potentialItemId);
			builder.pop();
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

		public List<? extends String> getBlacklistedGemstoneContainers() {
			return blacklistedGemstoneContainers.get();
		}

		public List<? extends Integer> getSkillPointCosts() {
			var maximumPoints = maximumSkillPoints.get();
			if (skillPointsCosts.get().size() < maximumPoints) {
				skillPointsCosts.set(generateDefaultPointsCosts(maximumPoints));
			}
			return skillPointsCosts.get();
		}

		public double getGemstoneDropChance() {
			return gemstoneDropChance.get();
		}

		public boolean shouldShowChatMessages() {
			return showChatMessages.get();
		}
	}

	static {
		var configPair = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
		COMMON_CONFIG_SPEC = configPair.getRight();
		COMMON_CONFIG = configPair.getLeft();
	}
}
