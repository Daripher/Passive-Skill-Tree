package daripher.skilltree.skill.bonus.player;

import com.google.gson.*;
import daripher.skilltree.client.screen.SkillTreeEditor;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.data.SerializationHelper;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.item.ItemHelper;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.condition.item.ItemCondition;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import java.util.Objects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;

public record CraftedItemBonus(ItemCondition itemCondition, ItemBonus<?> bonus)
    implements SkillBonus<CraftedItemBonus> {
  public void itemCrafted(ItemStack stack) {
    if (!itemCondition.met(stack)) return;
    ItemHelper.addItemBonus(stack, bonus);
  }

  @Override
  public SkillBonus.Serializer getSerializer() {
    return PSTSkillBonuses.CRAFTED_ITEM_BONUS.get();
  }

  @Override
  public SkillBonus<CraftedItemBonus> copy() {
    return new CraftedItemBonus(itemCondition, this.bonus.copy());
  }

  @Override
  public CraftedItemBonus multiply(double multiplier) {
    this.bonus.multiply(multiplier);
    return this;
  }

  @Override
  public boolean canMerge(SkillBonus<?> other) {
    if (!(other instanceof CraftedItemBonus otherBonus)) return false;
    if (!Objects.equals(otherBonus.itemCondition, this.itemCondition)) return false;
    return otherBonus.bonus.canMerge(this.bonus);
  }

  @Override
  public SkillBonus<CraftedItemBonus> merge(SkillBonus<?> other) {
    if (!(other instanceof CraftedItemBonus otherBonus)) {
      throw new IllegalArgumentException();
    }
    return new CraftedItemBonus(itemCondition, otherBonus.bonus.merge(this.bonus));
  }

  @Override
  public MutableComponent getTooltip() {
    MutableComponent itemDescription =
        TooltipHelper.getOptionalTooltip(itemCondition.getDescriptionId(), "crafted");
    MutableComponent bonusDescription =
        bonus.getTooltip().withStyle(Style.EMPTY.withColor(0x7AB3E2));
    return Component.translatable(getDescriptionId(), itemDescription, bonusDescription)
        .withStyle(Style.EMPTY.withColor(0x7B7BE5));
  }

  @Override
  public void addEditorWidgets(SkillTreeEditor editor, int row) {
    // TODO: add widgets
  }

  public static class Serializer implements SkillBonus.Serializer {
    @Override
    public CraftedItemBonus deserialize(JsonObject json) throws JsonParseException {
      ItemCondition condition = SerializationHelper.deserializeItemCondition(json);
      ItemBonus<?> bonus = SerializationHelper.deserializeItemBonus(json);
      return new CraftedItemBonus(condition, bonus);
    }

    @Override
    public void serialize(JsonObject json, SkillBonus<?> bonus) {
      if (!(bonus instanceof CraftedItemBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      SerializationHelper.serializeItemCondition(json, aBonus.itemCondition);
      SerializationHelper.serializeItemBonus(json, aBonus.bonus);
    }

    @Override
    public CraftedItemBonus deserialize(CompoundTag tag) {
      ItemCondition condition = SerializationHelper.deserializeItemCondition(tag);
      ItemBonus<?> bonus = SerializationHelper.deserializeItemBonus(tag);
      return new CraftedItemBonus(condition, bonus);
    }

    @Override
    public CompoundTag serialize(SkillBonus<?> bonus) {
      if (!(bonus instanceof CraftedItemBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      SerializationHelper.serializeItemCondition(tag, aBonus.itemCondition);
      SerializationHelper.serializeItemBonus(tag, aBonus.bonus);
      return tag;
    }

    @Override
    public CraftedItemBonus deserialize(FriendlyByteBuf buf) {
      ItemCondition condition = NetworkHelper.readItemCondition(buf);
      ItemBonus<?> bonus = NetworkHelper.readItemBonus(buf);
      return new CraftedItemBonus(condition, bonus);
    }

    @Override
    public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
      if (!(bonus instanceof CraftedItemBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      NetworkHelper.writeItemCondition(buf, aBonus.itemCondition);
      NetworkHelper.writeItemBonus(buf, aBonus.bonus);
    }
  }
}
