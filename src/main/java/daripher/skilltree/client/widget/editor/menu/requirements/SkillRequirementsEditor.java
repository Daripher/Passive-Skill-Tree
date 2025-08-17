package daripher.skilltree.client.widget.editor.menu.requirements;

import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.client.widget.editor.menu.EditorMenu;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.requirement.SkillStatRequirement;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraftforge.registries.ForgeRegistries;

public class SkillRequirementsEditor extends EditorMenu {
  public SkillRequirementsEditor(SkillTreeEditor editor, EditorMenu previousMenu) {
    super(editor, previousMenu);
  }

  @Override
  public void init() {
    editor.addButton(0, 0, 90, 14, "Back").setPressFunc(b -> editor.selectMenu(previousMenu));
    editor.increaseHeight(29);
    if (!editor.canEditSkillRequirements()) return;
    SkillStatRequirement defaultRequirement =
        new SkillStatRequirement(
            ForgeRegistries.STAT_TYPES.getKey(Stats.CUSTOM), Stats.INTERACT_WITH_FURNACE, 1);
    editor
        .addSelectionMenu(110, -29, 90, defaultRequirement)
        .setResponder(skillBonus -> addSkillRequirements(editor, skillBonus))
        .setMessage(Component.literal("Add"));
    PassiveSkill selectedSkill = editor.getFirstSelectedSkill();
    if (selectedSkill == null) return;
    List<SkillStatRequirement> requirements = selectedSkill.getRequirements();
    for (int i = 0; i < requirements.size(); i++) {
      final int requirementIndex = i;
      SkillStatRequirement bonus = requirements.get(i);
      String message = bonus.getTooltip().getString();
      message = TooltipHelper.getTrimmedString(message, 190);
      editor
          .addButton(0, 0, 200, 14, message)
          .setPressFunc(
              b -> editor.selectMenu(new SkillRequirementEditor(editor, this, requirementIndex)));
      editor.increaseHeight(19);
    }
  }

  private void addSkillRequirements(SkillTreeEditor editor, SkillStatRequirement requirement) {
    editor.getSelectedSkills().forEach(s -> s.getRequirements().add(requirement.copy()));
    editor.saveSelectedSkills();
    editor.selectMenu(editor.getSelectedMenu().previousMenu);
  }
}
