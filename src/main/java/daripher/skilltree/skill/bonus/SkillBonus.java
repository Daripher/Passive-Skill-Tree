package daripher.skilltree.skill.bonus;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.client.screen.SkillTreeEditor;
import daripher.skilltree.init.PSTRegistries;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public interface SkillBonus<T extends SkillBonus<T>> {
  default void onSkillLearned(ServerPlayer player, boolean firstTime) {}

  default void onSkillRemoved(ServerPlayer player) {}

  boolean canMerge(SkillBonus<?> other);

  default boolean sameBonus(SkillBonus<?> other) {
    return canMerge(other);
  }

  SkillBonus<T> merge(SkillBonus<?> other);

  SkillBonus<T> copy();

  Serializer<T> getSerializer();

  MutableComponent getTooltip();

  default MutableComponent getAdvancedTooltip() {
    return Component.empty();
  }

  void addEditorWidgets(SkillTreeEditor editor, int row);

  static @Nullable SkillBonus<?> load(CompoundTag tag) {
    if (!tag.contains("Type")) return null;
    ResourceLocation serializerId = new ResourceLocation(tag.getString("Type"));
    Serializer<?> serializer = PSTRegistries.SKILL_BONUS_SERIALIZERS.get().getValue(serializerId);
    if (serializer == null) {
      SkillTreeMod.LOGGER.error("Unknown skill bonus type {}", serializerId);
      return null;
    }
    return serializer.deserialize(tag);
  }

  interface Serializer<T extends SkillBonus<T>> {
    T deserialize(JsonObject json) throws JsonParseException;

    void serialize(JsonObject json, SkillBonus<?> bonus);

    T deserialize(CompoundTag tag);

    CompoundTag serialize(SkillBonus<?> bonus);

    T deserialize(FriendlyByteBuf buf);

    void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus);
  }
}
