package daripher.skilltree.compat.apotheosis;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.api.HasAdditionalSockets;
import daripher.skilltree.item.ItemHelper;
import daripher.skilltree.item.gem.GemHelper;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.data.DataGenerator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.event.RenderTooltipEvent.GatherComponents;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.lang3.tuple.Pair;
import shadows.apotheosis.Apotheosis;
import shadows.apotheosis.adventure.affix.Affix;
import shadows.apotheosis.adventure.affix.AffixHelper;
import shadows.apotheosis.adventure.affix.AffixInstance;
import shadows.apotheosis.adventure.affix.socket.SocketHelper;
import shadows.apotheosis.adventure.affix.socket.gem.Gem;
import shadows.apotheosis.adventure.affix.socket.gem.GemInstance;
import shadows.apotheosis.adventure.affix.socket.gem.GemItem;
import shadows.apotheosis.adventure.affix.socket.gem.GemManager;
import shadows.apotheosis.adventure.client.SocketTooltipRenderer.SocketComponent;
import shadows.apotheosis.adventure.event.GetItemSocketsEvent;
import shadows.apotheosis.adventure.loot.LootCategory;
import shadows.apotheosis.core.attributeslib.api.GatherSkippedAttributeTooltipsEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;

public enum ApotheosisCompatibility {
  ISNTANCE;

  public final LootCategory ring =
      LootCategory.register(
          null, "ring", ItemHelper::isRing, new EquipmentSlot[] {EquipmentSlot.CHEST});
  public final LootCategory necklace =
      LootCategory.register(
          null, "necklace", ItemHelper::isNecklace, new EquipmentSlot[] {EquipmentSlot.CHEST});

