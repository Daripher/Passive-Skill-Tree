package daripher.skilltree.item;

import java.awt.Color;
import java.util.List;

import org.apache.commons.lang3.tuple.Triple;

import daripher.skilltree.api.RainbowJewelRandomSeedContainer;
import daripher.skilltree.init.SkillTreeItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class RainbowGemstoneItem extends GemstoneItem {
	public RainbowGemstoneItem() {
		super(0);
	}

	@Override
	protected Triple<Attribute, Double, Operation> getGemstoneBonus(Player player, ItemStack itemStack) {
		if (!(player instanceof RainbowJewelRandomSeedContainer)) {
			return null;
		}
		var simpleGemstones = new Item[] { SkillTreeItems.LIGHT_GEMSTONE.get(), SkillTreeItems.STURDY_GEMSTONE.get(), SkillTreeItems.SOOTHING_GEMSTONE.get() };
		var random = RandomSource.create();
		var randomSeed = ((RainbowJewelRandomSeedContainer) player).getRainbowJewelRandomSeed();
		var itemSlot = Player.getEquipmentSlotForItem(itemStack).ordinal();
		random.setSeed(randomSeed + itemSlot);
		var randomGemstone = (SimpleGemstoneItem) simpleGemstones[random.nextInt(simpleGemstones.length)];
		var randomBonus = random.nextInt(7);
		switch (randomBonus) {
			case 0:
				return randomGemstone.getHelmetBonus();
			case 1:
				return randomGemstone.getChestplateBonus();
			case 2:
				return randomGemstone.getLeggingsBonus();
			case 3:
				return randomGemstone.getBootsBonus();
			case 4:
				return randomGemstone.getWeaponBonus();
			case 5:
				return randomGemstone.getShieldBonus();
			case 6:
				return randomGemstone.getBowBonus();
			default:
				return null;
		}
	}

	@Override
	public void appendHoverText(ItemStack itemStack, Level level, List<Component> components, TooltipFlag tooltipFlag) {
		components.add(Component.translatable(getDescriptionId() + ".tooltip").withStyle(ChatFormatting.DARK_PURPLE).withStyle(ChatFormatting.ITALIC));
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public MutableComponent applyGemstoneColorStyle(Component component) {
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
}
