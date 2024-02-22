package daripher.skilltree.skill.bonus.player;

import com.google.gson.*;
import daripher.skilltree.client.screen.SkillTreeEditorScreen;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTItemConditions;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.condition.item.ItemCondition;
import daripher.skilltree.skill.bonus.condition.item.NoneItemCondition;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public final class PlayerSocketsBonus implements SkillBonus<PlayerSocketsBonus> {
  private @Nonnull ItemCondition itemCondition;
  private int sockets;

  public PlayerSocketsBonus(@Nonnull ItemCondition itemCondition, int sockets) {
    this.itemCondition = itemCondition;
    this.sockets = sockets;
  }

  @Override
  public SkillBonus.Serializer getSerializer() {
    return PSTSkillBonuses.PLAYER_SOCKETS.get();
  }

  @Override
  public PlayerSocketsBonus copy() {
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
    Component itemDescription = itemCondition.getTooltip();
    Component bonusDescription = Component.translatable(getDescriptionId(), itemDescription);
    AttributeModifier.Operation operation = AttributeModifier.Operation.ADDITION;
    return TooltipHelper.getSkillBonusTooltip(bonusDescription, sockets, operation)
        .withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
  }

  @Override
  public boolean isPositive() {
    return sockets > 0;
  }

  @Override
  public void addEditorWidgets(
      SkillTreeEditorScreen editor, int row, Consumer<PlayerSocketsBonus> consumer) {
    editor.addLabel(0, 0, "Multiplier", ChatFormatting.GOLD);
    editor.shiftWidgets(0, 19);
    editor
        .addNumericTextField(0, 0, 50, 14, sockets)
        .setNumericResponder(
            v -> {
              setSockets(v.intValue());
              consumer.accept(this.copy());
            });
    editor.shiftWidgets(0, 19);
    editor.addLabel(0, 0, "Item Condition", ChatFormatting.GOLD);
    editor.shiftWidgets(0, 19);
    editor
        .addDropDownList(0, 0, 200, 14, 10, itemCondition, PSTItemConditions.conditionsList())
        .setToNameFunc(a -> Component.literal(PSTItemConditions.getName(a)))
        .setResponder(
            c -> {
              setItemCondition(c);
              consumer.accept(this.copy());
              editor.rebuildWidgets();
            });
    editor.shiftWidgets(0, 19);
    itemCondition.addEditorWidgets(
        editor,
        c -> {
          setItemCondition(c);
          consumer.accept(this.copy());
        });
  }

  public void setItemCondition(@Nonnull ItemCondition itemCondition) {
    this.itemCondition = itemCondition;
  }

  public void setSockets(int sockets) {
    this.sockets = sockets;
  }

  @Nonnull
  public ItemCondition getItemCondition() {
    return itemCondition;
  }

  public int getSockets() {
    return sockets;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj == null || obj.getClass() != this.getClass()) return false;
    PlayerSocketsBonus that = (PlayerSocketsBonus) obj;
    return Objects.equals(this.itemCondition, that.itemCondition) && this.sockets == that.sockets;
  }

  @Override
  public int hashCode() {
    return Objects.hash(itemCondition, sockets);
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

    @Override
    public SkillBonus<?> createDefaultInstance() {
      return new PlayerSocketsBonus(NoneItemCondition.INSTANCE, 1);
    }
  }
}
