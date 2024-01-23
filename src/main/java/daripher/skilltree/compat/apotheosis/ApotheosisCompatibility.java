package daripher.skilltree.compat.apotheosis;

import com.google.common.collect.ImmutableList;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.compat.apotheosis.gem.PSTGemBonus;
import daripher.skilltree.compat.apotheosis.gem.PSTGemsProvider;
import daripher.skilltree.entity.player.PlayerHelper;
import daripher.skilltree.item.ItemHelper;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import java.util.*;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.data.DataGenerator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import shadows.apotheosis.Apoth;
import shadows.apotheosis.Apotheosis;
import shadows.apotheosis.adventure.affix.AffixHelper;
import shadows.apotheosis.adventure.affix.socket.SocketHelper;
import shadows.apotheosis.adventure.affix.socket.gem.Gem;
import shadows.apotheosis.adventure.affix.socket.gem.GemInstance;
import shadows.apotheosis.adventure.affix.socket.gem.GemItem;
import shadows.apotheosis.adventure.affix.socket.gem.GemManager;
import shadows.apotheosis.adventure.affix.socket.gem.bonus.GemBonus;
import shadows.apotheosis.adventure.event.GetItemSocketsEvent;
import shadows.apotheosis.adventure.loot.GemLootPoolEntry;
import shadows.apotheosis.adventure.loot.LootCategory;
import shadows.apotheosis.adventure.loot.LootRarity;

public enum ApotheosisCompatibility {
  INSTANCE;

  public void addCompatibility() {
    GemBonus.CODECS.put(new ResourceLocation(SkillTreeMod.MOD_ID, "gem_bonus"), PSTGemBonus.CODEC);
    IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
    modEventBus.addListener(this::generateDataFiles);
    IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
    forgeEventBus.addListener(this::addItemSockets);
  }

  public List<ItemStack> getGems(ItemStack stack, int sockets) {
    if (sockets == 0 || stack.isEmpty()) return Collections.emptyList();
    List<ItemStack> gems = NonNullList.withSize(sockets, ItemStack.EMPTY);
    int i = 0;
    CompoundTag afxData = stack.getTagElement(SocketHelper.AFFIX_DATA);
    if (afxData != null && afxData.contains(SocketHelper.GEMS)) {
      ListTag gemData = afxData.getList(SocketHelper.GEMS, Tag.TAG_COMPOUND);
      for (Tag tag : gemData) {
        ItemStack gemStack = ItemStack.of((CompoundTag) tag);
        gemStack.setCount(1);
        if (GemInstance.unsocketed(gemStack).isValidUnsocketed()) {
          gems.set(i++, gemStack);
        }
        if (i >= sockets) break;
      }
    }
    return ImmutableList.copyOf(gems);
  }

  public List<? extends ItemBonus<?>> getGemBonuses(ItemStack stack) {
    List<ItemBonus<?>> list = new ArrayList<>();
    for (ItemStack gemStack : SocketHelper.getGems(stack)) {
      Gem gem = GemItem.getGem(gemStack);
      if (gem == null) continue;
      List<GemBonus> bonuses = gem.getBonuses();
      for (GemBonus gemBonus : bonuses) {
        Set<LootCategory> lootCategories = gemBonus.getGemClass().types();
        if (lootCategories.stream().noneMatch(c -> c.isValid(stack))) continue;
        if (gemBonus instanceof PSTGemBonus aBonus) {
          list.add(aBonus.getBonus(gemStack));
          break;
        }
      }
    }
    return list;
  }

  public List<ItemStack> getGems(ItemStack stack) {
    return getGems(stack, getSockets(stack, null));
  }

  public int getSockets(ItemStack stack, @Nullable Player player) {
    int playerSockets = player == null ? 0 : PlayerHelper.getPlayerSockets(stack, player);
    int sockets = SocketHelper.getSockets(stack);
    int gems = SocketHelper.getActiveGems(stack).size();
    playerSockets -= gems;
    if (playerSockets < 0) playerSockets = 0;
    return sockets + playerSockets;
  }

  public boolean hasEmptySockets(ItemStack stack, Player player) {
    return getGems(stack, getSockets(stack, player)).stream()
        .map(GemItem::getGem)
        .anyMatch(Objects::isNull);
  }

  public int getFirstEmptySocket(ItemStack stack, int sockets) {
    List<ItemStack> gems = getGems(stack, sockets);
    for (int socket = 0; socket < sockets; socket++) {
      Gem gem = GemItem.getGem(gems.get(socket));
      if (gem == null) return socket;
    }
    return 0;
  }

  private void generateDataFiles(GatherDataEvent event) {
    DataGenerator dataGenerator = event.getGenerator();
    ExistingFileHelper fileHelper = event.getExistingFileHelper();
    dataGenerator.addProvider(
        event.includeServer(), new PSTGemsProvider(dataGenerator, fileHelper));
  }

  public void createGemStack(
      Consumer<ItemStack> consumer, LootContext context, ResourceLocation gemTypeId) {
    Player player = GemLootPoolEntry.findPlayer(context);
    if (player == null) return;
    ItemStack gemStack = getGemStack(gemTypeId);
    if (!gemStack.isEmpty()) {
      consumer.accept(gemStack);
    }
  }

  public ItemStack getGemStack(ResourceLocation gemTypeId) {
    Gem gem = GemManager.INSTANCE.getValue(gemTypeId);
    if (gem == null) return ItemStack.EMPTY;
    LootRarity rarity = gem.getMinRarity();
    if (rarity == null) return ItemStack.EMPTY;
    return GemManager.createGemStack(gem, rarity);
  }

  public @Nullable ResourceLocation getGemId(ItemStack stack) {
    Gem gem = GemItem.getGem(stack);
    if (gem == null) return null;
    return gem.getId();
  }

  public boolean adventureModuleEnabled() {
    return Apotheosis.enableAdventure;
  }

  private void addItemSockets(GetItemSocketsEvent event) {
    if (Apoth.Affixes.SOCKET.get() == null) return;
    ItemStack stack = event.getStack();
    if (!ItemHelper.hasSockets(stack)) return;
    int sockets = event.getSockets();
    if (event.getSockets() == 0) {
      int defaultSockets = ItemHelper.getDefaultSockets(stack);
      SocketHelper.setSockets(stack, defaultSockets);
      sockets += defaultSockets;
    }
    sockets += ItemHelper.getAdditionalSockets(stack);
    CompoundTag affixTag = stack.getTagElement(AffixHelper.AFFIX_DATA);
    if (affixTag != null && affixTag.contains(SocketHelper.GEMS)) {
      ListTag gemsTag = affixTag.getList(SocketHelper.GEMS, Tag.TAG_COMPOUND);
      if (sockets < gemsTag.size()) sockets = gemsTag.size();
    }
    event.setSockets(sockets);
  }
}
