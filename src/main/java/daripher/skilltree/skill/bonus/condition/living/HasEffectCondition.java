package daripher.skilltree.skill.bonus.condition.living;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.screen.SkillTreeEditorScreen;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTLivingConditions;
import daripher.skilltree.network.NetworkHelper;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;

public final class HasEffectCondition implements LivingCondition {
  private MobEffect effect;
  private int amplifier;

  public HasEffectCondition(MobEffect effect) {
    this(effect, 0);
  }

  public HasEffectCondition(MobEffect effect, int amplifier) {
    this.effect = effect;
    this.amplifier = amplifier;
  }

  @Override
  public boolean met(LivingEntity living) {
    if (amplifier == 0) return living.hasEffect(this.effect);
    MobEffectInstance effect = living.getEffect(this.effect);
    return effect != null && effect.getAmplifier() >= this.amplifier;
  }

  @Override
  public MutableComponent getTooltip(MutableComponent bonusTooltip, String target) {
    String key = getDescriptionId();
    Component targetDescription = Component.translatable("%s.target.%s".formatted(key, target));
    Component effectDescription = effect.getDisplayName();
    if (amplifier == 0) {
      return Component.translatable(key, bonusTooltip, targetDescription, effectDescription);
    }
    Component amplifierDescription = Component.translatable("potion.potency." + amplifier);
    effectDescription =
        Component.translatable("potion.withAmplifier", effectDescription, amplifierDescription);
    return Component.translatable(
        key + ".amplifier", bonusTooltip, targetDescription, effectDescription);
  }

  @Override
  public LivingCondition.Serializer getSerializer() {
    return PSTLivingConditions.HAS_EFFECT.get();
  }

  @Override
  public void addEditorWidgets(SkillTreeEditorScreen editor, Consumer<LivingCondition> consumer) {
    editor.addLabel(0, 0, "Effect", ChatFormatting.GREEN);
    editor.addLabel(150, 0, "Level", ChatFormatting.GREEN);
    editor.shiftWidgets(0, 19);
    editor
        .addDropDownList(0, 0, 145, 14, 10, effect, ForgeRegistries.MOB_EFFECTS.getValues())
        .setToNameFunc(a -> Component.translatable(a.getDescriptionId()))
        .setResponder(
            e -> {
              setEffect(e);
              consumer.accept(this);
            });
    editor
        .addNumericTextField(150, 0, 50, 14, amplifier)
        .setNumericFilter(d -> d >= 0 && d == d.intValue())
        .setNumericResponder(
            v -> {
              setAmplifier(v.intValue());
              consumer.accept(this);
            });
    editor.shiftWidgets(0, 19);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    HasEffectCondition that = (HasEffectCondition) o;
    return amplifier == that.amplifier && Objects.equals(effect, that.effect);
  }

  @Override
  public int hashCode() {
    return Objects.hash(effect, amplifier);
  }

  public void setEffect(MobEffect effect) {
    this.effect = effect;
  }

  public void setAmplifier(int amplifier) {
    this.amplifier = amplifier;
  }

  public static class Serializer implements LivingCondition.Serializer {
    @Override
    public LivingCondition deserialize(JsonObject json) throws JsonParseException {
      MobEffect effect = SerializationHelper.deserializeEffect(json);
      int amplifier = !json.has("amplifier") ? 0 : json.get("amplifier").getAsInt();
      return new HasEffectCondition(effect, amplifier);
    }

    @Override
    public void serialize(JsonObject json, LivingCondition condition) {
      if (!(condition instanceof HasEffectCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      SerializationHelper.serializeEffect(json, aCondition.effect);
      json.addProperty("amplifier", aCondition.amplifier);
    }

    @Override
    public LivingCondition deserialize(CompoundTag tag) {
      MobEffect effect = SerializationHelper.deserializeEffect(tag);
      int amplifier = !tag.contains("amplifier") ? 0 : tag.getInt("amplifier");
      return new HasEffectCondition(effect, amplifier);
    }

    @Override
    public CompoundTag serialize(LivingCondition condition) {
      if (!(condition instanceof HasEffectCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      SerializationHelper.serializeEffect(tag, aCondition.effect);
      tag.putInt("amplifier", aCondition.amplifier);
      return tag;
    }

    @Override
    public LivingCondition deserialize(FriendlyByteBuf buf) {
      return new HasEffectCondition(NetworkHelper.readEffect(buf), buf.readInt());
    }

    @Override
    public void serialize(FriendlyByteBuf buf, LivingCondition condition) {
      if (!(condition instanceof HasEffectCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      NetworkHelper.writeEffect(buf, aCondition.effect);
      buf.writeInt(aCondition.amplifier);
    }

    @Override
    public LivingCondition createDefaultInstance() {
      return new HasEffectCondition(MobEffects.POISON);
    }
  }
}
