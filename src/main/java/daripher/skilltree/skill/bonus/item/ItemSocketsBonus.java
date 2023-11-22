package daripher.skilltree.skill.bonus.item;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.screen.SkillTreeEditor;
import daripher.skilltree.init.PSTItemBonuses;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public record ItemSocketsBonus(int sockets) implements ItemBonus<ItemSocketsBonus> {
  @Override
  public boolean canMerge(ItemBonus<?> other) {
    return other instanceof ItemSocketsBonus;
  }

  @Override
  public ItemSocketsBonus merge(ItemBonus<?> other) {
    if (!(other instanceof ItemSocketsBonus otherBonus)) {
      throw new IllegalArgumentException();
    }
    return new ItemSocketsBonus(this.sockets + otherBonus.sockets);
  }

  @Override
  public ItemSocketsBonus copy() {
    return new ItemSocketsBonus(sockets);
  }

  @Override
  public ItemSocketsBonus multiply(double multiplier) {
    return new ItemSocketsBonus((int) (sockets * multiplier));
  }

  @Override
  public ItemBonus.Serializer getSerializer() {
    return PSTItemBonuses.SOCKETS.get();
  }

  @Override
  public MutableComponent getTooltip() {
    return Component.translatable(getDescriptionId(), sockets);
  }

  @Override
  public void addEditorWidgets(SkillTreeEditor editor, int row) {
    // TODO
  }

  public static class Serializer implements ItemBonus.Serializer {
    @Override
    public ItemBonus<?> deserialize(JsonObject json) throws JsonParseException {
      return new ItemSocketsBonus(json.get("sockets").getAsInt());
    }

    @Override
    public void serialize(JsonObject json, ItemBonus<?> bonus) {
      if (!(bonus instanceof ItemSocketsBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      json.addProperty("sockets", aBonus.sockets);
    }

    @Override
    public ItemBonus<?> deserialize(CompoundTag tag) {
      return new ItemSocketsBonus(tag.getInt("sockets"));
    }

    @Override
    public CompoundTag serialize(ItemBonus<?> bonus) {
      if (!(bonus instanceof ItemSocketsBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      tag.putInt("sockets", aBonus.sockets);
      return tag;
    }

    @Override
    public ItemBonus<?> deserialize(FriendlyByteBuf buf) {
      return new ItemSocketsBonus(buf.readInt());
    }

    @Override
    public void serialize(FriendlyByteBuf buf, ItemBonus<?> bonus) {
      if (!(bonus instanceof ItemSocketsBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      buf.writeInt(aBonus.sockets);
    }
  }
}
