package daripher.skilltree.skill.requirement;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.init.PSTSkillRequirements;
import daripher.skilltree.skill.PassiveSkill;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class LearnedSkillRequirement implements SkillRequirement<LearnedSkillRequirement> {
  private ResourceLocation skillId;

  public LearnedSkillRequirement(ResourceLocation skillId) {
    this.skillId = skillId;
  }

  @Override
  public boolean test(Player player) {
    if (!PlayerSkillsProvider.hasSkills(player)) {
      return false;
    }
    NonNullList<PassiveSkill> skills = PlayerSkillsProvider.get(player).getPlayerSkills();
    return skills.stream().map(PassiveSkill::getId).anyMatch(skillId::equals);
  }

  @Override
  public MutableComponent getTooltip() {
    Component skillTitle = TooltipHelper.getSkillTitle(skillId);
    return Component.translatable(getDescriptionId(), skillTitle);
  }

  @Override
  public void addEditorWidgets(SkillTreeEditor editor, Consumer<LearnedSkillRequirement> consumer) {
    editor.addLabel(0, 0, "Skill ID", ChatFormatting.GOLD);
    editor.increaseHeight(19);
    Set<ResourceLocation> skillIDs = SkillsReloader.getSkills().keySet();
    editor
        .addSelectionMenu(0, 0, 200, skillIDs)
        .setValue(getSkillId())
        .setElementNameGetter(v -> Component.literal(v.toString()))
        .setResponder(v -> selectSkillId(consumer, v));
    editor.increaseHeight(19);
  }

  private void selectSkillId(Consumer<LearnedSkillRequirement> consumer, ResourceLocation id) {
    setSkillId(id);
    consumer.accept(this);
  }

  public void setSkillId(ResourceLocation skillId) {
    this.skillId = skillId;
  }

  @Override
  public LearnedSkillRequirement copy() {
    return new LearnedSkillRequirement(skillId);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    LearnedSkillRequirement that = (LearnedSkillRequirement) o;
    return Objects.equals(skillId, that.skillId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(skillId);
  }

  public ResourceLocation getSkillId() {
    return skillId;
  }

  @Override
  public SkillRequirement.Serializer getSerializer() {
    return PSTSkillRequirements.LEARNED_SKILL.get();
  }

  public static class Serializer implements SkillRequirement.Serializer {
    @Override
    public SkillRequirement<?> deserialize(JsonObject json) throws JsonParseException {
      ResourceLocation id = new ResourceLocation(json.get("skill_id").getAsString());
      return new LearnedSkillRequirement(id);
    }

    @Override
    public void serialize(JsonObject json, SkillRequirement<?> requirement) {
      if (requirement instanceof LearnedSkillRequirement aRequirement) {
        json.addProperty("skill_id", aRequirement.skillId.toString());
      }
    }

    @Override
    public SkillRequirement<?> deserialize(CompoundTag tag) {
      ResourceLocation id = new ResourceLocation(tag.getString("skill_id"));
      return new LearnedSkillRequirement(id);
    }

    @Override
    public CompoundTag serialize(SkillRequirement<?> requirement) {
      CompoundTag tag = new CompoundTag();
      if (requirement instanceof LearnedSkillRequirement aRequirement) {
        tag.putString("skill_id", aRequirement.skillId.toString());
      }
      return tag;
    }

    @Override
    public SkillRequirement<?> deserialize(FriendlyByteBuf buf) {
      ResourceLocation id = new ResourceLocation(buf.readUtf());
      return new LearnedSkillRequirement(id);
    }

    @Override
    public void serialize(FriendlyByteBuf buf, SkillRequirement<?> requirement) {
      if (requirement instanceof LearnedSkillRequirement aRequirement) {
        buf.writeUtf(aRequirement.skillId.toString());
      }
    }

    @Override
    public SkillRequirement<?> createDefaultInstance() {
      return new LearnedSkillRequirement(new ResourceLocation("skilltree:hunter_1"));
    }
  }
}
