package daripher.skilltree.skill.bonus.player;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.condition.item.EquipmentCondition;
import daripher.skilltree.skill.bonus.condition.living.HasItemEquippedCondition;
import daripher.skilltree.skill.bonus.condition.living.LivingCondition;
import daripher.skilltree.skill.bonus.condition.living.NoneLivingCondition;
import daripher.skilltree.skill.bonus.multiplier.LivingMultiplier;
import daripher.skilltree.skill.bonus.multiplier.NoneLivingMultiplier;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Consumer;

public final class ProjectileDuplicationBonus implements SkillBonus<ProjectileDuplicationBonus> {
  private float chance;
  private @Nonnull LivingMultiplier playerMultiplier = NoneLivingMultiplier.INSTANCE;
  private @Nonnull LivingCondition playerCondition = NoneLivingCondition.INSTANCE;

  public ProjectileDuplicationBonus(float chance) {
    this.chance = chance;
  }

  public float getChance(Player player) {
    if (!playerCondition.met(player)) return 0f;
    return chance * playerMultiplier.getValue(player);
  }

  @Override
  public SkillBonus.Serializer getSerializer() {
    return PSTSkillBonuses.PROJECTILE_DUPLICATION.get();
  }

  @Override
  public ProjectileDuplicationBonus copy() {
    ProjectileDuplicationBonus bonus = new ProjectileDuplicationBonus(chance);
    bonus.playerMultiplier = this.playerMultiplier;
    bonus.playerCondition = this.playerCondition;
    return bonus;
  }

  @Override
  public ProjectileDuplicationBonus multiply(double multiplier) {
    chance *= (float) multiplier;
    return this;
  }

  @Override
  public boolean canMerge(SkillBonus<?> other) {
    if (!(other instanceof ProjectileDuplicationBonus otherBonus)) return false;
    if (!Objects.equals(otherBonus.playerMultiplier, this.playerMultiplier)) return false;
    return Objects.equals(otherBonus.playerCondition, this.playerCondition);
  }

  @Override
  public SkillBonus<ProjectileDuplicationBonus> merge(SkillBonus<?> other) {
    if (!(other instanceof ProjectileDuplicationBonus otherBonus)) {
      throw new IllegalArgumentException();
    }
    float mergedChance = otherBonus.chance + this.chance;
    ProjectileDuplicationBonus mergedBonus = new ProjectileDuplicationBonus(mergedChance);
    mergedBonus.playerMultiplier = this.playerMultiplier;
    mergedBonus.playerCondition = this.playerCondition;
    return mergedBonus;
  }

  @Override
  public MutableComponent getTooltip() {
    MutableComponent tooltip;
    if (chance < 1f || chance % 1 != 0) {
      tooltip = TooltipHelper.getSkillBonusTooltip(getDescriptionId() + ".chance", chance, AttributeModifier.Operation.MULTIPLY_BASE);
    }
    else if (chance == 1f) {
      tooltip = Component.translatable(getDescriptionId());
    } else {
      tooltip = Component.translatable(getDescriptionId() + ".amount", (int) chance);
    }
    tooltip = playerMultiplier.getTooltip(tooltip, Target.PLAYER);
    tooltip = playerCondition.getTooltip(tooltip, Target.PLAYER);
    return tooltip.withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
  }

  @Override
  public boolean isPositive() {
    return chance > 0;
  }

  @Override
  public void addEditorWidgets(SkillTreeEditor editor, int row, Consumer<ProjectileDuplicationBonus> consumer) {
    editor.addLabel(0, 0, "Chance", ChatFormatting.GOLD);
    editor.increaseHeight(19);
    editor.addNumericTextField(0, 0, 50, 14, chance)
        .setNumericResponder(value -> selectChance(consumer, value));
    editor.increaseHeight(19);
    editor.addLabel(0, 0, "Player Condition", ChatFormatting.GOLD);
    editor.increaseHeight(19);
    editor.addSelectionMenu(0, 0, 200, playerCondition)
        .setResponder(condition -> selectPlayerCondition(editor, consumer, condition))
        .setMenuInitFunc(() -> addPlayerConditionWidgets(editor, consumer));
    editor.increaseHeight(19);
    editor.addLabel(0, 0, "Player Multiplier", ChatFormatting.GOLD);
    editor.increaseHeight(19);
    editor.addSelectionMenu(0, 0, 200, playerMultiplier)
        .setResponder(multiplier -> selectPlayerMultiplier(editor, consumer, multiplier))
        .setMenuInitFunc(() -> addPlayerMultiplierWidgets(editor, consumer));
    editor.increaseHeight(19);
  }

