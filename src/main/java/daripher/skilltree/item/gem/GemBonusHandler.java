package daripher.skilltree.item.gem;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang3.tuple.Pair;

import com.mojang.datafixers.util.Either;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.client.tooltip.SocketTooltipRenderer;
import daripher.skilltree.client.tooltip.SocketTooltipRenderer.SocketComponent;
import daripher.skilltree.compat.apotheosis.ApotheosisCompatibility;
import daripher.skilltree.config.Config;
import daripher.skilltree.init.PSTAttributes;
import daripher.skilltree.item.ItemHelper;
import daripher.skilltree.util.TooltipHelper;
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
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class GemBonusHandler {
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
	public static void applyGemBonuses(CurioAttributeModifierEvent event) {
		if (!ItemHelper.canInsertGem(event.getItemStack())) return;
		int socket = 0;
		while (GemHelper.hasGem(event.getItemStack(), socket)) {
			applyGemBonus(event, socket);
			socket++;
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void removeDuplicateTooltips(ItemTooltipEvent event) {
		if (!ItemHelper.canInsertGem(event.getItemStack())) return;
		int socket = 0;
		while (GemHelper.hasGem(event.getItemStack(), socket)) {
			removeGemTooltip(event, socket);
			socket++;
		}
	}

	@SubscribeEvent
	public static void addGemTooltipComponent(RenderTooltipEvent.GatherComponents event) {
		if (!ItemHelper.canInsertGem(event.getItemStack())) return;
		event.getTooltipElements().add(Either.right(new SocketComponent(event.getItemStack())));
	}

	@SubscribeEvent
	public static void dropGemFromOre(BlockEvent.BreakEvent event) {
		var player = event.getPlayer();
		if (player.isCreative()) return;
		var level = player.getLevel();
		if (level.isClientSide) return;
		var dropChance = Config.COMMON.getGemDropChance();
		dropChance += player.getAttributeValue(PSTAttributes.GEM_DROP_CHANCE.get()) - 1;
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
		if (itemStack.getItem() instanceof ICurioItem) return;
		EquipmentSlot slot = ItemHelper.getSlotForItem(itemStack);
		if (slot != event.getSlotType()) return;
		Optional<Pair<Attribute, AttributeModifier>> bonus = GemHelper.getAttributeBonus(itemStack, socket);
		if (!bonus.isPresent()) return;
		event.addModifier(bonus.get().getLeft(), bonus.get().getRight());
	}

	private static void applyGemBonus(CurioAttributeModifierEvent event, int socket) {
		ItemStack itemStack = event.getItemStack();
		if (!(itemStack.getItem() instanceof ICurioItem curio)) return;
		if (!curio.canEquip(event.getSlotContext(), itemStack)) return;
		Optional<Pair<Attribute, AttributeModifier>> bonus = GemHelper.getAttributeBonus(itemStack, socket);
		if (!bonus.isPresent()) return;
		event.addModifier(bonus.get().getLeft(), bonus.get().getRight());
	}

	private static void removeGemTooltip(ItemTooltipEvent event, int socket) {
		ItemStack stack = event.getItemStack();
		Optional<GemItem> gem = GemHelper.getGem(stack, socket);
		if (gem.isEmpty()) return;
		Optional<Pair<Attribute, AttributeModifier>> bonus = GemHelper.getAttributeBonus(stack, socket);
		if (!bonus.isPresent()) return;
		removeTooltip(event.getToolTip(), TooltipHelper.getAttributeBonusTooltip(bonus.get(), false));
	}

	private static void removeTooltip(List<Component> tooltips, MutableComponent tooltip) {
		Iterator<Component> iterator = tooltips.iterator();
		while (iterator.hasNext()) {
			if (!iterator.next().getString().equals(tooltip.getString())) continue;
			iterator.remove();
			break;
		}
	}

	protected static String getSlotId(Either<EquipmentSlot, SlotContext> slot) {
		AtomicReference<String> slotId = new AtomicReference<>();
		slot.ifLeft(s -> slotId.set(s.getName())).ifRight(s -> slotId.set(s.identifier()));
		return slotId.get();
	}

	@EventBusSubscriber(modid = SkillTreeMod.MOD_ID, bus = Bus.MOD)
	public static class ModEvents {
		@SubscribeEvent
		public static void registerGemTooltipComponent(RegisterClientTooltipComponentFactoriesEvent event) {
			event.register(SocketComponent.class, SocketTooltipRenderer::new);
		}
	}
}
