package daripher.skilltree.item.gem;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Triple;

import daripher.skilltree.api.PSTPlayer;
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
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.RegistryObject;

public class IrisciteItem extends GemItem {
	private static final List<Triple<Attribute, Double, Operation>> GEM_BONUSES_CACHE = new ArrayList<>();
	
	public IrisciteItem() {
		super(0);
	}

	@Override
	public Triple<Attribute, Double, Operation> getGemBonus(Player player, ItemStack itemStack) {
		var random = createInsertionRandomSource(player, itemStack);
		if(GEM_BONUSES_CACHE.isEmpty()) {
			SkillTreeItems.REGISTRY.getEntries().stream()
				.map(RegistryObject::get)
				.filter(SimpleGemItem.class::isInstance)
				.map(SimpleGemItem.class::cast)
				.map(SimpleGemItem::getBonuses)
				.forEach(GEM_BONUSES_CACHE::addAll);
		}
		return GEM_BONUSES_CACHE.get(random.nextInt(GEM_BONUSES_CACHE.size()));
	}

	protected RandomSource createInsertionRandomSource(Player player, ItemStack itemStack) {
		var random = RandomSource.create();
		var randomSeed = ((PSTPlayer) player).getIrisciteSeed();
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

	@Override
	protected void appenBonusesTooltip(List<Component> components) {
		var slotTooltip = Component.translatable("gem.slot.anything").withStyle(ChatFormatting.GOLD);
		var bonusTooltip = Component.translatable(getDescriptionId() + ".tooltip").withStyle(ChatFormatting.BLUE);
		components.add(slotTooltip.append(bonusTooltip));
	}
}
