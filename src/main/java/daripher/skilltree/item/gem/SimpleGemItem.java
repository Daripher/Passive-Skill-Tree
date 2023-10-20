package daripher.skilltree.item.gem;

import daripher.skilltree.init.PSTCreativeTabs;
import daripher.skilltree.item.ItemHelper;
import daripher.skilltree.util.TooltipHelper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

public abstract class SimpleGemItem extends GemItem {
  protected final Map<String, Pair<Attribute, AttributeModifier>> bonuses = new HashMap<>();

  public SimpleGemItem(Properties properties) {
    super(properties);
  }

  public SimpleGemItem() {
    super(new Properties().tab(PSTCreativeTabs.SKILLTREE));
  }

  @Override
  public Optional<Pair<Attribute, AttributeModifier>> getGemBonus(
      Player player, ItemStack itemStack, ItemStack gemStack) {
    EquipmentSlot slot = ItemHelper.getSlotForItem(itemStack);
    if (ItemHelper.isPickaxe(itemStack) && slot == EquipmentSlot.MAINHAND)
      return Optional.ofNullable(bonuses.get("pickaxe"));
    if (ItemHelper.isMeleeWeapon(itemStack) && slot == EquipmentSlot.MAINHAND)
      return Optional.ofNullable(bonuses.get("melee_weapon"));
    if (ItemHelper.isShield(itemStack) && slot == EquipmentSlot.OFFHAND)
      return Optional.ofNullable(bonuses.get("shield"));
    if (ItemHelper.isHelmet(itemStack) && slot == EquipmentSlot.HEAD)
      return Optional.ofNullable(bonuses.get("helmet"));
    if (ItemHelper.isChestplate(itemStack) && slot == EquipmentSlot.CHEST)
      return Optional.ofNullable(bonuses.get("chestplate"));
    if (ItemHelper.isLeggings(itemStack) && slot == EquipmentSlot.LEGS)
      return Optional.ofNullable(bonuses.get("leggings"));
    if (ItemHelper.isBoots(itemStack) && slot == EquipmentSlot.FEET)
      return Optional.ofNullable(bonuses.get("boots"));
    if (ItemHelper.isRangedWeapon(itemStack) && slot == EquipmentSlot.MAINHAND)
      return Optional.ofNullable(bonuses.get("ranged_weapon"));
    if (ItemHelper.isRing(itemStack)) return Optional.ofNullable(bonuses.get("ring"));
    if (ItemHelper.isNecklace(itemStack)) return Optional.ofNullable(bonuses.get("necklace"));
    return Optional.empty();
  }

  @Override
  public boolean canInsertInto(Player player, ItemStack stack, ItemStack gemStack, int socket) {
    return super.canInsertInto(player, stack, gemStack, socket)
        && getGemBonus(player, stack, gemStack).isPresent();
  }

  @Override
  protected void appendBonusesTooltip(ItemStack stack, List<Component> components) {
    Map<String, Pair<Attribute, AttributeModifier>> bonuses = new TreeMap<>(this.bonuses);
    groupBonuses(bonuses);
    bonuses.forEach(
        (slot, bonus) -> components.add(TooltipHelper.getAttributeBonusTooltip(slot, bonus)));
  }

  protected void groupBonuses(Map<String, Pair<Attribute, AttributeModifier>> bonuses) {
    groupBonuses(bonuses, "melee_weapon", "ranged_weapon", "weapon");
    groupBonuses(bonuses, "necklace", "ring", "jewelry");
  }

  protected void groupBonuses(
      Map<String, Pair<Attribute, AttributeModifier>> bonuses,
      String type1,
      String type2,
      String group) {
    if (sameBonuses(type1, type2)) {
      Pair<Attribute, AttributeModifier> bonus = bonuses.get(type1);
      bonuses.remove(type1);
      bonuses.remove(type2);
      bonuses.put(group, bonus);
    }
  }

  private boolean sameBonuses(String type1, String type2) {
    if (!bonuses.containsKey(type1) || !bonuses.containsKey(type2)) return false;
    Pair<Attribute, AttributeModifier> bonus1 = bonuses.get(type1);
    Pair<Attribute, AttributeModifier> bonus2 = bonuses.get(type2);
    if (bonus1.getLeft() != bonus2.getLeft()) return false;
    return bonus1.getRight().getAmount() == bonus2.getRight().getAmount()
        && bonus1.getRight().getOperation() == bonus2.getRight().getOperation();
  }

  public List<Pair<Attribute, AttributeModifier>> getBonusesList() {
    return bonuses.values().stream().toList();
  }

  public Map<String, Pair<Attribute, AttributeModifier>> getBonuses() {
    HashMap<String, Pair<Attribute, AttributeModifier>> bonuses = new HashMap<>(this.bonuses);
    groupBonuses(bonuses);
    return bonuses;
  }

  protected void setBonuses(
      Attribute attribute, double value, Operation operation, String... slots) {
    for (String slot : slots)
      bonuses.put(slot, Pair.of(attribute, new AttributeModifier("Gem Bonus", value, operation)));
  }
}
