package daripher.skilltree.gemstone;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.tuple.Triple;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.util.GemstoneHelper;
import daripher.skilltree.util.TooltipHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class GemstoneBonusHandler {
	private static final Map<EquipmentSlot, UUID[]> MODIFIER_IDS = new HashMap<>();

	static {
		MODIFIER_IDS.put(EquipmentSlot.CHEST, new UUID[] { UUID.fromString("13518502-5211-480b-a847-028791de292a"), UUID.fromString("fa4135ee-69d1-4e6d-9f12-d26a34f694ff") });
		MODIFIER_IDS.put(EquipmentSlot.FEET, new UUID[] { UUID.fromString("1829e876-fa03-42d3-8ad2-db138f4e7380"), UUID.fromString("2e040afb-c978-4906-bdfd-f909fd2813a1") });
		MODIFIER_IDS.put(EquipmentSlot.HEAD, new UUID[] { UUID.fromString("cd455ae0-b90a-42ef-b014-e4585b8316a6"), UUID.fromString("0bdbc590-353c-4b94-8c31-0b8611f13371") });
		MODIFIER_IDS.put(EquipmentSlot.LEGS, new UUID[] { UUID.fromString("d5cd2cf4-b0f7-448c-afee-4f3772b36c52"), UUID.fromString("c82520e3-e1c2-49f2-b56d-6ce8823558ec") });
		MODIFIER_IDS.put(EquipmentSlot.MAINHAND, new UUID[] { UUID.fromString("99c5a016-4224-4395-8f21-3a200f02325b"), UUID.fromString("80e663e7-fb40-4b72-8774-34ee86ba2635") });
		MODIFIER_IDS.put(EquipmentSlot.OFFHAND, new UUID[] { UUID.fromString("e53552dd-4468-41e9-bae8-b8a23c3bcce1"), UUID.fromString("ff4c3814-1c62-4de9-966b-3f8381f2faf4") });
	}

	@SubscribeEvent
	public static void applyGemstoneBonuses(ItemAttributeModifierEvent event) {
		applyGemstoneBonus(event, 0);
		applyGemstoneBonus(event, 1);
	}

	private static void applyGemstoneBonus(ItemAttributeModifierEvent event, int gemstoneSlot) {
		var itemStack = event.getItemStack();

		if (!GemstoneHelper.hasGemstone(itemStack, gemstoneSlot)) {
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

	@SubscribeEvent
	public static void addGemstoneTooltips(ItemTooltipEvent event) {
		addGemstoneTooltip(event, 0);
		addGemstoneTooltip(event, 1);
	}

	private static void addGemstoneTooltip(ItemTooltipEvent event, int gemstoneSlot) {
		var itemStack = event.getItemStack();

		if (!GemstoneHelper.hasGemstone(itemStack, gemstoneSlot)) {
			return;
		}
		
		var gemstoneItem = GemstoneHelper.getGemstone(itemStack, gemstoneSlot);
		var attributeBonus = GemstoneHelper.getAttributeBonus(itemStack, gemstoneItem);

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

				if (gemstoneSlot == 0) {
					event.getToolTip().add(Component.empty());
				}

				event.getToolTip().add(gemstoneItem.getName(new ItemStack(gemstoneItem)).append(":"));
				event.getToolTip().add(attributeBonusTooltip);
				break;
			}
		}
	}

	public static AttributeModifier getAttributeModifier(int gemstoneSlot, Triple<Attribute, Double, Operation> attributeBonus, double gemstoneStrengthBonus, EquipmentSlot equipmentSlot) {
		var modifierValue = attributeBonus.getMiddle() * (1 + gemstoneStrengthBonus);
		var modifierOperation = attributeBonus.getRight();
		return new AttributeModifier(MODIFIER_IDS.get(equipmentSlot)[gemstoneSlot], "Gemstone Bonus", modifierValue, modifierOperation);
	}
}
