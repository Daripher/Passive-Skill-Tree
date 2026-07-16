package daripher.skilltree.skill.requirement;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.init.PSTSkillRequirements;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import daripher.skilltree.util.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public final class StatRequirement implements SkillRequirement<StatRequirement> {
    private ResourceLocation statTypeId;
    private ResourceLocation statId;
    private int minValue;

    public StatRequirement(ResourceLocation statTypeId, ResourceLocation statId, int minValue) {
        this.statTypeId = statTypeId;
        this.statId = statId;
        this.minValue = minValue;
    }

    @Override
    public boolean test(Player player) {
        StatType<?> statType = ForgeRegistries.STAT_TYPES.getValue(statTypeId);
        Objects.requireNonNull(statType);
        int statValue = getStatValue(player, statType);
        return statValue >= minValue;
    }

    @Override
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
            return Component.translatable(getDescriptionId() + ".killed", minValue, entityName);
        }
        if (statType == Stats.ENTITY_KILLED_BY) {
            EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(statId);
            if (entityType == null) {
                return Component.literal("Unknown entity: " + statId).withStyle(ChatFormatting.RED);
            }
            Component entityName = entityType.getDescription();
            return Component.translatable(getDescriptionId() + ".killed_by", entityName, minValue);
        } else {
            Item item = ForgeRegistries.ITEMS.getValue(statId);
            if (item == null) {
                return Component.literal("Unknown item: " + statId).withStyle(ChatFormatting.RED);
            }
            Component itemName = item.getDescription();
            return Component.literal(statType.getDisplayName().getString() + " " + itemName.getString() + ": " + minValue);
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

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<StatRequirement> consumer) {
        editor.addLabel(0, 0, "Stat Type", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        Set<ResourceLocation> statTypeIds = ForgeRegistries.STAT_TYPES.getKeys();
        editor.addSelectionMenu(0, 0, 200, statTypeIds).setValue(getStatTypeId()).setElementNameGetter(v -> Component.literal(v.toString()))
                .setResponder(v -> selectStatType(consumer, v));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Stat", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        StatType<?> statType = ForgeRegistries.STAT_TYPES.getValue(getStatTypeId());
        Objects.requireNonNull(statType);
        Set<ResourceLocation> statIds = statType.getRegistry().keySet();
        editor.addSelectionMenu(0, 0, 200, statIds).setValue(getStatId()).setElementNameGetter(v -> Component.literal(v.toString()))
                .setResponder(v -> selectStat(consumer, v));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Min Value", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 50, 14, minValue).setNumericFilter(value -> value == value.intValue())
                .setNumericResponder(value -> selectMinValue(consumer, value));
        editor.increaseHeight(19);
    }

    private void selectMinValue(Consumer<StatRequirement> consumer, Double value) {
        setMinValue(value.intValue());
        consumer.accept(this);
    }

    private void selectStat(Consumer<StatRequirement> consumer, ResourceLocation statId) {
        setStatId(statId);
        consumer.accept(this);
    }

    private void selectStatType(Consumer<StatRequirement> consumer, ResourceLocation statTypeId) {
        setStatTypeId(statTypeId);
        StatType<?> statType = ForgeRegistries.STAT_TYPES.getValue(getStatTypeId());
        Objects.requireNonNull(statType);
        Set<ResourceLocation> statIds = statType.getRegistry().keySet();
        statIds.stream().findFirst().ifPresent(this::setStatId);
        consumer.accept(this);
    }

    public void setStatId(ResourceLocation statId) {
        this.statId = statId;
    }

    public void setStatTypeId(ResourceLocation statTypeId) {
        this.statTypeId = statTypeId;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    @Override
    public StatRequirement copy() {
        return new StatRequirement(statTypeId, statId, minValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StatRequirement that = (StatRequirement) o;
        return minValue == that.minValue && Objects.equals(statTypeId, that.statTypeId) && Objects.equals(statId, that.statId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statTypeId, statId, minValue);
    }

    public ResourceLocation getStatTypeId() {
        return statTypeId;
    }

    public ResourceLocation getStatId() {
        return statId;
    }

    @Override
    public SkillRequirement.Serializer getSerializer() {
        return PSTSkillRequirements.STAT_VALUE.get();
    }

    public static class Serializer implements SkillRequirement.Serializer {
        @Override
        public SkillRequirement<?> deserialize(JsonObject json) throws JsonParseException {
            ResourceLocation statTypeId = ResourceLocation.parse(json.get("statTypeId").getAsString());
            ResourceLocation statId = ResourceLocation.parse(json.get("statId").getAsString());
            int minValue = json.get("minValue").getAsInt();
            return new StatRequirement(statTypeId, statId, minValue);
        }

        @Override
        public void serialize(JsonObject json, SkillRequirement<?> requirement) {
            if (requirement instanceof StatRequirement aRequirement) {
                json.addProperty("statTypeId", aRequirement.statTypeId.toString());
                json.addProperty("statId", aRequirement.statId.toString());
                json.addProperty("minValue", aRequirement.minValue);
            }
        }

        @Override
        public SkillRequirement<?> deserialize(CompoundTag tag) {
            ResourceLocation statTypeId = ResourceLocation.parse(tag.getString("statTypeId"));
            ResourceLocation statId = ResourceLocation.parse(tag.getString("statId"));
            int minValue = tag.getInt("minValue");
            return new StatRequirement(statTypeId, statId, minValue);
        }

        @Override
        public CompoundTag serialize(SkillRequirement<?> requirement) {
            CompoundTag tag = new CompoundTag();
            if (requirement instanceof StatRequirement aRequirement) {
                tag.putString("statTypeId", aRequirement.statTypeId.toString());
                tag.putString("statId", aRequirement.statId.toString());
                tag.putInt("minValue", aRequirement.minValue);
            }
            return tag;
        }

        @Override
        public SkillRequirement<?> deserialize(FriendlyByteBuf buf) {
            ResourceLocation statTypeId = ResourceLocation.parse(buf.readUtf());
            ResourceLocation statId = ResourceLocation.parse(buf.readUtf());
            int minValue = buf.readInt();
            return new StatRequirement(statTypeId, statId, minValue);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillRequirement<?> requirement) {
            if (requirement instanceof StatRequirement aRequirement) {
                buf.writeUtf(aRequirement.statTypeId.toString());
                buf.writeUtf(aRequirement.statId.toString());
                buf.writeInt(aRequirement.minValue);
            }
        }

        @Override
        public SkillRequirement<?> createDefaultInstance() {
            return new StatRequirement(ForgeRegistries.STAT_TYPES.getKey(Stats.CUSTOM), Stats.DEATHS, 1);
        }
    }
}
