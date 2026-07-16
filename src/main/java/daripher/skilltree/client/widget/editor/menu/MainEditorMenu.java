package daripher.skilltree.client.widget.editor.menu;

import daripher.skilltree.data.client.SkillTreeEditorData;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.client.widget.editor.menu.bonuses.SkillBonusesEditor;
import daripher.skilltree.client.widget.editor.menu.description.SkillDescriptionEditor;
import daripher.skilltree.client.widget.editor.menu.requirements.SkillRequirementsEditor;
import daripher.skilltree.client.widget.editor.menu.tags.SkillTagsEditor;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.PassiveSkillTree;

import java.util.Set;

public class MainEditorMenu extends EditorMenu {
    public MainEditorMenu(SkillTreeEditor editor) {
        super(editor, null);
    }

    public void init() {
        clearWidgets();
        addMenuSelectionButton(editor, "Grid", GridSettingsEditor::new);
        if (editor.getSelectedSkills().isEmpty()) {
            return;
        }
        addMenuSelectionButton(editor, "Bonuses", SkillBonusesEditor::new);
        addMenuSelectionButton(editor, "Requirements", SkillRequirementsEditor::new);
        addMenuSelectionButton(editor, "Textures", SkillTexturesEditor::new);
        addMenuSelectionButton(editor, "Button", SkillButtonEditor::new);
        addMenuSelectionButton(editor, "New Skill", SkillNodeEditor::new);
        addMenuSelectionButton(editor, "Tags", SkillTagsEditor::new);
        addMenuSelectionButton(editor, "Description", SkillDescriptionEditor::new);
        if (editor.getSelectedSkills().size() >= 2) {
            addMenuSelectionButton(editor, "Connections", SkillConnectionsEditor::new);
        }
        editor.addConfirmationButton(0, 0, 200, 14, "Remove", "Confirm").setPressFunc(b -> deleteSelectedSkills());
        editor.increaseHeight(19);
    }

    private void deleteSelectedSkills() {
        Set<PassiveSkill> selectedSkills = editor.getSelectedSkills();
        PassiveSkillTree skillTree = editor.getSkillTree();
        selectedSkills.forEach(skill -> {
            skillTree.getSkillIds().remove(skill.getId());
            SkillTreeEditorData.deleteEditorSkill(skill);
            SkillTreeEditorData.saveEditorSkillTree(skillTree);
        });
        selectedSkills.clear();
        editor.rebuildWidgets();
    }

    private void addMenuSelectionButton(SkillTreeEditor editor, String name, MenuConstructor menuConstructor) {
        editor.addButton(0, 0, 200, 14, name).setPressFunc(b -> selectMenu(editor, menuConstructor));
        editor.increaseHeight(19);
    }

    private void selectMenu(SkillTreeEditor editor, MenuConstructor menuConstructor) {
        editor.selectMenu(menuConstructor.construct(editor, this));
    }
}
