package daripher.skilltree.client.widget.editor.menu.bonuses;

import daripher.skilltree.data.client.SkillTreeEditorData;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.client.widget.editor.menu.EditorMenu;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.bonus.SkillBonus;

import java.util.List;

public class SkillBonusEditor extends EditorMenu {
    private final int selectedBonus;

    public SkillBonusEditor(SkillTreeEditor editor, EditorMenu previousMenu, int selectedBonus) {
        super(editor, previousMenu);
        this.selectedBonus = selectedBonus;
    }

    @Override
    public void init() {
        editor.addButton(0, 0, 90, 14, "Back").setPressFunc(b -> editor.selectMenu(previousMenu));
        editor.addConfirmationButton(110, 0, 90, 14, "Remove", "Confirm").setPressFunc(b -> deleteSelectedSkillBonuses(editor));
        editor.increaseHeight(29);
        if (!editor.canEditSkillBonuses()) {
            return;
        }
        PassiveSkill selectedSkill = editor.getFirstSelectedSkill();
        if (selectedSkill == null) {
            return;
        }
        List<SkillBonus<?>> bonuses = selectedSkill.getBonuses();
        if (selectedBonus >= bonuses.size()) {
            editor.selectMenu(previousMenu);
            return;
        }
        selectedSkill.getBonuses().get(selectedBonus).addEditorWidgets(editor, selectedBonus, b -> setSkillBonuses(editor, b));
    }

    private void setSkillBonuses(SkillTreeEditor editor, SkillBonus<?> b) {
        editor.getSelectedSkills().forEach(s -> s.getBonuses().set(selectedBonus, b.copy()));
        editor.saveSelectedSkills();
    }

    private void deleteSelectedSkillBonuses(SkillTreeEditor editor) {
        editor.getSelectedSkills().forEach(s -> removeSkillBonus(s, selectedBonus));
        editor.selectMenu(previousMenu);
        editor.saveSelectedSkills();
        editor.rebuildWidgets();
    }

    private void removeSkillBonus(PassiveSkill skill, int index) {
        if (skill.getBonuses().size() > index) {
            skill.getBonuses().remove(index);
            SkillTreeEditorData.saveEditorSkill(skill);
        }
    }
}
