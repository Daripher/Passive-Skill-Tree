package daripher.skilltree.util;

import javax.annotation.Nullable;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.common.CuriosHelper;
import top.theillusivec4.curios.common.CuriosHelper.SlotAttributeWrapper;

public class AttributeHelper {
	@Nullable
	public static Attribute getAttributeById(String id) {
		if (id.startsWith("curios:")) {
			return CuriosHelper.getOrCreateSlotAttribute(id.replace("curios:", ""));
		} else {
			return ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(id));
		}
	}

	public static String getAttributeId(Attribute attribute) {
		if (attribute instanceof SlotAttributeWrapper wrapper) {
			return "curios:" + wrapper.getIdentifier();
		} else {
			return ForgeRegistries.ATTRIBUTES.getKey(attribute).toString();
		}
	}

	public static boolean isAttributeId(String id) {
		return id.startsWith("curios:") || ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(id)) != null;
	}
}
