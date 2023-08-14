package daripher.skilltree.client.widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.util.TooltipHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class SkillButton extends Button {
	private static final Style LESSER_TITLE_STYLE = Style.EMPTY.withColor(0xEAA169);
	private static final Style NOTABLE_TITLE_STYLE = Style.EMPTY.withColor(0x9B66D8);
	private static final Style KEYSTONE_TITLE_STYLE = Style.EMPTY.withColor(0xFFD75F);
	private static final Style GATEWAY_TITLE_STYLE = Style.EMPTY.withColor(0xA9A6B1);
	private static final Style DESCRIPTION_STYLE = Style.EMPTY.withColor(0x7B7BE5);
	private static final Style ID_STYLE = Style.EMPTY.withColor(0x545454);
	private static final Style LORE_STYLE = Style.EMPTY.withColor(0xB96526).withItalic(true);
	private final Supplier<Float> animationFunction;
	public final PassiveSkill skill;
	public boolean highlighted;
	public boolean animated;

	public SkillButton(Supplier<Float> animationFunction, int x, int y, PassiveSkill passiveSkill, OnPress pressFunction, OnTooltip tooltipFunction) {
		super(x, y, passiveSkill.getButtonSize(), passiveSkill.getButtonSize(), Component.empty(), pressFunction, tooltipFunction);
		this.skill = passiveSkill;
		this.animationFunction = animationFunction;
	}

	@Override
	public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		prepareTextureRendering(skill.getBackgroundTexture());
		blit(poseStack, x, y, width, height, 0, 0, width, height, width * 3, height);
		poseStack.pushPose();
		poseStack.translate(x + width / 2, y + height / 2, 0);
		poseStack.scale(0.5F, 0.5F, 1);
		RenderSystem.setShaderTexture(0, skill.getIconTexture());
		blit(poseStack, -width / 2, -height / 2, width, height, 0, 0, width, height, width, height);
		poseStack.popPose();
		RenderSystem.setShaderTexture(0, skill.getBackgroundTexture());
		blit(poseStack, x, y, width, height, width + (highlighted ? width : 0), 0, width, height, width * 3, height);
		if (animated) {
			RenderSystem.setShaderColor(1F, 1F, 1F, (Mth.sin(animationFunction.get() / 3F) + 1) / 2);
			blit(poseStack, x, y, width, height, width * 2, 0, width, height, width * 3, height);
			RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
		}
	}

	private void prepareTextureRendering(ResourceLocation textureLocation) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, textureLocation);
		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
	}

	public List<MutableComponent> getTooltip() {
		var tooltip = new ArrayList<MutableComponent>();
		addTitleTooltip(tooltip);
		addDescriptionTooltip(tooltip);
		addLoreTooltip(tooltip);
		var minecraft = Minecraft.getInstance();
		var useAdvancedTooltip = minecraft.options.advancedItemTooltips;
		if (useAdvancedTooltip) addAdvancedTooltip(tooltip);
		return tooltip;
	}

	protected void addAdvancedTooltip(ArrayList<MutableComponent> tooltip) {
		addIdTooltip(tooltip);
		var descriptionId = getSkillId() + ".description";
		var description = Component.translatable(descriptionId).getString();
		if (!description.equals(descriptionId)) {
			addAttributeModifiersTooltip(tooltip);
		}
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

	private void addLoreTooltip(ArrayList<MutableComponent> tooltip) {
		String loreId = getSkillId() + ".lore";
		MutableComponent loreComponent = Component.translatable(loreId);
		if (loreId.equals(loreComponent.getString())) return;
		tooltip.add(loreComponent.withStyle(LORE_STYLE));
	}

	protected void addTitleTooltip(ArrayList<MutableComponent> tooltip) {
		var titleId = getSkillId() + ".name";
		var title = Component.translatable(titleId);
		var attributeModifiers = skill.getAttributeModifiers();
		var hasAttributeModifiers = !attributeModifiers.isEmpty();
		if (titleId.equals(title.getString()) && hasAttributeModifiers) {
			title = generateTitle(attributeModifiers);
		}
		tooltip.add(title.withStyle(getTitleStyle()));
	}

	protected MutableComponent generateTitle(List<Pair<Attribute, AttributeModifier>> modifiers) {
		// formatter:off
		List<MutableComponent> attributes = modifiers.stream()
				.map(Pair::getLeft)
				.map(Attribute::getDescriptionId)
				.map(Component::translatable).toList();
		// formatter:on
		if (modifiers.size() > 1) {
			return Component.translatable("widget.skill_button.multiple_bonuses", attributes.get(0), attributes.get(1));
		} else {
			return Component.empty().append(attributes.get(0));
		}
	}

	private Style getTitleStyle() {
		return width == 33 ? GATEWAY_TITLE_STYLE : width == 24 ? KEYSTONE_TITLE_STYLE : width == 20 ? NOTABLE_TITLE_STYLE : LESSER_TITLE_STYLE;
	}

	protected void addIdTooltip(ArrayList<MutableComponent> tooltip) {
		MutableComponent idComponent = Component.literal(skill.getId().toString()).withStyle(ID_STYLE);
		tooltip.add(idComponent);
	}

	protected MutableComponent applyDescriptionStyle(MutableComponent component) {
		return component.withStyle(DESCRIPTION_STYLE);
	}

	protected void addAttributeModifiersTooltip(ArrayList<MutableComponent> tooltip) {
		skill.getAttributeModifiers().stream().map(TooltipHelper::getAttributeBonusTooltip).map(this::applyDescriptionStyle).forEach(tooltip::add);
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
