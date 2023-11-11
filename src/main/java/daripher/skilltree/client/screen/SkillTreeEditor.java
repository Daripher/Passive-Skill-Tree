package daripher.skilltree.client.screen;

import daripher.skilltree.client.widget.*;
import daripher.skilltree.skill.PassiveSkill;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;

public interface SkillTreeEditor {
  TextField addTextField(int x, int y, int width, int height, String defaultValue);

  NumericTextField addNumericTextField(int x, int y, int width, int height, double defaultValue);

  Button addRemoveButton(int x, int y, int width, int height);

  Label addLabel(int x, int y, String text, ChatFormatting... styles);

  <T extends Enum<T>> EnumCycleButton<T> addEnumCycleButton(
      int x, int y, int width, int height, T defaultValue);

  Stream<PassiveSkill> getSelectedSkills();

  void rebuildWidgets();

  void shiftWidgets(int x, int y);
}