  private void selectChance(Consumer<ProjectileDuplicationBonus> consumer, Double value) {
    setChance(value.floatValue());
    consumer.accept(this.copy());
  }

  private void addPlayerMultiplierWidgets(SkillTreeEditor editor, Consumer<ProjectileDuplicationBonus> consumer) {
    playerMultiplier.addEditorWidgets(editor, multiplier -> {
      setPlayerMultiplier(multiplier);
      consumer.accept(this.copy());
    });
  }

  private void selectPlayerMultiplier(SkillTreeEditor editor, Consumer<ProjectileDuplicationBonus> consumer, LivingMultiplier multiplier) {
    setPlayerMultiplier(multiplier);
    consumer.accept(this.copy());
    editor.rebuildWidgets();
  }

  private void addPlayerConditionWidgets(SkillTreeEditor editor, Consumer<ProjectileDuplicationBonus> consumer) {
    playerCondition.addEditorWidgets(editor, c -> {
      setPlayerCondition(c);
      consumer.accept(this.copy());
    });
  }

  private void selectPlayerCondition(SkillTreeEditor editor, Consumer<ProjectileDuplicationBonus> consumer, LivingCondition condition) {
    setPlayerCondition(condition);
    consumer.accept(this.copy());
    editor.rebuildWidgets();
  }

  public SkillBonus<?> setPlayerCondition(LivingCondition condition) {
    this.playerCondition = condition;
    return this;
  }

  public SkillBonus<?> setPlayerMultiplier(LivingMultiplier multiplier) {
    this.playerMultiplier = multiplier;
    return this;
  }

  public void setChance(float chance) {
    this.chance = chance;
  }

  public static class Serializer implements SkillBonus.Serializer {
    @Override
    public ProjectileDuplicationBonus deserialize(JsonObject json) throws JsonParseException {
      float chance = SerializationHelper.getElement(json, "chance")
          .getAsFloat();
      ProjectileDuplicationBonus bonus = new ProjectileDuplicationBonus(chance);
      bonus.playerMultiplier = SerializationHelper.deserializeLivingMultiplier(json, "player_multiplier");
      bonus.playerCondition = SerializationHelper.deserializeLivingCondition(json, "player_condition");
      return bonus;
    }

    @Override
    public void serialize(JsonObject json, SkillBonus<?> bonus) {
      if (!(bonus instanceof ProjectileDuplicationBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      json.addProperty("chance", aBonus.chance);
      SerializationHelper.serializeLivingMultiplier(json, aBonus.playerMultiplier, "player_multiplier");
      SerializationHelper.serializeLivingCondition(json, aBonus.playerCondition, "player_condition");
    }

    @Override
    public ProjectileDuplicationBonus deserialize(CompoundTag tag) {
      float chance = tag.getFloat("chance");
      ProjectileDuplicationBonus bonus = new ProjectileDuplicationBonus(chance);
      bonus.playerMultiplier = SerializationHelper.deserializeLivingMultiplier(tag, "player_multiplier");
      bonus.playerCondition = SerializationHelper.deserializeLivingCondition(tag, "player_condition");
      return bonus;
    }

    @Override
    public CompoundTag serialize(SkillBonus<?> bonus) {
      if (!(bonus instanceof ProjectileDuplicationBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      tag.putFloat("chance", aBonus.chance);
      SerializationHelper.serializeLivingMultiplier(tag, aBonus.playerMultiplier, "player_multiplier");
      SerializationHelper.serializeLivingCondition(tag, aBonus.playerCondition, "player_condition");
      return tag;
    }

    @Override
    public ProjectileDuplicationBonus deserialize(FriendlyByteBuf buf) {
      float chance = buf.readFloat();
      ProjectileDuplicationBonus bonus = new ProjectileDuplicationBonus(chance);
      bonus.playerMultiplier = NetworkHelper.readLivingMultiplier(buf);
      bonus.playerCondition = NetworkHelper.readLivingCondition(buf);
      return bonus;
    }

    @Override
    public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
      if (!(bonus instanceof ProjectileDuplicationBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      buf.writeFloat(aBonus.chance);
      NetworkHelper.writeLivingMultiplier(buf, aBonus.playerMultiplier);
      NetworkHelper.writeLivingCondition(buf, aBonus.playerCondition);
    }

    @Override
    public SkillBonus<?> createDefaultInstance() {
      return new ProjectileDuplicationBonus(0.1f).setPlayerCondition(new HasItemEquippedCondition(new EquipmentCondition(EquipmentCondition.Type.RANGED_WEAPON)));
    }
  }
}
