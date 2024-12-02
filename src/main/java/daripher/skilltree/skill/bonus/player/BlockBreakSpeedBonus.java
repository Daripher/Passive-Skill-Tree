package daripher.skilltree.skill.bonus.player;

import com.google.gson.*;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.condition.living.LivingCondition;
import daripher.skilltree.skill.bonus.condition.living.NoneLivingCondition;
import daripher.skilltree.skill.bonus.multiplier.LivingMultiplier;
import daripher.skilltree.skill.bonus.multiplier.NoneLivingMultiplier;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

public final class BlockBreakSpeedBonus implements SkillBonus<BlockBreakSpeedBonus> {
  private float multiplier;
  private @Nonnull LivingCondition playerCondition = NoneLivingCondition.INSTANCE;
  private @Nonnull LivingMultiplier playerMultiplier = NoneLivingMultiplier.INSTANCE;

  public BlockBreakSpeedBonus(float multiplier) {
    this.multiplier = multiplier;
  }

  public float getMultiplier(Player player) {
    if (!playerCondition.met(player)) return 0f;
    return multiplier * playerMultiplier.getValue(player);
  }

  @Override
  public SkillBonus.Serializer getSerializer() {
    return PSTSkillBonuses.BLOCK_BREAK_SPEED.get();
  }

  @Override
  public BlockBreakSpeedBonus copy() {
    return new BlockBreakSpeedBonus(multiplier)
        .setPlayerCondition(playerCondition)
        .setPlayerMultiplier(playerMultiplier);
  }

  @Override
  public BlockBreakSpeedBonus multiply(double multiplier) {
    this.multiplier = (float) (this.multiplier * multiplier);
    return this;
  }

  @Override
  public boolean canMerge(SkillBonus<?> other) {
    if (!(other instanceof BlockBreakSpeedBonus otherBonus)) return false;
    if (!Objects.equals(otherBonus.playerCondition, this.playerCondition)) return false;
    return Objects.equals(otherBonus.playerMultiplier, this.playerMultiplier);
  }

  @Override
  public SkillBonus<BlockBreakSpeedBonus> merge(SkillBonus<?> other) {
    if (!(other instanceof BlockBreakSpeedBonus otherBonus)) {
      throw new IllegalArgumentException();
    }
    return new BlockBreakSpeedBonus(otherBonus.multiplier + this.multiplier)
        .setPlayerCondition(playerCondition)
        .setPlayerMultiplier(playerMultiplier);
  }

  @Override
  public MutableComponent getTooltip() {
    MutableComponent bonusTooltip =
        TooltipHelper.getSkillBonusTooltip(
            getDescriptionId(), multiplier, AttributeModifier.Operation.MULTIPLY_BASE);
    bonusTooltip = playerCondition.getTooltip(bonusTooltip, Target.PLAYER);
    bonusTooltip = playerMultiplier.getTooltip(bonusTooltip, Target.PLAYER);
    return bonusTooltip.withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
  }

  @Override
  public boolean isPositive() {
    return multiplier > 0;
  }

  @Override
  public void addEditorWidgets(
      SkillTreeEditor editor, int index, Consumer<BlockBreakSpeedBonus> consumer) {
    editor.addLabel(0, 0, "Multiplier", ChatFormatting.GOLD);
    editor.increaseHeight(19);
    editor
        .addNumericTextField(0, 0, 50, 14, multiplier)
        .setNumericResponder(value -> selectMultiplier(consumer, value));
    editor.increaseHeight(19);
    editor.addLabel(0, 0, "Player Condition", ChatFormatting.GOLD);
    editor.increaseHeight(19);
    editor
        .addSelectionMenu(0, 0, 200, playerCondition)
        .setResponder(condition -> selectPlayerCondition(editor, consumer, condition))
        .setMenuInitFunc(() -> addPlayerConditionWidgets(editor, consumer));
    editor.increaseHeight(19);
    editor.addLabel(0, 0, "Player Multiplier", ChatFormatting.GOLD);
    editor.increaseHeight(19);
    editor
        .addSelectionMenu(0, 0, 200, playerMultiplier)
        .setResponder(multiplier -> selectPlayerMultiplier(editor, consumer, multiplier))
        .setMenuInitFunc(() -> addPlayerMultiplierWidgets(editor, consumer));
    editor.increaseHeight(19);
  }

  private void selectPlayerCondition(
      SkillTreeEditor editor, Consumer<BlockBreakSpeedBonus> consumer, LivingCondition condition) {
    setPlayerCondition(condition);
    consumer.accept(this.copy());
    editor.rebuildWidgets();
  }

  private void selectPlayerMultiplier(
      SkillTreeEditor editor,
      Consumer<BlockBreakSpeedBonus> consumer,
      LivingMultiplier multiplier) {
    setPlayerMultiplier(multiplier);
    consumer.accept(this.copy());
    editor.rebuildWidgets();
  }

