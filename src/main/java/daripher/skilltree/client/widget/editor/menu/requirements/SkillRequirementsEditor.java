package daripher.skilltree.client.widget.editor.menu.requirements;

import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.client.widget.editor.menu.EditorMenu;
import daripher.skilltree.init.PSTSkillRequirements;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.requirement.SkillRequirement;
import net.minecraft.network.chat.Component;

import java.util.List;

public class SkillRequirementsEditor extends EditorMenu {
    public SkillRequirementsEditor(SkillTreeEditor editor, EditorMenu previousMenu) {
        super(editor, previousMenu);
    }

    @Override
    public void init() {
        editor.addButton(0, 0, 90, 14, "Back").setPressFunc(b -> editor.selectMenu(previousMenu));
        editor.increaseHeight(29);
        if (editor.selectedMismatchingRequirements()) {
            return;
        }
        SkillRequirement<?> defaultRequirement = PSTSkillRequirements.STAT_VALUE.get().createDefaultInstance();
        editor.addSelectionMenu(110, -29, 90, defaultRequirement).setResponder(requirement -> addSkillRequirement(editor, requirement))
                .setMessage(Component.literal("Add"));
        PassiveSkill selectedSkill = editor.getFirstSelectedSkill();
        if (selectedSkill == null) {
            return;
        }
        List<SkillRequirement<?>> requirements = selectedSkill.getRequirements();
        for (int i = 0; i < requirements.size(); i++) {
            final int requirementIndex = i;
            SkillRequirement<?> requirement = requirements.get(i);
            String message = requirement.getTooltip().getString();
            message = TooltipHelper.getTrimmedString(message, 190);
            editor.addButton(0, 0, 200, 14, message)
                    .setPressFunc(b -> editor.selectMenu(new SkillRequirementEditor(editor, this, requirementIndex)));
            editor.increaseHeight(19);
        }
    }

    private void addSkillRequirement(SkillTreeEditor editor, SkillRequirement<?> requirement) {
        editor.getSelectedSkills().forEach(s -> s.getRequirements().add(requirement.copy()));
        editor.saveSelectedSkills();
        editor.selectMenu(editor.getSelectedMenu().previousMenu);
    }
}
