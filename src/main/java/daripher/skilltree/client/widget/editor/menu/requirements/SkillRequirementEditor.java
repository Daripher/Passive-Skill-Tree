package daripher.skilltree.client.widget.editor.menu.requirements;

import daripher.skilltree.client.data.SkillTreeClientData;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.client.widget.editor.menu.EditorMenu;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.requirement.SkillStatRequirement;
import java.util.List;

public class SkillRequirementEditor extends EditorMenu {
  private final int selectedRequirement;

  public SkillRequirementEditor(
      SkillTreeEditor editor, EditorMenu previousMenu, int selectedBonus) {
    super(editor, previousMenu);
    this.selectedRequirement = selectedBonus;
  }

  @Override
  public void init() {
    editor.addButton(0, 0, 90, 14, "Back").setPressFunc(b -> editor.selectMenu(previousMenu));
    editor
        .addConfirmationButton(110, 0, 90, 14, "Remove", "Confirm")
        .setPressFunc(b -> deleteSelectedSkillBonuses(editor));
    editor.increaseHeight(29);
    if (!editor.canEditSkillRequirements()) return;
    PassiveSkill selectedSkill = editor.getFirstSelectedSkill();
    if (selectedSkill == null) return;
    List<SkillStatRequirement> requirements = selectedSkill.getRequirements();
    if (selectedRequirement >= requirements.size()) {
      editor.selectMenu(previousMenu);
      return;
    }
    SkillStatRequirement requirement = selectedSkill.getRequirements().get(selectedRequirement);
    requirement.addEditorWidgets(editor, b -> setSkillBonuses(editor, b));
  }

  private void setSkillBonuses(SkillTreeEditor editor, SkillStatRequirement requirement) {
    editor
        .getSelectedSkills()
        .forEach(s -> s.getRequirements().set(selectedRequirement, requirement.copy()));
    editor.saveSelectedSkills();
  }

  private void deleteSelectedSkillBonuses(SkillTreeEditor editor) {
    editor.getSelectedSkills().forEach(s -> removeRequirement(s, selectedRequirement));
    editor.selectMenu(previousMenu);
    editor.saveSelectedSkills();
    editor.rebuildWidgets();
  }

  private void removeRequirement(PassiveSkill skill, int index) {
    if (skill.getRequirements().size() > index) {
      skill.getRequirements().remove(index);
      SkillTreeClientData.saveEditorSkill(skill);
    }
  }
}
