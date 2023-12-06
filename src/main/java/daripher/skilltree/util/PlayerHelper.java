package daripher.skilltree.util;

import com.google.common.collect.Streams;
import daripher.skilltree.item.ItemHelper;
import java.util.Arrays;
import java.util.stream.Stream;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

public class PlayerHelper {
  public static void hurtShield(Player player, final ItemStack shield, float amount) {
    if (!shield.canPerformAction(ToolActions.SHIELD_BLOCK)) return;
    if (!player.level.isClientSide) {
      player.awardStat(Stats.ITEM_USED.get(shield.getItem()));
    }
    if (amount < 3) return;
    amount = 1 + Mth.floor(amount);
    shield.hurtAndBreak(
        (int) amount,
        player,
        p -> {
          p.broadcastBreakEvent(InteractionHand.OFF_HAND);
          ForgeEventFactory.onPlayerDestroyItem(player, shield, InteractionHand.OFF_HAND);
        });
    if (shield.isEmpty()) {
      player.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
      player.playSound(
          SoundEvents.SHIELD_BREAK, 0.8F, 0.8F + player.level.random.nextFloat() * 0.4F);
    }
  }

  public static Stream<ItemStack> getAllEquipment(LivingEntity living) {
    return Streams.concat(getEquipment(living), getCurios(living));
  }

  public static Stream<ItemStack> getEquipment(LivingEntity living) {
    return Arrays.stream(EquipmentSlot.values()).map(slot -> getEquipment(living, slot));
  }

  @NotNull
  private static ItemStack getEquipment(LivingEntity living, EquipmentSlot slot) {
    ItemStack item = living.getItemBySlot(slot);
    if (slot == EquipmentSlot.MAINHAND && !ItemHelper.isWeapon(item)) {
      return ItemStack.EMPTY;
    }
    return item;
  }

  public static Stream<ItemStack> getCurios(LivingEntity living) {
    return CuriosApi.getCuriosHelper().findCurios(living, s -> true).stream()
        .map(SlotResult::stack);
  }
}
