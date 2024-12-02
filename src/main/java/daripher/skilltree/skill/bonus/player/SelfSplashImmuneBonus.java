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

public final class SelfSplashImmuneBonus implements SkillBonus<SelfSplashImmuneBonus> {
  @Override
  public SkillBonus.Serializer getSerializer() {
    return PSTSkillBonuses.SELF_SPLASH_IMMUNE.get();
  }

  @Override
  public SelfSplashImmuneBonus copy() {
    return new SelfSplashImmuneBonus();
  }

  @Override
  public SelfSplashImmuneBonus multiply(double multiplier) {
    return this;
  }

  @Override
  public boolean canMerge(SkillBonus<?> other) {
    return other instanceof SelfSplashImmuneBonus;
  }

  @Override
  public SkillBonus<SelfSplashImmuneBonus> merge(SkillBonus<?> other) {
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
      SkillTreeEditor editor, int row, Consumer<SelfSplashImmuneBonus> consumer) {}

  public static class Serializer implements SkillBonus.Serializer {
    @Override
    public SelfSplashImmuneBonus deserialize(JsonObject json) throws JsonParseException {
      return new SelfSplashImmuneBonus();
    }

    @Override
    public void serialize(JsonObject json, SkillBonus<?> bonus) {
      if (!(bonus instanceof SelfSplashImmuneBonus)) {
        throw new IllegalArgumentException();
      }
    }

    @Override
    public SelfSplashImmuneBonus deserialize(CompoundTag tag) {
      return new SelfSplashImmuneBonus();
    }

    @Override
    public CompoundTag serialize(SkillBonus<?> bonus) {
      if (!(bonus instanceof SelfSplashImmuneBonus)) {
        throw new IllegalArgumentException();
      }
      return new CompoundTag();
    }

    @Override
    public SelfSplashImmuneBonus deserialize(FriendlyByteBuf buf) {
      return new SelfSplashImmuneBonus();
    }

    @Override
    public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
      if (!(bonus instanceof SelfSplashImmuneBonus)) {
        throw new IllegalArgumentException();
      }
    }

    @Override
    public SkillBonus<?> createDefaultInstance() {
      return new SelfSplashImmuneBonus();
    }
  }
}
