package daripher.skilltree.skill.bonus.predicate.item;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.init.PSTItemConditions;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.Tags;

public class ItemTagPredicate implements ItemStackPredicate {
  private ResourceLocation tagId;

  public ItemTagPredicate(ResourceLocation tagId) {
    this.tagId = tagId;
  }

  @Override
  public boolean test(ItemStack stack) {
    return stack.is(ItemTags.create(tagId));
  }

  @Override
  public String getDescriptionId() {
    return "item_tag.%s".formatted(tagId.toString());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ItemTagPredicate that = (ItemTagPredicate) o;
    return Objects.equals(tagId, that.tagId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(tagId);
  }

  @Override
  public ItemStackPredicate.Serializer getSerializer() {
    return PSTItemConditions.TAG.get();
  }

  @Override
  public void addEditorWidgets(SkillTreeEditor editor, Consumer<ItemStackPredicate> consumer) {
    editor.addLabel(0, 0, "Tag", ChatFormatting.GREEN);
    editor.increaseHeight(19);
    editor
        .addTextField(0, 0, 200, 14, tagId.toString())
        .setSoftFilter(text -> ResourceLocation.tryParse(text) != null)
        .setResponder(text -> selectTagId(consumer, text));
    editor.increaseHeight(19);
  }

  private void selectTagId(Consumer<ItemStackPredicate> consumer, String text) {
    setTagId(ResourceLocation.parse(text));
    consumer.accept(this);
  }

  public void setTagId(ResourceLocation tagId) {
    this.tagId = tagId;
  }

  public static class Serializer implements ItemStackPredicate.Serializer {
    @Override
    public ItemStackPredicate deserialize(JsonObject json) throws JsonParseException {
      ResourceLocation tagId = ResourceLocation.parse(json.get("tag_id").getAsString());
      return new ItemTagPredicate(tagId);
    }

    @Override
    public void serialize(JsonObject json, ItemStackPredicate condition) {
      if (!(condition instanceof ItemTagPredicate aCondition)) {
        throw new IllegalArgumentException();
      }
      json.addProperty("tag_id", aCondition.tagId.toString());
    }

    @Override
    public ItemStackPredicate deserialize(CompoundTag tag) {
      ResourceLocation tagId = ResourceLocation.parse(tag.getString("tag_id"));
      return new ItemTagPredicate(tagId);
    }

    @Override
    public CompoundTag serialize(ItemStackPredicate condition) {
      if (!(condition instanceof ItemTagPredicate aCondition)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      tag.putString("tag_id", aCondition.tagId.toString());
      return tag;
    }

    @Override
    public ItemStackPredicate deserialize(FriendlyByteBuf buf) {
      ResourceLocation tagId = ResourceLocation.parse(buf.readUtf());
      return new ItemTagPredicate(tagId);
    }

    @Override
    public void serialize(FriendlyByteBuf buf, ItemStackPredicate condition) {
      if (!(condition instanceof ItemTagPredicate aCondition)) {
        throw new IllegalArgumentException();
      }
      buf.writeUtf(aCondition.tagId.toString());
    }

    @Override
    public ItemStackPredicate createDefaultInstance() {
      return new ItemTagPredicate(Tags.Items.ARMORS.location());
    }
  }
}
