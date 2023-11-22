package daripher.skilltree.item.gem;

import com.mojang.datafixers.util.Either;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.client.tooltip.SocketTooltipRenderer;
import daripher.skilltree.client.tooltip.SocketTooltipRenderer.SocketComponent;
import daripher.skilltree.compat.apotheosis.ApotheosisCompatibility;
import daripher.skilltree.config.Config;
import daripher.skilltree.init.PSTAttributes;
import daripher.skilltree.item.ItemHelper;
import daripher.skilltree.skill.bonus.player.AttributeBonus;
import daripher.skilltree.skill.bonus.SkillBonus;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
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
    Player player = event.getPlayer();
    if (player.isCreative()) return;
    Level level = player.getLevel();
    if (level.isClientSide) return;
    double dropChance = Config.gem_drop_chance;
    dropChance += player.getAttributeValue(PSTAttributes.GEM_DROP_CHANCE.get()) - 1;
    if (dropChance == 0) return;
    BlockPos blockPos = event.getPos();
    boolean isOre = level.getBlockState(blockPos).is(Tags.Blocks.ORES);
    if (!isOre) return;
    if (player.getRandom().nextFloat() >= dropChance) return;
    boolean usingCorrectTool = ForgeHooks.isCorrectToolForDrops(event.getState(), player);
    if (!usingCorrectTool) return;
    boolean hasSilkTouch =
        player.getMainHandItem().getEnchantmentLevel(Enchantments.SILK_TOUCH) > 0;
    if (hasSilkTouch) return;
    if (ModList.get().isLoaded("apotheosis")) {
      if (ApotheosisCompatibility.INSTANCE.adventureModuleEnabled()) {
        ApotheosisCompatibility.INSTANCE.dropGemFromOre(player, (ServerLevel) level, blockPos);
        return;
      }
    }
    ServerLevel serverLevel = (ServerLevel) level;
    LootTable lootTable =
        serverLevel
            .getServer()
            .getLootTables()
            .get(new ResourceLocation(SkillTreeMod.MOD_ID, "gems"));
    LootContext lootContext =
        new LootContext.Builder(serverLevel)
            .withParameter(LootContextParams.BLOCK_STATE, event.getState())
            .withParameter(
                LootContextParams.ORIGIN,
                new Vec3(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ()))
            .withParameter(LootContextParams.TOOL, player.getMainHandItem())
            .withLuck(player.getLuck())
            .create(LootContextParamSets.BLOCK);
    lootTable.getRandomItems(lootContext).forEach(item -> Block.popResource(level, blockPos, item));
  }

  private static void applyGemBonus(ItemAttributeModifierEvent event, int socket) {
    ItemStack itemStack = event.getItemStack();
    if (itemStack.getItem() instanceof ICurioItem) return;
    EquipmentSlot slot = ItemHelper.getSlotForItem(itemStack);
    if (slot != event.getSlotType()) return;
    SkillBonus<?> bonus = GemHelper.getBonus(itemStack, socket);
    if (bonus instanceof AttributeBonus attributeBonus) {
      event.addModifier(attributeBonus.getAttribute(), attributeBonus.getModifier());
    }
  }

  private static void applyGemBonus(CurioAttributeModifierEvent event, int socket) {
    ItemStack itemStack = event.getItemStack();
    if (!(itemStack.getItem() instanceof ICurioItem curio)) return;
    if (!curio.canEquip(event.getSlotContext(), itemStack)) return;
    SkillBonus<?> bonus = GemHelper.getBonus(itemStack, socket);
    if (bonus instanceof AttributeBonus attributeBonus) {
      event.addModifier(attributeBonus.getAttribute(), attributeBonus.getModifier());
    }
  }

  private static void removeGemTooltip(ItemTooltipEvent event, int socket) {
    ItemStack stack = event.getItemStack();
    Optional<GemItem> gem = GemHelper.getGem(stack, socket);
    if (gem.isEmpty()) return;
    SkillBonus<?> bonus = GemHelper.getBonus(stack, socket);
    if (bonus instanceof AttributeBonus) {
      removeTooltip(event.getToolTip(), bonus.getTooltip());
    }
  }

  private static void removeTooltip(List<Component> tooltips, MutableComponent tooltip) {
    Iterator<Component> iterator = tooltips.iterator();
    while (iterator.hasNext()) {
      if (!iterator.next().getString().equals(tooltip.getString())) continue;
      iterator.remove();
      break;
    }
  }

  @EventBusSubscriber(modid = SkillTreeMod.MOD_ID, bus = Bus.MOD)
  public static class ModEvents {
    @SubscribeEvent
    public static void registerGemTooltipComponent(
        RegisterClientTooltipComponentFactoriesEvent event) {
      event.register(SocketComponent.class, SocketTooltipRenderer::new);
    }
  }
}
