package daripher.skilltree.skill.bonus.player;

import com.google.gson.*;
import daripher.skilltree.client.screen.SkillTreeEditorScreen;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.skill.bonus.SkillBonus;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

public final class LootDuplicationBonus implements SkillBonus<LootDuplicationBonus> {
  private LootType lootType;
  private float multiplier;
  private float chance;

  public LootDuplicationBonus(float chance, float multiplier, LootType lootType) {
    this.chance = chance;
    this.multiplier = multiplier;
    this.lootType = lootType;
  }

  @Override
  public SkillBonus.Serializer getSerializer() {
    return PSTSkillBonuses.LOOT_DUPLICATION.get();
  }

  @Override
  public LootDuplicationBonus copy() {
    return new LootDuplicationBonus(chance, multiplier, lootType);
  }

  @Override
  public LootDuplicationBonus multiply(double multiplier) {
    return new LootDuplicationBonus((float) (chance * multiplier), this.multiplier, lootType);
  }

  @Override
  public boolean canMerge(SkillBonus<?> other) {
    if (!(other instanceof LootDuplicationBonus otherBonus)) return false;
    if (otherBonus.multiplier != this.multiplier) return false;
    return Objects.equals(otherBonus.lootType, this.lootType);
  }

  @Override
  public SkillBonus<LootDuplicationBonus> merge(SkillBonus<?> other) {
    if (!(other instanceof LootDuplicationBonus otherBonus)) {
      throw new IllegalArgumentException();
    }
    return new LootDuplicationBonus(otherBonus.chance + this.chance, multiplier, lootType);
  }

  @Override
  public MutableComponent getTooltip() {
    MutableComponent lootDescription = Component.translatable(lootType.getDescriptionId());
    String multiplierDescription = ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(multiplier * 100);
    MutableComponent bonusDescription =
        Component.translatable(getDescriptionId(), multiplierDescription, lootDescription);
    return TooltipHelper.getSkillBonusTooltip(
            bonusDescription, chance, AttributeModifier.Operation.MULTIPLY_BASE)
        .withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
  }

  @Override
  public boolean isPositive() {
    return chance > 0;
  }

  @Override
  public void addEditorWidgets(
      SkillTreeEditorScreen editor, int row, Consumer<LootDuplicationBonus> consumer) {
    editor.addLabel(0, 0, "Chance", ChatFormatting.GOLD);
    editor.addLabel(110, 0, "Multiplier", ChatFormatting.GOLD);
    editor.shiftWidgets(0, 19);
    editor
        .addNumericTextField(0, 0, 90, 14, chance)
        .setNumericResponder(
            v -> {
              setChance(v.floatValue());
              consumer.accept(this.copy());
            });
    editor
        .addNumericTextField(110, 0, 90, 14, multiplier)
        .setNumericResponder(
            v -> {
              setMultiplier(v.floatValue());
              consumer.accept(this.copy());
            });
    editor.shiftWidgets(0, 19);
    editor
        .addDropDownList(0, 0, 200, 14, 10, lootType)
        .setToNameFunc(LootType::getFormattedName)
        .setResponder(
            t -> {
              setLootType(t);
              consumer.accept(this.copy());
            });
    editor.shiftWidgets(0, 19);
  }

  public void setChance(float chance) {
    this.chance = chance;
  }

  public void setMultiplier(float multiplier) {
    this.multiplier = multiplier;
  }

  public void setLootType(LootType lootType) {
    this.lootType = lootType;
  }

  public float getChance() {
    return chance;
  }

  public float getMultiplier() {
    return multiplier;
  }

  public LootType getLootType() {
    return lootType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    LootDuplicationBonus that = (LootDuplicationBonus) o;
    if (Float.compare(multiplier, that.multiplier) != 0) return false;
    if (Float.compare(chance, that.chance) != 0) return false;
    return lootType == that.lootType;
  }

  @Override
  public int hashCode() {
    return Objects.hash(lootType, multiplier, chance);
  }

  public enum LootType {
    MOBS("mobs"),
    FISHING("fishing"),
    GEMS("gems");

    final String name;

    LootType(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }

    public Component getFormattedName() {
      return Component.literal(getName().substring(0, 1).toUpperCase() + getName().substring(1));
    }

    public static LootType byName(String name) {
      for (LootType type : values()) {
        if (type.name.equals(name)) return type;
      }
      return MOBS;
    }

    public String getDescriptionId() {
      return "loot.type." + getName();
    }
  }

  public static class Serializer implements SkillBonus.Serializer {
    @Override
    public LootDuplicationBonus deserialize(JsonObject json) throws JsonParseException {
      float chance = json.get("chance").getAsFloat();
      float multiplier = json.get("multiplier").getAsFloat();
      LootType lootType = LootType.byName(json.get("loot_type").getAsString());
      return new LootDuplicationBonus(chance, multiplier, lootType);
    }

    @Override
    public void serialize(JsonObject json, SkillBonus<?> bonus) {
      if (!(bonus instanceof LootDuplicationBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      json.addProperty("chance", aBonus.chance);
      json.addProperty("multiplier", aBonus.multiplier);
      json.addProperty("loot_type", aBonus.lootType.name);
    }

    @Override
    public LootDuplicationBonus deserialize(CompoundTag tag) {
      float chance = tag.getFloat("chance");
      float multiplier = tag.getFloat("multiplier");
      LootType lootType = LootType.byName(tag.getString("loot_type"));
      return new LootDuplicationBonus(chance, multiplier, lootType);
    }

    @Override
    public CompoundTag serialize(SkillBonus<?> bonus) {
      if (!(bonus instanceof LootDuplicationBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      tag.putFloat("chance", aBonus.chance);
      tag.putFloat("multiplier", aBonus.multiplier);
      tag.putString("loot_type", aBonus.lootType.name);
      return tag;
    }

    @Override
    public LootDuplicationBonus deserialize(FriendlyByteBuf buf) {
      return new LootDuplicationBonus(
          buf.readFloat(), buf.readFloat(), LootType.byName(buf.readUtf()));
    }

    @Override
    public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
      if (!(bonus instanceof LootDuplicationBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      buf.writeFloat(aBonus.chance);
      buf.writeFloat(aBonus.multiplier);
      buf.writeUtf(aBonus.lootType.name);
    }

    @Override
    public SkillBonus<?> createDefaultInstance() {
      return new LootDuplicationBonus(0.05f, 1f, LootType.MOBS);
    }
  }
}
