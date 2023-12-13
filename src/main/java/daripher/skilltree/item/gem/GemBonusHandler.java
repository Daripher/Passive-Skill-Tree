package daripher.skilltree.item.gem;

import com.mojang.datafixers.util.Either;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.client.tooltip.SocketTooltipRenderer;
import daripher.skilltree.client.tooltip.SocketTooltipRenderer.SocketComponent;
import daripher.skilltree.compat.apotheosis.ApotheosisCompatibility;
import daripher.skilltree.config.Config;
import daripher.skilltree.data.SerializationHelper;
import daripher.skilltree.item.ItemHelper;
import daripher.skilltree.skill.bonus.SkillBonusHandler;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import daripher.skilltree.skill.bonus.item.ItemSkillBonus;
import daripher.skilltree.skill.bonus.player.AttributeBonus;
import daripher.skilltree.skill.bonus.player.GemPowerBonus;
import daripher.skilltree.skill.bonus.player.LootDuplicationBonus;
import daripher.skilltree.skill.bonus.player.PlayerSocketsBonus;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class GemBonusHandler {
  @SubscribeEvent(priority = EventPriority.LOWEST)
  public static void removeDuplicateTooltips(ItemTooltipEvent event) {
    ItemStack stack = event.getItemStack();
    if (!ItemHelper.canInsertGem(stack)) return;
    List<ItemStack> gems = getGems(stack);
    for (int i = 0; i < gems.size(); i++) {
      removeGemTooltip(event, i);
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
    if (player.isCreative() || player.level().isClientSide) return;
    ServerLevel serverLevel = (ServerLevel) player.level();
    if (Config.gem_drop_chance == 0) return;
    if (!serverLevel.getBlockState(event.getPos()).is(Tags.Blocks.ORES)) return;
    if (player.getRandom().nextFloat() >= Config.gem_drop_chance) return;
    if (!ForgeHooks.isCorrectToolForDrops(event.getState(), player)) return;
    if (player.getMainHandItem().getEnchantmentLevel(Enchantments.SILK_TOUCH) > 0) return;
    LootTable lootTable = getGemsLootTable(serverLevel);
    LootParams lootContext = createGemsLootContext(event, serverLevel, player);
    float multiplier =
        1f + SkillBonusHandler.getLootMultiplier(player, LootDuplicationBonus.LootType.GEMS);
    while (multiplier > 1) {
      dropGems(player, serverLevel, event.getPos(), lootTable, lootContext);
      multiplier--;
    }
    if (player.getRandom().nextFloat() < multiplier) {
      dropGems(player, serverLevel, event.getPos(), lootTable, lootContext);
    }
  }

  @NotNull
  private static LootTable getGemsLootTable(ServerLevel serverLevel) {
    return serverLevel
        .getServer()
        .getLootData()
        .getLootTable(new ResourceLocation(SkillTreeMod.MOD_ID, "gems"));
  }

  @NotNull
  private static LootParams createGemsLootContext(
      BlockEvent.BreakEvent event, ServerLevel serverLevel, Player player) {
    return new LootParams.Builder(serverLevel)
        .withParameter(LootContextParams.BLOCK_STATE, event.getState())
        .withParameter(
            LootContextParams.ORIGIN,
            new Vec3(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ()))
        .withParameter(LootContextParams.TOOL, player.getMainHandItem())
        .withLuck(player.getLuck())
        .create(LootContextParamSets.BLOCK);
  }

  private static void dropGems(
      Player player, Level level, BlockPos blockPos, LootTable lootTable, LootParams lootContext) {
    if (SkillTreeMod.apotheosisEnabled()) {
      ApotheosisCompatibility.INSTANCE.dropGemFromOre(player, (ServerLevel) level, blockPos);
      return;
    }
    lootTable.getRandomItems(lootContext).forEach(s -> Block.popResource(level, blockPos, s));
  }

  private static void removeGemTooltip(ItemTooltipEvent event, int socket) {
    ItemBonus<?> bonus = getBonuses(event.getItemStack()).get(socket);
    if (bonus instanceof ItemSkillBonus skillBonus
        && skillBonus.getBonus() instanceof AttributeBonus) {
      removeTooltip(event.getToolTip(), skillBonus.getTooltip());
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

  public static void insertGem(
      Player player, ItemStack stack, ItemStack gemStack, int socket, double power) {
    if (!(gemStack.getItem() instanceof GemItem gem)) {
      SkillTreeMod.LOGGER.error("Attempting to insert non-gem item into socket!");
      return;
    }
    CompoundTag gemTag = new CompoundTag();
    ListTag gemsTags = stack.getOrCreateTag().getList("Gemstones", Tag.TAG_COMPOUND);
    if (gemsTags.size() > socket) {
      gemTag = gemsTags.getCompound(socket);
    }
    ItemBonus<?> bonus = gem.getGemBonus(player, stack, gemStack);
    if (bonus == null) {
      SkillTreeMod.LOGGER.error("Attempting to add empty gem bonus!");
      return;
    }
    bonus = bonus.copy().multiply(power);
    gemTag.put("Gem", gemStack.save(new CompoundTag()));
    CompoundTag bonusTag = new CompoundTag();
    SerializationHelper.serializeItemBonus(bonusTag, bonus);
    gemTag.put("Bonus", bonusTag);
    gemsTags.add(socket, gemTag);
    stack.getOrCreateTag().put("Gemstones", gemsTags);
  }

  public static List<? extends ItemBonus<?>> getBonuses(ItemStack stack) {
    if (!stack.hasTag() || SkillTreeMod.apotheosisEnabled()) return List.of();
    return stack.getOrCreateTag().getList("Gemstones", Tag.TAG_COMPOUND).stream()
        .map(CompoundTag.class::cast)
        .map(t -> t.getCompound("Bonus"))
        .map(SerializationHelper::deserializeItemBonus)
        .toList();
  }

  public static List<ItemStack> getGems(ItemStack stack) {
    if (!stack.hasTag() || SkillTreeMod.apotheosisEnabled()) return List.of();
    return stack.getOrCreateTag().getList("Gemstones", Tag.TAG_COMPOUND).stream()
        .map(CompoundTag.class::cast)
        .map(t -> t.getCompound("Gem"))
        .map(ItemStack::of)
        .toList();
  }

  public static boolean hasGem(ItemStack stack, int socket) {
    return getGems(stack).size() > socket;
  }

  public static int getPlayerSockets(ItemStack stack, @Nonnull Player player) {
    return SkillBonusHandler.getSkillBonuses(player, PlayerSocketsBonus.class).stream()
        .filter(bonus -> bonus.getItemCondition().met(stack))
        .map(PlayerSocketsBonus::getSockets)
        .reduce(Integer::sum)
        .orElse(0);
  }

  public static float getGemPower(Player player, ItemStack stack) {
    float power = 1f;
    power +=
        SkillBonusHandler.getSkillBonuses(player, GemPowerBonus.class).stream()
            .filter(bonus -> bonus.getItemCondition().met(stack))
            .map(GemPowerBonus::getMultiplier)
            .reduce(Float::sum)
            .orElse(0f);
    return power;
  }

  public static int getFirstEmptySocket(ItemStack stack, Player player) {
    int sockets = ItemHelper.getMaximumSockets(stack, player);
    int socket = 0;
    for (int i = 0; i < sockets; i++) {
      socket = i;
      if (!GemBonusHandler.hasGem(stack, socket)) break;
    }
    return socket;
  }

  public static void removeGems(ItemStack stack) {
    if (!stack.hasTag()) return;
    stack.getOrCreateTag().remove("Gemstones");
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
