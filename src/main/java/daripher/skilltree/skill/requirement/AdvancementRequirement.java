package daripher.skilltree.skill.requirement;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.init.PSTRegistries;
import daripher.skilltree.init.PSTSkillRequirements;
import daripher.skilltree.mixin.ClientAdvancementsAccessor;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public final class AdvancementRequirement implements SkillRequirement<AdvancementRequirement> {
  private ResourceLocation advancementId;

  public AdvancementRequirement(ResourceLocation advancementId) {
    this.advancementId = advancementId;
  }

  @Override
  public boolean isRequirementMet(Player player) {
    if (player.level().isClientSide) {
      LocalPlayer localPlayer = (LocalPlayer) player;
      ClientAdvancements advancements = localPlayer.connection.getAdvancements();
      ClientAdvancementsAccessor advancementsAccessor = (ClientAdvancementsAccessor) advancements;
      Advancement advancement = advancements.getAdvancements().get(advancementId);
      AdvancementProgress progress = advancementsAccessor.getProgress().get(advancement);
      if (progress == null) {
        return false;
      }
      return progress.getPercent() >= 1f;
    } else {
      ServerPlayer serverPlayer = (ServerPlayer) player;
      MinecraftServer server = serverPlayer.level().getServer();
      if (server == null) {
        return false;
      }
      ServerAdvancementManager advancementManager = server.getAdvancements();
      PlayerAdvancements advancements = serverPlayer.getAdvancements();
      Advancement advancement = advancementManager.getAdvancement(advancementId);
      if (advancement == null) {
        return false;
      }
      return advancements.getOrStartProgress(advancement).getPercent() >= 1f;
    }
  }

  @Override
  public MutableComponent getTooltip() {
    String advancementPath = advancementId.getPath().replaceAll("/", ".");
    String advancamentDescriptionId = "advancements.%s.title".formatted(advancementPath);
    Component advancementTooltip = Component.translatable(advancamentDescriptionId);
    ResourceLocation id = PSTRegistries.SKILL_REQUIREMENTS.get().getKey(getSerializer());
    Objects.requireNonNull(id);
    String descriptionId = "skill_requirements.%s.%s".formatted(id.getNamespace(), id.getPath());
    return Component.translatable(descriptionId, advancementTooltip);
  }

  @Override
  public void addEditorWidgets(SkillTreeEditor editor, Consumer<AdvancementRequirement> consumer) {
    LocalPlayer localPlayer = Minecraft.getInstance().player;
    Objects.requireNonNull(localPlayer);
    ClientAdvancements advancements = localPlayer.connection.getAdvancements();
    editor.addLabel(0, 0, "Advancement ID", ChatFormatting.GOLD);
    editor.increaseHeight(19);
    List<ResourceLocation> advancementIds =
        advancements.getAdvancements().getAllAdvancements().stream()
            .map(Advancement::getId)
            .toList();
    editor
        .addSelectionMenu(0, 0, 200, advancementIds)
        .setValue(getAdvancementId())
        .setElementNameGetter(v -> Component.literal(v.toString()))
        .setResponder(v -> selectAdvancementId(consumer, v));
    editor.increaseHeight(19);
  }

  private void selectAdvancementId(Consumer<AdvancementRequirement> consumer, ResourceLocation id) {
    setAdvancementId(id);
    consumer.accept(this);
  }

  public void setAdvancementId(ResourceLocation advancementId) {
    this.advancementId = advancementId;
  }

  @Override
  public AdvancementRequirement copy() {
    return new AdvancementRequirement(advancementId);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AdvancementRequirement that = (AdvancementRequirement) o;
    return Objects.equals(advancementId, that.advancementId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(advancementId);
  }

  public ResourceLocation getAdvancementId() {
    return advancementId;
  }

  @Override
  public SkillRequirement.Serializer getSerializer() {
    return PSTSkillRequirements.ADVANCEMENT.get();
  }

  public static class Serializer implements SkillRequirement.Serializer {
    @Override
    public SkillRequirement<?> deserialize(JsonObject json) throws JsonParseException {
      ResourceLocation id = new ResourceLocation(json.get("advancement").getAsString());
      return new AdvancementRequirement(id);
    }

    @Override
    public void serialize(JsonObject json, SkillRequirement<?> requirement) {
      if (requirement instanceof AdvancementRequirement aRequirement) {
        json.addProperty("advancement", aRequirement.advancementId.toString());
      }
    }

    @Override
    public SkillRequirement<?> deserialize(CompoundTag tag) {
      ResourceLocation id = new ResourceLocation(tag.getString("advancement"));
      return new AdvancementRequirement(id);
    }

    @Override
    public CompoundTag serialize(SkillRequirement<?> requirement) {
      CompoundTag tag = new CompoundTag();
      if (requirement instanceof AdvancementRequirement aRequirement) {
        tag.putString("advancement", aRequirement.advancementId.toString());
      }
      return tag;
    }

    @Override
    public SkillRequirement<?> deserialize(FriendlyByteBuf buf) {
      ResourceLocation id = new ResourceLocation(buf.readUtf());
      return new AdvancementRequirement(id);
    }

    @Override
    public void serialize(FriendlyByteBuf buf, SkillRequirement<?> requirement) {
      if (requirement instanceof AdvancementRequirement aRequirement) {
        buf.writeUtf(aRequirement.advancementId.toString());
      }
    }

    @Override
    public SkillRequirement<?> createDefaultInstance() {
      return new AdvancementRequirement(
          new ResourceLocation("minecraft:adventure/hero_of_the_village"));
    }
  }
}
