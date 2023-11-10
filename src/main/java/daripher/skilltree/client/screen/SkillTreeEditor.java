package daripher.skilltree.client.screen;

import daripher.skilltree.client.widget.EnumCycleButton;
import daripher.skilltree.client.widget.PSTNumericEditBox;
import daripher.skilltree.client.widget.PSTButton;
import daripher.skilltree.client.widget.PSTEditBox;
import daripher.skilltree.skill.PassiveSkill;
import java.util.stream.Stream;

public interface SkillTreeEditor {
  PSTEditBox addTextField(int x, int y, int width, int height, String defaultValue);

  PSTNumericEditBox addNumericTextField(int x, int y, int width, int height, double defaultValue);

  PSTButton addRemoveButton(int x, int y, int width, int height);

  <T extends Enum<T>> EnumCycleButton<T> addEnumCycleButton(
      int x, int y, int width, int height, T defaultValue);

  Stream<PassiveSkill> getSelectedSkills();

  void rebuildWidgets();

  void shiftWidgets(int x, int y);
}
