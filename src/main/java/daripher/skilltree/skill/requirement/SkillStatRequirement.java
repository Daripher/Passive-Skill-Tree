package daripher.skilltree.skill.requirement;

import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

public final class SkillStatRequirement {
  private final ResourceLocation statTypeId;
  private ResourceLocation statId;
  private int minValue;

  public SkillStatRequirement(ResourceLocation statTypeId, ResourceLocation statId, int minValue) {
    this.statTypeId = statTypeId;
    this.statId = statId;
    this.minValue = minValue;
  }

  public boolean isRequirementMet(Player player) {
    StatType<?> statType = ForgeRegistries.STAT_TYPES.getValue(statTypeId);
    Objects.requireNonNull(statType);
    int statValue = getStatValue(player, statType);
    return statValue >= minValue;
  }

  public MutableComponent getTooltip() {
    StatType<?> statType = ForgeRegistries.STAT_TYPES.getValue(statTypeId);
    if (statType == null) {
      return Component.literal("Unknown stat type: " + statTypeId).withStyle(ChatFormatting.RED);
    }
    if (statType == Stats.CUSTOM) {
      ResourceLocation originalStatId = Stats.CUSTOM.getRegistry().get(statId);
      if (originalStatId == null) {
        return Component.literal("Unknown stat: " + statId).withStyle(ChatFormatting.RED);
      }
      String statIdString = originalStatId.toString().replace(':', '.');
      Component statName = Component.translatable("stat." + statIdString);
      Stat<ResourceLocation> stat = Stats.CUSTOM.get(originalStatId);
      String formattedMinValue = stat.format(minValue).replace(".00", "");
      return Component.literal(statName.getString() + ": " + formattedMinValue);
    }
    if (statType == Stats.ENTITY_KILLED) {
      EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(statId);
      if (entityType == null) {
        return Component.literal("Unknown entity: " + statId).withStyle(ChatFormatting.RED);
      }
      Component entityName = entityType.getDescription();
      return Component.translatable(statType.getTranslationKey(), minValue, entityName);
    }
    if (statType == Stats.ENTITY_KILLED_BY) {
      EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(statId);
      if (entityType == null) {
        return Component.literal("Unknown entity: " + statId).withStyle(ChatFormatting.RED);
      }
      Component entityName = entityType.getDescription();
      return Component.translatable(statType.getTranslationKey(), entityName, minValue);
    } else {
      Item item = ForgeRegistries.ITEMS.getValue(statId);
      if (item == null) {
        return Component.literal("Unknown item: " + statId).withStyle(ChatFormatting.RED);
      }
      Component itemName = item.getDescription();
      return Component.literal(
          statType.getDisplayName().getString() + " " + itemName.getString() + ": " + minValue);
    }
  }

  private <T> int getStatValue(Player player, @Nonnull StatType<T> statType) {
    StatsCounter playerStats = getPlayerStats(player);
    int statValue;
    if (statType == Stats.CUSTOM) {
      ResourceLocation originalStatId = Stats.CUSTOM.getRegistry().get(statId);
      if (originalStatId == null) {
        return 0;
      }
      statValue = playerStats.getValue(Stats.CUSTOM, originalStatId);
    } else {
      T stat = statType.getRegistry().get(statId);
      Objects.requireNonNull(stat);
      statValue = playerStats.getValue(statType, stat);
    }
    return statValue;
  }

  private StatsCounter getPlayerStats(Player player) {
    if (player.level().isClientSide) {
      return getClientPlayerStats(player);
    }
    return ((ServerPlayer) player).getStats();
  }

  @OnlyIn(Dist.CLIENT)
  private static StatsCounter getClientPlayerStats(Player player) {
    return ((LocalPlayer) player).getStats();
  }

  public void addEditorWidgets(SkillTreeEditor editor, Consumer<SkillStatRequirement> consumer) {
    editor.addLabel(0, 0, "Stat", ChatFormatting.GOLD);
    editor.increaseHeight(19);
    StatType<?> statType = ForgeRegistries.STAT_TYPES.getValue(statTypeId());
    Objects.requireNonNull(statType);
    Set<ResourceLocation> statIds = statType.getRegistry().keySet();
    editor
        .addSelectionMenu(0, 0, 200, statIds)
        .setValue(statId())
        .setElementNameGetter(v -> Component.literal(v.toString()))
        .setResponder(v -> selectStat(consumer, v));
    editor.increaseHeight(19);
    editor.addLabel(0, 0, "Min Value", ChatFormatting.GOLD);
    editor.increaseHeight(19);
    editor
        .addNumericTextField(0, 0, 50, 14, minValue)
        .setNumericFilter(value -> value == value.intValue())
        .setNumericResponder(value -> selectMinValue(consumer, value));
    editor.increaseHeight(19);
  }

  private void selectMinValue(Consumer<SkillStatRequirement> consumer, Double value) {
    setMinValue(value.intValue());
    consumer.accept(this);
  }

  private void selectStat(Consumer<SkillStatRequirement> consumer, ResourceLocation statId) {
    setStatId(statId);
    consumer.accept(this);
  }

  public void setStatId(ResourceLocation statId) {
    this.statId = statId;
  }

  public void setMinValue(int minValue) {
    this.minValue = minValue;
  }

  public SkillStatRequirement copy() {
    return new SkillStatRequirement(statTypeId, statId, minValue);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SkillStatRequirement that = (SkillStatRequirement) o;
    return minValue == that.minValue
        && Objects.equals(statTypeId, that.statTypeId)
        && Objects.equals(statId, that.statId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(statTypeId, statId, minValue);
  }

  public ResourceLocation statTypeId() {
    return statTypeId;
  }

  public ResourceLocation statId() {
    return statId;
  }

  public int minValue() {
    return minValue;
  }

  @Override
  public String toString() {
    return "SkillStatRequirement["
        + "statTypeId="
        + statTypeId
        + ", "
        + "statId="
        + statId
        + ", "
        + "minValue="
        + minValue
        + ']';
  }
}
