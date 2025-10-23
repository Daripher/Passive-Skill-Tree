package daripher.skilltree.skill.bonus.item;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.init.PSTItemBonuses;
import daripher.skilltree.init.PSTRegistries;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.player.DamageBonus;
import java.util.Objects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public record ItemSkillBonus(SkillBonus<?> skillBonus) implements ItemBonus<ItemSkillBonus> {

  @Override
  public boolean canMerge(ItemBonus<?> other) {
    if (!(other instanceof ItemSkillBonus otherBonus)) return false;
    return otherBonus.skillBonus.canMerge(this.skillBonus);
  }

  @Override
  public ItemSkillBonus merge(ItemBonus<?> other) {
    if (!(other instanceof ItemSkillBonus otherBonus)) {
      throw new IllegalArgumentException();
    }
    return new ItemSkillBonus(otherBonus.skillBonus.merge(this.skillBonus));
  }

  @Override
  public ItemSkillBonus copy() {
    return new ItemSkillBonus(skillBonus.copy());
  }

  @Override
  public ItemSkillBonus multiply(double multiplier) {
    skillBonus.multiply(multiplier);
    return this;
  }

  @Override
  public ItemBonus.Serializer getSerializer() {
    return PSTItemBonuses.SKILL_BONUS.get();
  }

  @Override
  public MutableComponent getTooltip() {
    return skillBonus.getTooltip();
  }

  @Override
  public boolean isPositive() {
    return skillBonus.isPositive();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj == null || obj.getClass() != this.getClass()) return false;
    ItemSkillBonus that = (ItemSkillBonus) obj;
    return Objects.equals(this.skillBonus, that.skillBonus);
  }

  public static class Serializer implements ItemBonus.Serializer {
    @Override
    public ItemBonus<?> deserialize(JsonObject json) throws JsonParseException {
      return new ItemSkillBonus(
          SkillsReloader.GSON.fromJson(json.get("skill_bonus"), SkillBonus.class));
    }

    @Override
    public void serialize(JsonObject json, ItemBonus<?> bonus) {
      if (!(bonus instanceof ItemSkillBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      json.add("skill_bonus", SkillsReloader.GSON.toJsonTree(aBonus.skillBonus));
    }

    @Override
    public ItemBonus<?> deserialize(CompoundTag tag) {
      CompoundTag skillBonusTag = tag.getCompound("skill_bonus");
      Tag typeTag = skillBonusTag.get("type");
      Objects.requireNonNull(typeTag, "Missing skill type!");
      String type = typeTag.getAsString();
      ResourceLocation serializerId = new ResourceLocation(type);
      SkillBonus.Serializer serializer = PSTRegistries.SKILL_BONUSES.get().getValue(serializerId);
      Objects.requireNonNull(serializer, "Unknown skill bonus: " + serializerId);
      SkillBonus<?> skillBonus = serializer.deserialize(skillBonusTag);
      return new ItemSkillBonus(skillBonus);
    }

    @Override
    public CompoundTag serialize(ItemBonus<?> bonus) {
      if (!(bonus instanceof ItemSkillBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      SkillBonus<?> skillBonus = aBonus.skillBonus();
      SkillBonus.Serializer serializer = skillBonus.getSerializer();
      ResourceLocation serializerId = PSTRegistries.SKILL_BONUSES.get().getKey(serializer);
      Objects.requireNonNull(serializerId);
      CompoundTag skillBonusTag = serializer.serialize(skillBonus);
      skillBonusTag.putString("type", serializerId.toString());
      return tag;
    }

    @Override
    public ItemBonus<?> deserialize(FriendlyByteBuf buf) {
      return new ItemSkillBonus(NetworkHelper.readSkillBonus(buf));
    }

    @Override
    public void serialize(FriendlyByteBuf buf, ItemBonus<?> bonus) {
      if (!(bonus instanceof ItemSkillBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      NetworkHelper.writeSkillBonus(buf, aBonus.skillBonus);
    }

    @Override
    public ItemBonus<?> createDefaultInstance() {
      return new ItemSkillBonus(new DamageBonus(0.1f, AttributeModifier.Operation.MULTIPLY_BASE));
    }
  }
}
