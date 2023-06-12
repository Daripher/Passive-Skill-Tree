package daripher.skilltree.item.gem;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Triple;

import daripher.skilltree.gem.GemHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
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
	private final int gemstoneColor;

	public GemItem(int gemstoneColor) {
		super(new Properties().tab(CreativeModeTab.TAB_MATERIALS));
		this.gemstoneColor = gemstoneColor;
	}

	@Override
	public MutableComponent getName(ItemStack itemStack) {
		var name = Component.empty().append(super.getName(itemStack));
		var coloredName = DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> applyGemstoneColorStyle(name));
		return coloredName == null ? name : coloredName;
	}

	@OnlyIn(Dist.CLIENT)
	public MutableComponent applyGemstoneColorStyle(MutableComponent name) {
		return name.withStyle(Style.EMPTY.withColor(gemstoneColor));
	}

	@Override
	public void appendHoverText(ItemStack itemStack, Level level, List<Component> components, TooltipFlag tooltipFlag) {
		if (ModList.get().isLoaded("apotheosis")) {
			components.add(Component.translatable("gemstone.disabled").withStyle(ChatFormatting.RED));
			return;
		}
		var slotDescriptionLines = Component.translatable(getDescriptionId() + ".slots").getString().split("/n");
		Arrays.asList(slotDescriptionLines).stream().map(Component::literal).map(this::applySlotDescriptionStyle).forEach(components::add);
		appenBonusesTooltip(components);
	}

	private MutableComponent applySlotDescriptionStyle(MutableComponent component) {
		return component.withStyle(Style.EMPTY.withColor(0x0AFF0A));
	}

	public boolean canInsertInto(Player currentPlayer, ItemStack baseItem, int gemstoneSlot) {
		return !GemHelper.hasGem(baseItem, gemstoneSlot);
	}

	public void insertInto(Player player, ItemStack itemStack, int gemSlot, double gemPower) {
		GemHelper.insertGem(player, itemStack, this, gemSlot, gemPower);
	}

	public static Triple<Attribute, Double, Operation> getAttributeBonus(ItemStack itemStack, int gemstoneSlot) {
		if (!GemHelper.hasGem(itemStack, gemstoneSlot)) {
			return null;
		}
		var attributeBonus = GemHelper.getAttributeBonus(itemStack, gemstoneSlot);
		return attributeBonus;
	}

	public abstract @Nullable Triple<Attribute, Double, Operation> getGemBonus(Player player, ItemStack itemStack);

	protected abstract void appenBonusesTooltip(List<Component> components);
}
