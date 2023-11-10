package daripher.skilltree.item.gem;

import daripher.skilltree.init.PSTCreativeTabs;
import daripher.skilltree.item.ItemHelper;
import daripher.skilltree.skill.bonus.AttributeSkillBonus;
import daripher.skilltree.skill.bonus.SkillBonus;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public abstract class SimpleGemItem extends GemItem {
  protected final Map<String, SkillBonus<?>> bonuses = new HashMap<>();

  public SimpleGemItem(Properties properties) {
    super(properties);
  }

  public SimpleGemItem() {
    super(new Properties().tab(PSTCreativeTabs.SKILLTREE));
  }

  @Override
  public @Nullable SkillBonus<?> getGemBonus(
      Player player, ItemStack itemStack, ItemStack gemStack) {
    EquipmentSlot slot = ItemHelper.getSlotForItem(itemStack);
    if (ItemHelper.isPickaxe(itemStack) && slot == EquipmentSlot.MAINHAND) {
      return bonuses.get("pickaxe");
    }
    if (ItemHelper.isMeleeWeapon(itemStack) && slot == EquipmentSlot.MAINHAND) {
      return bonuses.get("melee_weapon");
    }
    if (ItemHelper.isShield(itemStack) && slot == EquipmentSlot.OFFHAND) {
      return bonuses.get("shield");
    }
    if (ItemHelper.isHelmet(itemStack) && slot == EquipmentSlot.HEAD) {
      return bonuses.get("helmet");
    }
    if (ItemHelper.isChestplate(itemStack) && slot == EquipmentSlot.CHEST) {
      return bonuses.get("chestplate");
    }
    if (ItemHelper.isLeggings(itemStack) && slot == EquipmentSlot.LEGS) {
      return bonuses.get("leggings");
    }
    if (ItemHelper.isBoots(itemStack) && slot == EquipmentSlot.FEET) {
      return bonuses.get("boots");
    }
    if (ItemHelper.isRangedWeapon(itemStack) && slot == EquipmentSlot.MAINHAND) {
      return bonuses.get("ranged_weapon");
    }
    if (ItemHelper.isRing(itemStack)) {
      return bonuses.get("ring");
    }
    if (ItemHelper.isNecklace(itemStack)) {
      return bonuses.get("necklace");
    }
    return null;
  }

  @Override
  public boolean canInsertInto(Player player, ItemStack stack, ItemStack gemStack, int socket) {
    return super.canInsertInto(player, stack, gemStack, socket)
        && getGemBonus(player, stack, gemStack) != null;
  }

  @Override
  protected void appendBonusesTooltip(ItemStack stack, List<Component> components) {
    Map<String, SkillBonus<?>> bonuses = new TreeMap<>(this.bonuses);
    groupBonuses(bonuses);
    bonuses.forEach((slot, bonus) -> components.add(bonus.getTooltip()));
  }

  protected void groupBonuses(Map<String, SkillBonus<?>> bonuses) {
    groupBonuses(bonuses, "melee_weapon", "ranged_weapon", "weapon");
    groupBonuses(bonuses, "necklace", "ring", "jewelry");
  }

  protected void groupBonuses(
      Map<String, SkillBonus<?>> bonuses, String type1, String type2, String group) {
    if (sameBonuses(type1, type2)) {
      SkillBonus<?> bonus = bonuses.get(type1);
      bonuses.remove(type1);
      bonuses.remove(type2);
      bonuses.put(group, bonus);
    }
  }

  private boolean sameBonuses(String type1, String type2) {
    if (!bonuses.containsKey(type1) || !bonuses.containsKey(type2)) return false;
    return bonuses.get(type1).sameBonus(bonuses.get(type2));
  }

  public List<SkillBonus<?>> getBonusesList() {
    return bonuses.values().stream().toList();
  }

  public Map<String, SkillBonus<?>> getBonuses() {
    HashMap<String, SkillBonus<?>> bonuses = new HashMap<>(this.bonuses);
    groupBonuses(bonuses);
    return bonuses;
  }

  protected void setBonuses(SkillBonus<?> bonus, String... slots) {
    for (String slot : slots) bonuses.put(slot, bonus);
  }

  protected void setAttributeBonuses(
      Attribute attribute, double amount, AttributeModifier.Operation operation, String... slots) {
    for (String slot : slots) {
      AttributeSkillBonus bonus =
          new AttributeSkillBonus(attribute, new AttributeModifier("Gem Bonus", amount, operation));
      bonuses.put(slot, bonus);
    }
  }
}
