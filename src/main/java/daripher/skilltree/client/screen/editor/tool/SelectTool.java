package daripher.skilltree.client.screen.editor.tool;

import daripher.skilltree.client.screen.editor.SkillTreeEditorScreen;
import daripher.skilltree.client.widget.SkillButton;
import net.minecraft.client.gui.screens.Screen;

public class SelectTool extends EditorTool {
	@Override
	public void skillButtonPressed(SkillTreeEditorScreen parentScreen, SkillButton button) {
		if (!Screen.hasShiftDown()) {
			parentScreen.selectedSkills.clear();
		}
		parentScreen.selectedSkills.add(button.skill.getId());
	}
}
