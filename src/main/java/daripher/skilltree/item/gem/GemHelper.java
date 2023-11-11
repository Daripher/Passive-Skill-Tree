package daripher.skilltree.item.gem;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.api.HasAdditionalSockets;
import daripher.skilltree.compat.apotheosis.ApotheosisCompatibility;
import daripher.skilltree.init.PSTAttributes;
import daripher.skilltree.item.ItemHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

public class GemHelper {
  protected static final String GEMS_TAG = "GEMSTONES";
  protected static final String GEM_TAG = "GEMSTONE";
  protected static final String BONUS_TAG = "BONUS";

  public static boolean hasGem(ItemStack stack, int socket) {
    if (ModList.get().isLoaded("apotheosis")) {
      if (ApotheosisCompatibility.INSTANCE.adventureModuleEnabled()) return false;
    }
    if (!stack.hasTag()) return false;
    if (!stack.getTag().contains(GEMS_TAG)) return false;
    ListTag gems = getGemsListTag(stack);
    if (gems.size() <= socket) return false;
    return gems.get(socket) != null;
  }

  public static void insertGem(
      Player player, ItemStack stack, ItemStack gemStack, int socket, double power) {
    if (!(gemStack.getItem() instanceof GemItem gem)) {
      SkillTreeMod.LOGGER.error(
          "Item {} is not a gem, can not insert it into an item!", stack.getItem());
      return;
    }
    CompoundTag gemTag = new CompoundTag();
    ListTag gemsTag = getGemsListTag(stack);
    if (gemsTag.size() > socket) gemTag = gemsTag.getCompound(socket);
    SkillBonus<?> bonus = gem.getGemBonus(player, stack, gemStack);
    if (bonus == null) {
      SkillTreeMod.LOGGER.error(
          "Cannot insert gem into {}, slot: {}",
          stack.getItem(),
          Player.getEquipmentSlotForItem(stack));
      return;
    }
    bonus = bonus.copy().multiply(power);
    gemTag.putString(GEM_TAG, ForgeRegistries.ITEMS.getKey(gem).toString());
    gemTag.put(BONUS_TAG, bonus.getSerializer().serialize(bonus));
    gemsTag.add(socket, gemTag);
    stack.getOrCreateTag().put(GEMS_TAG, gemsTag);
  }

  public static void removeGems(ItemStack itemStack) {
    itemStack.getTag().remove(GEMS_TAG);
  }

  public static @Nullable SkillBonus<?> getBonus(ItemStack itemStack, int socket) {
    if (!GemHelper.hasGem(itemStack, socket)) return null;
    CompoundTag gemTag = (CompoundTag) getGemsListTag(itemStack).get(socket);
    return SkillBonus.load(gemTag);
  }

  public static Optional<GemItem> getGem(ItemStack itemStack, int socket) {
    if (ModList.get().isLoaded("apotheosis")) {
      if (ApotheosisCompatibility.INSTANCE.adventureModuleEnabled()) return Optional.empty();
    }
    if (!hasGem(itemStack, socket)) return Optional.empty();
    CompoundTag gemTag = (CompoundTag) getGemsListTag(itemStack).get(socket);
    String gemId = gemTag.getString(GEM_TAG);
    Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(gemId));
    if (!(item instanceof GemItem gem)) return Optional.empty();
    return Optional.of(gem);
  }

  public static int getGemsCount(ItemStack itemStack) {
    if (ModList.get().isLoaded("apotheosis")) {
      return ApotheosisCompatibility.INSTANCE.getGemsCount(itemStack);
    }
    if (itemStack.isEmpty()) return 0;
    int gemsCount = 0;
    int socket = 0;
    while (hasGem(itemStack, socket)) {
      gemsCount++;
      socket++;
    }
    return gemsCount;
  }

  public static int getMaximumSockets(ItemStack stack, @Nullable Player player) {
    if (ModList.get().isLoaded("apotheosis")) {
      if (ApotheosisCompatibility.INSTANCE.adventureModuleEnabled()) return 0;
    }
    int sockets = ItemHelper.getDefaultSockets(stack);
    if (ItemHelper.hasBonus(stack, ItemHelper.ADDITIONAL_SOCKETS)) {
      sockets += (int) ItemHelper.getBonus(stack, ItemHelper.ADDITIONAL_SOCKETS);
    }
    if (stack.getItem() instanceof HasAdditionalSockets) {
      sockets += ((HasAdditionalSockets) stack.getItem()).getAdditionalSockets();
    }
    if (player != null) sockets += getPlayerSockets(stack, player);
    return sockets;
  }

  public static int getPlayerSockets(ItemStack stack, @Nullable Player player) {
    if (player == null) return 0;
    int sockets = 0;
    if (ItemHelper.isEquipment(stack)) {
      sockets += (int) player.getAttributeValue(PSTAttributes.MAXIMUM_EQUIPMENT_SOCKETS.get());
    }
    if (ItemHelper.isChestplate(stack)) {
      sockets += (int) player.getAttributeValue(PSTAttributes.MAXIMUM_CHESTPLATE_SOCKETS.get());
    }
    if (ItemHelper.isWeapon(stack)) {
      sockets += (int) player.getAttributeValue(PSTAttributes.MAXIMUM_WEAPON_SOCKETS.get());
    }
    if (ItemHelper.isRing(stack)) {
      sockets += (int) player.getAttributeValue(PSTAttributes.MAXIMUM_RING_SOCKETS.get());
    }
    return sockets;
  }

  protected static ListTag getGemsListTag(ItemStack itemStack) {
    return itemStack.getOrCreateTag().getList(GEMS_TAG, new CompoundTag().getId());
  }
}
