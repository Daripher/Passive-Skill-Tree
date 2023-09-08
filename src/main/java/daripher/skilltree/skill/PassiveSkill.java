package daripher.skilltree.skill;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.common.CuriosHelper.SlotAttributeWrapper;

public class PassiveSkill {
	private final ResourceLocation id;
	private final ResourceLocation treeId;
	private final ResourceLocation backgroundTexture;
	private final ResourceLocation iconTexture;
	private final ResourceLocation borderTexture;
	private Optional<ResourceLocation> connectedTreeId = Optional.empty();
	private Optional<ResourceLocation> gatewayId = Optional.empty();
	private final int buttonSize;
	private final boolean isStartingPoint;
	private List<Pair<Attribute, AttributeModifier>> attributeModifiers = new ArrayList<>();
	private final List<ResourceLocation> connectedSkills = new ArrayList<>();
	private float positionX, positionY;

	public PassiveSkill(ResourceLocation id, ResourceLocation treeId, int buttonSize, ResourceLocation backgroundTexture,
			ResourceLocation iconTexture, ResourceLocation borderTexture, boolean isStartingPoint) {
		this.id = id;
		this.treeId = treeId;
		this.backgroundTexture = backgroundTexture;
		this.iconTexture = iconTexture;
		this.borderTexture = borderTexture;
		this.buttonSize = buttonSize;
		this.isStartingPoint = isStartingPoint;
	}

	public ResourceLocation getId() {
		return id;
	}

	public ResourceLocation getTreeId() {
		return treeId;
	}

	public int getButtonSize() {
		return buttonSize;
	}

	public ResourceLocation getBackgroundTexture() {
		return backgroundTexture;
	}

	public ResourceLocation getIconTexture() {
		return iconTexture;
	}

	public ResourceLocation getBorderTexture() {
		return borderTexture;
	}

	public Optional<ResourceLocation> getConnectedTreeId() {
		return connectedTreeId;
	}

	public void setConnectedTree(@Nullable ResourceLocation treeId) {
		this.connectedTreeId = Optional.ofNullable(treeId);
	}

	public void setConnectedTree(Optional<ResourceLocation> treeId) {
		this.connectedTreeId = treeId;
	}

	public boolean isStartingPoint() {
		return isStartingPoint;
	}

	public List<Pair<Attribute, AttributeModifier>> getAttributeModifiers() {
		return attributeModifiers;
	}

	public void addAttributeBonus(Attribute attribute, AttributeModifier modifier) {
		attributeModifiers.add(Pair.of(attribute, modifier));
	}

	public void addAttributeBonus(Pair<Attribute, AttributeModifier> bonus) {
		attributeModifiers.add(bonus);
	}

	public void connect(PassiveSkill otherSkill) {
		connectedSkills.add(otherSkill.getId());
	}

	public void setPosition(float x, float y) {
		positionX = x;
		positionY = y;
	}

	public float getPositionX() {
		return positionX;
	}

	public float getPositionY() {
		return positionY;
	}

	public List<ResourceLocation> getConnectedSkills() {
		return connectedSkills;
	}

	public void setGatewayId(@Nullable ResourceLocation gatewayId) {
		this.gatewayId = Optional.ofNullable(gatewayId);
	}

	public void setGatewayId(Optional<ResourceLocation> gatewayId) {
		this.gatewayId = gatewayId;
	}

	public Optional<ResourceLocation> getGatewayId() {
		return gatewayId;
	}

	public boolean isGateway() {
		return gatewayId.isPresent();
	}

	public void learn(ServerPlayer player, boolean restoring) {
		getAttributeModifiers().forEach(pair -> addAttributeModifier(player, pair.getLeft(), pair.getRight(), restoring));
	}

	@SuppressWarnings("deprecation")
	public void addAttributeModifier(ServerPlayer player, Attribute attribute, AttributeModifier modifier, boolean restoring) {
		if (attribute instanceof SlotAttributeWrapper wrapper) {
			if (!restoring) CuriosApi.getSlotHelper().growSlotType(wrapper.identifier, (int) modifier.getAmount(), player);
			return;
		}
		AttributeInstance instance = player.getAttribute(attribute);
		if (!instance.hasModifier(modifier)) instance.addTransientModifier(modifier);
	}

	public void remove(ServerPlayer player) {
		getAttributeModifiers().forEach(pair -> removeAttributeModifier(player, pair.getLeft(), pair.getRight()));
	}

	@SuppressWarnings("deprecation")
	public void removeAttributeModifier(Player player, Attribute attribute, AttributeModifier modifier) {
		if (attribute instanceof SlotAttributeWrapper wrapper) {
			CuriosApi.getSlotHelper().shrinkSlotType(wrapper.identifier, (int) modifier.getAmount(), player);
			return;
		}
		AttributeInstance instance = player.getAttribute(attribute);
		if (instance.hasModifier(modifier)) instance.removeModifier(modifier);
	}

	public boolean sameBonuses(PassiveSkill other) {
		if (other == this) return true;
		if (attributeModifiers.size() != other.attributeModifiers.size()) return false;
		for (int i = 0; i < attributeModifiers.size(); i++) {
			if (attributeModifiers.get(i).getLeft() != other.attributeModifiers.get(i).getLeft()) return false;
			AttributeModifier modifier = attributeModifiers.get(i).getRight();
			AttributeModifier otherModifier = other.attributeModifiers.get(i).getRight();
			if (modifier.getAmount() != otherModifier.getAmount()) return false;
			if (modifier.getOperation() != otherModifier.getOperation()) return false;
		}
		return true;
	}
}
