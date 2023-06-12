package daripher.skilltree.item.gem;

import java.awt.Color;
import java.util.List;

import org.apache.commons.lang3.tuple.Triple;

import daripher.skilltree.api.SkillTreePlayer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

public class RainbowGemstoneItem extends GemItem {
	public RainbowGemstoneItem() {
		super(0);
	}

	@Override
	public Triple<Attribute, Double, Operation> getGemBonus(Player player, ItemStack itemStack) {
		var random = createInsertionRandomSource(player, itemStack);
		var randomGemstone = getRandomSimpleGemstone(random);
		var randomBonus = random.nextInt(7);
		return getRandomGemstoneBonus(randomGemstone, randomBonus);
	}

	protected Triple<Attribute, Double, Operation> getRandomGemstoneBonus(SimpleGemstoneItem gemstone, int randomBonus) {
		return switch (randomBonus) {
			case 0: yield gemstone.getHelmetBonus();
			case 1: yield gemstone.getChestplateBonus();
			case 2: yield gemstone.getLeggingsBonus();
			case 3: yield gemstone.getBootsBonus();
			case 4: yield gemstone.getWeaponBonus();
			case 5: yield gemstone.getShieldBonus();
			case 6: yield gemstone.getBowBonus();
			default: yield null;
		};
	}

	protected RandomSource createInsertionRandomSource(Player player, ItemStack itemStack) {
		var random = RandomSource.create();
		var randomSeed = ((SkillTreePlayer) player).getRainbowGemstoneRandomSeed();
		var itemSlot = Player.getEquipmentSlotForItem(itemStack).ordinal();
		random.setSeed(randomSeed + itemSlot);
		return random;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public MutableComponent applyGemstoneColorStyle(MutableComponent component) {
		var minecraft = Minecraft.getInstance();
		var player = minecraft.player;
		if (player == null) {
			return Component.empty().append(component);
		}
		var styledComponent = Component.empty();
		var hue = (player.tickCount % 360) / 360F;
		var characters = component.getString().toCharArray();
		for (var character : characters) {
			var color = Color.getHSBColor(hue, 0.6F, 1F).getRGB();
			var characterComponent = Component.literal("" + character);
			var colorStyle = Style.EMPTY.withColor(color);
			var coloredCharacter = characterComponent.withStyle(colorStyle);
			styledComponent.append(coloredCharacter);
			hue += 0.05F;
		}
		return styledComponent;
	}

	protected SimpleGemstoneItem getRandomSimpleGemstone(RandomSource random) {
		var simpleGemstones = getAllSimpleGemstones();
		var randomIndex = random.nextInt(simpleGemstones.size());
		var randomGemstone = simpleGemstones.get(randomIndex);
		return randomGemstone;
	}

	protected List<SimpleGemstoneItem> getAllSimpleGemstones() {
		var items = ForgeRegistries.ITEMS.getValues();
		return items.stream().filter(SimpleGemstoneItem.class::isInstance).map(SimpleGemstoneItem.class::cast).toList();
	}

	@Override
	protected void appenBonusesTooltip(List<Component> components) {
		components.add(Component.translatable(getDescriptionId() + ".bonus").withStyle(ChatFormatting.GOLD));
	}
}
