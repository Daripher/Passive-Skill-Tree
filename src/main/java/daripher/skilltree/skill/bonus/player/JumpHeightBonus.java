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
import net.minecraft.world.entity.player.Player;

public final class JumpHeightBonus implements SkillBonus<JumpHeightBonus> {
  private @Nonnull LivingCondition playerCondition;
  private float multiplier;

  public JumpHeightBonus(@Nonnull LivingCondition playerCondition, float multiplier) {
    this.playerCondition = playerCondition;
    this.multiplier = multiplier;
  }

  public float getJumpHeightMultiplier(Player player) {
    if (!playerCondition.met(player)) return 0f;
    return multiplier;
  }

  @Override
  public SkillBonus.Serializer getSerializer() {
    return PSTSkillBonuses.JUMP_HEIGHT.get();
  }

  @Override
  public JumpHeightBonus copy() {
    return new JumpHeightBonus(playerCondition, multiplier);
  }

  @Override
  public JumpHeightBonus multiply(double multiplier) {
    return new JumpHeightBonus(playerCondition, (float) (getMultiplier() * multiplier));
  }

  @Override
  public boolean canMerge(SkillBonus<?> other) {
    if (!(other instanceof JumpHeightBonus otherBonus)) return false;
    return Objects.equals(otherBonus.playerCondition, this.playerCondition);
  }

  @Override
  public SkillBonus<JumpHeightBonus> merge(SkillBonus<?> other) {
    if (!(other instanceof JumpHeightBonus otherBonus)) {
      throw new IllegalArgumentException();
    }
    return new JumpHeightBonus(playerCondition, otherBonus.multiplier + this.multiplier);
  }

  @Override
  public MutableComponent getTooltip() {
    MutableComponent tooltip =
        TooltipHelper.getSkillBonusTooltip(
            getDescriptionId(), multiplier, AttributeModifier.Operation.MULTIPLY_BASE);
    tooltip = playerCondition.getTooltip(tooltip, "you");
    return tooltip.withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
  }

  @Override
  public boolean isPositive() {
    return multiplier > 0;
  }

  @Override
  public void addEditorWidgets(
      SkillTreeEditorScreen editor, int row, Consumer<JumpHeightBonus> consumer) {
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

  public float getMultiplier() {
    return multiplier;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj == null || obj.getClass() != this.getClass()) return false;
    JumpHeightBonus that = (JumpHeightBonus) obj;
    if (!Objects.equals(this.playerCondition, that.playerCondition)) return false;
    return this.multiplier == that.multiplier;
  }

  @Override
  public int hashCode() {
    return Objects.hash(playerCondition, multiplier);
  }

  public static class Serializer implements SkillBonus.Serializer {
    @Override
    public JumpHeightBonus deserialize(JsonObject json) throws JsonParseException {
      LivingCondition condition =
          SerializationHelper.deserializeLivingCondition(json, "player_condition");
      float multiplier = json.get("multiplier").getAsFloat();
      return new JumpHeightBonus(condition, multiplier);
    }

    @Override
    public void serialize(JsonObject json, SkillBonus<?> bonus) {
      if (!(bonus instanceof JumpHeightBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      SerializationHelper.serializeLivingCondition(
          json, aBonus.playerCondition, "player_condition");
      json.addProperty("multiplier", aBonus.multiplier);
    }

    @Override
    public JumpHeightBonus deserialize(CompoundTag tag) {
      LivingCondition condition =
          SerializationHelper.deserializeLivingCondition(tag, "player_condition");
      float multiplier = tag.getFloat("multiplier");
      return new JumpHeightBonus(condition, multiplier);
    }

    @Override
    public CompoundTag serialize(SkillBonus<?> bonus) {
      if (!(bonus instanceof JumpHeightBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      SerializationHelper.serializeLivingCondition(tag, aBonus.playerCondition, "player_condition");
      tag.putFloat("multiplier", aBonus.multiplier);
      return tag;
    }

    @Override
    public JumpHeightBonus deserialize(FriendlyByteBuf buf) {
      return new JumpHeightBonus(NetworkHelper.readLivingCondition(buf), buf.readFloat());
    }

    @Override
    public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
      if (!(bonus instanceof JumpHeightBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      NetworkHelper.writeLivingCondition(buf, aBonus.playerCondition);
      buf.writeFloat(aBonus.multiplier);
    }

    @Override
    public SkillBonus<?> createDefaultInstance() {
      return new JumpHeightBonus(new NoneLivingCondition(), 0.1f);
    }
  }
}
