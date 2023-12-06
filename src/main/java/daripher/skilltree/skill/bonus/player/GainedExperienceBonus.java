package daripher.skilltree.skill.bonus.player;

import com.google.gson.*;
import daripher.skilltree.client.screen.SkillTreeEditorScreen;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.skill.bonus.SkillBonus;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;

public final class GainedExperienceBonus implements SkillBonus<GainedExperienceBonus> {
  private ExperienceSource experienceSource;
  private float multiplier;

  public GainedExperienceBonus(float multiplier, ExperienceSource source) {
    this.multiplier = multiplier;
    this.experienceSource = source;
  }

  @Override
  public SkillBonus.Serializer getSerializer() {
    return PSTSkillBonuses.GAINED_EXPERIENCE.get();
  }

  @Override
  public GainedExperienceBonus copy() {
    return new GainedExperienceBonus(multiplier, experienceSource);
  }

  @Override
  public GainedExperienceBonus multiply(double multiplier) {
    return new GainedExperienceBonus((float) (this.multiplier * multiplier), experienceSource);
  }

  @Override
  public boolean canMerge(SkillBonus<?> other) {
    if (!(other instanceof GainedExperienceBonus otherBonus)) return false;
    return Objects.equals(otherBonus.experienceSource, this.experienceSource);
  }

  @Override
  public SkillBonus<GainedExperienceBonus> merge(SkillBonus<?> other) {
    if (!(other instanceof GainedExperienceBonus otherBonus)) {
      throw new IllegalArgumentException();
    }
    return new GainedExperienceBonus(otherBonus.multiplier + this.multiplier, experienceSource);
  }

  @Override
  public MutableComponent getTooltip() {
    MutableComponent sourceDescription =
        Component.translatable(experienceSource.getDescriptionId());
    double visibleMultplier = multiplier * 100;
    if (multiplier < 0) visibleMultplier *= -1;
    String operationDescription = multiplier > 0 ? "plus" : "take";
    operationDescription = "attribute.modifier." + operationDescription + ".1";
    String multiplierDescription = ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(visibleMultplier);
    MutableComponent bonusDescription =
        Component.translatable(getDescriptionId(), sourceDescription);
    return Component.translatable(operationDescription, multiplierDescription, bonusDescription)
        .withStyle(Style.EMPTY.withColor(0x7B7BE5));
  }

  @Override
  public void addEditorWidgets(
      SkillTreeEditorScreen editor, int row, Consumer<GainedExperienceBonus> consumer) {
    editor.addLabel(0, 0, "Multiplier", ChatFormatting.GOLD);
    editor.addLabel(110, 0, "Source", ChatFormatting.GOLD);
    editor.shiftWidgets(0, 19);
    editor
        .addNumericTextField(0, 0, 90, 14, multiplier)
        .setNumericResponder(
            v -> {
              setMultiplier(v.floatValue());
              consumer.accept(this.copy());
            });
    editor
        .addDropDownList(110, 0, 90, 14, 10, experienceSource)
        .setToNameFunc(ExperienceSource::getFormattedName)
        .setResponder(
            s -> {
              setExpericenSource(s);
              consumer.accept(this.copy());
            });
    editor.shiftWidgets(0, 19);
  }

  public void setMultiplier(float multiplier) {
    this.multiplier = multiplier;
  }

  public void setExpericenSource(ExperienceSource experienceSource) {
    this.experienceSource = experienceSource;
  }

  public float getMultiplier() {
    return multiplier;
  }

  public ExperienceSource getSource() {
    return experienceSource;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GainedExperienceBonus that = (GainedExperienceBonus) o;
    if (Float.compare(multiplier, that.multiplier) != 0) return false;
    return experienceSource == that.experienceSource;
  }

  @Override
  public int hashCode() {
    return Objects.hash(experienceSource, multiplier);
  }

  public enum ExperienceSource {
    MOBS("mobs"),
    FISHING("fishing"),
    ORE("ore");

    final String name;

    ExperienceSource(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }

    public Component getFormattedName() {
      return Component.literal(getName().substring(0, 1).toUpperCase() + getName().substring(1));
    }

    public static ExperienceSource byName(String name) {
      for (ExperienceSource type : values()) {
        if (type.name.equals(name)) return type;
      }
      return MOBS;
    }

    public String getDescriptionId() {
      return "experience.source." + getName();
    }
  }

  public static class Serializer implements SkillBonus.Serializer {
    @Override
    public GainedExperienceBonus deserialize(JsonObject json) throws JsonParseException {
      float multiplier = json.get("multiplier").getAsFloat();
      ExperienceSource experienceSource =
          ExperienceSource.byName(json.get("experience_source").getAsString());
      return new GainedExperienceBonus(multiplier, experienceSource);
    }

    @Override
    public void serialize(JsonObject json, SkillBonus<?> bonus) {
      if (!(bonus instanceof GainedExperienceBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      json.addProperty("multiplier", aBonus.multiplier);
      json.addProperty("experience_source", aBonus.experienceSource.name);
    }

    @Override
    public GainedExperienceBonus deserialize(CompoundTag tag) {
      float multiplier = tag.getFloat("multiplier");
      ExperienceSource experienceSource =
          ExperienceSource.byName(tag.getString("experience_source"));
      return new GainedExperienceBonus(multiplier, experienceSource);
    }

    @Override
    public CompoundTag serialize(SkillBonus<?> bonus) {
      if (!(bonus instanceof GainedExperienceBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      tag.putFloat("multiplier", aBonus.multiplier);
      tag.putString("experience_source", aBonus.experienceSource.name);
      return tag;
    }

    @Override
    public GainedExperienceBonus deserialize(FriendlyByteBuf buf) {
      return new GainedExperienceBonus(buf.readFloat(), ExperienceSource.byName(buf.readUtf()));
    }

    @Override
    public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
      if (!(bonus instanceof GainedExperienceBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      buf.writeFloat(aBonus.multiplier);
      buf.writeUtf(aBonus.experienceSource.name);
    }

    @Override
    public SkillBonus<?> createDefaultInstance() {
      return new GainedExperienceBonus(0.25f, ExperienceSource.MOBS);
    }
  }
}
