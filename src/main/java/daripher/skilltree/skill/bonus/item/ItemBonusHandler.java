package daripher.skilltree.skill.bonus.item;

import com.google.common.collect.ImmutableList;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.init.PSTRegistries;
import daripher.skilltree.skill.bonus.SkillBonus;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ItemBonusHandler {
  @SubscribeEvent
  public static void addCraftedItemSkillBonusTooltips(ItemTooltipEvent event) {
    List<Component> components = event.getToolTip();
    for (ItemBonus<?> itemBonus : getItemBonuses(event.getItemStack())) {
      if (!(itemBonus instanceof ItemSkillBonus itemSkillBonus)) continue;
      SkillBonus<?> bonus = itemSkillBonus.skillBonus();
      MutableComponent tooltip = bonus.getTooltip();
      components.add(tooltip);
    }
  }

  public static List<? extends ItemBonus<?>> getItemBonuses(ItemStack stack) {
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
}
