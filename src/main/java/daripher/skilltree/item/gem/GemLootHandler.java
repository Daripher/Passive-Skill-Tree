package daripher.skilltree.item.gem;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.compat.apotheosis.ApotheosisCompatibility;
import daripher.skilltree.config.Config;
import daripher.skilltree.skill.bonus.SkillBonusHandler;
import daripher.skilltree.skill.bonus.player.LootDuplicationBonus;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class GemLootHandler {
  @SubscribeEvent
  public static void dropGemFromOre(BlockEvent.BreakEvent event) {
    Player player = event.getPlayer();
    if (!canDropGem(event, player)) return;
    ServerLevel serverLevel = (ServerLevel) player.level();
    LootTable lootTable = getGemsLootTable(serverLevel);
    LootParams lootContext = createGemsLootParams(event, serverLevel, player);
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

  private static boolean canDropGem(BlockEvent.BreakEvent event, Player player) {
    if (player.isCreative()) return false;
    if (player.level().isClientSide) return false;
    if (Config.gem_drop_chance == 0) return false;
    if (!player.level().getBlockState(event.getPos()).is(Tags.Blocks.ORES)) return false;
    if (player.getRandom().nextFloat() >= Config.gem_drop_chance) return false;
    if (!ForgeHooks.isCorrectToolForDrops(event.getState(), player)) return false;
    return player.getMainHandItem().getEnchantmentLevel(Enchantments.SILK_TOUCH) == 0;
  }

  @NotNull
  private static LootTable getGemsLootTable(ServerLevel serverLevel) {
    return serverLevel
        .getServer()
        .getLootData()
        .getLootTable(new ResourceLocation(SkillTreeMod.MOD_ID, "gems"));
  }

  @NotNull
  private static LootParams createGemsLootParams(
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
      Player player, Level level, BlockPos blockPos, LootTable lootTable, LootParams lootParams) {
    if (SkillTreeMod.apotheosisEnabled()) {
      ApotheosisCompatibility.INSTANCE.dropGemFromOre(player, (ServerLevel) level, blockPos);
      return;
    }
    lootTable.getRandomItems(lootParams).forEach(s -> Block.popResource(level, blockPos, s));
  }
}
