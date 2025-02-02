package daripher.skilltree.item.gem.loot;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.config.ServerConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
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

import java.util.List;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class GemLootHandler {
  @SubscribeEvent
  public static void dropGemFromOre(BlockEvent.BreakEvent event) {
    Player player = event.getPlayer();
    if (!canDropGem(event, player)) return;
    ServerLevel level = (ServerLevel) player.level();
    LootTable lootTable = getGemsLootTable(level);
    LootParams lootParams = createGemsLootParams(event, level, player);
    List<ItemStack> foundGems = lootTable.getRandomItems(lootParams);
    foundGems.forEach(gem -> Block.popResource(level, event.getPos(), gem));
  }

  public static int getGemLootWeight(ResourceLocation gemId) {
    if (gemId.getPath()
        .contains("vacucite")) {
      return 200;
    }
    int tier = Integer.parseInt(gemId.getPath()
                                    .substring(gemId.getPath()
                                                   .length() - 1));
    return switch (tier) {
      case 0 -> 1000;
      case 1 -> 350;
      case 2 -> 100;
      case 3 -> 10;
      default -> 0;
    };
  }

  public static int getGemLootQuality(ResourceLocation gemId) {
    if (gemId.getPath()
        .contains("vacucite")) {
      return 1;
    }
    int tier = Integer.parseInt(gemId.getPath()
                                    .substring(gemId.getPath()
                                                   .length() - 1));
    return switch (tier) {
      case 0 -> -50;
      case 1 -> -10;
      case 2 -> -1;
      case 4 -> 5;
      case 5 -> 15;
      default -> 0;
    };
  }

  private static boolean canDropGem(BlockEvent.BreakEvent event, Player player) {
    if (player.isCreative()) return false;
    if (player.level().isClientSide) return false;
    if (ServerConfig.gem_drop_chance == 0) return false;
    if (!player.level()
        .getBlockState(event.getPos())
        .is(Tags.Blocks.ORES)) {
      return false;
    }
    if (player.getRandom()
            .nextFloat() >= ServerConfig.gem_drop_chance) {
      return false;
    }
    if (!ForgeHooks.isCorrectToolForDrops(event.getState(), player)) return false;
    return player.getMainHandItem()
               .getEnchantmentLevel(Enchantments.SILK_TOUCH) == 0;
  }

  @NotNull
  private static LootTable getGemsLootTable(ServerLevel serverLevel) {
    String name = SkillTreeMod.apotheosisEnabled() ? "apotheosis_gems" : "gems";
    ResourceLocation id = new ResourceLocation(SkillTreeMod.MOD_ID, name);
    return serverLevel.getServer()
        .getLootData()
        .getLootTable(id);
  }

  @NotNull
  private static LootParams createGemsLootParams(BlockEvent.BreakEvent event, ServerLevel serverLevel, Player player) {
    BlockPos pos = event.getPos();
    return new LootParams.Builder(serverLevel).withParameter(LootContextParams.BLOCK_STATE, event.getState())
        .withParameter(LootContextParams.THIS_ENTITY, player)
        .withParameter(LootContextParams.ORIGIN, new Vec3(pos.getX(), pos.getY(), pos.getZ()))
        .withParameter(LootContextParams.TOOL, player.getMainHandItem())
        .withLuck(player.getLuck())
        .create(LootContextParamSets.BLOCK);
  }
}
