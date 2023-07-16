package daripher.skilltree.item.gem;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;

import daripher.skilltree.compat.apotheosis.ApotheosisCompatibility;
import daripher.skilltree.gem.GemHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;

public abstract class GemItem extends Item {
	private final int color;

	public GemItem(int color) {
		super(new Properties().tab(CreativeModeTab.TAB_MATERIALS));
		this.color = color;
	}

	@Override
	public MutableComponent getName(ItemStack itemStack) {
		var name = Component.empty().append(super.getName(itemStack));
		var coloredName = DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> applyGemstoneColorStyle(name));
		return coloredName == null ? name : coloredName;
	}

	@OnlyIn(Dist.CLIENT)
	public MutableComponent applyGemstoneColorStyle(MutableComponent name) {
		return name.withStyle(Style.EMPTY.withColor(color));
	}

	@Override
	public void appendHoverText(ItemStack itemStack, Level level, List<Component> components, TooltipFlag tooltipFlag) {
		components.add(Component.empty());
		if (ModList.get().isLoaded("apotheosis")) {
			if (ApotheosisCompatibility.ISNTANCE.adventureModuleEnabled()) {
				components.add(Component.translatable("gem.disabled").withStyle(ChatFormatting.RED));
				return;
			}
		}
		var slotsTooltip = Component.translatable("gem.slots").withStyle(Style.EMPTY.withColor(0x0AFF0A));
		components.add(slotsTooltip);
		appenBonusesTooltip(components);
	}

	public boolean canInsertInto(Player currentPlayer, ItemStack baseItem, int gemstoneSlot) {
		return !GemHelper.hasGem(baseItem, gemstoneSlot);
	}

	public void insertInto(Player player, ItemStack itemStack, int gemSlot, double gemPower) {
		GemHelper.insertGem(player, itemStack, this, gemSlot, gemPower);
	}

	public abstract Optional<Pair<Attribute, AttributeModifier>> getGemBonus(Player player, ItemStack itemStack);

	protected abstract void appenBonusesTooltip(List<Component> components);
}
