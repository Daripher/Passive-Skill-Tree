package daripher.skilltree.util;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;

import daripher.skilltree.skill.PassiveSkill;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.common.CuriosHelper;
import top.theillusivec4.curios.common.CuriosHelper.SlotAttributeWrapper;

public class JsonHelper {
	private static final Logger LOGGER = LogUtils.getLogger();

	public static void writeAttributeModifiers(JsonObject json, Iterable<Pair<Attribute, AttributeModifier>> modifiers) {
		JsonArray modifiersJson = new JsonArray();
		modifiers.forEach(pair -> writeAttributeModifier(modifiersJson, pair));
		json.add("attribute_modifiers", modifiersJson);
	}

	public static void writeAttributeModifier(JsonArray json, Pair<Attribute, AttributeModifier> pair) {
		JsonObject modifierJson = new JsonObject();
		writeAttribute(modifierJson, pair.getLeft());
		writeAttributeModifier(modifierJson, pair.getRight());
		json.add(modifierJson);
	}

	public static void writeAttributeModifier(JsonObject json, AttributeModifier modifier) {
		json.addProperty("amount", modifier.getAmount());
		json.addProperty("operation", modifier.getOperation().name().toLowerCase());
	}

	public static void writeAttribute(JsonObject json, Attribute attribute) {
		String attributeId;
		if (attribute instanceof SlotAttributeWrapper wrapper) {
			attributeId = "curios:" + wrapper.identifier;
		} else {
			attributeId = ForgeRegistries.ATTRIBUTES.getKey(attribute).toString();
		}
		json.addProperty("attribute", attributeId);
	}

	public static void writePosition(JsonObject json, int x, int y) {
		JsonObject positionJson = new JsonObject();
		positionJson.addProperty("x", x);
		positionJson.addProperty("y", y);
		json.add("position", positionJson);
	}

	public static JsonArray writeResourceLocations(Iterable<ResourceLocation> locations) {
		JsonArray json = new JsonArray();
		locations.forEach(location -> json.add(location.toString()));
		return json;
	}

	public static void writeOptionalResourceLocation(JsonObject json, Optional<ResourceLocation> location, String name) {
		if (location.isPresent()) json.addProperty(name, location.get().toString());
	}

	public static void writeOptionalBoolean(JsonObject json, boolean flag, String name) {
		if (flag) json.addProperty(name, flag);
	}

	public static JsonObject writePassiveSkill(PassiveSkill skill) {
		JsonObject json = new JsonObject();
		json.addProperty("button_size", skill.getButtonSize());
		json.addProperty("tree", skill.getTreeId().toString());
		json.addProperty("background_texture", skill.getBackgroundTexture().toString());
		json.addProperty("icon_texture", skill.getIconTexture().toString());
		writeOptionalResourceLocation(json, skill.getConnectedTreeId(), "connected_tree");
		writeOptionalBoolean(json, skill.isStartingPoint(), "starting_point");
		writeAttributeModifiers(json, skill.getAttributeModifiers());
		writePosition(json, skill.getPositionX(), skill.getPositionY());
		json.add("connections", writeResourceLocations(skill.getConnectedSkills()));
		writeOptionalResourceLocation(json, skill.getGatewayId(), "gateway");
		return json;
	}

	public static ResourceLocation readResourceLocation(JsonObject json, String name) {
		return new ResourceLocation(json.get(name).getAsString());
	}

	public static Optional<ResourceLocation> readOptionalResourceLocation(JsonObject json, String name) {
		return json.has(name) ? Optional.of(readResourceLocation(json, name)) : Optional.empty();
	}

	public static Iterable<Pair<Attribute, AttributeModifier>> readAttributeModifiers(JsonObject json) {
		JsonArray modifiersJson = json.get("attribute_modifiers").getAsJsonArray();
		List<Pair<Attribute, AttributeModifier>> modifiers = new ArrayList<>();
		modifiersJson.forEach(jsonElement -> {
			Attribute attribute = readAttribute((JsonObject) jsonElement);
			AttributeModifier modifier = readAttributeModifier((JsonObject) jsonElement);
			modifiers.add(Pair.of(attribute, modifier));
		});
		return modifiers;
	}

	public static AttributeModifier readAttributeModifier(JsonObject json) {
		double amount = json.get("amount").getAsDouble();
		Operation operation = Operation.valueOf(json.get("operation").getAsString().toUpperCase());
		UUID uniqueId = UUID.randomUUID();
		return new AttributeModifier(uniqueId, "Passive Skill Bonus", amount, operation);
	}

	public static Attribute readAttribute(JsonObject json) {
		String attributeId = json.get("attribute").getAsString();
		Attribute attribute;
		if (attributeId.startsWith("curios:")) {
			attribute = CuriosHelper.getOrCreateSlotAttribute(attributeId.replace("curios:", ""));
		} else {
			attribute = ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(attributeId));
		}
		if (attribute == null) LOGGER.error("Attribute {} does not exist", attributeId);
		return attribute;
	}

	public static boolean readOptionalBoolean(JsonObject json, String name) {
		return json.has(name) && json.get(name).getAsBoolean();
	}

	public static Point readPosition(JsonObject json) {
		JsonObject positionJson = json.get("position").getAsJsonObject();
		int x = positionJson.get("x").getAsInt();
		int y = positionJson.get("y").getAsInt();
		return new Point(x, y);
	}

	public static Iterable<ResourceLocation> readResourceLocations(JsonObject json, String name) {
		JsonArray locationsJson = json.get(name).getAsJsonArray();
		List<ResourceLocation> locations = new ArrayList<ResourceLocation>();
		locationsJson.forEach(element -> locations.add(new ResourceLocation(element.getAsString())));
		return locations;
	}

	public static PassiveSkill readPassiveSkill(ResourceLocation id, JsonObject json) {
		ResourceLocation tree = readResourceLocation(json, "tree");
		int size = json.get("button_size").getAsInt();
		ResourceLocation background = readResourceLocation(json, "background_texture");
		ResourceLocation icon = readResourceLocation(json, "icon_texture");
		boolean startingPoint = readOptionalBoolean(json, "starting_point");
		PassiveSkill skill = new PassiveSkill(id, tree, size, background, icon, startingPoint);
		skill.setConnectedTree(readOptionalResourceLocation(json, "connected_tree"));
		readAttributeModifiers(json).forEach(skill::addAttributeBonus);
		skill.setPosition(readPosition(json));
		readResourceLocations(json, "connections").forEach(skill.getConnectedSkills()::add);
		skill.setGatewayId(readOptionalResourceLocation(json, "gateway"));
		return skill;
	}
}