  public void addCompatibility() {
    IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
    modEventBus.addListener(this::generateGems);

    IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
    forgeEventBus.addListener(this::addItemSockets);
    forgeEventBus.addListener(this::ignoreGemTooltips);
    forgeEventBus.addListener(EventPriority.LOW, this::addJewelrySocketTooltip);
    forgeEventBus.addListener(this::applyCurioAttributeAffixes);
    forgeEventBus.addListener(this::applyCurioDamageAffixes);
    forgeEventBus.addListener(EventPriority.LOWEST, this::removeFakeCurioAttributes);
    forgeEventBus.addListener(EventPriority.LOWEST, this::removeDuplicateTooltips);
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

  public void ignoreGemTooltips(GatherSkippedAttributeTooltipsEvent event) {
    ItemStack stack = event.getStack();
    int socket = 0;
    while (GemHelper.hasGem(stack, socket)) {
      GemHelper.getAttributeBonus(stack, socket)
          .map(Pair::getRight)
          .map(AttributeModifier::getId)
          .ifPresent(event::skipUUID);
      socket++;
    }
  }

  public int getSockets(ItemStack stack, @Nullable Player player) {
    int playerSockets = GemHelper.getPlayerSockets(stack, player);
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

  private void removeDuplicateTooltips(ItemTooltipEvent event) {
    if (!adventureModuleEnabled()) return;
    ItemStack stack = event.getItemStack();
    if (!ItemHelper.isJewelry(stack)) return;
    if (!stack.hasTag()) return;
    SocketHelper.getGemInstances(stack)
        .forEach(
            gem -> {
              gem.gem()
                  .getBonus(LootCategory.forItem(stack))
                  .ifPresent(
                      bonus -> {
                        removeTooltip(
                            event, bonus.getSocketBonusTooltip(gem.gemStack(), gem.rarity()));
                      });
            });
  }

  private void removeTooltip(ItemTooltipEvent event, Component tooltip) {
    Iterator<Component> iterator = event.getToolTip().iterator();
    while (iterator.hasNext()) {
      if (!iterator.next().getString().equals(tooltip.getString())) continue;
      iterator.remove();
      break;
    }
  }

  private void applyCurioAttributeAffixes(CurioAttributeModifierEvent event) {
    if (!adventureModuleEnabled()) return;
    ItemStack stack = event.getItemStack();
    if (!stack.hasTag()) return;
    String slot = event.getSlotContext().identifier();
    if (ItemHelper.isRing(stack) && !slot.equals("ring")) return;
    if (ItemHelper.isNecklace(stack) && !slot.equals("necklace")) return;
    Stream<GemInstance> gems = SocketHelper.getGemInstances(stack);
    gems.forEach(
        gem -> {
          gem.gem()
              .getBonus(LootCategory.forItem(stack))
              .ifPresent(
                  bonus -> {
                    bonus.addModifiers(gem.gemStack(), gem.rarity(), event::addModifier);
                  });
        });
    Map<Affix, AffixInstance> affixes = AffixHelper.getAffixes(stack);
    affixes.forEach(
        (affix, instance) -> {
          instance.addModifiers(EquipmentSlot.CHEST, event::addModifier);
        });
  }

  private void applyCurioDamageAffixes(LivingHurtEvent event) {
    DamageSource source = event.getSource();
    LivingEntity entity = event.getEntity();
    CuriosApi.getCuriosHelper()
        .getEquippedCurios(entity)
        .ifPresent(
            itemHandler -> {
              for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
                ItemStack stack = itemHandler.getStackInSlot(slot);
                AffixHelper.getAffixes(stack)
                    .forEach(
                        (affix, instance) -> {
                          event.setAmount(instance.onHurt(source, entity, event.getAmount()));
                        });
              }
            });
  }

  private void removeFakeCurioAttributes(ItemAttributeModifierEvent event) {
    if (!ItemHelper.isJewelry(event.getItemStack())) return;
    if (event.getSlotType() == EquipmentSlot.CHEST) event.clearModifiers();
  }

  private void addJewelrySocketTooltip(GatherComponents event) {
    if (!adventureModuleEnabled()) return;
    ItemStack stack = event.getItemStack();
    if (!ItemHelper.isJewelry(stack)) return;
    event
        .getTooltipElements()
        .removeIf(
            component -> component.right().filter(SocketComponent.class::isInstance).isPresent());
    event
        .getTooltipElements()
        .add(Either.right(new SocketComponent(stack, SocketHelper.getGems(stack))));
  }

  private void generateGems(GatherDataEvent event) {
    DataGenerator dataGenerator = event.getGenerator();
    ExistingFileHelper fileHelper = event.getExistingFileHelper();
    dataGenerator.addProvider(event.includeServer(), new ModGemProvider(dataGenerator, fileHelper));
  }

  public boolean adventureModuleEnabled() {
    return Apotheosis.enableAdventure;
  }

  public int getGemsCount(ItemStack itemStack) {
    if (!adventureModuleEnabled()) return 0;
    return SocketHelper.getActiveGems(itemStack).size();
  }

  public void dropGemFromOre(Player player, ServerLevel level, BlockPos blockPos) {
    ItemStack gem =
        GemManager.createRandomGemStack(
            player.getRandom(), level, player.getLuck(), this::shouldDropFromOre);
    Block.popResource(level, blockPos, gem);
  }

  private boolean shouldDropFromOre(Gem gem) {
    return gem.getDimensions()
        .contains(new ResourceLocation(SkillTreeMod.MOD_ID, "fake_dimension"));
  }

  private void addItemSockets(GetItemSocketsEvent event) {
    ItemStack stack = event.getStack();
    if (!ItemHelper.hasSockets(stack)) return;
    int sockets = event.getSockets();
    if (event.getSockets() == 0) {
      int defaultSockets = ItemHelper.getDefaultSockets(stack);
      SocketHelper.setSockets(stack, defaultSockets);
      sockets += defaultSockets;
    }
    if (stack.getItem() instanceof HasAdditionalSockets item) {
      sockets += item.getAdditionalSockets();
    }
    if (ItemHelper.hasBonus(stack, ItemHelper.ADDITIONAL_SOCKETS)) {
      sockets += ItemHelper.getBonus(stack, ItemHelper.ADDITIONAL_SOCKETS);
    }
    CompoundTag affixTag = stack.getTagElement(AffixHelper.AFFIX_DATA);
    if (affixTag != null && affixTag.contains(SocketHelper.GEMS)) {
      ListTag gemsTag = affixTag.getList(SocketHelper.GEMS, Tag.TAG_COMPOUND);
      if (sockets < gemsTag.size()) sockets = gemsTag.size();
    }
    event.setSockets(sockets);
  }
}
