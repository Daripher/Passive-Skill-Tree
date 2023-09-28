package daripher.skilltree.client.screen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Predicates;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Vector3f;

import daripher.skilltree.client.skill.SkillTreeClientData;
import daripher.skilltree.client.widget.EnumCycleButton;
import daripher.skilltree.client.widget.NumberEditBox;
import daripher.skilltree.client.widget.PSTButton;
import daripher.skilltree.client.widget.PSTEditBox;
import daripher.skilltree.client.widget.PSTLabel;
import daripher.skilltree.client.widget.RemoveButton;
import daripher.skilltree.client.widget.SkillButton;
import daripher.skilltree.client.widget.SkillConnection;
import daripher.skilltree.client.widget.StatsList;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.PassiveSkillTree;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.common.CuriosHelper;
import top.theillusivec4.curios.common.CuriosHelper.SlotAttributeWrapper;

public class SkillTreeEditorScreen extends Screen {
	private final Map<ResourceLocation, SkillButton> skillButtons = new HashMap<>();
	private final List<SkillConnection> skillConnections = new ArrayList<>();
	private final List<SkillConnection> gatewayConnections = new ArrayList<>();
	private final Set<ResourceLocation> selectedSkills = new HashSet<>();
	private final PassiveSkillTree skillTree;
	private int prevMouseX;
	private int prevMouseY;
	private float zoom = 1F;
	protected double scrollSpeedX;
	protected double scrollSpeedY;
	protected double scrollX;
	protected double scrollY;
	protected int maxScrollX;
	protected int maxScrollY;
	protected int toolsY;
	protected int toolsX;

	public SkillTreeEditorScreen(ResourceLocation skillTreeId) {
		super(Component.empty());
		this.skillTree = SkillTreeClientData.getOrCreateEditorTree(skillTreeId);
		this.minecraft = Minecraft.getInstance();
	}

