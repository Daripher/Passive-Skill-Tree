package daripher.skilltree.init;

import daripher.skilltree.skill.bonus.predicate.item.EquipmentPredicate;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import daripher.skilltree.enchantment.EnchantmentCategory;

public class PSTEnchantmentCategories {
    public static EnchantmentCategory SHIELD = EnchantmentCategory.create("shield", item -> EquipmentPredicate.isShield(new ItemStack(item)));
    public static EnchantmentCategory POTION = EnchantmentCategory.create("potion", item -> item instanceof PotionItem);
}
