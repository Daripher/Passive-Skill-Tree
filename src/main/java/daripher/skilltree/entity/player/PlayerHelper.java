package daripher.skilltree.entity.player;

import com.google.common.collect.Streams;
import daripher.skilltree.item.ItemHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import daripher.skilltree.skill.bonus.SkillBonusHandler;
import daripher.skilltree.skill.bonus.player.GemPowerBonus;
import daripher.skilltree.skill.bonus.player.PlayerSocketsBonus;
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
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import javax.annotation.Nonnull;

public class PlayerHelper {
  public static void hurtShield(Player player, final ItemStack shield, float amount) {
    if (!shield.canPerformAction(ToolActions.SHIELD_BLOCK)) return;
    if (!player.level().isClientSide) {
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
          SoundEvents.SHIELD_BREAK, 0.8F, 0.8F + player.level().random.nextFloat() * 0.4F);
    }
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

  public static Stream<ItemStack> getAllEquipment(LivingEntity living) {
    return Streams.concat(getEquipment(living), getCurios(living));
  }

  public static Stream<ItemStack> getItemsInHands(LivingEntity living) {
    return Stream.of(living.getMainHandItem(), living.getOffhandItem());
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
    List<ItemStack> curios = new ArrayList<>();
    CuriosApi.getCuriosInventory(living)
        .map(ICuriosItemHandler::getEquippedCurios)
        .ifPresent(
            inv -> {
              for (int i = 0; i < inv.getSlots(); i++) {
                curios.add(inv.getStackInSlot(i));
              }
            });
    return curios.stream();
  }
}
