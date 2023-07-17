package daripher.skilltree.gem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.compat.apotheosis.ApotheosisCompatibility;
import daripher.skilltree.config.Config;
import daripher.skilltree.init.SkillTreeAttributes;
import daripher.skilltree.item.ItemHelper;
import daripher.skilltree.item.gem.GemItem;
import daripher.skilltree.util.TooltipHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
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
	public static void applyGemBonuses(ItemAttributeModifierEvent event) {
		if (!ItemHelper.canInsertGem(event.getItemStack())) return;
		int socket = 0;
		while (GemHelper.hasGem(event.getItemStack(), socket)) {
			applyGemBonus(event, socket);
			socket++;
		}
	}

	@SubscribeEvent
	public static void addGemTooltips(ItemTooltipEvent event) {
		if (!ItemHelper.canInsertGem(event.getItemStack())) return;
		event.getToolTip().add(Component.empty());
		int socket = 0;
		while (GemHelper.hasGem(event.getItemStack(), socket)) {
			addGemTooltip(event, socket);
			socket++;
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
		// formatter:off
		var lootContext = new LootContext.Builder(serverLevel)
				.withParameter(LootContextParams.BLOCK_STATE, event.getState())
				.withParameter(LootContextParams.ORIGIN, new Vec3(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ()))
				.withParameter(LootContextParams.TOOL, player.getMainHandItem())
				.withLuck(player.getLuck())
				.create(LootContextParamSets.BLOCK);
		// formatter:on
		lootTable.getRandomItems(lootContext).forEach(item -> Block.popResource(level, blockPos, item));
	}

	private static void applyGemBonus(ItemAttributeModifierEvent event, int socket) {
		ItemStack itemStack = event.getItemStack();
		EquipmentSlot slot = ItemHelper.getSlotForItem(itemStack);
		if (slot != event.getSlotType()) return;
		Optional<Pair<Attribute, AttributeModifier>> optionalBonus = GemHelper.getAttributeBonus(itemStack, socket);
		if (!optionalBonus.isPresent()) return;
		Pair<Attribute, AttributeModifier> bonus = optionalBonus.get();
		AttributeModifier modifier = getAttributeModifier(socket, bonus, event.getSlotType());
		event.addModifier(bonus.getLeft(), modifier);
	}

	public static void addEmptySocketsTooltip(ItemTooltipEvent event) {
		if (!ItemHelper.canInsertGem(event.getItemStack())) return;
		var emptySockets = GemHelper.getEmptySockets(event.getItemStack(), event.getEntity());
		for (int i = 0; i < emptySockets; i++) {
			event.getToolTip().add(Component.translatable("gem.socket").withStyle(ChatFormatting.DARK_GRAY));
		}
	}

	private static void addGemTooltip(ItemTooltipEvent event, int socket) {
		ItemStack itemStack = event.getItemStack();
		Optional<GemItem> optionalGem = GemHelper.getGem(itemStack, socket);
		if (optionalGem.isEmpty()) return;
		GemItem gem = optionalGem.get();
		Optional<Pair<Attribute, AttributeModifier>> optionalBonus = GemHelper.getAttributeBonus(itemStack, socket);
		if (!optionalBonus.isPresent()) return;
		Pair<Attribute, AttributeModifier> bonus = optionalBonus.get();
		if (socket > 0) {
			for (int i = socket - 1; i >= 0; i--) {
				if (sameBonuses(itemStack, gem, bonus, i)) return;
			}
		}
		removeTooltip(event.getToolTip(), TooltipHelper.getAttributeBonusTooltip(bonus));
		int sameGemsCount = 1;
		for (int i = socket + 1; i < 4; i++) {
			if (sameBonuses(itemStack, gem, bonus, i)) {
				Pair<Attribute, AttributeModifier> secondBonus = GemHelper.getAttributeBonus(itemStack, i).get();
				removeTooltip(event.getToolTip(), TooltipHelper.getAttributeBonusTooltip(secondBonus));
				bonus = sumBonuses(bonus, secondBonus);
				sameGemsCount++;
			}
		}
		MutableComponent gemBonusTooltip = TooltipHelper.getAttributeBonusTooltip(bonus);
		MutableComponent gemName = gem.getName(new ItemStack(gem));
		if (sameGemsCount > 1) {
			gemName.append(" x" + sameGemsCount);
		}
		gemName.append(":");
		gemName = gem.applyGemstoneColorStyle(gemName);
		event.getToolTip().add(gemName);
		event.getToolTip().add(gemBonusTooltip);
	}

	protected static Pair<Attribute, AttributeModifier> sumBonuses(Pair<Attribute, AttributeModifier> bonus, Pair<Attribute, AttributeModifier> secondBonus) {
		double summedAmounts = bonus.getRight().getAmount() + secondBonus.getRight().getAmount();
		return Pair.of(bonus.getLeft(), new AttributeModifier("Gem Bonus", summedAmounts, bonus.getRight().getOperation()));
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

	protected static boolean sameBonuses(@NotNull ItemStack stack, GemItem gem, Pair<Attribute, AttributeModifier> bonus, int socket) {
		if (!GemHelper.getGem(stack, socket).filter(gem::equals).isPresent()) return false;
		Optional<Pair<Attribute, AttributeModifier>> secondBonus = GemHelper.getAttributeBonus(stack, socket);
		if (!secondBonus.isPresent()) return false;
		if (!sameBonus(bonus, secondBonus.get())) return false;
		return true;
	}

	private static boolean sameBonus(Pair<Attribute, AttributeModifier> first, Pair<Attribute, AttributeModifier> second) {
		return first.getLeft() == second.getLeft() && first.getRight().getOperation() == second.getRight().getOperation();
	}

	public static AttributeModifier getAttributeModifier(int socket, Pair<Attribute, AttributeModifier> bonus, EquipmentSlot slot) {
		UUID modifierId = UUID.fromString(MODIFIER_IDS.get(slot)[socket]);
		return new AttributeModifier(modifierId, "Gem Bonus", bonus.getRight().getAmount(), bonus.getRight().getOperation());
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