	@Override
	public void init() {
		clearWidgets();
		addSkillButtons();
		maxScrollX -= width / 2 - 80;
		maxScrollY -= height / 2 - 80;
		if (maxScrollX < 0)
			maxScrollX = 0;
		if (maxScrollY < 0)
			maxScrollY = 0;
		addSkillConnections();
		addGatewayConnections();
		addToolButtons();
	}

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		updateScreen(partialTick);
		renderBackground(poseStack);
		renderConnections(poseStack, mouseX, mouseY, partialTick);
		renderSkills(poseStack, mouseX, mouseY, partialTick);
		renderOverlay(poseStack, mouseX, mouseY, partialTick);
		renderWidgets(poseStack, mouseX, mouseY, partialTick);
		renderSkillTooltip(poseStack, mouseX, mouseY, partialTick);
		prevMouseX = mouseX;
		prevMouseY = mouseY;
	}

	private void renderWidgets(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		if (selectedSkills.size() > 0) {
			fill(poseStack, toolsX - 5, 0, width, toolsY, 0xDD000000);
		}
		for (Widget widget : renderables) {
			if (widget instanceof SkillButton)
				continue;
			widget.render(poseStack, mouseX, mouseY, partialTick);
		}
	}

	private void renderSkillTooltip(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		if (getWidgetAt(mouseX, mouseY).isPresent())
			return;
		getSkillAt(mouseX, mouseY).ifPresent(button -> {
			renderSkillTooltip(button, poseStack, mouseX + (prevMouseX - mouseX) * partialTick,
					mouseY + (prevMouseY - mouseY) * partialTick);
		});
	}

	private void renderSkills(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		poseStack.pushPose();
		poseStack.translate(scrollX, scrollY, 0);
		for (SkillButton widget : skillButtons.values()) {
			poseStack.pushPose();
			poseStack.translate(widget.x + widget.getWidth() / 2, widget.y + widget.getHeight() / 2, 0F);
			poseStack.scale(zoom, zoom, 1F);
			poseStack.translate(-widget.x - widget.getWidth() / 2, -widget.y - widget.getHeight() / 2, 0F);
			widget.render(poseStack, mouseX, mouseY, partialTick);
			if (selectedSkills.contains(widget.skill.getId())) {
				poseStack.pushPose();
				poseStack.translate(widget.x, widget.y, 0);
				ScreenHelper.drawRectangle(poseStack, -1, -1, widget.getWidth() + 2, widget.getHeight() + 2,
						0xAA32FF00);
				poseStack.popPose();
			}
			poseStack.popPose();
		}
		poseStack.popPose();
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		AtomicBoolean pressedEditBox = new AtomicBoolean();
		children().stream()
				.filter(EditBox.class::isInstance)
				.forEach(b -> {
					if (b.mouseClicked(mouseX, mouseY, button))
						pressedEditBox.set(true);
				});
		if (pressedEditBox.get())
			return true;
		Optional<? extends GuiEventListener> widget = getWidgetAt(mouseX, mouseY);
		if (widget.isPresent())
			return widget.get().mouseClicked(mouseX, mouseY, button);
		Optional<SkillButton> skill = getSkillAt(mouseX, mouseY);
		if (skill.isPresent())
			return skill.get().mouseClicked(skill.get().x + 1, skill.get().y + 1, button);
		return false;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		children().stream()
				.filter(EditBox.class::isInstance)
				.forEach(b -> b.keyPressed(keyCode, scanCode, modifiers));
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		children().stream()
				.filter(EditBox.class::isInstance)
				.forEach(b -> b.keyReleased(keyCode, scanCode, modifiers));
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char character, int keyCode) {
		children().stream()
				.filter(EditBox.class::isInstance)
				.map(EditBox.class::cast)
				.forEach(b -> b.charTyped(character, keyCode));
		return super.charTyped(character, keyCode);
	}

	public Optional<? extends GuiEventListener> getWidgetAt(double mouseX, double mouseY) {
		return children()
				.stream()
				.filter(Predicates.not(SkillButton.class::isInstance))
				.filter(e -> e.isMouseOver(mouseX, mouseY))
				.findFirst();
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
		getTreeSkills().forEach(this::addSkillButton);
	}

	private void addToolButtons() {
		toolsX = width - 379;
		toolsY = 0;
		if (selectedSkills.size() > 0) {
			toolsY += 5;
			addAttributeToolsButtons();
			addTextureToolsButtons();
			addButtonToolsButtons();
		}
		if (selectedSkills.size() == 2) {
			addConnectionToolsButton();
		}
	}

	private void addButtonToolsButtons() {
		ResourceLocation skillId = (ResourceLocation) selectedSkills.toArray()[0];
		PassiveSkill skill = SkillTreeClientData.getEditorSkill(skillId);
		if (getSelectedSkills().allMatch(otherSkill -> sameSize(skill, otherSkill))) {
			addRenderableOnly(new PSTLabel(toolsX, toolsY, Component.literal("• Size").withStyle(ChatFormatting.GOLD)));
			toolsY += 19;
			NumberEditBox sizeEditor = new NumberEditBox(font, toolsX, toolsY, 60, 14, skill.getButtonSize());
			sizeEditor.setNumericFilter(d -> d >= 2);
			sizeEditor.setResponder(s -> {
				getSelectedSkills().forEach(skill_ -> {
					skill_.setButtonSize((int) sizeEditor.getNumericValue());
				});
				getSelectedSkillButtons().forEach(button -> {
					button.setButtonSize((int) sizeEditor.getNumericValue());
				});
				saveSelectedSkills();
			});
			addRenderableWidget(sizeEditor);
			toolsY += 19;
		}
		if (selectedSkills.size() == 1) {
			toolsY -= 38;
			addRenderableOnly(
					new PSTLabel(toolsX + 85, toolsY, Component.literal("• Position").withStyle(ChatFormatting.GOLD)));
			toolsY += 19;
			NumberEditBox xPosEditor = new NumberEditBox(font, toolsX + 85, toolsY, 60, 14, skill.getPositionX());
			xPosEditor.setResponder(s -> {
				skill.setPosition((float) xPosEditor.getNumericValue(), skill.getPositionY());
				getSelectedSkillButtons().forEach(button -> {
					button.setPosition(getSkillButtonX(skill), getSkillButtonY(skill));
				});
				saveSelectedSkills();
			});
			addRenderableWidget(xPosEditor);
			NumberEditBox yPosEditor = new NumberEditBox(font, toolsX + 150, toolsY, 60, 14, skill.getPositionY());
			yPosEditor.setResponder(s -> {
				skill.setPosition(skill.getPositionX(), (float) yPosEditor.getNumericValue());
				getSelectedSkillButtons().forEach(button -> {
					button.setPosition(getSkillButtonX(skill), getSkillButtonY(skill));
				});
				saveSelectedSkills();
			});
			addRenderableWidget(yPosEditor);
			toolsY += 19;
		}
	}

	private void addAttributeToolsButtons() {
		ResourceLocation skillId = (ResourceLocation) selectedSkills.toArray()[0];
		PassiveSkill skill = SkillTreeClientData.getEditorSkill(skillId);
		if (getSelectedSkills().allMatch(otherSkill -> sameAttributeModifiers(skill, otherSkill))) {
			addRenderableOnly(new PSTLabel(toolsX, toolsY,
					Component.literal("• Attribute Modifiers").withStyle(ChatFormatting.GOLD)));
			toolsY += 19;
			for (int row = 0; row < skill.getAttributeModifiers().size(); row++) {
				addAttributeToolsButtonsRow(skill, row);
				toolsY += 19;
			}
			addAddModifierButton();
			toolsY += 19;
		}
	}

	private void addTextureToolsButtons() {
		ResourceLocation skillId = (ResourceLocation) selectedSkills.toArray()[0];
		PassiveSkill skill = SkillTreeClientData.getEditorSkill(skillId);
		if (getSelectedSkills().allMatch(otherSkill -> sameTextures(skill, otherSkill))) {
			addRenderableOnly(
					new PSTLabel(toolsX, toolsY, Component.literal("• Textures").withStyle(ChatFormatting.GOLD)));
			toolsY += 19;
			PSTEditBox backgroundEditor = new PSTEditBox(font, toolsX, toolsY, 355, 14,
					skill.getBackgroundTexture().toString());
			backgroundEditor.setResponder(value -> {
				getSelectedSkills().forEach(s -> {
					s.setBackgroundTexture(new ResourceLocation(value));
				});
				saveSelectedSkills();
			});
			addRenderableWidget(backgroundEditor);
			toolsY += 19;
			PSTEditBox iconEditor = new PSTEditBox(font, toolsX, toolsY, 355, 14, skill.getIconTexture().toString());
			iconEditor.setResponder(value -> {
				getSelectedSkills().forEach(s -> {
					s.setIconTexture(new ResourceLocation(value));
				});
				saveSelectedSkills();
			});
			addRenderableWidget(iconEditor);
			toolsY += 19;
			PSTEditBox borderEditor = new PSTEditBox(font, toolsX, toolsY, 355, 14,
					skill.getBorderTexture().toString());
			borderEditor.setResponder(value -> {
				getSelectedSkills().forEach(s -> {
					s.setBorderTexture(new ResourceLocation(value));
				});
				saveSelectedSkills();
			});
			addRenderableWidget(borderEditor);
			toolsY += 19;
		}
	}

	private Stream<PassiveSkill> getSelectedSkills() {
		return selectedSkills.stream().map(SkillTreeClientData::getEditorSkill);
	}

	private Stream<SkillButton> getSelectedSkillButtons() {
		return selectedSkills.stream().map(skillButtons::get);
	}

	private void addAddModifierButton() {
		Button addModifierButton = new PSTButton(toolsX, toolsY, 100, 14,
				Component.translatable("Add Modifier"),
				b -> {
					getSelectedSkills().forEach(skill -> {
						skill.getAttributeModifiers().add(Pair.of(Attributes.ARMOR,
								new AttributeModifier(UUID.randomUUID(), "SkillTree",
										1, Operation.ADDITION)));
						SkillTreeClientData.saveEditorSkill(skill);
						rebuildWidgets();
					});
				});
		addRenderableWidget(addModifierButton);
	}

	private void addAttributeToolsButtonsRow(PassiveSkill firstSkill, int row) {
		Attribute attribute = firstSkill.getAttributeModifiers().get(row).getLeft();
		String attributeId;
		if (attribute instanceof SlotAttributeWrapper wrapper) {
			attributeId = "curios:" + wrapper.identifier;
		} else {
			attributeId = ForgeRegistries.ATTRIBUTES.getKey(attribute).toString();
		}
		PSTEditBox attributeEditor = new PSTEditBox(font, toolsX, toolsY, 220, 14, attributeId);
		attributeEditor.setSoftFilter(s -> {
			if (s.startsWith("curios:"))
				return true;
			if (!ResourceLocation.isValidResourceLocation(s))
				return false;
			return ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(s)) != null;
		});
		addRenderableWidget(attributeEditor);
		AttributeModifier modifier = firstSkill.getAttributeModifiers().get(row).getRight();
		NumberEditBox valueEditor = new NumberEditBox(font, toolsX + 225, toolsY, 30, 14, modifier.getAmount());
		addRenderableWidget(valueEditor);
		EnumCycleButton<Operation> operationButton = new EnumCycleButton<>(toolsX + 260, toolsY, 95, 14,
				modifier.getOperation());
		addRenderableWidget(operationButton);
		Button removeButton = new RemoveButton(toolsX + 360, toolsY, 14, 14, b -> {
			getSelectedSkills().forEach(skill -> {
				skill.getAttributeModifiers().remove(row);
				SkillTreeClientData.saveEditorSkill(skill);
				rebuildWidgets();
			});
		});
		addRenderableWidget(removeButton);
		final int modifierIndex = row;
		Runnable onChange = () -> {
			if (!attributeEditor.isValueValid())
				return;
			getSelectedSkills().forEach(skill -> {
				String newAttributeId = attributeEditor.getValue();
				Attribute newAttribute;
				if (newAttributeId.startsWith("curios:")) {
					newAttribute = CuriosHelper.getOrCreateSlotAttribute(newAttributeId.replace("curios:", ""));
				} else {
					newAttribute = ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(newAttributeId));
				}
				AttributeModifier oldModifier = skill.getAttributeModifiers().get(modifierIndex).getRight();
				double amount = valueEditor.getNumericValue();
				Operation operation = operationButton.getValue();
				AttributeModifier newModifier = new AttributeModifier(oldModifier.getId(), oldModifier.getName(),
						amount, operation);
				skill.getAttributeModifiers().set(modifierIndex, Pair.of(newAttribute, newModifier));
				SkillTreeClientData.saveEditorSkill(skill);
			});
		};
		attributeEditor.setResponder(s -> onChange.run());
		valueEditor.setResponder(s -> onChange.run());
		operationButton.setPressFunc(b -> onChange.run());
	}

	private void addConnectionToolsButton() {
		addRenderableOnly(
				new PSTLabel(toolsX, toolsY, Component.literal("• Connections").withStyle(ChatFormatting.GOLD)));
		toolsY += 19;
		ResourceLocation first = (ResourceLocation) selectedSkills.toArray()[0];
		ResourceLocation second = (ResourceLocation) selectedSkills.toArray()[1];
		PassiveSkill firstSkill = SkillTreeClientData.getEditorSkill(first);
		PassiveSkill secondSkill = SkillTreeClientData.getEditorSkill(second);
		if (areSkillsConnected(firstSkill, secondSkill)) {
			addRenderableWidget(new PSTButton(toolsX, toolsY, 100, 14, Component.literal("Disconnect"), b -> {
				firstSkill.getConnectedSkills().remove(second);
				firstSkill.getConnectedAsGateways().remove(second);
				secondSkill.getConnectedSkills().remove(first);
				secondSkill.getConnectedAsGateways().remove(first);
				saveSelectedSkills();
				rebuildWidgets();
			}));
		} else {
			addRenderableWidget(new PSTButton(toolsX, toolsY, 110, 14, Component.literal("Connect"), b -> {
				skillButtons.get(first).skill.getConnectedSkills().add(second);
				saveSelectedSkills();
				rebuildWidgets();
			}));
			addRenderableWidget(
					new PSTButton(toolsX + 115, toolsY, 110, 14, Component.literal("Connect as Gateway"), b -> {
						skillButtons.get(first).skill.getConnectedAsGateways().add(second);
						saveSelectedSkills();
						rebuildWidgets();
					}));
		}
		toolsY += 19;
	}

	private boolean areSkillsConnected(PassiveSkill first, PassiveSkill second) {
		return first.getConnectedSkills().contains(second.getId())
				|| second.getConnectedSkills().contains(first.getId())
				|| first.getConnectedAsGateways().contains(second.getId())
				|| second.getConnectedAsGateways().contains(first.getId());
	}

	private void saveSelectedSkills() {
		selectedSkills.stream().map(skillButtons::get).map(button -> button.skill)
				.forEach(id -> SkillTreeClientData.saveEditorSkill(id));
	}

	protected void addSkillButton(PassiveSkill skill) {
		float skillX = skill.getPositionX();
		float skillY = skill.getPositionY();
		SkillButton button = new SkillButton(() -> 0F, getSkillButtonX(skill), getSkillButtonY(skill), skill,
				this::buttonPressed);
		addRenderableWidget(button);
		button.highlighted = true;
		skillButtons.put(skill.getId(), button);
		if (maxScrollX < Mth.abs(skillX))
			maxScrollX = (int) Mth.abs(skillX);
		if (maxScrollY < Mth.abs(skillY))
			maxScrollY = (int) Mth.abs(skillY);
	}

	private float getSkillButtonX(PassiveSkill skill) {
		float skillX = skill.getPositionX();
		return skillX - skill.getButtonSize() / 2F + width / 2F + skillX * (zoom - 1);
	}

	private float getSkillButtonY(PassiveSkill skill) {
		float skillY = skill.getPositionY();
		return skillY - skill.getButtonSize() / 2F + height / 2F + skillY * (zoom - 1);
	}

	public void addSkillConnections() {
		skillConnections.clear();
		getTreeSkills().forEach(this::addSkillConnections);
	}

	public void addGatewayConnections() {
		gatewayConnections.clear();
		getTreeSkills().forEach(this::addGatewayConnections);
	}

	private Stream<PassiveSkill> getTreeSkills() {
		return skillTree.getSkillIds().stream().map(SkillTreeClientData::getEditorSkill);
	}

	private void addSkillConnections(PassiveSkill skill) {
		skill.getConnectedSkills().forEach(connectedSkillId -> {
			connectSkills(skillConnections, skill.getId(), connectedSkillId);
		});
	}

	private void addGatewayConnections(PassiveSkill skill) {
		skill.getConnectedAsGateways().forEach(connectedSkillId -> {
			connectSkills(gatewayConnections, skill.getId(), connectedSkillId);
		});
	}

	protected void connectSkills(List<SkillConnection> connections, ResourceLocation skillId1,
			ResourceLocation skillId2) {
		SkillButton button1 = skillButtons.get(skillId1);
		SkillButton button2 = skillButtons.get(skillId2);
		connections.add(new SkillConnection(button1, button2));
	}

	public void renderSkillTooltip(SkillButton button, PoseStack poseStack, float mouseX, float mouseY) {
		List<MutableComponent> tooltip = button.getTooltip();
		if (tooltip.isEmpty())
			return;
		int tooltipWidth = 0;
		int tooltipHeight = tooltip.size() == 1 ? 8 : 10;
		for (MutableComponent component : tooltip) {
			int k = font.width(component);
			if (k > tooltipWidth)
				tooltipWidth = k;
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
		poseStack.pushPose();
		poseStack.translate(tooltipX, tooltipY, 0);
		float zOffset = itemRenderer.blitOffset;
		itemRenderer.blitOffset = 400.0F;
		fill(poseStack, 1, 4, tooltipWidth - 1, tooltipHeight + 4, 0xDD000000);
		RenderSystem.enableTexture();
		MultiBufferSource.BufferSource buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
		poseStack.translate(0.0D, 0.0D, 400.0D);
		int textX = 5;
		int textY = 2;
		ScreenHelper.prepareTextureRendering(button.skill.getBorderTexture());
		blit(poseStack, -4, -4, 0, 0, 21, 20, 110, 20);
		blit(poseStack, tooltipWidth + 4 - 21, -4, -21, 0, 21, 20, 110, 20);
		int centerWidth = tooltipWidth + 8 - 42;
		int centerX = -4 + 21;
		while (centerWidth > 0) {
			int partWidth = Math.min(centerWidth, 68);
			blit(poseStack, centerX, -4, 21, 0, partWidth, 20, 110, 20);
			centerX += partWidth;
			centerWidth -= partWidth;
		}
		MutableComponent title = tooltip.remove(0);
		drawCenteredString(poseStack, font, title, tooltipWidth / 2, textY, 0xFFFFFF);
		textY += 17;
		for (MutableComponent component : tooltip) {
			font.draw(poseStack, component, textX, textY, 0xFFFFFF);
			textY += font.lineHeight;
		}
		buffer.endBatch();
		poseStack.popPose();
		itemRenderer.blitOffset = zOffset;
	}

	public void buttonPressed(Button button) {
		if (button instanceof SkillButton skillButton)
			skillButtonPressed(skillButton);
	}

	protected void skillButtonPressed(SkillButton button) {
		if (!hasShiftDown() && !selectedSkills.isEmpty()) {
			selectedSkills.clear();
		}
		ResourceLocation skillId = button.skill.getId();
		if (selectedSkills.contains(skillId))
			selectedSkills.remove(skillId);
		else
			selectedSkills.add(skillId);
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

	private void renderOverlay(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		ScreenHelper.prepareTextureRendering(new ResourceLocation("skilltree:textures/screen/skill_tree_overlay.png"));
		blit(poseStack, 0, 0, 0, 0F, 0F, width, height, width, height);
	}

	@Override
	public void renderBackground(PoseStack poseStack) {
		ScreenHelper
				.prepareTextureRendering(new ResourceLocation("skilltree:textures/screen/skill_tree_background.png"));
		poseStack.pushPose();
		poseStack.translate(scrollX / 3F, scrollY / 3F, 0);
		int size = getBackgroundSize();
		blit(poseStack, (width - size) / 2, (height - size) / 2, 0, 0F, 0F, size, size, size, size);
		poseStack.popPose();
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dragAmountX, double dragAmountY) {
		if (mouseButton != 0 && mouseButton != 2)
			return false;
		if (maxScrollX > 0)
			scrollSpeedX += dragAmountX * 0.25;
		if (maxScrollY > 0)
			scrollSpeedY += dragAmountY * 0.25;
		return true;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		if (!getWidgetAt(mouseX, mouseY).filter(StatsList.class::isInstance).isPresent()) {
			if (amount > 0 && zoom < 2F)
				zoom += 0.05;
			if (amount < 0 && zoom > 0.25F)
				zoom -= 0.05;
			rebuildWidgets();
		}
		return super.mouseScrolled(mouseX, mouseY, amount);
	}

	protected void renderConnections(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		ScreenHelper.prepareTextureRendering(new ResourceLocation("skilltree:textures/screen/skill_connection.png"));
		skillConnections.forEach(connection -> renderConnection(poseStack, connection));
		ScreenHelper.prepareTextureRendering(new ResourceLocation("skilltree:textures/screen/gateway_connection.png"));
		gatewayConnections.forEach(connection -> renderGatewayConnection(poseStack, connection, mouseX, mouseY));
	}

	private void renderGatewayConnection(PoseStack poseStack, SkillConnection connection, int mouseX, int mouseY) {
		SkillButton button1 = connection.getFirstButton();
		SkillButton button2 = connection.getSecondButton();
		Optional<SkillButton> hoveredSkill = getSkillAt(mouseX, mouseY);
		boolean selected = selectedSkills.contains(button1.skill.getId())
				|| selectedSkills.contains(button2.skill.getId());
		if (selected) {
			if (selectedSkills.contains(button2.skill.getId())) {
				renderGatewayConnection(poseStack, button2, button1);
			} else {
				renderGatewayConnection(poseStack, button1, button2);
			}
			return;
		}
		boolean hovered = !hoveredSkill.isEmpty() && (hoveredSkill.get() == button1 || hoveredSkill.get() == button2);
		if (hovered) {
			if (hoveredSkill.get() == button2)
				renderGatewayConnection(poseStack, button2, button1);
			else
				renderGatewayConnection(poseStack, button1, button2);
		}
	}

	private void renderGatewayConnection(PoseStack poseStack, SkillButton button1, SkillButton button2) {
		poseStack.pushPose();
		double connectionX = button1.x + button1.getWidth() / 2F;
		double connectionY = button1.y + button1.getHeight() / 2F;
		poseStack.translate(connectionX + scrollX, connectionY + scrollY, 0);
		float rotation = ScreenHelper.getAngleBetweenButtons(button1, button2);
		poseStack.mulPose(Vector3f.ZP.rotation(rotation));
		int length = (int) (ScreenHelper.getDistanceBetweenButtons(button1, button2) / zoom);
		poseStack.scale(zoom, zoom, 1F);
		blit(poseStack, 0, -3, length, 6, 0, 0, length, 6, 30, 12);
		poseStack.popPose();
	}

	private void renderConnection(PoseStack poseStack, SkillConnection connection) {
		poseStack.pushPose();
		SkillButton button1 = connection.getFirstButton();
		SkillButton button2 = connection.getSecondButton();
		double connectionX = button1.x + button1.getWidth() / 2F;
		double connectionY = button1.y + button1.getHeight() / 2F;
		poseStack.translate(connectionX + scrollX, connectionY + scrollY, 0);
		float rotation = ScreenHelper.getAngleBetweenButtons(button1, button2);
		poseStack.mulPose(Vector3f.ZP.rotation(rotation));
		int length = (int) ScreenHelper.getDistanceBetweenButtons(button1, button2);
		poseStack.scale(1F, zoom, 1F);
		blit(poseStack, 0, -3, length, 6, 0, 0, length, 6, 50, 12);
		poseStack.popPose();
	}

	protected int getBackgroundSize() {
		return 2048;
	}

	protected boolean sameAttributeModifiers(PassiveSkill skill, PassiveSkill otherSkill) {
		if (skill == otherSkill)
			return true;
		List<Pair<Attribute, AttributeModifier>> modifiers = skill.getAttributeModifiers();
		List<Pair<Attribute, AttributeModifier>> otherModifiers = otherSkill.getAttributeModifiers();
		if (modifiers.size() != otherModifiers.size())
			return false;
		for (int i = 0; i < modifiers.size(); i++) {
			if (modifiers.get(i).getLeft() != otherModifiers.get(i).getLeft())
				return false;
			AttributeModifier modifier = modifiers.get(i).getRight();
			AttributeModifier otherModifier = otherModifiers.get(i).getRight();
			if (modifier.getAmount() != otherModifier.getAmount())
				return false;
			if (modifier.getOperation() != otherModifier.getOperation())
				return false;
		}
		return true;
	}

	protected boolean sameTextures(PassiveSkill skill, PassiveSkill otherSkill) {
		if (skill == otherSkill)
			return true;
		if (!skill.getBackgroundTexture().equals(otherSkill.getBackgroundTexture()))
			return false;
		if (!skill.getIconTexture().equals(otherSkill.getIconTexture()))
			return false;
		if (!skill.getBorderTexture().equals(otherSkill.getBorderTexture()))
			return false;
		return true;
	}

	protected boolean sameSize(PassiveSkill skill, PassiveSkill otherSkill) {
		if (skill == otherSkill)
			return true;
		return skill.getButtonSize() == otherSkill.getButtonSize();
	}
}
