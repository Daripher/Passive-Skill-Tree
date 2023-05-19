package daripher.skilltree.client.screen.editor.tool;

import daripher.skilltree.client.screen.editor.SkillTreeEditorScreen;
import daripher.skilltree.client.widget.SkillButton;

public abstract class EditorTool {
	public static final EditorTool SELECT = new SelectTool();
	public static final EditorTool CONNECT = new ConnectTool();

	public abstract void skillButtonPressed(SkillTreeEditorScreen parentScreen, SkillButton button);

	public void toolSelected(SkillTreeEditorScreen parentScreen) {
	}
}
