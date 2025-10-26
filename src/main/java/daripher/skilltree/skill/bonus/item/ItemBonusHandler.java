package daripher.skilltree.skill.bonus.item;

import com.google.common.collect.ImmutableList;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.init.PSTRegistries;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.SkillBonusHandler;
import daripher.skilltree.skill.bonus.player.AttributeBonus;
import daripher.skilltree.skill.bonus.player.MoreItemBonusesBonus;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ItemBonusHandler {
  @SubscribeEvent
  public static void addCraftedItemSkillBonusTooltips(ItemTooltipEvent event) {
    List<Component> components = event.getToolTip();
    List<ItemBonus<?>> itemBonuses = getItemBonuses(event.getItemStack());
    if (!itemBonuses.isEmpty()) {
      components.add(Component.empty());
    }
    for (ItemBonus<?> itemBonus : itemBonuses) {
      if (!(itemBonus instanceof ItemSkillBonus itemSkillBonus)) continue;
      SkillBonus<?> bonus = itemSkillBonus.skillBonus();
      MutableComponent tooltip = bonus.getTooltip().withStyle(TooltipHelper.getItemBonusStyle());
      components.add(tooltip);
    }
  }

  @SubscribeEvent
  public static void addCraftedItemAttributeBonuses(LivingEquipmentChangeEvent event) {
    LivingEntity entity = event.getEntity();
    if (!(entity instanceof Player)) return;
    for (ItemBonus<?> itemBonus : getItemBonuses(event.getFrom(), ItemSkillBonus.class)) {
      ItemSkillBonus itemSkillBonus = (ItemSkillBonus) itemBonus;
      if (!(itemSkillBonus.skillBonus() instanceof AttributeBonus attributeBonus)) {
        continue;
      }
      AttributeInstance attributeInstance = entity.getAttribute(attributeBonus.getAttribute());
      if (attributeInstance == null) {
        continue;
      }
      attributeInstance.removeModifier(attributeBonus.getModifier().getId());
    }
    for (ItemBonus<?> itemBonus : getItemBonuses(event.getTo(), ItemSkillBonus.class)) {
      ItemSkillBonus itemSkillBonus = (ItemSkillBonus) itemBonus;
      if (!(itemSkillBonus.skillBonus() instanceof AttributeBonus attributeBonus)) {
        continue;
      }
      if (attributeBonus.isDynamic()) {
        continue;
      }
      AttributeInstance attributeInstance = entity.getAttribute(attributeBonus.getAttribute());
      if (attributeInstance == null) {
        continue;
      }
      if (attributeInstance.hasModifier(attributeBonus.getModifier())) {
        continue;
      }
      attributeInstance.addTransientModifier(attributeBonus.getModifier());
    }
  }

  public static List<ItemBonus<?>> getItemBonuses(ItemStack stack) {
    if (!stack.hasTag()) return ImmutableList.of();
    List<ItemBonus<?>> list = new ArrayList<>();
    CompoundTag stackTag = stack.getOrCreateTag();
    CompoundTag bonusesTag = stackTag.getCompound("SkillBonuses");
    for (int i = 0; true; i++) {
      if (!bonusesTag.contains("" + i)) {
        return list;
      }
      CompoundTag itemBonusTag = bonusesTag.getCompound("" + i);
      list.add(deserializeBonus(itemBonusTag));
    }
  }

  public static List<ItemBonus<?>> getItemBonuses(ItemStack stack, Class<?> type) {
    return getItemBonuses(stack).stream().filter(type::isInstance).toList();
  }

  public static void setItemBonuses(ItemStack stack, List<ItemBonus<?>> bonuses) {
    CompoundTag bonusesTag = new CompoundTag();
    int i = 0;
    for (ItemBonus<?> itemBonus : bonuses) {
      CompoundTag bonusTag = serializeBonus(itemBonus);
      bonusesTag.put("" + i, bonusTag);
      i++;
    }
    stack.getOrCreateTag().put("SkillBonuses", bonusesTag);
  }

  public static void removeItemBonuses(ItemStack stack) {
    if (!stack.hasTag()) return;
    stack.getOrCreateTag().remove("SkillBonuses");
  }

  private static CompoundTag serializeBonus(ItemBonus<? extends ItemBonus<?>> bonus) {
    ItemBonus.Serializer serializer = bonus.getSerializer();
    CompoundTag bonusTag = serializer.serialize(bonus);
    ResourceLocation id = PSTRegistries.ITEM_BONUSES.get().getKey(serializer);
    bonusTag.putString("type", Objects.requireNonNull(id).toString());
    return bonusTag;
  }

  private static ItemBonus<?> deserializeBonus(CompoundTag tag) {
    if (!tag.contains("type")) return null;
    ResourceLocation id = new ResourceLocation(tag.getString("type"));
    ItemBonus.Serializer serializer = PSTRegistries.ITEM_BONUSES.get().getValue(id);
    if (serializer == null) return null;
    try {
      return serializer.deserialize(tag);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public static int getCraftedBonusLimit(ItemStack itemStack, @Nullable Player player) {
    int limit = 1;
    if (player != null) {
      limit +=
          SkillBonusHandler.getSkillBonuses(player, MoreItemBonusesBonus.class).stream()
              .filter(bonus -> bonus.getItemCondition().met(itemStack))
              .map(MoreItemBonusesBonus::getAmount)
              .reduce(Integer::sum)
              .orElse(0);
    }
    return limit;
  }
}
