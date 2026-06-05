package daripher.skilltree.entity.player;

import com.google.common.collect.Streams;
import daripher.skilltree.compat.curios.CuriosCompatibility;
import daripher.skilltree.skill.bonus.predicate.item.EquipmentPredicate;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.Stream;

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
        return Arrays.stream(EquipmentSlot.values()).filter(EquipmentSlot::isArmor).map(slot -> getEquipmentInSlot(living, slot));
    }

    @NotNull
    private static ItemStack getEquipmentInSlot(LivingEntity living, EquipmentSlot slot) {
        ItemStack stack = living.getItemBySlot(slot);
        if (slot == EquipmentSlot.MAINHAND && !EquipmentPredicate.isWeapon(stack) && !EquipmentPredicate.isTool(stack) && !EquipmentPredicate.isPotion(stack)) {
            return ItemStack.EMPTY;
        }
        if (slot == EquipmentSlot.OFFHAND && EquipmentPredicate.isPotion(stack)) {
            return ItemStack.EMPTY;
        }
        return stack;
    }

    public static Stream<ItemStack> getCurios(LivingEntity living) {
        if (ModList.get().isLoaded("curios")) {
            return CuriosCompatibility.INSTANCE.getCurios(living);
        }
        return Stream.of();
    }
}
