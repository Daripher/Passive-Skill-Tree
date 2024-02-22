package daripher.skilltree.skill.bonus.condition.item;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.screen.SkillTreeEditorScreen;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTItemConditions;
import daripher.skilltree.network.NetworkHelper;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.common.Tags;

public final class EnchantedCondition implements ItemCondition {
  private ItemCondition itemCondition;

  public EnchantedCondition(ItemCondition itemCondition) {
    this.itemCondition = itemCondition;
  }

  @Override
  public boolean met(ItemStack stack) {
    return !EnchantmentHelper.getEnchantments(stack).isEmpty() && itemCondition.met(stack);
  }

  @Override
  public Component getTooltip() {
    return Component.translatable(getDescriptionId(), itemCondition.getTooltip("type"));
  }

  @Override
  public Component getTooltip(String type) {
    return Component.translatable(getDescriptionId(), itemCondition.getTooltip(type + ".type"));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    EnchantedCondition that = (EnchantedCondition) o;
    return itemCondition.equals(that.itemCondition);
  }

  @Override
  public int hashCode() {
    return Objects.hash(itemCondition);
  }

  @Override
  public ItemCondition.Serializer getSerializer() {
    return PSTItemConditions.ENCHANTED.get();
  }

  @Override
  public void addEditorWidgets(SkillTreeEditorScreen editor, Consumer<ItemCondition> consumer) {
    editor.addLabel(0, 0, "Inner Item Condition", ChatFormatting.GREEN);
    editor.shiftWidgets(0, 19);
    editor
        .addDropDownList(0, 0, 200, 14, 10, itemCondition, PSTItemConditions.conditionsList())
        .setToNameFunc(a -> Component.translatable(PSTItemConditions.getName(a)))
        .setResponder(
            c -> {
              setItemCondition(c);
              consumer.accept(this);
              editor.rebuildWidgets();
            });
    editor.shiftWidgets(0, 19);
    itemCondition.addEditorWidgets(
        editor,
        c -> {
          setItemCondition(c);
          consumer.accept(this);
        });
  }

  public void setItemCondition(ItemCondition itemCondition) {
    this.itemCondition = itemCondition;
  }

  public static class Serializer implements ItemCondition.Serializer {
    @Override
    public ItemCondition deserialize(JsonObject json) throws JsonParseException {
      return new EnchantedCondition(SerializationHelper.deserializeItemCondition(json));
    }

    @Override
    public void serialize(JsonObject json, ItemCondition condition) {
      if (!(condition instanceof EnchantedCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      SerializationHelper.serializeItemCondition(json, aCondition.itemCondition);
    }

    @Override
    public ItemCondition deserialize(CompoundTag tag) {
      return new EnchantedCondition(SerializationHelper.deserializeItemCondition(tag));
    }

    @Override
    public CompoundTag serialize(ItemCondition condition) {
      if (!(condition instanceof EnchantedCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      SerializationHelper.serializeItemCondition(tag, aCondition.itemCondition);
      return tag;
    }

    @Override
    public ItemCondition deserialize(FriendlyByteBuf buf) {
      return new EnchantedCondition(NetworkHelper.readItemCondition(buf));
    }

    @Override
    public void serialize(FriendlyByteBuf buf, ItemCondition condition) {
      if (!(condition instanceof EnchantedCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      NetworkHelper.writeItemCondition(buf, aCondition.itemCondition);
    }

    @Override
    public ItemCondition createDefaultInstance() {
      return new EnchantedCondition(new ItemTagCondition(Tags.Items.TOOLS_SWORDS.location()));
    }
  }
}
