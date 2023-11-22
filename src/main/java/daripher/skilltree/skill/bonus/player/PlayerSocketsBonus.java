package daripher.skilltree.skill.bonus.player;

import com.google.gson.*;
import daripher.skilltree.client.screen.SkillTreeEditor;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.data.SerializationHelper;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.condition.item.ItemCondition;
import java.util.Objects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;

public record PlayerSocketsBonus(ItemCondition itemCondition, int sockets)
    implements SkillBonus<PlayerSocketsBonus> {
  @Override
  public SkillBonus.Serializer getSerializer() {
    return PSTSkillBonuses.PLAYER_SOCKETS.get();
  }

  @Override
  public SkillBonus<PlayerSocketsBonus> copy() {
    return new PlayerSocketsBonus(itemCondition, sockets);
  }

  @Override
  public PlayerSocketsBonus multiply(double multiplier) {
    return new PlayerSocketsBonus(itemCondition, (int) (sockets * multiplier));
  }

  @Override
  public boolean canMerge(SkillBonus<?> other) {
    if (!(other instanceof PlayerSocketsBonus otherBonus)) return false;
    return Objects.equals(otherBonus.itemCondition, this.itemCondition);
  }

  @Override
  public SkillBonus<PlayerSocketsBonus> merge(SkillBonus<?> other) {
    if (!(other instanceof PlayerSocketsBonus otherBonus)) {
      throw new IllegalArgumentException();
    }
    return new PlayerSocketsBonus(itemCondition, otherBonus.sockets + this.sockets);
  }

  @Override
  public MutableComponent getTooltip() {
    MutableComponent itemDescription =
        TooltipHelper.getOptionalTooltip(itemCondition.getDescriptionId(), "plural");
    double visibleSockets = sockets;
    if (sockets < 0) visibleSockets *= -1;
    String operationDescription = sockets > 0 ? "plus" : "take";
    operationDescription = "attribute.modifier." + operationDescription + ".0";
    String multiplierDescription = ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(visibleSockets);
    MutableComponent bonusDescription = Component.translatable(getDescriptionId(), itemDescription);
    return Component.translatable(operationDescription, multiplierDescription, bonusDescription)
        .withStyle(Style.EMPTY.withColor(0x7B7BE5));
  }

  @Override
  public void addEditorWidgets(SkillTreeEditor editor, int row) {
    // TODO: add widgets
  }

  public static class Serializer implements SkillBonus.Serializer {
    @Override
    public PlayerSocketsBonus deserialize(JsonObject json) throws JsonParseException {
      ItemCondition condition = SerializationHelper.deserializeItemCondition(json);
      int sockets = json.get("sockets").getAsInt();
      return new PlayerSocketsBonus(condition, sockets);
    }

    @Override
    public void serialize(JsonObject json, SkillBonus<?> bonus) {
      if (!(bonus instanceof PlayerSocketsBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      SerializationHelper.serializeItemCondition(json, aBonus.itemCondition);
      json.addProperty("sockets", aBonus.sockets);
    }

    @Override
    public PlayerSocketsBonus deserialize(CompoundTag tag) {
      ItemCondition condition = SerializationHelper.deserializeItemCondition(tag);
      int sockets = tag.getInt("sockets");
      return new PlayerSocketsBonus(condition, sockets);
    }

    @Override
    public CompoundTag serialize(SkillBonus<?> bonus) {
      if (!(bonus instanceof PlayerSocketsBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      SerializationHelper.serializeItemCondition(tag, aBonus.itemCondition);
      tag.putInt("sockets", aBonus.sockets);
      return tag;
    }

    @Override
    public PlayerSocketsBonus deserialize(FriendlyByteBuf buf) {
      return new PlayerSocketsBonus(NetworkHelper.readItemCondition(buf), buf.readInt());
    }

    @Override
    public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
      if (!(bonus instanceof PlayerSocketsBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      NetworkHelper.writeItemCondition(buf, aBonus.itemCondition);
      buf.writeInt(aBonus.sockets);
    }
  }
}
