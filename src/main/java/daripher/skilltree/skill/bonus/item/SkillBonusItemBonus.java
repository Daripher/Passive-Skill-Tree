package daripher.skilltree.skill.bonus.item;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.init.PSTItemBonuses;
import daripher.skilltree.init.PSTRegistries;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.player.DamageBonus;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.Objects;
import java.util.function.Consumer;

public record SkillBonusItemBonus(SkillBonus<?> skillBonus) implements ItemBonus<SkillBonusItemBonus> {
    @Override
    public boolean canMerge(ItemBonus<?> other) {
        if (!(other instanceof SkillBonusItemBonus otherBonus)) {
            return false;
        }
        return otherBonus.skillBonus.canMerge(this.skillBonus);
    }

    @Override
    public SkillBonusItemBonus merge(ItemBonus<?> other) {
        if (!(other instanceof SkillBonusItemBonus otherBonus)) {
            throw new IllegalArgumentException();
        }
        return new SkillBonusItemBonus(otherBonus.skillBonus.merge(this.skillBonus));
    }

    @Override
    public SkillBonusItemBonus copy() {
        return new SkillBonusItemBonus(skillBonus.copy());
    }

    @Override
    public SkillBonusItemBonus multiply(double multiplier) {
        skillBonus.multiply(multiplier);
        return this;
    }

    @Override
    public ItemBonus.Serializer getSerializer() {
        return PSTItemBonuses.SKILL_BONUS.get();
    }

    @Override
    public void addTooltip(Consumer<MutableComponent> consumer) {
        consumer.accept(skillBonus.getTooltip());
    }

    @Override
    public boolean isPositive() {
        return skillBonus.isPositive();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        SkillBonusItemBonus that = (SkillBonusItemBonus) obj;
        return Objects.equals(this.skillBonus, that.skillBonus);
    }

    public static class Serializer implements ItemBonus.Serializer {
        @Override
        public ItemBonus<?> deserialize(JsonObject json) throws JsonParseException {
            return new SkillBonusItemBonus(SkillsReloader.GSON.fromJson(json.get("skill_bonus"), SkillBonus.class));
        }

        @Override
        public void serialize(JsonObject json, ItemBonus<?> bonus) {
            if (!(bonus instanceof SkillBonusItemBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            JsonObject skillBonusJson = new JsonObject();
            SkillBonus<?> skillBonus = aBonus.skillBonus;
            ResourceLocation serializerId = PSTRegistries.SKILL_BONUSES.get().getKey(skillBonus.getSerializer());
            Objects.requireNonNull(serializerId);
            skillBonusJson.addProperty("type", serializerId.toString());
            skillBonus.getSerializer().serialize(skillBonusJson, skillBonus);
            json.add("skill_bonus", skillBonusJson);
        }

        @Override
        public ItemBonus<?> deserialize(CompoundTag tag) {
            CompoundTag skillBonusTag = tag.getCompound("skill_bonus");
            String type = skillBonusTag.getString("type");
            ResourceLocation serializerId = ResourceLocation.parse(type);
            SkillBonus.Serializer serializer = PSTRegistries.SKILL_BONUSES.get().getValue(serializerId);
            Objects.requireNonNull(serializer, "Unknown skill bonus: " + serializerId);
            SkillBonus<?> skillBonus = serializer.deserialize(skillBonusTag);
            return new SkillBonusItemBonus(skillBonus);
        }

        @Override
        public CompoundTag serialize(ItemBonus<?> bonus) {
            if (!(bonus instanceof SkillBonusItemBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            SkillBonus<?> skillBonus = aBonus.skillBonus();
            SkillBonus.Serializer serializer = skillBonus.getSerializer();
            ResourceLocation serializerId = PSTRegistries.SKILL_BONUSES.get().getKey(serializer);
            Objects.requireNonNull(serializerId);
            CompoundTag skillBonusTag = serializer.serialize(skillBonus);
            skillBonusTag.putString("type", serializerId.toString());
            tag.put("skill_bonus", skillBonusTag);
            return tag;
        }

        @Override
        public ItemBonus<?> deserialize(FriendlyByteBuf buf) {
            return new SkillBonusItemBonus(NetworkHelper.readSkillBonus(buf));
        }

        @Override
        public void serialize(FriendlyByteBuf buf, ItemBonus<?> bonus) {
            if (!(bonus instanceof SkillBonusItemBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            NetworkHelper.writeSkillBonus(buf, aBonus.skillBonus);
        }

        @Override
        public ItemBonus<?> createDefaultInstance() {
            return new SkillBonusItemBonus(new DamageBonus(0.1f, AttributeModifier.Operation.MULTIPLY_BASE));
        }
    }
}
