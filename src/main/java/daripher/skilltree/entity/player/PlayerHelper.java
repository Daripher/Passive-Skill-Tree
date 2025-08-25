package daripher.skilltree.entity.player;

import com.google.common.collect.Streams;
import daripher.skilltree.skill.bonus.condition.item.EquipmentCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

public class PlayerHelper {
  public static Stream<ItemStack> getAllEquipment(LivingEntity living) {
    return Streams.concat(getVanillaEquipment(living), getCurios(living));
  }

  public static Stream<ItemStack> getItemsInHands(LivingEntity living) {
    return Stream.of(living.getMainHandItem(), living.getOffhandItem());
  }

  public static Stream<ItemStack> getVanillaEquipment(LivingEntity living) {
    return Arrays.stream(EquipmentSlot.values()).map(slot -> getEquipmentInSlot(living, slot));
  }

  public static Stream<ItemStack> getArmor(LivingEntity living) {
    return Arrays.stream(EquipmentSlot.values())
        .filter(EquipmentSlot::isArmor)
        .map(slot -> getEquipmentInSlot(living, slot));
  }

  @NotNull
  private static ItemStack getEquipmentInSlot(LivingEntity living, EquipmentSlot slot) {
    ItemStack stack = living.getItemBySlot(slot);
    if (slot == EquipmentSlot.MAINHAND
        && !EquipmentCondition.isWeapon(stack)
        && !EquipmentCondition.isTool(stack)
        && !EquipmentCondition.isPotion(stack)) {
      return ItemStack.EMPTY;
    }
    if (slot == EquipmentSlot.OFFHAND && EquipmentCondition.isPotion(stack)) {
      return ItemStack.EMPTY;
    }
    return stack;
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
