package daripher.skilltree.skill.bonus.predicate.item;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.init.PSTItemConditions;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;
import java.util.function.Consumer;

public final class ItemIdPredicate implements ItemStackPredicate {
  private ResourceLocation id;

  public ItemIdPredicate(ResourceLocation id) {
    this.id = id;
  }

  @Override
  public boolean test(ItemStack stack) {
    return ForgeRegistries.ITEMS.getValue(id) == stack.getItem();
  }

  @Override
  public String getDescriptionId() {
    Item item = ForgeRegistries.ITEMS.getValue(id);
    if (item != null) {
      return item.getDescriptionId();
    }
    return ItemStackPredicate.super.getDescriptionId();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ItemIdPredicate that = (ItemIdPredicate) o;
    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public ItemStackPredicate.Serializer getSerializer() {
    return PSTItemConditions.ITEM_ID.get();
  }

  @Override
  public void addEditorWidgets(SkillTreeEditor editor, Consumer<ItemStackPredicate> consumer) {
    editor.addLabel(0, 0, "Item Id", ChatFormatting.GREEN);
    editor.increaseHeight(19);
    editor
        .addTextField(0, 0, 200, 14, id.toString())
        .setSoftFilter(ItemIdPredicate::isItemId)
        .setResponder(text -> selectItemId(consumer, text));
    editor.increaseHeight(19);
  }

  private void selectItemId(Consumer<ItemStackPredicate> consumer, String text) {
    setId(ResourceLocation.parse(text));
    consumer.accept(this);
  }

  private static boolean isItemId(String text) {
    if (!ResourceLocation.isValidResourceLocation(text)) return false;
    return ForgeRegistries.ITEMS.containsKey(ResourceLocation.parse(text));
  }

  public void setId(ResourceLocation id) {
    this.id = id;
  }

  public static class Serializer implements ItemStackPredicate.Serializer {
    @Override
    public ItemStackPredicate deserialize(JsonObject json) throws JsonParseException {
      ResourceLocation id = ResourceLocation.parse(json.get("id").getAsString());
      return new ItemIdPredicate(id);
    }

    @Override
    public void serialize(JsonObject json, ItemStackPredicate condition) {
      if (!(condition instanceof ItemIdPredicate aCondition)) {
        throw new IllegalArgumentException();
      }
      json.addProperty("id", aCondition.id.toString());
    }

    @Override
    public ItemStackPredicate deserialize(CompoundTag tag) {
      Tag idTag = tag.get("id");
      Objects.requireNonNull(idTag);
      ResourceLocation id = ResourceLocation.parse(idTag.getAsString());
      return new ItemIdPredicate(id);
    }

    @Override
    public CompoundTag serialize(ItemStackPredicate condition) {
      if (!(condition instanceof ItemIdPredicate aCondition)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      tag.putString("id", aCondition.id.toString());
      return tag;
    }

    @Override
    public ItemStackPredicate deserialize(FriendlyByteBuf buf) {
      return new ItemIdPredicate(ResourceLocation.parse(buf.readUtf()));
    }

    @Override
    public void serialize(FriendlyByteBuf buf, ItemStackPredicate condition) {
      if (!(condition instanceof ItemIdPredicate aCondition)) {
        throw new IllegalArgumentException();
      }
      buf.writeUtf(aCondition.id.toString());
    }

    @Override
    public ItemStackPredicate createDefaultInstance() {
      return new ItemIdPredicate(ResourceLocation.parse("minecraft:shield"));
    }
  }
}
