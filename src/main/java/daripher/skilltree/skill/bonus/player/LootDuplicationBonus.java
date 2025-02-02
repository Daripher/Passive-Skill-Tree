package daripher.skilltree.skill.bonus.player;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.SelectionList;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.skill.bonus.SkillBonus;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.Objects;
import java.util.function.Consumer;

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
    chance = (float) (chance * multiplier);
    return this;
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
    Component lootDescription = Component.translatable(lootType.getDescriptionId());
    String descriptionId = getDescriptionId();
    MutableComponent multiplierDescription;
    if (multiplier == 1) {
      multiplierDescription = Component.translatable(descriptionId + ".double");
    }
    else if (multiplier == 2) {
      multiplierDescription = Component.translatable(descriptionId + ".triple");
    }
    else {
      String formattedMultiplier = ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(multiplier * 100);
      multiplierDescription = Component.translatable(descriptionId + ".multiplier", formattedMultiplier);
    }
    MutableComponent bonusDescription;
    if (chance < 1) {
      bonusDescription = Component.translatable(descriptionId, multiplierDescription, lootDescription);
      bonusDescription = TooltipHelper.getSkillBonusTooltip(bonusDescription, chance, AttributeModifier.Operation.MULTIPLY_BASE);
    }
    else {
      bonusDescription = Component.translatable(descriptionId + ".guaranteed", multiplierDescription, lootDescription);
    }
    return bonusDescription.withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
  }

  @Override
  public boolean isPositive() {
    return chance > 0;
  }

  @Override
  public void addEditorWidgets(SkillTreeEditor editor, int row, Consumer<LootDuplicationBonus> consumer) {
    editor.addLabel(0, 0, "Chance", ChatFormatting.GOLD);
    editor.addLabel(110, 0, "Multiplier", ChatFormatting.GOLD);
    editor.increaseHeight(19);
    editor.addNumericTextField(0, 0, 90, 14, chance)
        .setNumericResponder(value -> selectChance(consumer, value));
    editor.addNumericTextField(110, 0, 90, 14, multiplier)
        .setNumericResponder(value -> selectMultiplier(consumer, value));
    editor.increaseHeight(19);
    editor.addLabel(0, 0, "Loot Type", ChatFormatting.GOLD);
    editor.increaseHeight(19);
    SelectionList<LootType> lootTypeSelection = editor.addSelection(0, 0, 200, 3, lootType)
        .setNameGetter(LootType::getFormattedName)
        .setResponder(lootType -> selectLootType(consumer, lootType));
    editor.increaseHeight(lootTypeSelection.getMaxDisplayed() * 14 + 5);
  }

  private void selectLootType(Consumer<LootDuplicationBonus> consumer, LootType lootType) {
    setLootType(lootType);
    consumer.accept(this.copy());
  }

  private void selectMultiplier(Consumer<LootDuplicationBonus> consumer, Double value) {
    setMultiplier(value.floatValue());
    consumer.accept(this.copy());
  }

  private void selectChance(Consumer<LootDuplicationBonus> consumer, Double value) {
    setChance(value.floatValue());
    consumer.accept(this.copy());
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
    GEMS("gems"),
    CHESTS("chests"),
    ORE("ore"),
    ARCHAEOLOGY("archaeology");

    public boolean canAffect(LootContext lootContext) {
      LootContextParam<Entity> playerLootContextParam = getPlayerLootContextParam();
      if (!lootContext.hasParam(playerLootContextParam)) return false;
      if (!(lootContext.getParam(playerLootContextParam) instanceof Player)) return false;
      ResourceLocation lootTableId = lootContext.getQueriedLootTableId();
      String lootTableName = lootTableId.toString();
      return switch (this) {
        case MOBS -> lootTableName.contains("entities/");
        case FISHING -> lootTableName.contains("fishing");
        case GEMS -> lootTableName.contains("gems");
        case CHESTS -> lootTableName.contains("chests/");
        case ORE -> lootTableName.contains("blocks/") && lootTableName.contains("_ore");
        case ARCHAEOLOGY -> lootTableName.contains("archaeology/");
      };
    }

    public LootContextParam<Entity> getPlayerLootContextParam() {
      return switch (this) {
        case MOBS, FISHING -> LootContextParams.KILLER_ENTITY;
        case GEMS, CHESTS, ORE, ARCHAEOLOGY -> LootContextParams.THIS_ENTITY;
      };
    }

    final String name;

    LootType(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }

    public Component getFormattedName() {
      String firstLetter = getName().substring(0, 1);
      return Component.literal(firstLetter.toUpperCase() + getName().substring(1));
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
      float chance = SerializationHelper.getElement(json, "chance")
          .getAsFloat();
      float multiplier = SerializationHelper.getElement(json, "multiplier")
          .getAsFloat();
      LootType lootType = LootType.byName(json.get("loot_type")
                                              .getAsString());
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
      return new LootDuplicationBonus(buf.readFloat(), buf.readFloat(), LootType.byName(buf.readUtf()));
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
