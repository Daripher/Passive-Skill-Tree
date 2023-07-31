package daripher.skilltree.item.gem;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;

import daripher.skilltree.api.IrisciteSeedContainer;
import daripher.skilltree.init.PSTItems;
import daripher.skilltree.item.ItemHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;

public class IrisciteItem extends GemItem {
	private static final List<Pair<Attribute, AttributeModifier>> SIMPLE_GEM_BONUSES = new ArrayList<>();

	@Override
	public Optional<Pair<Attribute, AttributeModifier>> getGemBonus(Player player, ItemStack itemStack) {
		RandomSource random = createRandomSource(player, itemStack);
		if (SIMPLE_GEM_BONUSES.isEmpty()) initSimpleGemBonuses();
		return Optional.of(SIMPLE_GEM_BONUSES.get(random.nextInt(SIMPLE_GEM_BONUSES.size())));
	}

	@Override
	public boolean canInsertInto(Player player, ItemStack stack, int socket) {
		if (!ItemHelper.isRing(stack) && !ItemHelper.isNecklace(stack)) return false;
		return super.canInsertInto(player, stack, socket);
	}

	protected void initSimpleGemBonuses() {
		// formatter:off
		PSTItems.REGISTRY.getEntries().stream()
			.map(RegistryObject::get)
			.filter(SimpleGemItem.class::isInstance)
			.map(SimpleGemItem.class::cast)
			.map(SimpleGemItem::getBonuses)
			.forEach(SIMPLE_GEM_BONUSES::addAll);
		// formatter:on
	}

	protected RandomSource createRandomSource(Player player, ItemStack itemStack) {
		RandomSource random = RandomSource.create();
		int seed = ((IrisciteSeedContainer) player).getIrisciteSeed();
		int slot = Player.getEquipmentSlotForItem(itemStack).ordinal();
		random.setSeed(seed + slot);
		return random;
	}

	@Override
	protected void appendBonusesTooltip(List<Component> components) {
		MutableComponent slot = Component.translatable("gem_class.jewelry").withStyle(ChatFormatting.GRAY);
		MutableComponent bonus = Component.translatable(getDescriptionId() + ".tooltip").withStyle(ChatFormatting.BLUE);
		components.add(slot.append(bonus));
	}
}
