package daripher.skilltree.skill;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraftforge.registries.ForgeRegistries;

public class PassiveSkill {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final ResourceLocation id;
	private final ResourceLocation treeId;
	private final ResourceLocation backgroundTexture;
	private final ResourceLocation iconTexture;
	private @Nullable ResourceLocation connectedTreeId;
	private @Nullable ResourceLocation gatewayId;
	private final int buttonSize;
	private final boolean isStartingPoint;
	private List<Pair<Attribute, AttributeModifier>> attributeModifiers = new ArrayList<>();
	private final List<ResourceLocation> connectedSkills = new ArrayList<>();
	private int positionX, positionY;

	public PassiveSkill(ResourceLocation id, ResourceLocation treeId, int buttonSize, ResourceLocation backgroundTexture, ResourceLocation iconTexture, boolean isStartingPoint) {
		this.id = id;
		this.treeId = treeId;
		this.backgroundTexture = backgroundTexture;
		this.iconTexture = iconTexture;
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

	public @Nullable ResourceLocation getConnectedTreeId() {
		return connectedTreeId;
	}

	public void setConnectedTree(ResourceLocation treeId) {
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

	public void connect(PassiveSkill otherSkill) {
		connectedSkills.add(otherSkill.getId());
	}

	public void setPosition(int x, int y) {
		positionX = x;
		positionY = y;
	}

	public int getPositionX() {
		return positionX;
	}

	public int getPositionY() {
		return positionY;
	}

	public List<ResourceLocation> getConnectedSkills() {
		return connectedSkills;
	}

	public void setGatewayId(ResourceLocation gatewayId) {
		this.gatewayId = gatewayId;
	}

	public ResourceLocation getGatewayId() {
		return gatewayId;
	}

	public boolean isGateway() {
		return gatewayId != null;
	}

	public JsonObject writeToJson() {
		var jsonObject = new JsonObject();
		jsonObject.addProperty("button_size", getButtonSize());
		jsonObject.addProperty("tree", getTreeId().toString());
		jsonObject.addProperty("background_texture", getBackgroundTexture().toString());
		jsonObject.addProperty("icon_texture", getIconTexture().toString());

		if (connectedTreeId != null) {
			jsonObject.addProperty("connected_tree", getConnectedTreeId().toString());
		}

		if (isStartingPoint()) {
			jsonObject.addProperty("starting_point", true);
		}

		if (!getAttributeModifiers().isEmpty()) {
			var modifiersJsonArray = new JsonArray();
			getAttributeModifiers().forEach(pair -> {
				var attribute = pair.getLeft();
				var modifier = pair.getRight();
				var modifierJsonObject = new JsonObject();
				var attributeId = ForgeRegistries.ATTRIBUTES.getKey(attribute).toString();
				modifierJsonObject.addProperty("attribute", attributeId);
				modifierJsonObject.addProperty("amount", modifier.getAmount());
				modifierJsonObject.addProperty("operation", modifier.getOperation().name().toLowerCase());
				modifiersJsonArray.add(modifierJsonObject);
			});
			jsonObject.add("attribute_modifiers", modifiersJsonArray);
		}

		var positionJsonObject = new JsonObject();
		positionJsonObject.addProperty("x", positionX);
		positionJsonObject.addProperty("y", positionY);
		jsonObject.add("position", positionJsonObject);

		if (!getConnectedSkills().isEmpty()) {
			var connectionsJsonArray = new JsonArray();
			getConnectedSkills().forEach(skillId -> {
				connectionsJsonArray.add(skillId.toString());
			});
			jsonObject.add("connections", connectionsJsonArray);
		}

		if (isGateway()) jsonObject.addProperty("gatewayId", getGatewayId().toString());
		return jsonObject;
	}

	public static PassiveSkill loadFromJson(ResourceLocation id, JsonObject jsonObject) {
		var buttonSize = jsonObject.get("button_size").getAsInt();
		var treeId = new ResourceLocation(jsonObject.get("tree").getAsString());
		var backgroundTexture = new ResourceLocation(jsonObject.get("background_texture").getAsString());
		var iconTexture = new ResourceLocation(jsonObject.get("icon_texture").getAsString());
		var connectedTreeId = jsonObject.has("connected_tree") ? new ResourceLocation(jsonObject.get("connected_tree").getAsString()) : null;
		var isStartingPoint = jsonObject.has("starting_point");
		var skill = new PassiveSkill(id, treeId, buttonSize, backgroundTexture, iconTexture, isStartingPoint);
		skill.connectedTreeId = connectedTreeId;

		if (jsonObject.has("attribute_modifiers")) {
			var modifiersJsonArray = jsonObject.get("attribute_modifiers").getAsJsonArray();
			modifiersJsonArray.forEach(modifierJsonElement -> {
				var modifierJsonObject = (JsonObject) modifierJsonElement;
				var attributeId = new ResourceLocation(modifierJsonObject.get("attribute").getAsString());
				var attribute = ForgeRegistries.ATTRIBUTES.getValue(attributeId);
				if (attribute == null) {
					LOGGER.error("Attribute {} does not exist", attributeId);
				}
				var amount = modifierJsonObject.get("amount").getAsDouble();
				var operation = Operation.valueOf(modifierJsonObject.get("operation").getAsString().toUpperCase());
				var uniqueId = UUID.randomUUID();
				var modifier = new AttributeModifier(uniqueId, "Passive Skill Bonus", amount, operation);
				skill.addAttributeBonus(attribute, modifier);
			});
		}

		var positionJsonObject = jsonObject.get("position").getAsJsonObject();
		var positionX = positionJsonObject.get("x").getAsInt();
		var positionY = positionJsonObject.get("y").getAsInt();
		skill.setPosition(positionX, positionY);

		if (jsonObject.has("connections")) {
			var connectionsJsonArray = jsonObject.get("connections").getAsJsonArray();
			connectionsJsonArray.forEach(connectionJsonElement -> {
				var connectedSkillId = new ResourceLocation(connectionJsonElement.getAsString());
				skill.connectedSkills.add(connectedSkillId);
			});
		}

		if (jsonObject.has("gatewayId")) skill.setGatewayId(new ResourceLocation(jsonObject.get("gatewayId").getAsString()));
		return skill;
	}

	public void writeToByteBuf(FriendlyByteBuf buf) {
		buf.writeUtf(getId().toString());
		buf.writeUtf(getTreeId().toString());
		buf.writeInt(getButtonSize());
		buf.writeUtf(getBackgroundTexture().toString());
		buf.writeUtf(getIconTexture().toString());
		buf.writeBoolean(isStartingPoint());
		buf.writeInt(getPositionX());
		buf.writeInt(getPositionY());
		buf.writeInt(getConnectedSkills().size());
		getConnectedSkills().forEach(skillId -> buf.writeUtf(skillId.toString()));
		buf.writeBoolean(getConnectedTreeId() != null);
		if (connectedTreeId != null) buf.writeUtf(getConnectedTreeId().toString());
		buf.writeInt(getAttributeModifiers().size());
		getAttributeModifiers().forEach(pair -> {
			var attribute = pair.getLeft();
			var attributeId = ForgeRegistries.ATTRIBUTES.getKey(attribute).toString();
			buf.writeUtf(attributeId);
			var modifier = pair.getRight();
			var amount = modifier.getAmount();
			var operation = modifier.getOperation();
			buf.writeDouble(amount);
			buf.writeInt(operation.ordinal());
		});
		buf.writeBoolean(isGateway());
		if (isGateway()) buf.writeUtf(getGatewayId().toString());
	}

	public static PassiveSkill loadFromByteBuf(FriendlyByteBuf buf) {
		var skillId = new ResourceLocation(buf.readUtf());
		var treeId = new ResourceLocation(buf.readUtf());
		var buttonSize = buf.readInt();
		var backgroundTexture = new ResourceLocation(buf.readUtf());
		var iconTexture = new ResourceLocation(buf.readUtf());
		var isStartingPoint = buf.readBoolean();
		var positionX = buf.readInt();
		var positionY = buf.readInt();
		var skill = new PassiveSkill(skillId, treeId, buttonSize, backgroundTexture, iconTexture, isStartingPoint);
		skill.setPosition(positionX, positionY);
		var connectionsCount = buf.readInt();
		for (var i = 0; i < connectionsCount; i++) {
			var connectedSkillId = new ResourceLocation(buf.readUtf());
			skill.connectedSkills.add(connectedSkillId);
		}
		var hasConnectedTreeId = buf.readBoolean();
		if (hasConnectedTreeId) {
			var connectedTreeId = new ResourceLocation(buf.readUtf());
			skill.connectedTreeId = connectedTreeId;
		}
		var attributeModifiersCount = buf.readInt();
		for (var i = 0; i < attributeModifiersCount; i++) {
			var attributeId = buf.readUtf();
			var attribute = ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(attributeId));
			var amount = buf.readDouble();
			var operation = Operation.values()[buf.readInt()];
			skill.addAttributeBonus(attribute, new AttributeModifier(UUID.randomUUID(), "Passive Skill Bonus", amount, operation));
		}
		boolean isGateway = buf.readBoolean();
		if (isGateway) skill.setGatewayId(new ResourceLocation(buf.readUtf()));
		return skill;
	}
}
