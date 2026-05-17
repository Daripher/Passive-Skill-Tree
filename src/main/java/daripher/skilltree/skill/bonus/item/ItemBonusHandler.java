package daripher.skilltree.skill.bonus.item;

import com.google.common.collect.ImmutableList;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.init.PSTRegistries;
import daripher.skilltree.skill.bonus.SkillBonusHandler;
import daripher.skilltree.skill.bonus.player.AttributeBonus;
import daripher.skilltree.skill.bonus.player.MoreItemBonusesBonus;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID, bus = Bus.GAME)
public class ItemBonusHandler {
  private static final String SKILL_BONUSES_TAG = "SkillBonuses";

  @SubscribeEvent
  public static void addItemBonusTooltips(ItemTooltipEvent event) {
    List<Component> components = event.getToolTip();
    List<ItemBonus<?>> itemBonuses = getItemBonuses(event.getItemStack());
    if (!itemBonuses.isEmpty()) {
      components.add(Component.empty());
    }
    for (ItemBonus<?> itemBonus : itemBonuses) {
      Style style = TooltipHelper.getItemBonusStyle();
      itemBonus.addTooltip(tooltip -> components.add(tooltip.withStyle(style)));
    }
  }

  @SubscribeEvent
  public static void addCraftedItemAttributeBonuses(LivingEquipmentChangeEvent event) {
    LivingEntity entity = event.getEntity();
    if (!(entity instanceof Player)) return;
    for (ItemBonus<?> itemBonus : getItemBonuses(event.getFrom(), SkillBonusItemBonus.class)) {
      SkillBonusItemBonus bonus = (SkillBonusItemBonus) itemBonus;
      if (!(bonus.skillBonus() instanceof AttributeBonus attributeBonus)) {
        continue;
      }
      AttributeInstance attributeInstance = entity.getAttribute(attributeBonus.getAttributeHolder());
      if (attributeInstance == null) {
        continue;
      }
      attributeInstance.removeModifier(attributeBonus.getModifier().id());
    }
    for (ItemBonus<?> itemBonus : getItemBonuses(event.getTo(), SkillBonusItemBonus.class)) {
      SkillBonusItemBonus bonus = (SkillBonusItemBonus) itemBonus;
      if (!(bonus.skillBonus() instanceof AttributeBonus attributeBonus)) {
        continue;
      }
      if (attributeBonus.isDynamic()) {
        continue;
      }
      AttributeInstance attributeInstance = entity.getAttribute(attributeBonus.getAttributeHolder());
      if (attributeInstance == null) {
        continue;
      }
      if (attributeInstance.hasModifier(attributeBonus.getModifier().id())) {
        continue;
      }
      attributeInstance.addTransientModifier(attributeBonus.getModifier());
    }
  }

  public static List<ItemBonus<?>> getItemBonuses(ItemStack stack) {
    CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
    if (!customData.contains(SKILL_BONUSES_TAG)) return ImmutableList.of();
    List<ItemBonus<?>> list = new ArrayList<>();
    CompoundTag bonusesTag = customData.copyTag().getCompound(SKILL_BONUSES_TAG);
    for (int i = 0; true; i++) {
      if (!bonusesTag.contains("" + i)) {
        return list;
      }
      CompoundTag itemBonusTag = bonusesTag.getCompound("" + i);
      list.add(deserializeBonus(itemBonusTag));
    }
  }

  public static List<ItemBonus<?>> getItemBonuses(ItemStack stack, Class<?> type) {
    List<ItemBonus<?>> bonuses = new ArrayList<>();
    for (ItemBonus<?> bonus : getItemBonuses(stack)) {
      if (bonus instanceof ItemBonusListItemBonus listBonus) {
        bonuses.addAll(listBonus.innerBonuses());
      } else {
        bonuses.add(bonus);
      }
    }
    return bonuses.stream().filter(type::isInstance).toList();
  }

  public static void setItemBonuses(ItemStack stack, List<ItemBonus<?>> bonuses) {
    CompoundTag bonusesTag = new CompoundTag();
    int i = 0;
    for (ItemBonus<?> itemBonus : bonuses) {
      CompoundTag bonusTag = serializeBonus(itemBonus);
      bonusesTag.put("" + i, bonusTag);
      i++;
    }
    CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.put(SKILL_BONUSES_TAG, bonusesTag));
  }

  public static void removeItemBonuses(ItemStack stack) {
    CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.remove(SKILL_BONUSES_TAG));
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
    ResourceLocation id = ResourceLocation.parse(tag.getString("type"));
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
              .filter(bonus -> bonus.getItemCondition().test(itemStack))
              .map(MoreItemBonusesBonus::getAmount)
              .reduce(Integer::sum)
              .orElse(0);
    }
    return limit;
  }
}
