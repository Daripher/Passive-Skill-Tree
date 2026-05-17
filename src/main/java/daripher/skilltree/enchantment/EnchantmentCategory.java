package daripher.skilltree.enchantment;

import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TridentItem;

public final class EnchantmentCategory {
  public static final EnchantmentCategory ARMOR =
      new EnchantmentCategory("armor", item -> item instanceof ArmorItem);
  public static final EnchantmentCategory ARMOR_CHEST =
      new EnchantmentCategory(
          "armor_chest",
          item -> item instanceof ArmorItem armor && armor.getEquipmentSlot() == EquipmentSlot.CHEST);
  public static final EnchantmentCategory ARMOR_FEET =
      new EnchantmentCategory(
          "armor_feet",
          item -> item instanceof ArmorItem armor && armor.getEquipmentSlot() == EquipmentSlot.FEET);
  public static final EnchantmentCategory ARMOR_HEAD =
      new EnchantmentCategory(
          "armor_head",
          item -> item instanceof ArmorItem armor && armor.getEquipmentSlot() == EquipmentSlot.HEAD);
  public static final EnchantmentCategory ARMOR_LEGS =
      new EnchantmentCategory(
          "armor_legs",
          item -> item instanceof ArmorItem armor && armor.getEquipmentSlot() == EquipmentSlot.LEGS);
  public static final EnchantmentCategory WEAPON =
      new EnchantmentCategory(
          "weapon", item -> item instanceof SwordItem || item instanceof AxeItem || item instanceof TridentItem);
  public static final EnchantmentCategory BOW =
      new EnchantmentCategory("bow", item -> item instanceof BowItem);
  public static final EnchantmentCategory CROSSBOW =
      new EnchantmentCategory("crossbow", item -> item instanceof CrossbowItem);
  public static final EnchantmentCategory TRIDENT =
      new EnchantmentCategory("trident", item -> item instanceof TridentItem);

  private final String name;
  private final Predicate<Item> predicate;

  private EnchantmentCategory(String name, Predicate<Item> predicate) {
    this.name = name;
    this.predicate = predicate;
  }

  public static EnchantmentCategory create(String name, Predicate<Item> predicate) {
    return new EnchantmentCategory(name, predicate);
  }

  public boolean canEnchant(Item item) {
    return predicate.test(item);
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof EnchantmentCategory that)) return false;
    return Objects.equals(name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }
}
