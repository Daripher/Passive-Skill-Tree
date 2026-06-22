package daripher.skilltree.client.widget.editor.menu;

import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.skill.PassiveSkill;
import net.minecraft.ChatFormatting;

public class SkillButtonEditor extends EditorMenu {

    public SkillButtonEditor(SkillTreeEditor editor, EditorMenu previousMenu) {
        super(editor, previousMenu);
    }

    @Override
    public void init() {
        editor.addButton(0, 0, 90, 14, "Back").setPressFunc(b -> editor.selectMenu(previousMenu));
        editor.increaseHeight(29);
        PassiveSkill selectedSkill = editor.getFirstSelectedSkill();
        if (selectedSkill == null) {
            return;
        }
        if (editor.canEdit(PassiveSkill::getSkillSize)) {
            editor.addLabel(0, 0, "Size", ChatFormatting.GOLD);
            editor.increaseHeight(19);
            editor.addNumericTextField(0, 0, 40, 14, selectedSkill.getSkillSize()).setNumericFilter(d -> d >= 2)
                    .setNumericResponder(this::setSkillsSize);
            editor.increaseHeight(19);
        }
        if (editor.getSelectedSkills().size() == 1) {
            editor.increaseHeight(-38);
            editor.addLabel(65, 0, "Position", ChatFormatting.GOLD);
            editor.increaseHeight(19);
            editor.addNumericTextField(65, 0, 60, 14, selectedSkill.getPositionX())
                    .setNumericResponder(v -> setSkillPosition(v.floatValue(), selectedSkill.getPositionY()));
            editor.addNumericTextField(130, 0, 60, 14, selectedSkill.getPositionY())
                    .setNumericResponder(v -> setSkillPosition(selectedSkill.getPositionX(), v.floatValue()));
            editor.increaseHeight(19);
        }
        if (editor.canEdit(PassiveSkill::getTitle)) {
            editor.addLabel(0, 0, "Title", ChatFormatting.GOLD);
            editor.increaseHeight(19);
            editor.addTextField(0, 0, 200, 14, selectedSkill.getTitle()).setResponder(this::setSkillsTitle);
            editor.increaseHeight(19);
        }
        boolean canEditTitleColor = editor.canEdit(PassiveSkill::getTitleColor);
        if (canEditTitleColor) {
            editor.addLabel(0, 0, "Title Color", ChatFormatting.GOLD);
            editor.increaseHeight(19);
            editor.addTextField(0, 0, 80, 14, selectedSkill.getTitleColor())
                    .setSoftFilter(v -> v.matches("^#?[a-fA-F0-9]{6}") || v.isEmpty()).setResponder(this::setSkillsTitleColor);
            editor.increaseHeight(19);
        }
        if (editor.canEdit(PassiveSkill::isStartingPoint)) {
            editor.addLabel(0, 0, "Starting Point", ChatFormatting.GOLD);
            editor.increaseHeight(19);
            editor.addCheckBox(0, 0, selectedSkill.isStartingPoint()).setResponder(v -> {
                editor.getSelectedSkills().forEach(s -> s.setStartingPoint(v));
                editor.saveSelectedSkills();
            });
            editor.increaseHeight(19);
        }
        if (editor.canEdit(PassiveSkill::isAlwaysStartingPoint)) {
            editor.addLabel(0, 0, "Always Starting Point", ChatFormatting.GOLD);
            editor.increaseHeight(19);
            editor.addCheckBox(0, 0, selectedSkill.isAlwaysStartingPoint()).setResponder(v -> {
                editor.getSelectedSkills().forEach(s -> s.setAlwaysStartingPoint(v));
                editor.saveSelectedSkills();
            });
            editor.increaseHeight(19);
        }
    }

    private void setSkillsSize(double size) {
        editor.getSelectedSkills().forEach(skill -> {
            skill.setButtonSize((int) size);
            editor.removeSkillButton(skill);
            editor.addSkillButton(skill);
        });
        editor.updateSkillConnections();
        editor.saveSelectedSkills();
    }

    private void setSkillPosition(float x, float y) {
        PassiveSkill selectedSkill = editor.getFirstSelectedSkill();
        if (selectedSkill == null) {
            return;
        }
        selectedSkill.setPosition(x, y);
        editor.removeSkillButton(selectedSkill);
        editor.addSkillButton(selectedSkill);
        editor.updateSkillConnections();
        editor.saveSelectedSkills();
    }

    private void setSkillsTitle(String title) {
        editor.getSelectedSkills().forEach(skill -> skill.setTitle(title));
        editor.saveSelectedSkills();
    }

    private void setSkillsTitleColor(String color) {
        if (color.startsWith("#")) {
            color = color.substring(1);
        }
        String finalColor = color;
        editor.getSelectedSkills().forEach(skill -> skill.setTitleColor(finalColor));
        editor.saveSelectedSkills();
    }
}
