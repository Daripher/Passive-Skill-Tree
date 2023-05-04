package daripher.skilltree.gemstone;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.tuple.Triple;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.init.SkillTreeItems;
import daripher.skilltree.util.GemstoneHelper;
import daripher.skilltree.util.ItemHelper;
import daripher.skilltree.util.TooltipHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class GemstoneBonusHandler {
	private static final Map<EquipmentSlot, String[]> MODIFIER_IDS = new HashMap<>();

	static {
		MODIFIER_IDS.put(EquipmentSlot.CHEST, new String[] { "13518502-5211-480b-a847-028791de292a", "fa4135ee-69d1-4e6d-9f12-d26a34f694ff", "9d89442a-2b8b-4512-91e5-a56b2e4673eb" });
		MODIFIER_IDS.put(EquipmentSlot.FEET, new String[] { "1829e876-fa03-42d3-8ad2-db138f4e7380", "2e040afb-c978-4906-bdfd-f909fd2813a1", "67276182-31f2-48df-b308-f4fe2d8d2560" });
		MODIFIER_IDS.put(EquipmentSlot.HEAD, new String[] { "cd455ae0-b90a-42ef-b014-e4585b8316a6", "0bdbc590-353c-4b94-8c31-0b8611f13371", "1a3f8247-7efc-4a85-b5be-896833374920" });
		MODIFIER_IDS.put(EquipmentSlot.LEGS, new String[] { "d5cd2cf4-b0f7-448c-afee-4f3772b36c52", "c82520e3-e1c2-49f2-b56d-6ce8823558ec", "25d82da0-d30a-4e53-98bb-c22717b0b289" });
		MODIFIER_IDS.put(EquipmentSlot.MAINHAND, new String[] { "99c5a016-4224-4395-8f21-3a200f02325b", "80e663e7-fb40-4b72-8774-34ee86ba2635", "bfb3aa0b-3853-4fa0-a996-5bc3a861891a" });
		MODIFIER_IDS.put(EquipmentSlot.OFFHAND, new String[] { "e53552dd-4468-41e9-bae8-b8a23c3bcce1", "ff4c3814-1c62-4de9-966b-3f8381f2faf4", "30fc1b4e-84ca-4ca3-a64e-1ce02cf3a49b" });
	}

	@SubscribeEvent
	public static void applyGemstoneBonuses(ItemAttributeModifierEvent event) {
		for (var i = 0; i < 3; i++) {
			applyGemstoneBonus(event, i);
		}
	}

	private static void applyGemstoneBonus(ItemAttributeModifierEvent event, int gemstoneSlot) {
		var itemStack = event.getItemStack();
		if (!GemstoneHelper.hasGemstone(itemStack, gemstoneSlot)) {
			return;
		}
		if (GemstoneHelper.hasRainbowGemstone(itemStack, gemstoneSlot)) {
			applyRainbowGemstoneBonus(event, gemstoneSlot, itemStack, event.getSlotType());
			return;
		}
		var gemstoneItem = GemstoneHelper.getGemstone(itemStack, gemstoneSlot);
		var attributeBonus = GemstoneHelper.getAttributeBonus(itemStack, gemstoneItem, event.getSlotType());
		if (attributeBonus == null) {
			return;
		}
		var gemstoneStrengthBonus = GemstoneHelper.getGemstoneStrength(itemStack, gemstoneSlot);
		var modifiedAttribute = attributeBonus.getLeft();
		var attributeModifier = getAttributeModifier(gemstoneSlot, attributeBonus, gemstoneStrengthBonus, event.getSlotType());
		event.addModifier(modifiedAttribute, attributeModifier);
	}

	public static void applyRainbowGemstoneBonus(ItemAttributeModifierEvent event, int gemstoneSlot, ItemStack itemStack, EquipmentSlot slotType) {
		if (!GemstoneHelper.isRainbowGemstoneBonusSet(itemStack, gemstoneSlot)) {
			return;
		}
		var attributeBonus = GemstoneHelper.getRainbowGemstoneBonus(itemStack, gemstoneSlot, slotType);
		if (attributeBonus == null) {
			return;
		}
		var gemstoneStrengthBonus = GemstoneHelper.getGemstoneStrength(itemStack, gemstoneSlot);
		var modifiedAttribute = attributeBonus.getLeft();
		var attributeModifier = getAttributeModifier(gemstoneSlot, attributeBonus, gemstoneStrengthBonus, event.getSlotType());
		event.addModifier(modifiedAttribute, attributeModifier);
	}

	@SubscribeEvent
	public static void addGemstoneTooltips(ItemTooltipEvent event) {
		for (var i = 0; i < 3; i++) {
			addGemstoneTooltip(event, i);
		}
		addEmptyGemstoneSlotsTooltip(event);
	}

	public static void addEmptyGemstoneSlotsTooltip(ItemTooltipEvent event) {
		if (!ItemHelper.canApplyGemstone(event.getItemStack())) {
			return;
		}
		var emptyGemstoneSlotsCount = GemstoneHelper.getEmptyGemstoneSlots(event.getItemStack(), event.getEntity());
		for (var i = 0; i < emptyGemstoneSlotsCount; i++) {
			event.getToolTip().add(Component.translatable("gemstone.empty").withStyle(ChatFormatting.DARK_GRAY));
		}
	}

	private static void addGemstoneTooltip(ItemTooltipEvent event, int gemstoneSlot) {
		var itemStack = event.getItemStack();
		if (!ItemHelper.canApplyGemstone(itemStack)) {
			return;
		}
		if (gemstoneSlot == 0) {
			event.getToolTip().add(Component.empty());
		}
		if (!GemstoneHelper.hasGemstone(itemStack, gemstoneSlot)) {
			return;
		}
		if (GemstoneHelper.hasRainbowGemstone(itemStack, gemstoneSlot)) {
			addRainbowGemstoneTooltip(event, gemstoneSlot);
			return;
		}
		var gemstoneItem = GemstoneHelper.getGemstone(itemStack, gemstoneSlot);
		var attributeBonus = GemstoneHelper.getAttributeBonus(itemStack, gemstoneItem);
		var gemstoneStrengthBonus = GemstoneHelper.getGemstoneStrength(itemStack, gemstoneSlot);
		var attributeBonusTooltip = TooltipHelper.getAttributeBonusTooltip(attributeBonus, gemstoneStrengthBonus);
		var tooltipLinesIterator = event.getToolTip().iterator();
		while (tooltipLinesIterator.hasNext()) {
			var tooltipLine = tooltipLinesIterator.next();
			if (tooltipLine.equals(attributeBonusTooltip)) {
				tooltipLinesIterator.remove();
				event.getToolTip().add(gemstoneItem.getName(new ItemStack(gemstoneItem)).append(":"));
				event.getToolTip().add(attributeBonusTooltip);
				break;
			}
		}
	}

	private static void addRainbowGemstoneTooltip(ItemTooltipEvent event, int gemstoneSlot) {
		var gemstoneItem = SkillTreeItems.RAINBOW_GEMSTONE.get();
		var itemStack = event.getItemStack();
		event.getToolTip().add(Component.empty().append(gemstoneItem.getName(new ItemStack(gemstoneItem))).append(":"));
		if (!GemstoneHelper.isRainbowGemstoneBonusSet(itemStack, gemstoneSlot)) {
			event.getToolTip().add(Component.translatable("gemstone.rainbow").withStyle(ChatFormatting.DARK_PURPLE).withStyle(ChatFormatting.ITALIC));
		} else {
			var attributeBonus = GemstoneHelper.getRainbowGemstoneBonus(itemStack, gemstoneSlot, Player.getEquipmentSlotForItem(itemStack));
			if (attributeBonus == null) {
				return;
			}
			var gemstoneStrengthBonus = GemstoneHelper.getGemstoneStrength(itemStack, gemstoneSlot);
			var attributeBonusTooltip = TooltipHelper.getAttributeBonusTooltip(attributeBonus, gemstoneStrengthBonus);
			var tooltipLinesIterator = event.getToolTip().iterator();
			while (tooltipLinesIterator.hasNext()) {
				var tooltipLine = tooltipLinesIterator.next();
				if (tooltipLine.equals(attributeBonusTooltip)) {
					tooltipLinesIterator.remove();
					event.getToolTip().add(attributeBonusTooltip);
					break;
				}
			}
		}
	}

	public static AttributeModifier getAttributeModifier(int gemstoneSlot, Triple<Attribute, Double, Operation> attributeBonus, double gemstoneStrengthBonus, EquipmentSlot equipmentSlot) {
		var modifierAmount = attributeBonus.getMiddle() * (1 + gemstoneStrengthBonus);
		var modifierOperation = attributeBonus.getRight();
		var modifierId = UUID.fromString(MODIFIER_IDS.get(equipmentSlot)[gemstoneSlot]);
		return new AttributeModifier(modifierId, "Gemstone Bonus", modifierAmount, modifierOperation);
	}
}
