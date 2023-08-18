package daripher.skilltree.client.screen.editor;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.mojang.blaze3d.vertex.PoseStack;

import daripher.skilltree.client.screen.SkillTreeScreen;
import daripher.skilltree.client.screen.editor.tool.EditorTool;
import daripher.skilltree.client.widget.EditorToolButton;
import daripher.skilltree.client.widget.SkillButton;
import daripher.skilltree.skill.PassiveSkill;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.resources.ResourceLocation;

public class SkillTreeEditorScreen extends SkillTreeScreen {
	public final List<ResourceLocation> selectedSkills = new ArrayList<>();
	private final List<Pair<ResourceLocation, ResourceLocation>> newConnections = new ArrayList<>();
	private EditorTool selectedTool = EditorTool.SELECT;

	public SkillTreeEditorScreen(ResourceLocation skillTreeId) {
		super(skillTreeId);
	}

	@Override
	public void init() {
		maxScrollX = 512;
		maxScrollY = 512;
		addSkillButtons();
		addSkillConnections();
		addToolButtons();
	}

	protected void addSkillButton(ResourceLocation skillId, PassiveSkill skill) {
		var buttonX = (int) (skill.getPositionX() + scrollX + width / 2);
		var buttonY = (int) (skill.getPositionY() + scrollY + height / 2);
		var button = new SkillButton(() -> renderAnimation, buttonX, buttonY, skill, this::buttonPressed);
		addRenderableWidget(button);
		skillButtons.put(skillId, button);
		if (selectedSkills.contains(skill.getId())) {
			button.highlighted = true;
		}
	}

	private void addToolButtons() {
		addRenderableWidget(new EditorToolButton(this, EditorTool.SELECT, 0, 0));
		addRenderableWidget(new EditorToolButton(this, EditorTool.CONNECT, 0, 15));
	}

	public void renderButtonTooltip(GuiGraphics graphics, Button button, PoseStack poseStack, int mouseX, int mouseY) {
	if (!(button instanceof SkillButton)) return;
		var borderStyleStack = ((SkillButton) button).getTooltipBorderStyleStack();
		var tooltip = ((SkillButton) button).getSkillTooltip();
		graphics.renderComponentTooltip(font, tooltip, mouseX, mouseY, borderStyleStack);
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		renderAnimation += partialTick;
		renderBackground(graphics);
		renderConnections(graphics, mouseX, mouseY, partialTick);
		for (Renderable widget : renderables) {
			widget.render(graphics, mouseX, mouseY, partialTick);
		}
	}

	@Override
	protected void skillButtonPressed(SkillButton button) {
		selectedTool.skillButtonPressed(this, button);
		rebuildWidgets();
	}

	@Override
	public void buttonPressed(Button button) {
		if (button instanceof EditorToolButton toolButton) {
			toolButtonPressed(toolButton);
		}
		super.buttonPressed(button);
	}

	public void toolButtonPressed(EditorToolButton button) {
		this.selectedTool = button.tool;
		button.tool.toolSelected(this);
	}

	public void addNewConnection(ResourceLocation skillId1, ResourceLocation skillId2) {
		newConnections.add(Pair.of(skillId1, skillId2));
	}

	@Override
	public void rebuildWidgets() {
		super.rebuildWidgets();
	}
}
