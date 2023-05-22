package daripher.skilltree.client.widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.client.screen.SkillTreeScreen;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.util.TooltipHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class SkillButton extends Button {
	private static final ResourceLocation TITLE_FONT = new ResourceLocation(SkillTreeMod.MOD_ID, "skill_title");
	private static final ResourceLocation DESCRIPTION_FONT = new ResourceLocation(SkillTreeMod.MOD_ID, "skill_description");
	private static final Style LESSER_TITLE_STYLE = Style.EMPTY.withColor(0xEAA169).withFont(TITLE_FONT);
	private static final Style NOTABLE_TITLE_STYLE = Style.EMPTY.withColor(0xD28AF2).withFont(TITLE_FONT);
	private static final Style KEYSTONE_TITLE_STYLE = Style.EMPTY.withColor(0xFFD75F).withFont(TITLE_FONT);
	private static final Style DESCRIPTION_STYLE = Style.EMPTY.withColor(0x7B7BE5).withFont(DESCRIPTION_FONT);
	private final SkillTreeScreen parentScreen;
	public final PassiveSkill skill;
	public boolean highlighted;
	public boolean animated;

	public SkillButton(SkillTreeScreen screen, int x, int y, PassiveSkill passiveSkill) {
		super(x, y, passiveSkill.getButtonSize(), passiveSkill.getButtonSize(), Component.empty(), screen::buttonPressed, screen::renderButtonTooltip);
		this.parentScreen = screen;
		this.skill = passiveSkill;
	}

	@Override
	public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		parentScreen.prepareTextureRendering(skill.getBackgroundTexture());
		blit(poseStack, x, y, width, height, 0, 0, width, height, width * 3, height);
		var iconSize = width / 34F;
		poseStack.pushPose();
		poseStack.translate(x + width / 2, y + height / 2, 0);
		poseStack.scale(iconSize, iconSize, 1);
		RenderSystem.setShaderTexture(0, skill.getIconTexture());
		blit(poseStack, -8, -8, 16, 16, 0, 0, 16, 16, 16, 16);
		poseStack.popPose();
		RenderSystem.setShaderTexture(0, skill.getBackgroundTexture());
		blit(poseStack, x, y, width, height, width + (highlighted ? width : 0), 0, width, height, width * 3, height);
		if (animated) {
			RenderSystem.setShaderColor(1F, 1F, 1F, parentScreen.getAnimationProgress());
			blit(poseStack, x, y, width, height, width * 2, 0, width, height, width * 3, height);
			RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
		}
		if (isHoveredOrFocused()) {
			renderToolTip(poseStack, mouseX, mouseY);
		}
	}

	public List<MutableComponent> getTooltip() {
		var tooltip = new ArrayList<MutableComponent>();
		addTitleTooltip(tooltip);
		addDescriptionTooltip(tooltip);
		var minecraft = parentScreen.getMinecraft();
		var useAdvancedTooltip = minecraft.options.advancedItemTooltips;
		if (useAdvancedTooltip) {
			addIdTooltip(tooltip);
			addAttributeModifiersTooltip(tooltip);
		}
		return tooltip;
	}

	protected void addDescriptionTooltip(ArrayList<MutableComponent> tooltip) {
		var descriptionId = getSkillId() + ".description";
		var description = Component.translatable(descriptionId).getString();
		if (description.equals(descriptionId)) {
			addAttributeModifiersTooltip(tooltip);
		} else {
			var descriptionStrings = Arrays.asList(description.split("/n"));
			descriptionStrings.stream().map(Component::translatable).map(this::applyDescriptionStyle).forEach(tooltip::add);
		}
	}

	protected void addTitleTooltip(ArrayList<MutableComponent> tooltip) {
		var skillTitle = Component.translatable(getSkillId() + ".name").withStyle(getTitleStyle());
		tooltip.add(skillTitle);
	}

	private Style getTitleStyle() {
		return width == 24 ? KEYSTONE_TITLE_STYLE : width == 20 ? NOTABLE_TITLE_STYLE : LESSER_TITLE_STYLE;
	}

	protected void addIdTooltip(ArrayList<MutableComponent> tooltip) {
		var skillIdComponent = Component.literal(skill.getId().toString()).withStyle(ChatFormatting.DARK_GRAY);
		tooltip.add(skillIdComponent);
	}

	protected MutableComponent applyDescriptionStyle(MutableComponent component) {
		return component.withStyle(DESCRIPTION_STYLE);
	}

	protected void addAttributeModifiersTooltip(ArrayList<MutableComponent> tooltip) {
		skill.getAttributeModifiers().stream().map(TooltipHelper::getAttributeBonusTooltip).forEach(tooltip::add);
	}

	public void animate() {
		animated = true;
	}

	private String getSkillId() {
		return "skill." + skill.getId().getNamespace() + "." + skill.getId().getPath();
	}

	public ItemStack getTooltipBorderStyleStack() {
		var styleItem = width == 24 ? Items.EXPERIENCE_BOTTLE : width == 20 ? Items.SHULKER_SHELL : Items.BUCKET;
		return new ItemStack(styleItem);
	}
}
