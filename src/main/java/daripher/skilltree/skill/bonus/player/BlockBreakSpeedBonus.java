package daripher.skilltree.skill.bonus.player;

import com.google.gson.*;
import daripher.skilltree.client.screen.SkillTreeEditorScreen;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.data.SerializationHelper;
import daripher.skilltree.init.PSTLivingConditions;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.condition.living.LivingCondition;
import daripher.skilltree.skill.bonus.condition.living.NoneLivingCondition;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public final class BlockBreakSpeedBonus implements SkillBonus<BlockBreakSpeedBonus> {
  private @Nonnull LivingCondition playerCondition;
  private float multiplier;

  public BlockBreakSpeedBonus(@Nonnull LivingCondition playerCondition, float multiplier) {
    this.playerCondition = playerCondition;
    this.multiplier = multiplier;
  }

  @Override
  public SkillBonus.Serializer getSerializer() {
    return PSTSkillBonuses.BLOCK_BREAK_SPEED.get();
  }

  @Override
  public BlockBreakSpeedBonus copy() {
    return new BlockBreakSpeedBonus(playerCondition, multiplier);
  }

  @Override
  public BlockBreakSpeedBonus multiply(double multiplier) {
    this.multiplier = (float) (this.multiplier * multiplier);
    return this;
  }

  @Override
  public boolean canMerge(SkillBonus<?> other) {
    if (!(other instanceof BlockBreakSpeedBonus otherBonus)) return false;
    return Objects.equals(otherBonus.playerCondition, this.playerCondition);
  }

  @Override
  public SkillBonus<BlockBreakSpeedBonus> merge(SkillBonus<?> other) {
    if (!(other instanceof BlockBreakSpeedBonus otherBonus)) {
      throw new IllegalArgumentException();
    }
    return new BlockBreakSpeedBonus(playerCondition, otherBonus.multiplier + this.multiplier);
  }

  @Override
  public MutableComponent getTooltip() {
    MutableComponent bonusTooltip =
        TooltipHelper.getSkillBonusTooltip(
            getDescriptionId(), multiplier, AttributeModifier.Operation.MULTIPLY_BASE);
    bonusTooltip = playerCondition.getTooltip(bonusTooltip, "you");
    return bonusTooltip.withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
  }

  @Override
  public boolean isPositive() {
    return multiplier > 0;
  }

  @Override
  public void addEditorWidgets(
      SkillTreeEditorScreen editor, int index, Consumer<BlockBreakSpeedBonus> consumer) {
    editor.addLabel(0, 0, "Multiplier", ChatFormatting.GOLD);
    editor.shiftWidgets(0, 19);
    editor
        .addNumericTextField(0, 0, 50, 14, multiplier)
        .setNumericResponder(
            v -> {
              setMultiplier(v.floatValue());
              consumer.accept(this.copy());
            });
    editor.shiftWidgets(0, 19);
    editor.addLabel(0, 0, "Player Condition", ChatFormatting.GOLD);
    editor.shiftWidgets(0, 19);
    editor
        .addDropDownList(0, 0, 200, 14, 10, playerCondition, PSTLivingConditions.conditionsList())
        .setToNameFunc(c -> Component.literal(PSTLivingConditions.getName(c)))
        .setResponder(
            c -> {
              setPlayerCondition(c);
              consumer.accept(this.copy());
              editor.rebuildWidgets();
            });
    editor.shiftWidgets(0, 19);
    playerCondition.addEditorWidgets(
        editor,
        c -> {
          setPlayerCondition(c);
          consumer.accept(this.copy());
        });
  }

  public void setPlayerCondition(@Nonnull LivingCondition playerCondition) {
    this.playerCondition = playerCondition;
  }

  public void setMultiplier(float multiplier) {
    this.multiplier = multiplier;
  }

  @Nonnull
  public LivingCondition getPlayerCondition() {
    return playerCondition;
  }

  public float getMultiplier() {
    return multiplier;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj == null || obj.getClass() != this.getClass()) return false;
    BlockBreakSpeedBonus that = (BlockBreakSpeedBonus) obj;
    if (!Objects.equals(this.playerCondition, that.playerCondition)) return false;
    return this.multiplier == that.multiplier;
  }

  @Override
  public int hashCode() {
    return Objects.hash(playerCondition, multiplier);
  }

  public static class Serializer implements SkillBonus.Serializer {
    @Override
    public BlockBreakSpeedBonus deserialize(JsonObject json) throws JsonParseException {
      LivingCondition condition =
          SerializationHelper.deserializeLivingCondition(json, "player_condition");
      float multiplier = json.get("multiplier").getAsFloat();
      return new BlockBreakSpeedBonus(condition, multiplier);
    }

    @Override
    public void serialize(JsonObject json, SkillBonus<?> bonus) {
      if (!(bonus instanceof BlockBreakSpeedBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      SerializationHelper.serializeLivingCondition(
          json, aBonus.playerCondition, "player_condition");
      json.addProperty("multiplier", aBonus.multiplier);
    }

    @Override
    public BlockBreakSpeedBonus deserialize(CompoundTag tag) {
      LivingCondition condition =
          SerializationHelper.deserializeLivingCondition(tag, "player_condition");
      float multiplier = tag.getFloat("multiplier");
      return new BlockBreakSpeedBonus(condition, multiplier);
    }

    @Override
    public CompoundTag serialize(SkillBonus<?> bonus) {
      if (!(bonus instanceof BlockBreakSpeedBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      SerializationHelper.serializeLivingCondition(tag, aBonus.playerCondition, "player_condition");
      tag.putFloat("multiplier", aBonus.multiplier);
      return tag;
    }

    @Override
    public BlockBreakSpeedBonus deserialize(FriendlyByteBuf buf) {
      return new BlockBreakSpeedBonus(NetworkHelper.readLivingCondition(buf), buf.readFloat());
    }

    @Override
    public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
      if (!(bonus instanceof BlockBreakSpeedBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      NetworkHelper.writeLivingCondition(buf, aBonus.playerCondition);
      buf.writeFloat(aBonus.multiplier);
    }

    @Override
    public SkillBonus<?> createDefaultInstance() {
      return new BlockBreakSpeedBonus(new NoneLivingCondition(), 0.1f);
    }
  }
}
