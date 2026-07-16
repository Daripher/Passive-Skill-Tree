package daripher.skilltree.client.widget.editor.menu.description;

import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.client.widget.editor.menu.EditorMenu;
import daripher.skilltree.skill.PassiveSkill;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.List;

public class SkillDescriptionEditor extends EditorMenu {
    public SkillDescriptionEditor(SkillTreeEditor editor, EditorMenu previousMenu) {
        super(editor, previousMenu);
    }

    @Override
    public void init() {
        editor.addButton(0, 0, 90, 14, "Back").setPressFunc(b -> editor.selectMenu(previousMenu));
        if (editor.getSelectedSkills().isEmpty()) {
            return;
        }
        PassiveSkill selectedSkill = editor.getFirstSelectedSkill();
        if (selectedSkill == null) {
            return;
        }
        if (!canEditDescription()) {
            return;
        }
        List<MutableComponent> description = selectedSkill.getDescription();
        editor.addConfirmationButton(110, 0, 90, 14, "Regenerate", "Confirm").setPressFunc(b -> regenerateSkillsDescription());
        editor.increaseHeight(29);
        if (description != null) {
            for (int i = 0; i < description.size(); i++) {
                int selectedLine = i;
                String message = description.get(i).getString();
                Font font = Minecraft.getInstance().font;
                message = TooltipHelper.getTrimmedString(font, message, 190);
                editor.addButton(0, 0, 200, 14, message)
                        .setPressFunc(b -> editor.selectMenu(new SkillDescriptionLineEditor(editor, this, selectedLine)));
                editor.increaseHeight(19);
            }
        }
        editor.increaseHeight(10);
        editor.addButton(0, 0, 90, 14, "Add").setPressFunc(b -> addSelectedSkillsDescriptionLine());
        editor.addConfirmationButton(110, 0, 90, 14, "Clear", "Confirm").setPressFunc(b -> removeSelectedSkillsDescription());
        editor.increaseHeight(19);
    }

    private boolean canEditDescription() {
        PassiveSkill selectedSkill = editor.getFirstSelectedSkill();
        if (selectedSkill == null) {
            return false;
        }
        if (editor.getSelectedSkills().size() < 2) {
            return true;
        }
        for (PassiveSkill otherSkill : editor.getSelectedSkills()) {
            List<MutableComponent> description = selectedSkill.getDescription();
            List<MutableComponent> otherDescription = otherSkill.getDescription();
            if (description == null && otherDescription == null) {
                continue;
            }
            if (description == null || otherDescription == null) {
                return false;
            }
            if (description.size() != otherDescription.size()) {
                return false;
            }
            for (int i = 0; i < description.size(); i++) {
                if (!description.get(i).equals(otherDescription.get(i))) {
                    return false;
                }
            }
        }
        return true;
    }

    private void regenerateSkillsDescription() {
        editor.getSelectedSkills().forEach(skill -> skill.setDescription(null));
        editor.saveSelectedSkills();
        editor.getSelectedSkills().forEach(skill -> {
            List<MutableComponent> description = new ArrayList<>();
            editor.getSkillButton(skill.getId()).addSkillBonusTooltip(description);
            skill.setDescription(description);
        });
        editor.rebuildWidgets();
    }

    private void addSelectedSkillsDescriptionLine() {
        editor.getSelectedSkills().forEach(skill -> {
            List<MutableComponent> description = skill.getDescription();
            if (description == null) {
                description = new ArrayList<>();
                skill.setDescription(description);
            }
            description.add(Component.empty().withStyle(TooltipHelper.getSkillBonusStyle(true)));
        });
        editor.saveSelectedSkills();
        editor.rebuildWidgets();
    }

    private void removeSelectedSkillsDescription() {
        editor.getSelectedSkills().forEach(skill -> skill.setDescription(null));
        editor.saveSelectedSkills();
        editor.rebuildWidgets();
    }
}