  private void selectMultiplier(Consumer<BlockBreakSpeedBonus> consumer, Double value) {
    setMultiplier(value.floatValue());
    consumer.accept(this.copy());
  }

  private void addPlayerConditionWidgets(
      SkillTreeEditor editor, Consumer<BlockBreakSpeedBonus> consumer) {
    playerCondition.addEditorWidgets(
        editor,
        c -> {
          setPlayerCondition(c);
          consumer.accept(this.copy());
        });
  }

  private void addPlayerMultiplierWidgets(
      SkillTreeEditor editor, Consumer<BlockBreakSpeedBonus> consumer) {
    playerMultiplier.addEditorWidgets(
        editor,
        m -> {
          setPlayerMultiplier(m);
          consumer.accept(this.copy());
        });
  }

  public BlockBreakSpeedBonus setPlayerCondition(@Nonnull LivingCondition playerCondition) {
    this.playerCondition = playerCondition;
    return this;
  }

  public BlockBreakSpeedBonus setPlayerMultiplier(@Nonnull LivingMultiplier playerMultiplier) {
    this.playerMultiplier = playerMultiplier;
    return this;
  }

  public BlockBreakSpeedBonus setMultiplier(float multiplier) {
    this.multiplier = multiplier;
    return this;
  }

  @Nonnull
  public LivingCondition getPlayerCondition() {
    return playerCondition;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BlockBreakSpeedBonus that = (BlockBreakSpeedBonus) o;
    return Float.compare(multiplier, that.multiplier) == 0
        && Objects.equals(playerCondition, that.playerCondition)
        && Objects.equals(playerMultiplier, that.playerMultiplier);
  }

  @Override
  public int hashCode() {
    return Objects.hash(multiplier, playerCondition, playerMultiplier);
  }

  public static class Serializer implements SkillBonus.Serializer {
    @Override
    public BlockBreakSpeedBonus deserialize(JsonObject json) throws JsonParseException {
      float multiplier = SerializationHelper.getElement(json, "multiplier").getAsFloat();
      LivingCondition playerCondition =
          SerializationHelper.deserializeLivingCondition(json, "player_condition");
      LivingMultiplier playerMultiplier =
          SerializationHelper.deserializeLivingMultiplier(json, "player_multiplier");
      return new BlockBreakSpeedBonus(multiplier)
          .setPlayerCondition(playerCondition)
          .setPlayerMultiplier(playerMultiplier);
    }

    @Override
    public void serialize(JsonObject json, SkillBonus<?> bonus) {
      if (!(bonus instanceof BlockBreakSpeedBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      json.addProperty("multiplier", aBonus.multiplier);
      SerializationHelper.serializeLivingCondition(
          json, aBonus.playerCondition, "player_condition");
      SerializationHelper.serializeLivingMultiplier(
          json, aBonus.playerMultiplier, "player_multiplier");
    }

    @Override
    public BlockBreakSpeedBonus deserialize(CompoundTag tag) {
      float multiplier = tag.getFloat("multiplier");
      LivingCondition playerCondition =
          SerializationHelper.deserializeLivingCondition(tag, "player_condition");
      LivingMultiplier playerMultiplier =
          SerializationHelper.deserializeLivingMultiplier(tag, "player_multiplier");
      return new BlockBreakSpeedBonus(multiplier)
          .setPlayerCondition(playerCondition)
          .setPlayerMultiplier(playerMultiplier);
    }

    @Override
    public CompoundTag serialize(SkillBonus<?> bonus) {
      if (!(bonus instanceof BlockBreakSpeedBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      SerializationHelper.serializeLivingCondition(tag, aBonus.playerCondition, "player_condition");
      SerializationHelper.serializeLivingMultiplier(
          tag, aBonus.playerMultiplier, "player_multiplier");
      tag.putFloat("multiplier", aBonus.multiplier);
      return tag;
    }

    @Override
    public BlockBreakSpeedBonus deserialize(FriendlyByteBuf buf) {
      LivingCondition playerCondition = NetworkHelper.readLivingCondition(buf);
      LivingMultiplier playerMultiplier = NetworkHelper.readLivingMultiplier(buf);
      return new BlockBreakSpeedBonus(buf.readFloat())
          .setPlayerCondition(playerCondition)
          .setPlayerMultiplier(playerMultiplier);
    }

    @Override
    public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
      if (!(bonus instanceof BlockBreakSpeedBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      NetworkHelper.writeLivingCondition(buf, aBonus.playerCondition);
      NetworkHelper.writeLivingMultiplier(buf, aBonus.playerMultiplier);
      buf.writeFloat(aBonus.multiplier);
    }

    @Override
    public SkillBonus<?> createDefaultInstance() {
      return new BlockBreakSpeedBonus(0.1f);
    }
  }
}
