package daripher.skilltree.client.widget.editor.menu;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.client.data.SkillTreeClientData;
import daripher.skilltree.client.widget.NumericTextField;
import daripher.skilltree.client.widget.editor.SkillFactory;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.bonus.SkillBonus;
import javax.annotation.Nullable;

import daripher.skilltree.skill.requirement.SkillStatRequirement;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class SkillNodeEditor extends EditorMenu {
  private NumericTextField distanceEditor;
  private NumericTextField angleEditor;
  private static double lastUsedDistance = 10;
  private static double lastUsedAngle = 0;

  public SkillNodeEditor(SkillTreeEditor editor, EditorMenu previousMenu) {
    super(editor, previousMenu);
  }

  @Override
  public void init() {
    editor.addButton(0, 0, 90, 14, "Back").setPressFunc(b -> editor.selectMenu(previousMenu));
    editor.increaseHeight(29);
    if (editor.getSelectedSkills().isEmpty()) return;
    editor.addLabel(0, 0, "Distance", ChatFormatting.GOLD);
    editor.addLabel(65, 0, "Angle", ChatFormatting.GOLD);
    editor.increaseHeight(19);
    distanceEditor = editor.addNumericTextField(0, 0, 60, 14, lastUsedDistance);
    distanceEditor.setNumericResponder(v -> lastUsedDistance = v);
    angleEditor = editor.addNumericTextField(65, 0, 60, 14, lastUsedAngle);
    angleEditor.setNumericResponder(v -> lastUsedAngle = v);
    editor.increaseHeight(19);
    editor.addButton(0, 0, 60, 14, "Add").setPressFunc(b -> createSkills(this::createNewSkill));
    editor.addButton(65, 0, 60, 14, "Copy").setPressFunc(b -> createSkills(this::createSkillCopy));
    editor.increaseHeight(19);
    editor.addMirrorerWidgets();
  }

  private void createSkills(SkillFactory factory) {
    float angle = (float) angleEditor.getNumericValue();
    float distance = (float) distanceEditor.getNumericValue();
    createSkills(angle, distance, factory);
  }

  private void createSkills(float angle, float distance, SkillFactory skillFactory) {
    editor.getSelectedSkills().forEach(skill -> createSkill(distance, angle, skill, skillFactory));
    editor.getSkillMirrorer().createSkills(angle, distance, skillFactory);
    editor.rebuildWidgets();
  }

  private void createSkill(
      float distance, float angle, PassiveSkill skill, SkillFactory skillFactory) {
    angle = (float) Math.toRadians(angle);
    float skillSize = skill.getSkillSize() / 2f + 8;
    float skillX = skill.getPositionX() + Mth.sin(angle) * (distance + skillSize);
    float skillY = skill.getPositionY() + Mth.cos(angle) * (distance + skillSize);
    skillFactory.accept(skillX, skillY, skill);
  }

  private void createSkillCopy(float x, float y, PassiveSkill original) {
    ResourceLocation skillTreeId = editor.getSkillTree().getId();
    PassiveSkill skill =
        new PassiveSkill(
            createNewSkillId(skillTreeId),
            original.getSkillSize(),
            original.getFrameTexture(),
            original.getIconTexture(),
            original.getTooltipFrameTexture(),
            original.isStartingPoint());
    skill.setPosition(x, y);
    skill.setConnectedTree(original.getConnectedTreeId());
    skill.setStartingPoint(original.isStartingPoint());
    original.getBonuses().stream().map(SkillBonus::copy).forEach(skill::addSkillBonus);
    original.getRequirements().stream().map(SkillStatRequirement::copy).forEach(skill::addSkillRequirement);
    original.getTags().forEach(skill.getTags()::add);
    skill.setTitle(original.getTitle());
    skill.setTitleColor(original.getTitleColor());
    skill.setDescription(original.getDescription());
    skill.connect(original);
    SkillTreeClientData.saveEditorSkill(skill);
    SkillTreeClientData.loadEditorSkill(skill.getId());
    editor.getSkillTree().getSkillIds().add(skill.getId());
    SkillTreeClientData.saveEditorSkillTree(editor.getSkillTree());
  }

  private void createNewSkill(float x, float y, @Nullable PassiveSkill original) {
    ResourceLocation background =
        new ResourceLocation(SkillTreeMod.MOD_ID, "textures/icons/background/lesser.png");
    ResourceLocation icon = new ResourceLocation(SkillTreeMod.MOD_ID, "textures/icons/void.png");
    ResourceLocation border =
        new ResourceLocation(SkillTreeMod.MOD_ID, "textures/tooltip/lesser.png");
    ResourceLocation skillTreeId = editor.getSkillTree().getId();
    PassiveSkill skill =
        new PassiveSkill(createNewSkillId(skillTreeId), 16, background, icon, border, false);
    skill.setPosition(x, y);
    if (original != null) skill.connect(original);
    SkillTreeClientData.saveEditorSkill(skill);
    SkillTreeClientData.loadEditorSkill(skill.getId());
    editor.getSkillTree().getSkillIds().add(skill.getId());
    SkillTreeClientData.saveEditorSkillTree(editor.getSkillTree());
  }

  public static ResourceLocation createNewSkillId(ResourceLocation skillTreeId) {
    ResourceLocation id;
    int counter = 1;
    do {
      id = new ResourceLocation("skilltree", skillTreeId.getPath() + "_" + counter++);
    } while (SkillTreeClientData.getEditorSkill(id) != null);
    return id;
  }
}
