package daripher.skilltree.skill.bonus.condition.item;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.screen.SkillTreeEditorScreen;
import daripher.skilltree.init.PSTItemConditions;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public final class ItemIdCondition implements ItemCondition {
  private ResourceLocation id;

  public ItemIdCondition(ResourceLocation id) {
    this.id = id;
  }

  @Override
  public boolean met(ItemStack stack) {
    return ForgeRegistries.ITEMS.getValue(id) == stack.getItem();
  }

  @Override
  public String getDescriptionId() {
    Item item = ForgeRegistries.ITEMS.getValue(id);
    if (item != null) {
      return item.getDescriptionId();
    }
    return ItemCondition.super.getDescriptionId();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ItemIdCondition that = (ItemIdCondition) o;
    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public ItemCondition.Serializer getSerializer() {
    return PSTItemConditions.ITEM_ID.get();
  }

  @Override
  public void addEditorWidgets(SkillTreeEditorScreen editor, Consumer<ItemCondition> consumer) {
    editor.addLabel(0, 0, "Item Id", ChatFormatting.GREEN);
    editor.shiftWidgets(0, 19);
    editor
        .addTextField(0, 0, 200, 14, id.toString())
        .setSoftFilter(
            s -> {
              if (!ResourceLocation.isValidResourceLocation(s)) return false;
              return ForgeRegistries.ITEMS.containsKey(new ResourceLocation(s));
            })
        .setResponder(
            s -> {
              setId(new ResourceLocation(s));
              consumer.accept(this);
            });
    editor.shiftWidgets(0, 19);
  }

  public void setId(ResourceLocation id) {
    this.id = id;
  }

  public static class Serializer implements ItemCondition.Serializer {
    @Override
    public ItemCondition deserialize(JsonObject json) throws JsonParseException {
      ResourceLocation id = new ResourceLocation(json.get("id").getAsString());
      return new ItemIdCondition(id);
    }

    @Override
    public void serialize(JsonObject json, ItemCondition condition) {
      if (!(condition instanceof ItemIdCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      json.addProperty("id", aCondition.id.toString());
    }

    @Override
    public ItemCondition deserialize(CompoundTag tag) {
      Tag idTag = tag.get("id");
      Objects.requireNonNull(idTag);
      ResourceLocation id = new ResourceLocation(idTag.getAsString());
      return new ItemIdCondition(id);
    }

    @Override
    public CompoundTag serialize(ItemCondition condition) {
      if (!(condition instanceof ItemIdCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      tag.putString("id", aCondition.id.toString());
      return tag;
    }

    @Override
    public ItemCondition deserialize(FriendlyByteBuf buf) {
      return new ItemIdCondition(new ResourceLocation(buf.readUtf()));
    }

    @Override
    public void serialize(FriendlyByteBuf buf, ItemCondition condition) {
      if (!(condition instanceof ItemIdCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      buf.writeUtf(aCondition.id.toString());
    }

    @Override
    public ItemCondition createDefaultInstance() {
      return new ItemIdCondition(new ResourceLocation("minecraft:shield"));
    }
  }
}
