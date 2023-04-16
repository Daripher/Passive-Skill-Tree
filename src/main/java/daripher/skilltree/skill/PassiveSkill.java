package daripher.skilltree.skill;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Triple;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraftforge.registries.ForgeRegistries;

public class PassiveSkill {
	private final ResourceLocation id;
	private final ResourceLocation backgroundTexture;
	private final ResourceLocation iconTexture;
	private final int buttonSize;
	private final boolean isStartingPoint;
	private final @Nullable Triple<Attribute, Double, Operation> attributeBonus;
	private @Nullable UUID uniqueId = null;
	private final List<ResourceLocation> connectedSkills = new ArrayList<>();
	private int positionX, positionY;

	public PassiveSkill(ResourceLocation id, int buttonSize, ResourceLocation backgroundTexture, ResourceLocation iconTexture, boolean isStartingPoint, @Nullable Triple<Attribute, Double, Operation> attributeBonus) {
		this.id = id;
		this.backgroundTexture = backgroundTexture;
		this.iconTexture = iconTexture;
		this.buttonSize = buttonSize;
		this.isStartingPoint = isStartingPoint;
		this.attributeBonus = attributeBonus;
	}

	public ResourceLocation getId() {
		return id;
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

	public boolean isStartingPoint() {
		return isStartingPoint;
	}

	@Nullable
	public Triple<Attribute, Double, Operation> getAttributeBonus() {
		return attributeBonus;
	}

	public UUID getUniqueId() {
		return uniqueId;
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

	public JsonObject writeToJson() {
		var jsonObject = new JsonObject();
		jsonObject.addProperty("button_size", getButtonSize());
		jsonObject.addProperty("background_texture", getBackgroundTexture().toString());
		jsonObject.addProperty("icon_texture", getIconTexture().toString());

		if (isStartingPoint()) {
			jsonObject.addProperty("starting_point", true);
		}

		if (getAttributeBonus() != null) {
			var attributeBonusJsonObject = new JsonObject();
			var attributeId = ForgeRegistries.ATTRIBUTES.getKey(getAttributeBonus().getLeft()).toString();
			attributeBonusJsonObject.addProperty("attribute", attributeId);
			attributeBonusJsonObject.addProperty("bonus", getAttributeBonus().getMiddle());
			attributeBonusJsonObject.addProperty("operation", getAttributeBonus().getRight().name().toLowerCase());
			var modifierId = uniqueId == null ? UUID.randomUUID() : uniqueId;
			attributeBonusJsonObject.addProperty("unique_id", modifierId.toString());
			jsonObject.add("attribute_bonus", attributeBonusJsonObject);
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

		return jsonObject;
	}

	public static PassiveSkill loadFromJson(ResourceLocation id, JsonObject jsonObject) {
		var buttonSize = jsonObject.get("button_size").getAsInt();
		var backgroundTexture = new ResourceLocation(jsonObject.get("background_texture").getAsString());
		var iconTexture = new ResourceLocation(jsonObject.get("icon_texture").getAsString());
		var isStartingPoint = jsonObject.has("starting_point");
		Triple<Attribute, Double, Operation> attributeBonus = null;
		var hasAttributeBonus = jsonObject.has("attribute_bonus");

		if (hasAttributeBonus) {
			var attributeBonusJsonObject = jsonObject.get("attribute_bonus").getAsJsonObject();
			var attributeId = new ResourceLocation(attributeBonusJsonObject.get("attribute").getAsString());
			var attribute = ForgeRegistries.ATTRIBUTES.getValue(attributeId);
			var bonus = attributeBonusJsonObject.get("bonus").getAsDouble();
			var operation = Operation.valueOf(attributeBonusJsonObject.get("operation").getAsString().toUpperCase());
			attributeBonus = Triple.of(attribute, bonus, operation);
		}

		var passiveSkill = new PassiveSkill(id, buttonSize, backgroundTexture, iconTexture, isStartingPoint, attributeBonus);

		if (hasAttributeBonus) {
			var attributeBonusJsonObject = jsonObject.get("attribute_bonus").getAsJsonObject();
			var uniqueIdString = attributeBonusJsonObject.get("unique_id").getAsString();
			passiveSkill.uniqueId = UUID.fromString(uniqueIdString);
		}

		var positionJsonObject = jsonObject.get("position").getAsJsonObject();
		var positionX = positionJsonObject.get("x").getAsInt();
		var positionY = positionJsonObject.get("y").getAsInt();
		passiveSkill.setPosition(positionX, positionY);

		if (jsonObject.has("connections")) {
			var connectionsJsonArray = jsonObject.get("connections").getAsJsonArray();
			connectionsJsonArray.forEach(connectionJsonElement -> {
				var connectedSkillId = new ResourceLocation(connectionJsonElement.getAsString());
				passiveSkill.connectedSkills.add(connectedSkillId);
			});
		}

		return passiveSkill;
	}

	public void writeToByteBuf(FriendlyByteBuf buf) {
		buf.writeUtf(getId().toString());
		buf.writeInt(getButtonSize());
		buf.writeUtf(getBackgroundTexture().toString());
		buf.writeUtf(getIconTexture().toString());
		buf.writeBoolean(isStartingPoint());
		buf.writeInt(getPositionX());
		buf.writeInt(getPositionY());
		buf.writeInt(getConnectedSkills().size());
		getConnectedSkills().forEach(skillId -> {
			buf.writeUtf(skillId.toString());
		});
	}

	public static PassiveSkill loadFromByteBuf(FriendlyByteBuf buf) {
		var skillId = new ResourceLocation(buf.readUtf());
		var buttonSize = buf.readInt();
		var backgroundTexture = new ResourceLocation(buf.readUtf());
		var iconTexture = new ResourceLocation(buf.readUtf());
		var isStartingPoint = buf.readBoolean();
		var positionX = buf.readInt();
		var positionY = buf.readInt();
		var passiveSkill = new PassiveSkill(skillId, buttonSize, backgroundTexture, iconTexture, isStartingPoint, null);
		passiveSkill.setPosition(positionX, positionY);
		var connectionsCount = buf.readInt();

		for (var i = 0; i < connectionsCount; i++) {
			var connectedSkillId = new ResourceLocation(buf.readUtf());
			passiveSkill.connectedSkills.add(connectedSkillId);
		}

		return passiveSkill;
	}
}
