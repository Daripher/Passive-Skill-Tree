package daripher.skilltree.item.gem;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;

import daripher.skilltree.api.PSTPlayer;
import daripher.skilltree.init.SkillTreeItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.RegistryObject;

public class IrisciteItem extends GemItem {
	private static final List<Pair<Attribute, AttributeModifier>> SIMPLE_GEM_BONUSES = new ArrayList<>();

	public IrisciteItem() {
		super(0);
	}

	@Override
	public Optional<Pair<Attribute, AttributeModifier>> getGemBonus(Player player, ItemStack itemStack) {
		RandomSource random = createRandomSource(player, itemStack);
		if (SIMPLE_GEM_BONUSES.isEmpty()) initSimpleGemBonuses();
		return Optional.of(SIMPLE_GEM_BONUSES.get(random.nextInt(SIMPLE_GEM_BONUSES.size())));
	}

	protected void initSimpleGemBonuses() {
		// formatter:off
		SkillTreeItems.REGISTRY.getEntries().stream()
			.map(RegistryObject::get)
			.filter(SimpleGemItem.class::isInstance)
			.map(SimpleGemItem.class::cast)
			.map(SimpleGemItem::getBonuses)
			.forEach(SIMPLE_GEM_BONUSES::addAll);
		// formatter:on
	}

	protected RandomSource createRandomSource(Player player, ItemStack itemStack) {
		RandomSource random = RandomSource.create();
		int seed = ((PSTPlayer) player).getIrisciteSeed();
		int slot = Player.getEquipmentSlotForItem(itemStack).ordinal();
		random.setSeed(seed + slot);
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

	@Override
	protected void appenBonusesTooltip(List<Component> components) {
		var slotTooltip = Component.translatable("gem.slot.anything").withStyle(ChatFormatting.GOLD);
		var bonusTooltip = Component.translatable(getDescriptionId() + ".tooltip").withStyle(ChatFormatting.BLUE);
		components.add(slotTooltip.append(bonusTooltip));
	}
}
