package daripher.skilltree.skill.bonus.item;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.screen.SkillTreeEditor;
import daripher.skilltree.data.SerializationHelper;
import daripher.skilltree.init.PSTItemBonuses;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;

public record ItemSkillBonus(SkillBonus<?> bonus) implements ItemBonus<ItemSkillBonus> {
  @Override
  public boolean canMerge(ItemBonus<?> other) {
    if (!(other instanceof ItemSkillBonus otherBonus)) return false;
    return otherBonus.bonus.canMerge(this.bonus);
  }

  @Override
  public ItemSkillBonus merge(ItemBonus<?> other) {
    if (!(other instanceof ItemSkillBonus otherBonus)) {
      throw new IllegalArgumentException();
    }
    return new ItemSkillBonus(otherBonus.bonus.merge(this.bonus));
  }

  @Override
  public ItemSkillBonus copy() {
    return new ItemSkillBonus(bonus.copy());
  }

  @Override
  public ItemSkillBonus multiply(double multiplier) {
    bonus.multiply(multiplier);
    return this;
  }

  @Override
  public ItemBonus.Serializer getSerializer() {
    return PSTItemBonuses.SKILL_BONUS.get();
  }

  @Override
  public MutableComponent getTooltip() {
    return bonus.getTooltip();
  }

  @Override
  public void addEditorWidgets(SkillTreeEditor editor, int row) {
    // TODO
  }

  public static class Serializer implements ItemBonus.Serializer {
    @Override
    public ItemBonus<?> deserialize(JsonObject json) throws JsonParseException {
      return new ItemSkillBonus(SerializationHelper.deserializeSkillBonus(json));
    }

    @Override
    public void serialize(JsonObject json, ItemBonus<?> bonus) {
      if (!(bonus instanceof ItemSkillBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      SerializationHelper.serializeSkillBonus(json, aBonus.bonus);
    }

    @Override
    public ItemBonus<?> deserialize(CompoundTag tag) {
      return new ItemSkillBonus(SerializationHelper.deserializeSkillBonus(tag));
    }

    @Override
    public CompoundTag serialize(ItemBonus<?> bonus) {
      if (!(bonus instanceof ItemSkillBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      SerializationHelper.serializeSkillBonus(tag, aBonus.bonus);
      return tag;
    }

    @Override
    public ItemBonus<?> deserialize(FriendlyByteBuf buf) {
      return new ItemSkillBonus(NetworkHelper.readSkillBonus(buf));
    }

    @Override
    public void serialize(FriendlyByteBuf buf, ItemBonus<?> bonus) {
      if (!(bonus instanceof ItemSkillBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      NetworkHelper.writeSkillBonus(buf, aBonus.bonus);
    }
  }
}
