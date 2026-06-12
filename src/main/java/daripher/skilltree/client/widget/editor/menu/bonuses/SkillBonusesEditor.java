package daripher.skilltree.client.widget.editor.menu.bonuses;

import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.client.widget.editor.menu.EditorMenu;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.bonus.SkillBonus;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.List;

public class SkillBonusesEditor extends EditorMenu {
    public SkillBonusesEditor(SkillTreeEditor editor, EditorMenu previousMenu) {
        super(editor, previousMenu);
    }

    @Override
    public void init() {
        editor.addButton(0, 0, 90, 14, "Back").setPressFunc(b -> editor.selectMenu(previousMenu));
        editor.increaseHeight(29);
        if (!editor.canEditSkillBonuses()) {
            return;
        }
        SkillBonus<?> defaultBonus = PSTSkillBonuses.ATTRIBUTE.get().createDefaultInstance();
        editor.addSelectionMenu(110, -29, 90, defaultBonus).setResponder(skillBonus -> addSkillBonus(editor, skillBonus))
                .setMessage(Component.literal("Add"));
        PassiveSkill selectedSkill = editor.getFirstSelectedSkill();
        if (selectedSkill == null) {
            return;
        }
        List<SkillBonus<?>> bonuses = selectedSkill.getBonuses();
        for (int i = 0; i < bonuses.size(); i++) {
            final int bonusIndex = i;
            SkillBonus<?> bonus = bonuses.get(i);
            String message = bonus.getSimpleTooltip().getString();
            message = TooltipHelper.getTrimmedString(message, 190);
            editor.addButton(0, 0, 200, 14, message).setPressFunc(button -> {
                SkillBonusEditor skillBonusEditor = new SkillBonusEditor(editor, this, skillBonus -> skillBonusChanged(skillBonus, bonusIndex), () -> getSelectedSkillBonus(bonusIndex));
                editor.selectMenu(skillBonusEditor);
            });
            editor.increaseHeight(19);
        }
    }

    private @Nullable SkillBonus<?> getSelectedSkillBonus(int selectedBonusIndex) {
        if (!editor.canEditSkillBonuses()) {
            return null;
        }
        PassiveSkill selectedSkill = editor.getFirstSelectedSkill();
        if (selectedSkill == null) {
            return null;
        }
        List<SkillBonus<?>> bonuses = selectedSkill.getBonuses();
        if (selectedBonusIndex >= bonuses.size()) {
            editor.selectMenu(previousMenu);
            return null;
        }
        return selectedSkill.getBonuses().get(selectedBonusIndex);
    }

    private void skillBonusChanged(@Nullable SkillBonus<?> skillBonus, int selectedBonusIndex) {
        if (skillBonus == null) {
            deleteSelectedSkillBonuses(selectedBonusIndex);
        } else {
            setSkillBonuses(skillBonus, selectedBonusIndex);
        }
    }

    private void setSkillBonuses(SkillBonus<?> bonus, int selectedBonusIndex) {
        editor.getSelectedSkills().forEach(s -> s.getBonuses().set(selectedBonusIndex, bonus.copy()));
        editor.saveSelectedSkills();
    }

    private void deleteSelectedSkillBonuses(int selectedBonusIndex) {
        editor.getSelectedSkills().forEach(skill -> {
            if (skill.getBonuses().size() > selectedBonusIndex) {
                skill.getBonuses().remove(selectedBonusIndex);
            }
        });
        editor.saveSelectedSkills();
        editor.selectMenu(previousMenu);
    }

    private void addSkillBonus(SkillTreeEditor editor, SkillBonus<?> skillBonus) {
        editor.getSelectedSkills().forEach(s -> s.getBonuses().add(skillBonus.copy()));
        editor.saveSelectedSkills();
        editor.selectMenu(editor.getSelectedMenu().previousMenu);
    }
}
