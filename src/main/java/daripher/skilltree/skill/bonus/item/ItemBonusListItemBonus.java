package daripher.skilltree.skill.bonus.item;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.init.PSTItemBonuses;
import daripher.skilltree.init.PSTRegistries;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.player.AttributeBonus;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public record ItemBonusListItemBonus(List<? extends ItemBonus<?>> innerBonuses) implements ItemBonus<ItemBonusListItemBonus> {
    @Override
    public boolean canMerge(ItemBonus<?> other) {
        if (!(other instanceof ItemBonusListItemBonus otherBonus)) {
            return false;
        }
        if (otherBonus.innerBonuses.size() != innerBonuses.size()) {
            return false;
        }
        for (int i = 0; i < innerBonuses.size(); i++) {
            if (!innerBonuses.get(i).canMerge(otherBonus.innerBonuses.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemBonusListItemBonus merge(ItemBonus<?> other) {
        if (!(other instanceof ItemBonusListItemBonus otherBonus)) {
            throw new IllegalArgumentException();
        }
        if (otherBonus.innerBonuses.size() != innerBonuses.size()) {
            throw new IllegalArgumentException();
        }
        List<ItemBonus<?>> mergedSkillBonuses = new ArrayList<>();
        for (int i = 0; i < innerBonuses.size(); i++) {
            if (!innerBonuses.get(i).canMerge(otherBonus.innerBonuses.get(i))) {
                throw new IllegalArgumentException();
            }
            mergedSkillBonuses.add(innerBonuses.get(i).merge(otherBonus.innerBonuses.get(i)));
        }
        return new ItemBonusListItemBonus(mergedSkillBonuses);
    }

    @Override
    public ItemBonusListItemBonus copy() {
        return new ItemBonusListItemBonus(innerBonuses.stream().map(ItemBonus::copy).toList());
    }

    @Override
    public ItemBonusListItemBonus multiply(double multiplier) {
        innerBonuses.forEach(bonus -> bonus.multiply(multiplier));
        return this;
    }

    @Override
    public ItemBonus.Serializer getSerializer() {
        return PSTItemBonuses.ITEM_BONUS_LIST.get();
    }

    @Override
    public void addTooltip(Consumer<MutableComponent> consumer) {
        for (ItemBonus<?> itemBonus : innerBonuses) {
            itemBonus.addTooltip(consumer);
        }
    }

    @Override
    public boolean isPositive() {
        return innerBonuses.stream().anyMatch(ItemBonus::isPositive);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ItemBonusListItemBonus that = (ItemBonusListItemBonus) o;
        return Objects.equals(innerBonuses, that.innerBonuses);
    }

    @Override
    public int hashCode() {
        return Objects.hash(innerBonuses);
    }

    public static class Serializer implements ItemBonus.Serializer {
        @Override
        public ItemBonus<?> deserialize(JsonObject json) throws JsonParseException {
            JsonArray innerBonusesJson = json.get("inner_bonuses").getAsJsonArray();
            List<ItemBonus<?>> innerBonuses = new ArrayList<>();
            for (int i = 0; i < innerBonusesJson.size(); i++) {
                JsonObject innerBonusTag = innerBonusesJson.get(i).getAsJsonObject();
                String serializerIdString = innerBonusTag.get("type").getAsString();
                ResourceLocation serializerId = ResourceLocation.parse(serializerIdString);
                ItemBonus.Serializer serializer = PSTRegistries.ITEM_BONUSES.get().getValue(serializerId);
                Objects.requireNonNull(serializer, "Unknown item bonus: " + serializerId);
                ItemBonus<?> innerBonus = serializer.deserialize(innerBonusTag);
                innerBonuses.add(innerBonus);
            }
            return new ItemBonusListItemBonus(innerBonuses);
        }

        @Override
        public void serialize(JsonObject json, ItemBonus<?> bonus) {
            if (!(bonus instanceof ItemBonusListItemBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            JsonArray innerBonusesJson = new JsonArray();
            for (int i = 0; i < aBonus.innerBonuses.size(); i++) {
                ItemBonus<?> innerBonus = aBonus.innerBonuses.get(i);
                ItemBonus.Serializer serializer = innerBonus.getSerializer();
                ResourceLocation serializerId = PSTRegistries.ITEM_BONUSES.get().getKey(serializer);
                Objects.requireNonNull(serializerId);
                JsonObject innerBonusJson = new JsonObject();
                innerBonusJson.addProperty("type", serializerId.toString());
                serializer.serialize(innerBonusJson, innerBonus);
                innerBonusesJson.add(innerBonusJson);
            }
            json.add("inner_bonuses", innerBonusesJson);
        }

        @Override
        public ItemBonus<?> deserialize(CompoundTag tag) {
            List<ItemBonus<?>> innerBonuses = new ArrayList<>();
            ListTag innerBonusesTag = tag.getList("inner_bonuses", Tag.TAG_COMPOUND);
            for (Tag value : innerBonusesTag) {
                CompoundTag innerBonusTag = (CompoundTag) value;
                String type = innerBonusTag.getString("type");
                ResourceLocation serializerId = ResourceLocation.parse(type);
                ItemBonus.Serializer serializer = PSTRegistries.ITEM_BONUSES.get().getValue(serializerId);
                Objects.requireNonNull(serializer, "Unknown item bonus: " + serializerId);
                innerBonuses.add(serializer.deserialize(innerBonusTag));
            }
            return new ItemBonusListItemBonus(innerBonuses);
        }

        @Override
        public CompoundTag serialize(ItemBonus<?> bonus) {
            if (!(bonus instanceof ItemBonusListItemBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            ListTag innerBonusesTag = new ListTag();
            for (int i = 0; i < aBonus.innerBonuses.size(); i++) {
                ItemBonus<?> innerBonus = aBonus.innerBonuses.get(i);
                ItemBonus.Serializer serializer = innerBonus.getSerializer();
                ResourceLocation serializerId = PSTRegistries.ITEM_BONUSES.get().getKey(serializer);
                Objects.requireNonNull(serializerId);
                CompoundTag innerBonusTag = serializer.serialize(innerBonus);
                innerBonusTag.putString("type", serializerId.toString());
                innerBonusesTag.add(innerBonusTag);
            }
            tag.put("inner_bonuses", innerBonusesTag);
            return tag;
        }

        @Override
        public ItemBonus<?> deserialize(FriendlyByteBuf buf) {
            List<ItemBonus<?>> innerBonuses = new ArrayList<>();
            int size = buf.readInt();
            for (int i = 0; i < size; i++) {
                innerBonuses.add(NetworkHelper.readItemBonus(buf));
            }
            return new ItemBonusListItemBonus(innerBonuses);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, ItemBonus<?> bonus) {
            if (!(bonus instanceof ItemBonusListItemBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            buf.writeInt(aBonus.innerBonuses.size());
            for (int i = 0; i < aBonus.innerBonuses.size(); i++) {
                NetworkHelper.writeItemBonus(buf, aBonus.innerBonuses.get(i));
            }
        }

        @Override
        public ItemBonus<?> createDefaultInstance() {
            AttributeModifier defaultModifier = new AttributeModifier("Default Modifier", 1, AttributeModifier.Operation.ADDITION);
            ItemBonus<?> bonus1 = new SkillBonusItemBonus(new AttributeBonus(Attributes.ARMOR, defaultModifier));
            ItemBonus<?> bonus2 = new SkillBonusItemBonus(new AttributeBonus(Attributes.ARMOR_TOUGHNESS, defaultModifier));
            return new ItemBonusListItemBonus(List.of(bonus1, bonus2));
        }
    }
}
