package daripher.skilltree.gem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.compat.apotheosis.ApotheosisCompatibility;
import daripher.skilltree.config.Config;
import daripher.skilltree.init.SkillTreeAttributes;
import daripher.skilltree.item.gem.GemItem;
import daripher.skilltree.util.ItemHelper;
import daripher.skilltree.util.TooltipHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class GemBonusHandler {
	private static final Map<EquipmentSlot, String[]> MODIFIER_IDS = new HashMap<>();

	@SubscribeEvent
	public static void applyGemstoneBonuses(ItemAttributeModifierEvent event) {
		if (!ItemHelper.canInsertGem(event.getItemStack())) {
			return;
		}
		var gemstoneSlot = 0;
		while (GemHelper.hasGem(event.getItemStack(), gemstoneSlot)) {
			applyGemstoneAttributeModifier(event, gemstoneSlot);
			gemstoneSlot++;
		}
	}

	@SubscribeEvent
	public static void addGemstoneTooltips(ItemTooltipEvent event) {
		if (!ItemHelper.canInsertGem(event.getItemStack())) {
			return;
		}
		event.getToolTip().add(Component.empty());
		var gemstoneSlot = 0;
		while (GemHelper.hasGem(event.getItemStack(), gemstoneSlot)) {
			addGemstoneTooltip(event, gemstoneSlot);
			gemstoneSlot++;
		}
		addEmptySocketsTooltip(event);
	}

	@SubscribeEvent
	public static void dropGemFromOre(BlockEvent.BreakEvent event) {
		var player = event.getPlayer();
		if (player.isCreative()) return;
		var level = player.getLevel();
		if (level.isClientSide) return;
		var dropChance = Config.COMMON_CONFIG.getGemDropChance();
		dropChance += player.getAttributeValue(SkillTreeAttributes.GEM_DROP_CHANCE.get()) - 1;
		if (dropChance == 0) return;
		var blockPos = event.getPos();
		var isOre = level.getBlockState(blockPos).is(Tags.Blocks.ORES);
		if (!isOre) return;
		if (player.getRandom().nextFloat() >= dropChance) return;
		var usingCorrectTool = ForgeHooks.isCorrectToolForDrops(event.getState(), player);
		if (!usingCorrectTool) return;
		var hasSilkTouch = player.getMainHandItem().getEnchantmentLevel(Enchantments.SILK_TOUCH) > 0;
		if (hasSilkTouch) return;
		if (ModList.get().isLoaded("apotheosis")) {
			if (ApotheosisCompatibility.ISNTANCE.adventureModuleEnabled()) {
				ApotheosisCompatibility.ISNTANCE.dropGemFromOre(player, (ServerLevel) level, blockPos);
				return;
			}
		}
		var serverLevel = (ServerLevel) level;
		var lootTable = serverLevel.getServer().getLootTables().get(new ResourceLocation(SkillTreeMod.MOD_ID, "gems"));
		var lootContext = new LootContext.Builder(serverLevel)
				.withParameter(LootContextParams.BLOCK_STATE, event.getState())
				.withParameter(LootContextParams.ORIGIN, new Vec3(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ()))
				.withParameter(LootContextParams.TOOL, player.getMainHandItem())
				.withLuck(player.getLuck())
				.create(LootContextParamSets.BLOCK);
		lootTable.getRandomItems(lootContext).forEach(item -> Block.popResource(level, blockPos, item));
	}

	private static void applyGemstoneAttributeModifier(ItemAttributeModifierEvent event, int gemstoneSlot) {
		var itemStack = event.getItemStack();
		var itemSlot = ItemHelper.getSlotForItem(itemStack);
		if (itemSlot != event.getSlotType())
			return;
		var attributeBonus = GemItem.getAttributeBonus(itemStack, gemstoneSlot);
		var attribute = attributeBonus.getLeft();
		var attributeModifier = getAttributeModifier(gemstoneSlot, attributeBonus, event.getSlotType());
		event.addModifier(attribute, attributeModifier);
	}

	public static void addEmptySocketsTooltip(ItemTooltipEvent event) {
		if (!ItemHelper.canInsertGem(event.getItemStack())) {
			return;
		}
		var emptySockets = GemHelper.getEmptySockets(event.getItemStack(), event.getEntity());
		for (var i = 0; i < emptySockets; i++) {
			event.getToolTip().add(Component.translatable("gem.socket").withStyle(ChatFormatting.DARK_GRAY));
		}
	}

	private static void addGemstoneTooltip(ItemTooltipEvent event, int gemstoneSlot) {
		var itemStack = event.getItemStack();
		var gemstoneItem = GemHelper.getGem(itemStack, gemstoneSlot);
		var gemstoneBonus = GemItem.getAttributeBonus(itemStack, gemstoneSlot);
		if (gemstoneSlot > 0) {
			for (var i = gemstoneSlot - 1; i >= 0; i--) {
				if (areGemstonesAndBonusesSame(itemStack, gemstoneItem, gemstoneBonus, i)) {
					return;
				}
			}
		}
		removeTooltip(event.getToolTip(), TooltipHelper.getAttributeBonusTooltip(gemstoneBonus));
		var sameGemstonesCount = 1;
		for (var i = gemstoneSlot + 1; i < 4; i++) {
			if (areGemstonesAndBonusesSame(itemStack, gemstoneItem, gemstoneBonus, i)) {
				var secondBonus = GemItem.getAttributeBonus(itemStack, i);
				removeTooltip(event.getToolTip(), TooltipHelper.getAttributeBonusTooltip(secondBonus));
				var summedAmounts = gemstoneBonus.getMiddle() + secondBonus.getMiddle();
				gemstoneBonus = Triple.of(gemstoneBonus.getLeft(), summedAmounts, gemstoneBonus.getRight());
				sameGemstonesCount++;
			}
		}
		var gemstoneBonusTooltip = TooltipHelper.getAttributeBonusTooltip(gemstoneBonus);
		var gemstoneName = gemstoneItem.getName(new ItemStack(gemstoneItem));
		if (sameGemstonesCount > 1) {
			gemstoneName.append(" x" + sameGemstonesCount);
		}
		gemstoneName.append(":");
		gemstoneName = gemstoneItem.applyGemstoneColorStyle(gemstoneName);
		event.getToolTip().add(gemstoneName);
		event.getToolTip().add(gemstoneBonusTooltip);
	}

	private static void removeTooltip(List<Component> tooltips, MutableComponent tooltip) {
		var tooltipsIterator = tooltips.iterator();
		while (tooltipsIterator.hasNext()) {
			if (!tooltipsIterator.next().equals(tooltip)) {
				continue;
			}
			tooltipsIterator.remove();
			break;
		}
	}

	protected static boolean areGemstonesAndBonusesSame(@NotNull ItemStack itemStack, GemItem gemstoneItem, Triple<Attribute, Double, Operation> gemstoneBonus, int gemstoneSlot) {
		if (GemHelper.getGem(itemStack, gemstoneSlot) != gemstoneItem) {
			return false;
		}
		if (!isSameBonus(gemstoneBonus, GemItem.getAttributeBonus(itemStack, gemstoneSlot))) {
			return false;
		}
		return true;
	}

	private static boolean isSameBonus(Triple<Attribute, Double, Operation> first, Triple<Attribute, Double, Operation> second) {
		if (first.equals(second)) {
			return true;
		}
		if (first.getLeft() == second.getLeft() && first.getRight() == second.getRight()) {
			return true;
		}
		return false;
	}

	public static AttributeModifier getAttributeModifier(int gemstoneSlot, Triple<Attribute, Double, Operation> attributeBonus, EquipmentSlot equipmentSlot) {
		var amount = attributeBonus.getMiddle();
		var operation = attributeBonus.getRight();
		var modifierId = UUID.fromString(MODIFIER_IDS.get(equipmentSlot)[gemstoneSlot]);
		return new AttributeModifier(modifierId, "Gemstone Bonus", amount, operation);
	}

	static {
		MODIFIER_IDS.put(EquipmentSlot.CHEST, new String[] {
			"13518502-5211-480b-a847-028791de292a",
			"fa4135ee-69d1-4e6d-9f12-d26a34f694ff",
			"9d89442a-2b8b-4512-91e5-a56b2e4673eb",
			"d17a0be7-0d41-4b58-9a23-30996eda86d7"
		});
		MODIFIER_IDS.put(EquipmentSlot.FEET, new String[] {
			"1829e876-fa03-42d3-8ad2-db138f4e7380",
			"2e040afb-c978-4906-bdfd-f909fd2813a1",
			"67276182-31f2-48df-b308-f4fe2d8d2560",
			"1d690ba4-716b-4071-a8e0-141ef403f945"
		});
		MODIFIER_IDS.put(EquipmentSlot.HEAD, new String[] {
			"cd455ae0-b90a-42ef-b014-e4585b8316a6",
			"0bdbc590-353c-4b94-8c31-0b8611f13371",
			"1a3f8247-7efc-4a85-b5be-896833374920",
			"816bff45-9044-4610-9585-c48526b71fac"
		});
		MODIFIER_IDS.put(EquipmentSlot.LEGS, new String[] {
			"d5cd2cf4-b0f7-448c-afee-4f3772b36c52",
			"c82520e3-e1c2-49f2-b56d-6ce8823558ec",
			"25d82da0-d30a-4e53-98bb-c22717b0b289",
			"de9d7b3d-e8d2-4e3b-8125-2ec83b3a0d63"
		});
		MODIFIER_IDS.put(EquipmentSlot.MAINHAND, new String[] {
			"99c5a016-4224-4395-8f21-3a200f02325b",
			"80e663e7-fb40-4b72-8774-34ee86ba2635",
			"bfb3aa0b-3853-4fa0-a996-5bc3a861891a",
			"12ac91c2-a41b-4073-9e8e-572453d9463d"
		});
		MODIFIER_IDS.put(EquipmentSlot.OFFHAND, new String[] {
			"e53552dd-4468-41e9-bae8-b8a23c3bcce1",
			"ff4c3814-1c62-4de9-966b-3f8381f2faf4",
			"30fc1b4e-84ca-4ca3-a64e-1ce02cf3a49b",
			"f443fe93-8c93-4433-b3a9-aa6bb15bc0eb"
		});
	}
}
