package daripher.skilltree.client.screen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.google.common.base.Predicates;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Axis;

import daripher.skilltree.client.SkillTreeClientData;
import daripher.skilltree.client.widget.SkillButton;
import daripher.skilltree.client.widget.SkillConnection;
import daripher.skilltree.client.widget.SkillTreeButton;
import daripher.skilltree.client.widget.StatsList;
import daripher.skilltree.skill.PassiveSkill;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class SkillTreeEditorScreen extends Screen {
	private final Map<ResourceLocation, SkillButton> skillButtons = new HashMap<>();
	private final List<SkillConnection> skillConnections = new ArrayList<>();
	private final List<SkillConnection> gatewayConnections = new ArrayList<>();
	private final Set<ResourceLocation> selectedSkills = new HashSet<>();
	private final ResourceLocation treeId;
	private int prevMouseX;
	private int prevMouseY;
	private float zoom = 1F;
	protected double scrollSpeedX;
	protected double scrollSpeedY;
	protected double scrollX;
	protected double scrollY;
	protected int maxScrollX;
	protected int maxScrollY;

	public SkillTreeEditorScreen(ResourceLocation skillTreeId) {
		super(Component.empty());
		this.treeId = skillTreeId;
		this.minecraft = Minecraft.getInstance();
	}

	@Override
	public void init() {
		clearWidgets();
		addSkillButtons();
		maxScrollX -= width / 2 - 80;
		maxScrollY -= height / 2 - 80;
		if (maxScrollX < 0) maxScrollX = 0;
		if (maxScrollY < 0) maxScrollY = 0;
		addSkillConnections();
		addToolButtons();
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		updateScreen(partialTick);
		renderBackground(graphics);
		renderConnections(graphics, mouseX, mouseY, partialTick);
		renderSkills(graphics, mouseX, mouseY, partialTick);
		RenderSystem.enableBlend();
		renderOverlay(graphics, mouseX, mouseY, partialTick);
		renderWidgets(graphics, mouseX, mouseY, partialTick);
		renderSkillTooltip(graphics, mouseX, mouseY, partialTick);
		prevMouseX = mouseX;
		prevMouseY = mouseY;
	}

	private void renderWidgets(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		for (Renderable widget : renderables) {
			if (widget instanceof SkillButton) continue;
			widget.render(graphics, mouseX, mouseY, partialTick);
		}
	}

	private void renderSkillTooltip(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		if (getWidgetAt(mouseX, mouseY).isPresent()) return;
		getSkillAt(mouseX, mouseY).ifPresent(button -> {
			renderSkillTooltip(button, graphics, mouseX + (prevMouseX - mouseX) * partialTick, mouseY + (prevMouseY - mouseY) * partialTick);
		});
	}

	private void renderSkills(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		graphics.pose().pushPose();
		graphics.pose().translate(scrollX, scrollY, 0);
		for (SkillButton widget : skillButtons.values()) {
			graphics.pose().pushPose();
			graphics.pose().translate(widget.x + widget.getWidth() / 2, widget.y + widget.getHeight() / 2, 0F);
			graphics.pose().scale(zoom, zoom, 1F);
			graphics.pose().translate(-widget.x - widget.getWidth() / 2, -widget.y - widget.getHeight() / 2, 0F);
			widget.render(graphics, mouseX, mouseY, partialTick);
			if (selectedSkills.contains(widget.skill.getId())) {
				graphics.pose().pushPose();
				graphics.pose().translate(widget.x, widget.y, 0);
				ScreenHelper.drawRectangle(graphics, -1, -1, widget.getWidth() + 2, widget.getHeight() + 2, 0xAA32FF00);
				graphics.pose().popPose();
			}
			graphics.pose().popPose();
		}
		graphics.pose().popPose();
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		Optional<GuiEventListener> widget = getWidgetAt(mouseX, mouseY);
		if (widget.isPresent()) return widget.get().mouseClicked(mouseX, mouseY, button);
		Optional<SkillButton> skill = getSkillAt(mouseX, mouseY);
		if (skill.isPresent()) return skill.get().mouseClicked(skill.get().x + 1, skill.get().y + 1, button);
		return false;
	}

	public Optional<GuiEventListener> getWidgetAt(double mouseX, double mouseY) {
		return super.getChildAt(mouseX, mouseY).filter(Predicates.not(SkillButton.class::isInstance));
	}

	public Optional<SkillButton> getSkillAt(double mouseX, double mouseY) {
		mouseX -= scrollX;
		mouseY -= scrollY;
		for (SkillButton button : skillButtons.values()) {
			double skillSize = button.skill.getButtonSize() * zoom;
			double skillX = button.x + button.getWidth() / 2 - skillSize / 2;
			double skillY = button.y + button.getHeight() / 2 - skillSize / 2;
			if (mouseX >= skillX && mouseY >= skillY && mouseX < skillX + skillSize && mouseY < skillY + skillSize) {
				return Optional.of(button);
			}
		}
		return Optional.empty();
	}

	private void addSkillButtons() {
		skillButtons.clear();
		SkillTreeClientData.getOrCreateEditorTree(treeId).forEach(this::addSkillButton);
	}

	private void addToolButtons() {
		if (selectedSkills.size() == 2) {
			ResourceLocation first = (ResourceLocation) selectedSkills.toArray()[0];
			ResourceLocation second = (ResourceLocation) selectedSkills.toArray()[1];
			if (areSkillsConnected(first, second)) {
				addRenderableWidget(new SkillTreeButton(width - 110, 10, 100, 14, Component.literal("Disconnect"), b -> {
					skillButtons.get(first).skill.getConnectedSkills().remove(second);
					skillButtons.get(second).skill.getConnectedSkills().remove(first);
					selectedSkills.forEach(id -> SkillTreeClientData.saveEditorSkill(treeId, id, skillButtons.get(id).skill));
					rebuildWidgets();
				}));
			} else {
				addRenderableWidget(new SkillTreeButton(width - 110, 10, 100, 14, Component.literal("Connect"), b -> {
					skillButtons.get(first).skill.getConnectedSkills().add(second);
					selectedSkills.forEach(id -> SkillTreeClientData.saveEditorSkill(treeId, id, skillButtons.get(id).skill));
					rebuildWidgets();
				}));
			}
		}
	}

	private boolean areSkillsConnected(ResourceLocation first, ResourceLocation second) {
		return skillButtons.get(first).skill.getConnectedSkills().contains(second)
				|| skillButtons.get(second).skill.getConnectedSkills().contains(first);
	}

	protected void addSkillButton(ResourceLocation skillId, PassiveSkill skill) {
		float skillX = skill.getPositionX();
		float skillY = skill.getPositionY();
		double buttonX = skillX + width / 2F + (skillX + skill.getButtonSize() / 2) * (zoom - 1);
		double buttonY = skillY + height / 2F + (skillY + skill.getButtonSize() / 2) * (zoom - 1);
		SkillButton button = new SkillButton(() -> 0F, buttonX, buttonY, skill, this::buttonPressed);
		addRenderableWidget(button);
		button.highlighted = true;
		skillButtons.put(skillId, button);
		if (maxScrollX < Mth.abs(skillX)) maxScrollX = (int) Mth.abs(skillX);
		if (maxScrollY < Mth.abs(skillY)) maxScrollY = (int) Mth.abs(skillY);
	}

	public void addSkillConnections() {
		skillConnections.clear();
		gatewayConnections.clear();
		Map<ResourceLocation, PassiveSkill> skills = SkillTreeClientData.getOrCreateEditorTree(treeId);
		skills.forEach((skillId1, skill) -> {
			skill.getConnectedSkills().forEach(skillId2 -> {
				connectSkills(skillConnections, skillId1, skillId2);
			});
		});
		Map<ResourceLocation, List<PassiveSkill>> gateways = new HashMap<ResourceLocation, List<PassiveSkill>>();
		skills.values().stream().filter(PassiveSkill::isGateway).forEach(skill -> {
			ResourceLocation gatewayId = skill.getGatewayId().get();
			if (!gateways.containsKey(gatewayId)) {
				gateways.put(gatewayId, new ArrayList<>());
			}
			gateways.get(gatewayId).add(skill);
		});
		gateways.forEach((gatewayId, list) -> {
			for (int i = 1; i < list.size(); i++) {
				connectSkills(gatewayConnections, list.get(0).getId(), list.get(i).getId());
			}
		});
	}

	protected void connectSkills(List<SkillConnection> connections, ResourceLocation skillId1, ResourceLocation skillId2) {
		SkillButton button1 = skillButtons.get(skillId1);
		SkillButton button2 = skillButtons.get(skillId2);
		connections.add(new SkillConnection(button1, button2));
	}

	public void renderSkillTooltip(SkillButton button, GuiGraphics graphics, float mouseX, float mouseY) {
		List<MutableComponent> tooltip = button.getSkillTooltip();
		if (tooltip.isEmpty()) return;
		int tooltipWidth = 0;
		int tooltipHeight = tooltip.size() == 1 ? 8 : 10;
		for (MutableComponent component : tooltip) {
			int k = font.width(component);
			if (k > tooltipWidth) tooltipWidth = k;
			tooltipHeight += font.lineHeight;
		}
		tooltipWidth += 42;
		float tooltipX = mouseX + 12;
		float tooltipY = mouseY - 12;
		if (tooltipX + tooltipWidth > width) {
			tooltipX -= 28 + tooltipWidth;
		}
		if (tooltipY + tooltipHeight + 6 > height) {
			tooltipY = height - tooltipHeight - 6;
		}
		graphics.pose().pushPose();
		graphics.pose().translate(tooltipX, tooltipY, 0);
		graphics.fill(1, 4, tooltipWidth - 1, tooltipHeight + 4, 0xDD000000);
		MultiBufferSource.BufferSource buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
		graphics.pose().translate(0.0D, 0.0D, 400.0D);
		int textX = 5;
		int textY = 2;
		RenderSystem.enableBlend();
		graphics.blit(button.skill.getBorderTexture(), -4, -4, 0, 0, 21, 20, 110, 20);
		graphics.blit(button.skill.getBorderTexture(), tooltipWidth + 4 - 21, -4, -21, 0, 21, 20, 110, 20);
		int centerWidth = tooltipWidth + 8 - 42;
		int centerX = -4 + 21;
		while (centerWidth > 0) {
			int partWidth = Math.min(centerWidth, 68);
			graphics.blit(button.skill.getBorderTexture(), centerX, -4, 21, 0, partWidth, 20, 110, 20);
			centerX += partWidth;
			centerWidth -= partWidth;
		}
		MutableComponent title = tooltip.remove(0);
		graphics.drawCenteredString(font, title, tooltipWidth / 2, textY, 0xFFFFFF);
		textY += 17;
		for (MutableComponent component : tooltip) {
			graphics.drawString(font, component, textX, textY, 0xFFFFFF);
			textY += font.lineHeight;
		}
		buffer.endBatch();
		graphics.pose().popPose();
	}

	public void buttonPressed(Button button) {
		if (button instanceof SkillButton skillButton) skillButtonPressed(skillButton);
	}

	protected void skillButtonPressed(SkillButton button) {
		if (!hasShiftDown() && !selectedSkills.isEmpty()) {
			selectedSkills.clear();
		}
		selectedSkills.add(button.skill.getId());
		rebuildWidgets();
	}

	private void updateScreen(float partialTick) {
		scrollX += scrollSpeedX * partialTick;
		scrollX = Math.max(-maxScrollX * zoom, Math.min(maxScrollX * zoom, scrollX));
		scrollSpeedX *= 0.8;
		scrollY += scrollSpeedY * partialTick;
		scrollY = Math.max(-maxScrollY * zoom, Math.min(maxScrollY * zoom, scrollY));
		scrollSpeedY *= 0.8;
	}

	private void renderOverlay(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		ResourceLocation texture = new ResourceLocation("skilltree:textures/screen/skill_tree_overlay.png");
		graphics.blit(texture, 0, 0, 0, 0F, 0F, width, height, width, height);
	}

	@Override
	public void renderBackground(GuiGraphics graphics) {
		ResourceLocation texture = new ResourceLocation("skilltree:textures/screen/skill_tree_background.png");
		graphics.pose().pushPose();
		graphics.pose().translate(scrollX / 3F, scrollY / 3F, 0);
		int size = getBackgroundSize();
		graphics.blit(texture, (width - size) / 2, (height - size) / 2, 0, 0F, 0F, size, size, size, size);
		graphics.pose().popPose();
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dragAmountX, double dragAmountY) {
		if (mouseButton != 0 && mouseButton != 2) return false;
		if (maxScrollX > 0) scrollSpeedX += dragAmountX * 0.25;
		if (maxScrollY > 0) scrollSpeedY += dragAmountY * 0.25;
		return true;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		if (!getWidgetAt(mouseX, mouseY).filter(StatsList.class::isInstance).isPresent()) {
			if (amount > 0 && zoom < 2F) zoom += 0.05;
			if (amount < 0 && zoom > 0.25F) zoom -= 0.05;
			rebuildWidgets();
		}
		return super.mouseScrolled(mouseX, mouseY, amount);
	}

	protected void renderConnections(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		skillConnections.forEach(connection -> renderConnection(graphics, connection));
		gatewayConnections.forEach(connection -> renderGatewayConnection(graphics, connection, mouseX, mouseY));
	}

	private void renderGatewayConnection(GuiGraphics graphics, SkillConnection connection, int mouseX, int mouseY) {
		SkillButton button1 = connection.getFirstButton();
		SkillButton button2 = connection.getSecondButton();
		Optional<SkillButton> hoveredSkill = getSkillAt(mouseX, mouseY);
		boolean selected = selectedSkills.contains(button1.skill.getId()) || selectedSkills.contains(button2.skill.getId());
		if (selected) {
			if (selectedSkills.contains(button2.skill.getId())) {
				renderGatewayConnection(graphics, button2, button1);
			} else {
				renderGatewayConnection(graphics, button1, button2);
			}
			return;
		}
		boolean hovered = !hoveredSkill.isEmpty() && (hoveredSkill.get() == button1 || hoveredSkill.get() == button2);
		if (hovered) {
			if (hoveredSkill.get() == button2) renderGatewayConnection(graphics, button2, button1);
			else renderGatewayConnection(graphics, button1, button2);
		}
	}

	private void renderGatewayConnection(GuiGraphics graphics, SkillButton button1, SkillButton button2) {
		ResourceLocation texture = new ResourceLocation("skilltree:textures/screen/gateway_connection.png");
		graphics.pose().pushPose();
		double connectionX = button1.x + button1.getWidth() / 2F;
		double connectionY = button1.y + button1.getHeight() / 2F;
		graphics.pose().translate(connectionX + scrollX, connectionY + scrollY, 0);
		float rotation = ScreenHelper.getAngleBetweenButtons(button1, button2);
		graphics.pose().mulPose(Axis.ZP.rotation(rotation));
		int length = (int) (ScreenHelper.getDistanceBetweenButtons(button1, button2) / zoom);
		graphics.pose().scale(zoom, zoom, 1F);
		graphics.blit(texture, 0, -3, length, 6, 0, 6, length, 6, 30, 12);
		graphics.pose().popPose();
	}

	private void renderConnection(GuiGraphics graphics, SkillConnection connection) {
		ResourceLocation texture = new ResourceLocation("skilltree:textures/screen/skill_connection.png");
		graphics.pose().pushPose();
		SkillButton button1 = connection.getFirstButton();
		SkillButton button2 = connection.getSecondButton();
		double connectionX = button1.x + button1.getWidth() / 2F;
		double connectionY = button1.y + button1.getHeight() / 2F;
		graphics.pose().translate(connectionX + scrollX, connectionY + scrollY, 0);
		float rotation = ScreenHelper.getAngleBetweenButtons(button1, button2);
		graphics.pose().mulPose(Axis.ZP.rotation(rotation));
		int length = (int) ScreenHelper.getDistanceBetweenButtons(button1, button2);
		boolean highlighted = button1.highlighted && button2.highlighted;
		graphics.pose().scale(1F, zoom, 1F);
		graphics.blit(texture, 0, -3, length, 6, 0, highlighted ? 0 : 6, length, 6, 50, 12);
		graphics.pose().popPose();
	}

	protected int getBackgroundSize() {
		return 2048;
	}
}