package daripher.skilltree.client.screen.editor.tool;

import daripher.skilltree.client.screen.editor.SkillTreeEditorScreen;
import daripher.skilltree.client.widget.SkillButton;

public class ConnectTool extends EditorTool {
	@Override
	public void skillButtonPressed(SkillTreeEditorScreen parentScreen, SkillButton button) {
		if (parentScreen.selectedSkills.size() == 1) {
			parentScreen.addNewConnection(parentScreen.selectedSkills.get(0), button.skill.getId());
			parentScreen.selectedSkills.clear();
		} else {
			parentScreen.selectedSkills.add(button.skill.getId());
		}
		parentScreen.rebuildWidgets();
	}

	@Override
	public void toolSelected(SkillTreeEditorScreen parentScreen) {
		parentScreen.selectedSkills.clear();
		parentScreen.rebuildWidgets();
	}
}
