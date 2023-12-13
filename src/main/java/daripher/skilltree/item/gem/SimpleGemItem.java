package daripher.skilltree.item.gem;

import daripher.skilltree.item.ItemHelper;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import daripher.skilltree.skill.bonus.item.ItemSkillBonus;
import daripher.skilltree.skill.bonus.player.AttributeBonus;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public abstract class SimpleGemItem extends GemItem {
  protected final Map<String, ItemBonus<?>> bonuses = new HashMap<>();

  public SimpleGemItem(Properties properties) {
    super(properties);
  }

  public SimpleGemItem() {
    super(new Properties());
  }

  @Override
  public @Nullable ItemBonus<?> getGemBonus(
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
    Map<String, ItemBonus<?>> bonuses = new TreeMap<>(this.bonuses);
    groupBonuses(bonuses);
    bonuses.forEach(
        (slot, bonus) -> {
          Component slotDescription = Component.translatable("gem_class." + slot);
          Component tooltip =
              Component.translatable("gem_class_format", slotDescription, bonus.getTooltip())
                  .withStyle(ChatFormatting.GRAY);
          components.add(tooltip);
        });
  }

  protected void groupBonuses(Map<String, ItemBonus<?>> bonuses) {
    groupBonuses(bonuses, "melee_weapon", "ranged_weapon", "weapon");
    groupBonuses(bonuses, "necklace", "ring", "jewelry");
  }

  protected void groupBonuses(
      Map<String, ItemBonus<?>> bonuses, String type1, String type2, String group) {
    if (sameBonuses(type1, type2)) {
      ItemBonus<?> bonus = bonuses.get(type1);
      bonuses.remove(type1);
      bonuses.remove(type2);
      bonuses.put(group, bonus);
    }
  }

  private boolean sameBonuses(String type1, String type2) {
    if (!bonuses.containsKey(type1) || !bonuses.containsKey(type2)) return false;
    return bonuses.get(type1).sameBonus(bonuses.get(type2));
  }

  public List<ItemBonus<?>> getBonusesList() {
    return bonuses.values().stream().toList();
  }

  public Map<String, ItemBonus<?>> getBonuses() {
    HashMap<String, ItemBonus<?>> bonuses = new HashMap<>(this.bonuses);
    groupBonuses(bonuses);
    return bonuses;
  }

  protected void setBonuses(ItemBonus<?> bonus, String... slots) {
    for (String slot : slots) bonuses.put(slot, bonus);
  }

  protected void setAttributeBonuses(
      Attribute attribute, double amount, AttributeModifier.Operation operation, String... slots) {
    ItemBonus<?> bonus =
        new ItemSkillBonus(
            new AttributeBonus(attribute, new AttributeModifier("GemBonus", amount, operation)));
    setBonuses(bonus, slots);
  }
}
