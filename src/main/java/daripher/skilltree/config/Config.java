package daripher.skilltree.config;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig.Type;

public class Config {
	public static final CommonConfig COMMON;
	public static final ForgeConfigSpec COMMON_SPEC;

	public static class CommonConfig {
		private final ConfigValue<Integer> maximumSkillPoints;
		private final ConfigValue<Double> gemDropChance;
		private final ConfigValue<Double> amnesiaScrollPenalty;
		private final ConfigValue<Double> grindstoneExp;
		private final ConfigValue<Boolean> showChatMessages;
		private final ConfigValue<Boolean> enableExperienceExchange;
		private final ConfigValue<Boolean> enableAmnesiaScrollDrop;
		private final ConfigValue<List<? extends Integer>> skillPointsCosts;
		private final ConfigValue<List<? extends String>> blacklistedGemContainers;

		public CommonConfig(ForgeConfigSpec.Builder builder) {
			Predicate<Object> positiveOrZeroInteger = o -> o instanceof Integer i && i >= 0;
			Predicate<Object> potentialItemId = o -> o instanceof String s && s.contains(":");
			builder.push("Skill Points");
			maximumSkillPoints = builder.defineInRange("Maximum skill points", 85, 1, 1000);
			builder.comment("This list's size must be equal to maximum skill points.");
			skillPointsCosts = builder.defineList("Levelup costs", generateDefaultPointsCosts(50), positiveOrZeroInteger);
			builder.comment("Disabling this will remove chat messages when you gain a skill point.");
			showChatMessages = builder.define("Show chat messages", true);
			builder.comment("Warning: If you disable this make sure you make alternative way of getting skill points.");
			enableExperienceExchange = builder.define("Enable exprerience exchange for skill points", true);
			builder.pop();
			builder.push("Gems");
			gemDropChance = builder.defineInRange("Base drop chance", 0.05, 0, 1);
			builder.comment("This is how to blacklist specific items: [\"minecraft:diamond_hoe\", \"minecraft:golden_hoe\"]");
			builder.comment("You can also blacklist whole namespace like this: [\"<mod_id>:*\"]");
			builder.comment("You can also blacklist all items like this: [\"*:*\"]");
			blacklistedGemContainers = builder.defineList("IDs of items that shouldn't have sockets", new ArrayList<String>(), potentialItemId);
			builder.pop();
			builder.push("Amnesia Scroll");
			builder.comment("How much levels (percentage) player lose using amnesia scroll");
			amnesiaScrollPenalty = builder.defineInRange("Amnesia scroll penalty", 0.2D, 0D, 1D);
			enableAmnesiaScrollDrop = builder.define("Drop amnesia scrolls from the Ender Dragon", true);
			builder.pop();
			builder.push("Experience");
			grindstoneExp = builder.defineInRange("Grindstone experience multiplier", 0.1D, 0D, 1D);
			builder.pop();
		}

		private List<Integer> generateDefaultPointsCosts(int maximumPoints) {
			var costs = new ArrayList<Integer>();
			costs.add(15);
			for (int i = 1; i < maximumPoints; i++) {
				var previousCost = costs.get(costs.size() - 1);
				var cost = previousCost + 3 + i;
				costs.add(cost);
			}
			return costs;
		}

		public int getMaximumSkillPoints() {
			return maximumSkillPoints.get();
		}

		public List<? extends String> getBlacklistedGemstoneContainers() {
			return blacklistedGemContainers.get();
		}

		public List<? extends Integer> getSkillPointCosts() {
			int points = maximumSkillPoints.get();
			if (skillPointsCosts.get().size() < points) {
				skillPointsCosts.set(generateDefaultPointsCosts(points));
			}
			return skillPointsCosts.get();
		}

		public double getGemDropChance() {
			return gemDropChance.get();
		}

		public boolean shouldShowChatMessages() {
			return showChatMessages.get();
		}

		public boolean experienceGainEnabled() {
			return enableExperienceExchange.get();
		}

		public double getAmnesiaScrollPenalty() {
			return amnesiaScrollPenalty.get();
		}

		public boolean shouldDropAmnesiaScrolls() {
			return enableAmnesiaScrollDrop.get();
		}

		public double getGrindstoneExpMuliplier() {
			return grindstoneExp.get();
		}
	}

	public static void register() {
		ModLoadingContext.get().registerConfig(Type.COMMON, COMMON_SPEC);
	}

	static {
		var configPair = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
		COMMON_SPEC = configPair.getRight();
		COMMON = configPair.getLeft();
	}
}
