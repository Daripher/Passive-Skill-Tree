package daripher.skilltree.skill.bonus.player;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.skill.bonus.SkillBonus;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.function.Consumer;

public final class CanPoisonAnyoneBonus implements SkillBonus<CanPoisonAnyoneBonus> {
  @Override
  public SkillBonus.Serializer getSerializer() {
    return PSTSkillBonuses.CAN_POISON_ANYONE.get();
  }

  @Override
  public CanPoisonAnyoneBonus copy() {
    return new CanPoisonAnyoneBonus();
  }

  @Override
  public CanPoisonAnyoneBonus multiply(double multiplier) {
    return this;
  }

  @Override
  public boolean canMerge(SkillBonus<?> other) {
    return other instanceof CanPoisonAnyoneBonus;
  }

  @Override
  public SkillBonus<CanPoisonAnyoneBonus> merge(SkillBonus<?> other) {
    return this;
  }

  @Override
  public MutableComponent getTooltip() {
    return Component.translatable(getDescriptionId())
        .withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
  }

  @Override
  public boolean isPositive() {
    return true;
  }

  @Override
  public void addEditorWidgets(
      SkillTreeEditor editor, int row, Consumer<CanPoisonAnyoneBonus> consumer) {}

  public static class Serializer implements SkillBonus.Serializer {
    @Override
    public CanPoisonAnyoneBonus deserialize(JsonObject json) throws JsonParseException {
      return new CanPoisonAnyoneBonus();
    }

    @Override
    public void serialize(JsonObject json, SkillBonus<?> bonus) {
      if (!(bonus instanceof CanPoisonAnyoneBonus)) {
        throw new IllegalArgumentException();
      }
    }

    @Override
    public CanPoisonAnyoneBonus deserialize(CompoundTag tag) {
      return new CanPoisonAnyoneBonus();
    }

    @Override
    public CompoundTag serialize(SkillBonus<?> bonus) {
      if (!(bonus instanceof CanPoisonAnyoneBonus)) {
        throw new IllegalArgumentException();
      }
      return new CompoundTag();
    }

    @Override
    public CanPoisonAnyoneBonus deserialize(FriendlyByteBuf buf) {
      return new CanPoisonAnyoneBonus();
    }

    @Override
    public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
      if (!(bonus instanceof CanPoisonAnyoneBonus)) {
        throw new IllegalArgumentException();
      }
    }

    @Override
    public SkillBonus<?> createDefaultInstance() {
      return new CanPoisonAnyoneBonus();
    }
  }
}
