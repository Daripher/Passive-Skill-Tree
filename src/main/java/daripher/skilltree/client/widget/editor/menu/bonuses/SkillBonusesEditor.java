package daripher.skilltree.client.widget.editor.menu.bonuses;

import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.client.widget.editor.menu.EditorMenu;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.bonus.SkillBonus;
import net.minecraft.network.chat.Component;

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
    editor
        .addSelectionMenu(110, -29, 90, defaultBonus)
        .setResponder(skillBonus -> addSkillBonus(editor, skillBonus))
        .setMessage(Component.literal("Add"));
    PassiveSkill selectedSkill = editor.getFirstSelectedSkill();
    if (selectedSkill == null) {
        return;
    }
    List<SkillBonus<?>> bonuses = selectedSkill.getBonuses();
    for (int i = 0; i < bonuses.size(); i++) {
      final int bonusIndex = i;
      SkillBonus<?> bonus = bonuses.get(i);
      String message = bonus.getTooltip().getString();
      message = TooltipHelper.getTrimmedString(message, 190);
      editor
          .addButton(0, 0, 200, 14, message)
          .setPressFunc(b -> editor.selectMenu(new SkillBonusEditor(editor, this, bonusIndex)));
      editor.increaseHeight(19);
    }
  }

  private void addSkillBonus(SkillTreeEditor editor, SkillBonus<?> skillBonus) {
    editor.getSelectedSkills().forEach(s -> s.getBonuses().add(skillBonus.copy()));
    editor.saveSelectedSkills();
    editor.selectMenu(editor.getSelectedMenu().previousMenu);
  }
}
