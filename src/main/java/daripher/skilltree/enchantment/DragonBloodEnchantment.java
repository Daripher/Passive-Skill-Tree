package daripher.skilltree.enchantment;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.event.DamageTakenEventListener;
import daripher.skilltree.skill.bonus.player.HealingBonus;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DragonBloodEnchantment extends CraftableEnchantment implements SkillBonusEnchantment {
  public DragonBloodEnchantment() {
    super(3, EnchantmentCategory.ARMOR_CHEST, new EquipmentSlot[]{EquipmentSlot.CHEST});
  }

  @Override
  protected boolean checkCompatibility(@NotNull Enchantment other) {
    return other != Enchantments.THORNS && other != Enchantments.ALL_DAMAGE_PROTECTION;
  }

  @Override
  public SkillBonus<?> getBonus(int enchantmentLevel) {
    return new HealingBonus(1f, 2f, new DamageTakenEventListener());
  }
}
