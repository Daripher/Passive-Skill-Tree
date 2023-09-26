package daripher.skilltree.data.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.common.CuriosHelper;
import top.theillusivec4.curios.common.CuriosHelper.SlotAttributeWrapper;

/**
 * Class to serialize and deserialize {@link Attribute} and
 * {@link SlotAttributeWrapper} objects in JSON format.
 */
public class AttributeSerializer implements JsonSerializer<Attribute>, JsonDeserializer<Attribute> {
    @Override
    public Attribute deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        ResourceLocation id = context.deserialize(json, ResourceLocation.class);
        Attribute attribute;
        if (id.getNamespace().equals("curios"))
            attribute = CuriosHelper.getOrCreateSlotAttribute(id.getPath());
        else
            attribute = ForgeRegistries.ATTRIBUTES.getValue(id);
        if (attribute == null)
            throw new NullPointerException("Attribute " + id + " doesn't exist!");
        return attribute;
    }

    @Override
    public JsonElement serialize(Attribute src, Type typeOfSrc, JsonSerializationContext context) {
        ResourceLocation id;
        if (src instanceof SlotAttributeWrapper wrapper)
            id = new ResourceLocation("curios", wrapper.identifier);
        else
            id = ForgeRegistries.ATTRIBUTES.getKey(src);
        return context.serialize(id);
    }
}