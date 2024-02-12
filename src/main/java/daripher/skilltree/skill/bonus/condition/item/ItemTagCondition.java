package daripher.skilltree.skill.bonus.condition.item;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.screen.SkillTreeEditorScreen;
import daripher.skilltree.init.PSTItemConditions;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.Tags;

public class ItemTagCondition implements ItemCondition {
  private ResourceLocation tagId;

  public ItemTagCondition(ResourceLocation tagId) {
    this.tagId = tagId;
  }

  @Override
  public boolean met(ItemStack stack) {
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
    ItemTagCondition that = (ItemTagCondition) o;
    return Objects.equals(tagId, that.tagId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(tagId);
  }

  @Override
  public ItemCondition.Serializer getSerializer() {
    return PSTItemConditions.TAG.get();
  }

  @Override
  public void addEditorWidgets(SkillTreeEditorScreen editor, Consumer<ItemCondition> consumer) {
    editor.addLabel(0, 0, "Tag", ChatFormatting.GREEN);
    editor.shiftWidgets(0, 19);
    editor
        .addTextField(0, 0, 200, 14, tagId.toString())
        .setSoftFilter(ResourceLocation::isValidResourceLocation)
        .setResponder(
            s -> {
              if (!ResourceLocation.isValidResourceLocation(s)) return;
              setTagId(new ResourceLocation(s));
              consumer.accept(this);
            });
    editor.shiftWidgets(0, 19);
  }

  public void setTagId(ResourceLocation tagId) {
    this.tagId = tagId;
  }

  public static class Serializer implements ItemCondition.Serializer {
    @Override
    public ItemCondition deserialize(JsonObject json) throws JsonParseException {
      ResourceLocation tagId = new ResourceLocation(json.get("tag_id").getAsString());
      return new ItemTagCondition(tagId);
    }

    @Override
    public void serialize(JsonObject json, ItemCondition condition) {
      if (!(condition instanceof ItemTagCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      json.addProperty("tag_id", aCondition.tagId.toString());
    }

    @Override
    public ItemCondition deserialize(CompoundTag tag) {
      ResourceLocation tagId = new ResourceLocation(tag.getString("tag_id"));
      return new ItemTagCondition(tagId);
    }

    @Override
    public CompoundTag serialize(ItemCondition condition) {
      if (!(condition instanceof ItemTagCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      tag.putString("tag_id", aCondition.tagId.toString());
      return tag;
    }

    @Override
    public ItemCondition deserialize(FriendlyByteBuf buf) {
      ResourceLocation tagId = new ResourceLocation(buf.readUtf());
      return new ItemTagCondition(tagId);
    }

    @Override
    public void serialize(FriendlyByteBuf buf, ItemCondition condition) {
      if (!(condition instanceof ItemTagCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      buf.writeUtf(aCondition.tagId.toString());
    }

    @Override
    public ItemCondition createDefaultInstance() {
      return new ItemTagCondition(Tags.Items.ARMORS.location());
    }
  }
}
