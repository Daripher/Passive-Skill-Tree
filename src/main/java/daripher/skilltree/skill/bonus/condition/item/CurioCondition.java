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
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

public final class CurioCondition implements ItemCondition {
  private String slot;

  public CurioCondition(String slot) {
    this.slot = slot;
  }

  @Override
  public boolean met(ItemStack stack) {
    SlotContext ctx = new SlotContext(slot, null, 0, false, false);
    return CuriosApi.getCuriosHelper().isStackValid(ctx, stack);
  }

  @Override
  public String getDescriptionId() {
    return "%s.%s".formatted(ItemCondition.super.getDescriptionId(), slot);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CurioCondition that = (CurioCondition) o;
    return Objects.equals(slot, that.slot);
  }

  @Override
  public int hashCode() {
    return Objects.hash(slot);
  }

  @Override
  public ItemCondition.Serializer getSerializer() {
    return PSTItemConditions.CURIO.get();
  }

  @Override
  public void addEditorWidgets(SkillTreeEditorScreen editor, Consumer<ItemCondition> consumer) {
    editor.addLabel(0, 0, "Type", ChatFormatting.GREEN);
    editor.shiftWidgets(0, 19);
    editor
        .addDropDownList(0, 0, 200, 14, 10, slot, CuriosApi.getSlotHelper().getSlotTypeIds())
        .setToNameFunc(s -> Component.literal(s.substring(0, 1).toUpperCase() + s.substring(1)))
        .setResponder(
            t -> {
              setSlot(t);
              consumer.accept(this);
            });
    editor.shiftWidgets(0, 19);
  }

  public void setSlot(String slot) {
    this.slot = slot;
  }

  public String getSlot() {
    return slot;
  }

  public static class Serializer implements ItemCondition.Serializer {
    @Override
    public ItemCondition deserialize(JsonObject json) throws JsonParseException {
      return new CurioCondition(json.get("slot").getAsString());
    }

    @Override
    public void serialize(JsonObject json, ItemCondition condition) {
      if (!(condition instanceof CurioCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      json.addProperty("slot", aCondition.slot);
    }

    @Override
    public ItemCondition deserialize(CompoundTag tag) {
      return new CurioCondition(tag.getString("slot"));
    }

    @Override
    public CompoundTag serialize(ItemCondition condition) {
      if (!(condition instanceof CurioCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      tag.putString("slot", aCondition.slot);
      return tag;
    }

    @Override
    public ItemCondition deserialize(FriendlyByteBuf buf) {
      return new CurioCondition(buf.readUtf());
    }

    @Override
    public void serialize(FriendlyByteBuf buf, ItemCondition condition) {
      if (!(condition instanceof CurioCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      buf.writeUtf(aCondition.slot);
    }

    @Override
    public ItemCondition createDefaultInstance() {
      return new CurioCondition("ring");
    }
  }
}
