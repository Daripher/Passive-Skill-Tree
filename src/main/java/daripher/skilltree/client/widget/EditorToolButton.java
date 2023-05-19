package daripher.skilltree.client.widget;

import daripher.skilltree.client.screen.editor.SkillTreeEditorScreen;
import daripher.skilltree.client.screen.editor.tool.EditorTool;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class EditorToolButton extends Button {
	public final EditorTool tool;

	public EditorToolButton(SkillTreeEditorScreen parentScreen, EditorTool tool, int x, int y) {
		super(x, y, 15, 15, Component.empty(), parentScreen::buttonPressed, parentScreen::renderButtonTooltip);
		this.tool = tool;
	}
}
