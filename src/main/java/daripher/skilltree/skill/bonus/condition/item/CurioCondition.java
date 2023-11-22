package daripher.skilltree.skill.bonus.condition.item;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.init.PSTItemConditions;
import java.util.Objects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

public record CurioCondition(String slot) implements ItemCondition {
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
  }
}
